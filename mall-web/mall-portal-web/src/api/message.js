import request from '@/utils/request'

/**
 * 获取消息列表
 * GET /api/ums/message/list?type=&isRead=&pageNum=1&pageSize=20
 */
export function getMessageList(params) {
  return request({
    url: '/api/ums/message/list',
    method: 'get',
    params
  })
}

/**
 * 获取未读消息数量
 * GET /api/ums/message/unreadCount
 */
export function getUnreadCount() {
  return request({
    url: '/api/ums/message/unreadCount',
    method: 'get'
  })
}

/**
 * 标记消息已读
 * PUT /api/ums/message/markRead?messageId=xxx
 */
export function markRead(messageId) {
  return request({
    url: '/api/ums/message/markRead',
    method: 'put',
    params: { messageId }
  })
}

/**
 * 一键已读
 * PUT /api/ums/message/markAllRead
 */
export function markAllRead() {
  return request({
    url: '/api/ums/message/markAllRead',
    method: 'put'
  })
}
