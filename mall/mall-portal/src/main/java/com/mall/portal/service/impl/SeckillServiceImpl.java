package com.mall.portal.service.impl;

import com.mall.portal.service.SeckillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀服务实现类
 *
 * 核心设计：
 * 1. Redis Lua脚本原子性扣减库存
 * 2. Redis String记录已秒杀用户（带过期时间）
 * 3. 秒杀成功后将订单创建任务投递到MQ异步处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeckillServiceImpl implements SeckillService {

    private final StringRedisTemplate redisTemplate;

    /**
     * 秒杀库存Key前缀: seckill:stock:{flashPromotionId}:{skuId}
     */
    private static final String SECKILL_STOCK_KEY_PREFIX = "seckill:stock:";

    /**
     * 用户秒杀标记Key前缀: seckill:user:{flashPromotionId}:{memberId}
     */
    private static final String SECKILL_USER_KEY_PREFIX = "seckill:user:";

    /**
     * 秒杀结果Key前缀: seckill:result:{flashPromotionId}:{memberId}
     */
    private static final String SECKILL_RESULT_KEY_PREFIX = "seckill:result:";

    /**
     * 用户秒杀标记过期时间（24小时）
     */
    private static final long USER_MARK_EXPIRE_HOURS = 24;

    /**
     * 秒杀库存扣减Lua脚本
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
    private static final String SECKILL_DEDUCT_LUA =
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
     * 秒杀库存回滚Lua脚本
     */
    private static final String SECKILL_ROLLBACK_LUA =
            "local key = KEYS[1] " +
            "local quantity = tonumber(ARGV[1]) " +
            "local stock = redis.call('GET', key) " +
            "if not stock then " +
            "    return -1 " +
            "end " +
            "redis.call('INCRBY', key, quantity) " +
            "return tonumber(redis.call('GET', key))";

    @Override
    public boolean seckill(Long flashPromotionId, Long skuId, Long memberId) {
        // Step 1: 检查用户是否已参与
        if (hasParticipated(flashPromotionId, memberId)) {
            log.warn("用户已参与秒杀, memberId={}, flashPromotionId={}", memberId, flashPromotionId);
            return false;
        }

        // Step 2: 标记用户已参与（防止重复秒杀，设置过期时间）
        String userKey = buildUserKey(flashPromotionId, memberId);
        Boolean setSuccess = redisTemplate.opsForValue().setIfAbsent(userKey, "1", USER_MARK_EXPIRE_HOURS, TimeUnit.HOURS);
        if (Boolean.FALSE.equals(setSuccess)) {
            log.warn("用户秒杀标记已存在, memberId={}, flashPromotionId={}", memberId, flashPromotionId);
            return false;
        }

        // Step 3: Redis Lua脚本原子性扣减库存
        String stockKey = buildStockKey(flashPromotionId, skuId);
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(SECKILL_DEDUCT_LUA);
        redisScript.setResultType(Long.class);

        Long result = redisTemplate.execute(redisScript, Collections.singletonList(stockKey), "1");

        if (result == null) {
            log.error("秒杀扣减库存执行异常, flashPromotionId={}, skuId={}", flashPromotionId, skuId);
            // 回滚用户标记
            redisTemplate.delete(userKey);
            return false;
        }

        if (result == -1) {
            log.warn("秒杀库存不存在, flashPromotionId={}, skuId={}", flashPromotionId, skuId);
            redisTemplate.delete(userKey);
            return false;
        }

        if (result == -2) {
            log.warn("秒杀库存不足, flashPromotionId={}, skuId={}", flashPromotionId, skuId);
            redisTemplate.delete(userKey);
            return false;
        }

        // Step 4: 记录秒杀结果（排队中）
        String resultKey = buildResultKey(flashPromotionId, memberId);
        redisTemplate.opsForValue().set(resultKey, "PROCESSING", 30, TimeUnit.MINUTES);

        // Step 5: 发送MQ消息异步创建订单（后续实现）
        // rabbitTemplate.convertAndSend("seckill.order.exchange", "seckill.order", seckillOrderMessage);

        log.info("秒杀成功, memberId={}, flashPromotionId={}, skuId={}, remaining={}",
                memberId, flashPromotionId, skuId, result);
        return true;
    }

    @Override
    public void initSeckillStock(Long flashPromotionId, Long skuId, int stock) {
        String key = buildStockKey(flashPromotionId, skuId);
        redisTemplate.opsForValue().set(key, String.valueOf(stock));
        log.info("初始化秒杀库存成功, flashPromotionId={}, skuId={}, stock={}", flashPromotionId, skuId, stock);
    }

    @Override
    public boolean hasParticipated(Long flashPromotionId, Long memberId) {
        String key = buildUserKey(flashPromotionId, memberId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public Long getSeckillStock(Long flashPromotionId, Long skuId) {
        String key = buildStockKey(flashPromotionId, skuId);
        String stockStr = redisTemplate.opsForValue().get(key);
        if (stockStr == null) {
            return 0L;
        }
        try {
            return Long.parseLong(stockStr);
        } catch (NumberFormatException e) {
            log.error("秒杀库存格式错误, flashPromotionId={}, skuId={}, value={}", flashPromotionId, skuId, stockStr);
            return 0L;
        }
    }

    @Override
    public boolean rollbackSeckillStock(Long flashPromotionId, Long skuId, int quantity) {
        if (quantity <= 0) {
            log.warn("回滚数量必须大于0, flashPromotionId={}, skuId={}, quantity={}", flashPromotionId, skuId, quantity);
            return false;
        }

        String key = buildStockKey(flashPromotionId, skuId);
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(SECKILL_ROLLBACK_LUA);
        redisScript.setResultType(Long.class);

        Long result = redisTemplate.execute(redisScript, Collections.singletonList(key), String.valueOf(quantity));

        if (result == null || result == -1) {
            log.error("秒杀库存回滚失败, flashPromotionId={}, skuId={}", flashPromotionId, skuId);
            return false;
        }

        log.info("秒杀库存回滚成功, flashPromotionId={}, skuId={}, quantity={}, current={}",
                flashPromotionId, skuId, quantity, result);
        return true;
    }

    @Override
    public void clearUserSeckillMark(Long flashPromotionId, Long memberId) {
        String key = buildUserKey(flashPromotionId, memberId);
        redisTemplate.delete(key);
        log.info("清除用户秒杀标记, flashPromotionId={}, memberId={}", flashPromotionId, memberId);
    }

    /**
     * 构建秒杀库存Key
     */
    private String buildStockKey(Long flashPromotionId, Long skuId) {
        return SECKILL_STOCK_KEY_PREFIX + flashPromotionId + ":" + skuId;
    }

    /**
     * 构建用户秒杀标记Key
     */
    private String buildUserKey(Long flashPromotionId, Long memberId) {
        return SECKILL_USER_KEY_PREFIX + flashPromotionId + ":" + memberId;
    }

    /**
     * 构建秒杀结果Key
     */
    private String buildResultKey(Long flashPromotionId, Long memberId) {
        return SECKILL_RESULT_KEY_PREFIX + flashPromotionId + ":" + memberId;
    }
}
