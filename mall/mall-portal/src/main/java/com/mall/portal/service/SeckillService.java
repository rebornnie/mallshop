package com.mall.portal.service;

/**
 * 秒杀服务接口
 *
 * 核心设计：
 * 1. Redis Lua脚本原子性扣减库存
 * 2. Redis Set记录已秒杀用户，防止重复秒杀
 * 3. 秒杀成功后将订单创建任务投递到MQ异步处理
 */
public interface SeckillService {

    /**
     * 秒杀下单
     *
     * 流程：
     * 1. 检查用户是否已参与（Redis Set）
     * 2. Redis Lua脚本预扣库存
     * 3. 记录用户参与标记
     * 4. 发送MQ消息异步创建订单
     *
     * @param flashPromotionId 秒杀活动ID
     * @param skuId SKU ID
     * @param memberId 会员ID
     * @return true-秒杀成功, false-秒杀失败
     */
    boolean seckill(Long flashPromotionId, Long skuId, Long memberId);

    /**
     * 初始化秒杀库存
     *
     * @param flashPromotionId 秒杀活动ID
     * @param skuId SKU ID
     * @param stock 库存数量
     */
    void initSeckillStock(Long flashPromotionId, Long skuId, int stock);

    /**
     * 检查用户是否已参与秒杀
     *
     * @param flashPromotionId 秒杀活动ID
     * @param memberId 会员ID
     * @return true-已参与, false-未参与
     */
    boolean hasParticipated(Long flashPromotionId, Long memberId);

    /**
     * 获取秒杀库存
     *
     * @param flashPromotionId 秒杀活动ID
     * @param skuId SKU ID
     * @return 当前库存数量
     */
    Long getSeckillStock(Long flashPromotionId, Long skuId);

    /**
     * 回滚秒杀库存
     *
     * 订单取消或超时未支付时返还库存。
     *
     * @param flashPromotionId 秒杀活动ID
     * @param skuId SKU ID
     * @param quantity 回滚数量
     * @return true-回滚成功
     */
    boolean rollbackSeckillStock(Long flashPromotionId, Long skuId, int quantity);

    /**
     * 清除用户秒杀标记
     *
     * @param flashPromotionId 秒杀活动ID
     * @param memberId 会员ID
     */
    void clearUserSeckillMark(Long flashPromotionId, Long memberId);
}
