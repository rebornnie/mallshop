import request from '@/utils/request'

export function alipay(orderId) {
  return request({ url: '/api/oms/pay/alipay', method: 'post', data: { orderId } })
}

export function getPayStatus(orderId) {
  return request({ url: '/api/oms/pay/status', method: 'get', params: { orderId } })
}
