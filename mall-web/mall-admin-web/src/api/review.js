import request from '@/utils/request'

/**
 * 获取评价列表
 * GET /api/admin/comment/list?productId=&showStatus=&keyword=&pageNum=1&pageSize=20
 */
export function getReviewList(params) {
  return request({
    url: '/api/admin/comment/list',
    method: 'get',
    params
  })
}

/**
 * 获取评价详情
 * GET /api/admin/comment/{id}
 */
export function getReviewDetail(id) {
  return request({
    url: `/api/admin/comment/${id}`,
    method: 'get'
  })
}

/**
 * 更新评价显示状态
 * PUT /api/admin/comment/updateShowStatus?id=xxx&showStatus=xxx
 */
export function updateReviewStatus(id, showStatus) {
  return request({
    url: '/api/admin/comment/updateShowStatus',
    method: 'put',
    params: { id, showStatus }
  })
}

/**
 * 回复评价
 * POST /api/admin/comment/reply
 * Body: { commentId, content }
 */
export function replyReview(data) {
  return request({
    url: '/api/admin/comment/reply',
    method: 'post',
    data
  })
}
