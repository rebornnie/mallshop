import request from '@/utils/request'

/**
 * 秒杀下单
 * POST /api/seckill/order/create?flashPromotionId=&skuId=
 */
export function seckillOrder(flashPromotionId, skuId) {
  return request({
    url: '/api/seckill/order/create',
    method: 'post',
    params: { flashPromotionId, skuId }
  })
}

/**
 * 查询秒杀结果
 * GET /api/seckill/order/result?flashPromotionId=
 */
export function getSeckillResult(flashPromotionId) {
  return request({
    url: '/api/seckill/order/result',
    method: 'get',
    params: { flashPromotionId }
  })
}

/**
 * 获取秒杀库存
 * GET /api/seckill/stock?flashPromotionId=&skuId=
 */
export function getSeckillStock(flashPromotionId, skuId) {
  return request({
    url: '/api/seckill/stock',
    method: 'get',
    params: { flashPromotionId, skuId }
  })
}
