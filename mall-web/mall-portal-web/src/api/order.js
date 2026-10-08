import request from '@/utils/request'

export function generateConfirm(data) {
  return request({ url: '/api/oms/order/generateConfirm', method: 'post', data })
}

export function createOrder(data) {
  return request({ url: '/api/oms/order/create', method: 'post', data })
}

export function getOrderList(params) {
  return request({ url: '/api/oms/order/list', method: 'get', params })
}

export function getOrderDetail(id) {
  return request({ url: `/api/oms/order/${id}`, method: 'get' })
}

export function cancelOrder(id, reason) {
  return request({ url: `/api/oms/order/cancel/${id}`, method: 'put', data: { reason } })
}

export function confirmReceive(id) {
  return request({ url: `/api/oms/order/confirmReceive/${id}`, method: 'put' })
}
