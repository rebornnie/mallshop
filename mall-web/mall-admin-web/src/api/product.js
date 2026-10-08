import request from '@/utils/request'

export function getProductList(params) {
  return request({ url: '/api/admin/product/list', method: 'get', params })
}

export function createProduct(data) {
  return request({ url: '/api/admin/product/create', method: 'post', data })
}

export function updateProduct(data) {
  return request({ url: '/api/admin/product/update', method: 'put', data })
}

export function deleteProduct(id) {
  return request({ url: `/api/admin/product/${id}`, method: 'delete' })
}

export function updatePublishStatus(data) {
  return request({ url: '/api/admin/product/updatePublishStatus', method: 'put', data })
}

export function batchUpdatePublishStatus(data) {
  return request({ url: '/api/admin/product/batchUpdatePublishStatus', method: 'put', data })
}

export function batchDeleteProduct(data) {
  return request({ url: '/api/admin/product/batchDelete', method: 'delete', data })
}

export function getCategoryList() {
  return request({ url: '/api/admin/category/list', method: 'get' })
}

export function createCategory(data) {
  return request({ url: '/api/admin/category/create', method: 'post', data })
}

export function updateCategory(data) {
  return request({ url: '/api/admin/category/update', method: 'put', data })
}

export function deleteCategory(id) {
  return request({ url: `/api/admin/category/${id}`, method: 'delete' })
}

export function getBrandList(params) {
  return request({ url: '/api/admin/brand/list', method: 'get', params })
}

export function createBrand(data) {
  return request({ url: '/api/admin/brand/create', method: 'post', data })
}

export function updateBrand(data) {
  return request({ url: '/api/admin/brand/update', method: 'put', data })
}

export function deleteBrand(id) {
  return request({ url: `/api/admin/brand/${id}`, method: 'delete' })
}

export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({ url: '/api/upload/image', method: 'post', data: formData, headers: { 'Content-Type': 'multipart/form-data' } })
}
