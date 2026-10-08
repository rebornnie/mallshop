import request from '@/utils/request'

/**
 * 获取可领取优惠券列表
 * GET /api/oms/coupon/available
 */
export function getAvailableCoupons() {
  return request({
    url: '/api/oms/coupon/available',
    method: 'get'
  })
}

/**
 * 领取优惠券
 * POST /api/oms/coupon/receive/{couponId}
 */
export function receiveCoupon(couponId) {
  return request({
    url: `/api/oms/coupon/receive/${couponId}`,
    method: 'post'
  })
}

/**
 * 获取我的优惠券列表
 * GET /api/oms/coupon/my?status=
 */
export function getMyCoupons(status) {
  return request({
    url: '/api/oms/coupon/my',
    method: 'get',
    params: { status }
  })
}

/**
 * 获取当前订单可用优惠券
 * GET /api/oms/coupon/availableForOrder?orderAmount=
 */
export function getAvailableForOrder(orderAmount) {
  return request({
    url: '/api/oms/coupon/availableForOrder',
    method: 'get',
    params: { orderAmount }
  })
}
