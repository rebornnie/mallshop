import request from '@/utils/request'

export function getProductList(params) {
  return request({ url: '/api/pms/product/list', method: 'get', params })
}

export function getProductDetail(id) {
  return request({ url: `/api/pms/product/${id}`, method: 'get' })
}

export function getCategoryList() {
  return request({ url: '/api/pms/category/list', method: 'get' })
}

export function getBrandList() {
  return request({ url: '/api/pms/brand/list', method: 'get' })
}

export function searchProducts(params) {
  return request({ url: '/api/pms/search', method: 'get', params })
}
