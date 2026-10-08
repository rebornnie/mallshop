import request from '@/utils/request'

export function getOrderStat(params) {
  return request({ url: '/api/admin/stat/order', method: 'get', params })
}

export function getProductStat() {
  return request({ url: '/api/admin/stat/product', method: 'get' })
}

export function getUserStat() {
  return request({ url: '/api/admin/stat/user', method: 'get' })
}
