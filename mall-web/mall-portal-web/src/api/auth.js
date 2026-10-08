import request from '@/utils/request'

export function register(data) {
  return request({ url: '/api/ums/register', method: 'post', data })
}

export function login(data) {
  return request({ url: '/api/ums/login', method: 'post', data })
}

export function getCurrentUser() {
  return request({ url: '/api/ums/currentUser', method: 'get' })
}

export function updateUserInfo(data) {
  return request({ url: '/api/ums/update', method: 'put', data })
}

export function updatePassword(data) {
  return request({ url: '/api/ums/updatePassword', method: 'put', data })
}
