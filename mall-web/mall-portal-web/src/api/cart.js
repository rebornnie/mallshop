import request from '@/utils/request'

export function addCart(data) {
  return request({ url: '/api/oms/cart/add', method: 'post', data })
}

export function getCartList() {
  return request({ url: '/api/oms/cart/list', method: 'get' })
}

export function updateCart(data) {
  return request({ url: '/api/oms/cart/update', method: 'put', data })
}

export function deleteCartItem(id) {
  return request({ url: `/api/oms/cart/${id}`, method: 'delete' })
}

export function clearCart() {
  return request({ url: '/api/oms/cart/clear', method: 'delete' })
}
