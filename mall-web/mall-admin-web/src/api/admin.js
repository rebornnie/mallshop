import request from '@/utils/request'

export function login(data) {
  return request({ url: '/api/admin/login', method: 'post', data })
}

export function getAdminInfo() {
  return request({ url: '/api/admin/info', method: 'get' })
}

export function logout() {
  return request({ url: '/api/admin/logout', method: 'post' })
}

export function getAdminList(params) {
  return request({ url: '/api/admin/sys/admin/list', method: 'get', params })
}

export function createAdmin(data) {
  return request({ url: '/api/admin/sys/admin/create', method: 'post', data })
}

export function updateAdmin(data) {
  return request({ url: '/api/admin/sys/admin/update', method: 'put', data })
}

export function deleteAdmin(id) {
  return request({ url: `/api/admin/sys/admin/${id}`, method: 'delete' })
}

export function getRoleList() {
  return request({ url: '/api/admin/sys/role/list', method: 'get' })
}

export function createRole(data) {
  return request({ url: '/api/admin/sys/role/create', method: 'post', data })
}

export function updateRole(data) {
  return request({ url: '/api/admin/sys/role/update', method: 'put', data })
}

export function allocMenu(data) {
  return request({ url: '/api/admin/sys/role/allocMenu', method: 'put', data })
}

export function getMenuList() {
  return request({ url: '/api/admin/sys/menu/list', method: 'get' })
}

export function createMenu(data) {
  return request({ url: '/api/admin/sys/menu/create', method: 'post', data })
}

export function updateMenu(data) {
  return request({ url: '/api/admin/sys/menu/update', method: 'put', data })
}

export function deleteMenu(id) {
  return request({ url: `/api/admin/sys/menu/${id}`, method: 'delete' })
}
