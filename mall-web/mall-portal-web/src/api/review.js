import request from '@/utils/request'

/**
 * 获取商品评价列表
 * GET /api/pms/comment/list?productId=xxx&pageNum=1&pageSize=20
 */
export function getProductReviews(productId, params) {
  return request({
    url: '/api/pms/comment/list',
    method: 'get',
    params: { productId, ...params }
  })
}

/**
 * 获取商品评价统计
 * GET /api/pms/comment/statistics?productId=xxx
 */
export function getProductReviewStats(productId) {
  return request({
    url: '/api/pms/comment/statistics',
    method: 'get',
    params: { productId }
  })
}

/**
 * 提交评价
 * POST /api/pms/comment/submit
 * Body: { orderId, orderItemId, star, content, pics }
 */
export function submitReview(data) {
  return request({
    url: '/api/pms/comment/submit',
    method: 'post',
    data
  })
}

/**
 * 获取待评价订单列表
 * 注意：后端暂无此接口，需要后端补充
 * 临时使用订单列表接口过滤
 */
export function getReviewableOrders() {
  return request({
    url: '/api/oms/order/list',
    method: 'get',
    params: { status: 3, pageSize: 100 }
  })
}
