import request from '@/utils/request'

export function getOrderList(params) {
  return request({ url: '/api/admin/order/list', method: 'get', params })
}

export function getOrderDetail(id) {
  return request({ url: `/api/admin/order/${id}`, method: 'get' })
}

export function deliverOrder(data) {
  return request({ url: '/api/admin/order/deliver', method: 'post', data })
}

export function refundOrder(data) {
  return request({ url: '/api/admin/order/refund', method: 'post', data })
}
