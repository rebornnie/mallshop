import request from '@/utils/request'

/**
 * 获取优惠券列表
 * GET /api/admin/coupon/list?keyword=&type=&status=&pageNum=&pageSize=
 */
export function getCouponList(params) {
  return request({
    url: '/api/admin/coupon/list',
    method: 'get',
    params
  })
}

/**
 * 获取优惠券详情
 * GET /api/admin/coupon/{id}
 */
export function getCouponById(id) {
  return request({
    url: `/api/admin/coupon/${id}`,
    method: 'get'
  })
}

/**
 * 创建优惠券
 * POST /api/admin/coupon/create
 */
export function createCoupon(data) {
  return request({
    url: '/api/admin/coupon/create',
    method: 'post',
    data
  })
}

/**
 * 更新优惠券
 * PUT /api/admin/coupon/update?id=
 */
export function updateCoupon(id, data) {
  return request({
    url: '/api/admin/coupon/update',
    method: 'put',
    params: { id },
    data
  })
}

/**
 * 删除优惠券
 * DELETE /api/admin/coupon/{id}
 */
export function deleteCoupon(id) {
  return request({
    url: `/api/admin/coupon/${id}`,
    method: 'delete'
  })
}

/**
 * 更新优惠券状态
 * PUT /api/admin/coupon/updateStatus?id=&status=
 */
export function updateCouponStatus(id, status) {
  return request({
    url: '/api/admin/coupon/updateStatus',
    method: 'put',
    params: { id, status }
  })
}
