const TOKEN_KEY = 'admin_token'
const TOKEN_HEAD_KEY = 'admin_token_head'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function getTokenHead() {
  return localStorage.getItem(TOKEN_HEAD_KEY)
}

export function setTokenHead(head) {
  localStorage.setItem(TOKEN_HEAD_KEY, head)
}

export function getFullToken() {
  const token = getToken()
  const head = getTokenHead()
  if (!token || !head) return ''
  return `${head}${token}`
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(TOKEN_HEAD_KEY)
}

export function hasToken() {
  return !!getToken()
}
