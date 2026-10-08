package com.mall.portal.service;

/**
 * 库存服务接口
 *
 * 基于Redis Lua脚本实现原子性库存扣减，确保并发场景下不超卖。
 *
 * 核心设计：
 * 1. 使用Redis String存储库存数量，Key格式: stock:sku:{skuId}
 * 2. 扣减操作使用Lua脚本保证原子性（读取+判断+扣减一步完成）
 * 3. 数据库库存作为最终一致性保障，通过定时同步或订单完成时扣减
 *
 * @author MallShop
 */
public interface StockService {

    /**
     * 初始化库存
     *
     * 将SKU库存加载到Redis中，通常在商品上架或系统启动时调用。
     *
     * @param skuId SKU ID
     * @param stock 库存数量
     */
    void initStock(Long skuId, int stock);

    /**
     * 扣减库存
     *
     * 使用Redis Lua脚本原子性扣减库存。
     * 如果库存不足，返回false且不做任何扣减。
     *
     * @param skuId SKU ID
     * @param quantity 扣减数量
     * @return true-扣减成功, false-库存不足
     */
    boolean deductStock(Long skuId, int quantity);

    /**
     * 批量扣减库存
     *
     * 同时扣减多个SKU的库存，使用Redis事务保证原子性。
     * 任一SKU库存不足则全部回滚。
     *
     * @param skuIds SKU ID数组
     * @param quantities 扣减数量数组
     * @return true-全部扣减成功, false-至少一个SKU库存不足
     */
    boolean deductStockBatch(Long[] skuIds, int[] quantities);

    /**
     * 回滚库存
     *
     * 订单取消或退款时返还库存。
     *
     * @param skuId SKU ID
     * @param quantity 回滚数量
     * @return true-回滚成功
     */
    boolean rollbackStock(Long skuId, int quantity);

    /**
     * 获取库存
     *
     * @param skuId SKU ID
     * @return 当前库存数量，不存在返回0
     */
    Long getStock(Long skuId);

    /**
     * 删除库存缓存
     *
     * 商品下架或删除时清理Redis库存。
     *
     * @param skuId SKU ID
     */
    void removeStock(Long skuId);

    /**
     * 同步数据库库存到Redis
     *
     * 系统启动或数据不一致时，从数据库同步库存到Redis。
     *
     * @param skuId SKU ID
     * @param dbStock 数据库中的库存数量
     */
    void syncStockFromDb(Long skuId, int dbStock);
}
