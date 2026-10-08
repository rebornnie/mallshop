import request from '@/utils/request'

export function getAddressList() {
  return request({ url: '/api/ums/address/list', method: 'get' })
}

export function addAddress(data) {
  return request({ url: '/api/ums/address/add', method: 'post', data })
}

export function updateAddress(data) {
  return request({ url: '/api/ums/address/update', method: 'put', data })
}

export function deleteAddress(id) {
  return request({ url: `/api/ums/address/${id}`, method: 'delete' })
}

export function setDefaultAddress(id) {
  return request({ url: `/api/ums/address/default/${id}`, method: 'put' })
}
