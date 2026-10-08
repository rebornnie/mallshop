import request from '@/utils/request'

export function getMemberList(params) {
  return request({ url: '/api/admin/member/list', method: 'get', params })
}

export function getMemberDetail(id) {
  return request({ url: `/api/admin/member/${id}`, method: 'get' })
}
