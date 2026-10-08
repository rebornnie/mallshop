package com.mall.portal.service.impl;

import com.mall.portal.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 库存服务实现类
 *
 * 基于Redis Lua脚本实现原子性库存扣减，确保并发场景下不超卖。
 *
 * 核心设计：
 * 1. 使用Redis String存储库存数量
 * 2. 扣减操作使用Lua脚本保证原子性（读取+判断+扣减一步完成）
 * 3. 数据库库存作为最终一致性保障
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StringRedisTemplate redisTemplate;

    /**
     * Redis库存Key前缀
     */
    private static final String STOCK_KEY_PREFIX = "stock:sku:";

    /**
     * 扣减库存Lua脚本
     *
     * 参数:
     *   KEYS[1] - 库存Key
     *   ARGV[1] - 扣减数量
     *
     * 返回值:
     *   -1 - 库存不存在
     *   -2 - 库存不足
     *   >=0 - 扣减成功，返回剩余库存
     */
    private static final String DEDUCT_STOCK_LUA =
            "local key = KEYS[1] " +
            "local quantity = tonumber(ARGV[1]) " +
            "local stock = redis.call('GET', key) " +
            "if not stock then " +
            "    return -1 " +
            "end " +
            "stock = tonumber(stock) " +
            "if stock < quantity then " +
            "    return -2 " +
            "end " +
            "redis.call('DECRBY', key, quantity) " +
            "return stock - quantity";

    /**
     * 回滚库存Lua脚本
     *
     * 参数:
     *   KEYS[1] - 库存Key
     *   ARGV[1] - 回滚数量
     *
     * 返回值:
     *   -1 - 库存不存在
     *   >=0 - 回滚成功，返回当前库存
     */
    private static final String ROLLBACK_STOCK_LUA =
            "local key = KEYS[1] " +
            "local quantity = tonumber(ARGV[1]) " +
            "local stock = redis.call('GET', key) " +
            "if not stock then " +
            "    return -1 " +
            "end " +
            "redis.call('INCRBY', key, quantity) " +
            "return tonumber(redis.call('GET', key))";

    /**
     * 批量扣减库存Lua脚本
     *
     * 同时扣减多个SKU库存，任一SKU库存不足则全部回滚
     *
     * 参数:
     *   KEYS[1..n] - 库存Key列表
     *   ARGV[1..n] - 扣减数量列表
     *
     * 返回值:
     *   -1 - 某个库存不存在
     *   -2 - 某个库存不足
     *   1 - 全部扣减成功
     */
    private static final String BATCH_DEDUCT_STOCK_LUA =
            "local keys = KEYS " +
            "local quantities = ARGV " +
            "local tempKeys = {} " +
            "-- 第一阶段：预扣减到临时Key " +
            "for i = 1, #keys do " +
            "    local stock = redis.call('GET', keys[i]) " +
            "    if not stock then " +
            "        return -1 " +
            "    end " +
            "    stock = tonumber(stock) " +
            "    local quantity = tonumber(quantities[i]) " +
            "    if stock < quantity then " +
            "        return -2 " +
            "    end " +
            "    tempKeys[i] = keys[i] .. ':temp' " +
            "    redis.call('SET', tempKeys[i], stock - quantity) " +
            "end " +
            "-- 第二阶段：确认扣减（删除原Key，重命名临时Key） " +
            "for i = 1, #keys do " +
            "    redis.call('DEL', keys[i]) " +
            "    redis.call('RENAME', tempKeys[i], keys[i]) " +
            "end " +
            "return 1";

    @Override
    public void initStock(Long skuId, int stock) {
        String key = buildStockKey(skuId);
        redisTemplate.opsForValue().set(key, String.valueOf(stock));
        log.info("初始化库存成功, skuId={}, stock={}", skuId, stock);
    }

    @Override
    public boolean deductStock(Long skuId, int quantity) {
        if (quantity <= 0) {
            log.warn("扣减数量必须大于0, skuId={}, quantity={}", skuId, quantity);
            return false;
        }

        String key = buildStockKey(skuId);
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(DEDUCT_STOCK_LUA);
        redisScript.setResultType(Long.class);

        Long result = redisTemplate.execute(redisScript, Collections.singletonList(key), String.valueOf(quantity));

        if (result == null) {
            log.error("扣减库存执行异常, skuId={}", skuId);
            return false;
        }

        if (result == -1) {
            log.warn("库存不存在, skuId={}", skuId);
            return false;
        }

        if (result == -2) {
            log.warn("库存不足, skuId={}, quantity={}", skuId, quantity);
            return false;
        }

        log.info("扣减库存成功, skuId={}, quantity={}, remaining={}", skuId, quantity, result);
        return true;
    }

    @Override
    public boolean deductStockBatch(Long[] skuIds, int[] quantities) {
        if (skuIds == null || quantities == null || skuIds.length != quantities.length) {
            log.warn("批量扣减参数错误");
            return false;
        }

        List<String> keys = new java.util.ArrayList<>();
        List<String> args = new java.util.ArrayList<>();

        for (int i = 0; i < skuIds.length; i++) {
            keys.add(buildStockKey(skuIds[i]));
            args.add(String.valueOf(quantities[i]));
        }

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(BATCH_DEDUCT_STOCK_LUA);
        redisScript.setResultType(Long.class);

        Long result = redisTemplate.execute(redisScript, keys, args.toArray(new String[0]));

        if (result == null || result != 1) {
            log.warn("批量扣减库存失败, result={}", result);
            return false;
        }

        log.info("批量扣减库存成功, skuIds={}", (Object) skuIds);
        return true;
    }

    @Override
    public boolean rollbackStock(Long skuId, int quantity) {
        if (quantity <= 0) {
            log.warn("回滚数量必须大于0, skuId={}, quantity={}", skuId, quantity);
            return false;
        }

        String key = buildStockKey(skuId);
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(ROLLBACK_STOCK_LUA);
        redisScript.setResultType(Long.class);

        Long result = redisTemplate.execute(redisScript, Collections.singletonList(key), String.valueOf(quantity));

        if (result == null) {
            log.error("回滚库存执行异常, skuId={}", skuId);
            return false;
        }

        if (result == -1) {
            log.warn("回滚库存不存在, skuId={}", skuId);
            return false;
        }

        log.info("回滚库存成功, skuId={}, quantity={}, current={}", skuId, quantity, result);
        return true;
    }

    @Override
    public Long getStock(Long skuId) {
        String key = buildStockKey(skuId);
        String stockStr = redisTemplate.opsForValue().get(key);
        if (stockStr == null) {
            return 0L;
        }
        try {
            return Long.parseLong(stockStr);
        } catch (NumberFormatException e) {
            log.error("库存格式错误, skuId={}, value={}", skuId, stockStr);
            return 0L;
        }
    }

    @Override
    public void removeStock(Long skuId) {
        String key = buildStockKey(skuId);
        redisTemplate.delete(key);
        log.info("删除库存缓存, skuId={}", skuId);
    }

    @Override
    public void syncStockFromDb(Long skuId, int dbStock) {
        String key = buildStockKey(skuId);
        redisTemplate.opsForValue().set(key, String.valueOf(dbStock));
        log.info("同步数据库库存到Redis, skuId={}, dbStock={}", skuId, dbStock);
    }

    /**
     * 构建库存Redis Key
     */
    private String buildStockKey(Long skuId) {
        return STOCK_KEY_PREFIX + skuId;
    }
}
