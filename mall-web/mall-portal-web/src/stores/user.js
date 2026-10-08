import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('portalUser', () => {
  const token = ref(localStorage.getItem('portal_token') || '')
  const tokenHead = ref(localStorage.getItem('portal_tokenHead') || 'Bearer ')
  const userInfo = ref(JSON.parse(localStorage.getItem('portal_userInfo') || '{}'))

  const setToken = (data) => {
    token.value = data.token
    tokenHead.value = data.tokenHead || 'Bearer '
    localStorage.setItem('portal_token', data.token)
    localStorage.setItem('portal_tokenHead', data.tokenHead || 'Bearer ')
  }

  const setUserInfo = (data) => {
    userInfo.value = data
    localStorage.setItem('portal_userInfo', JSON.stringify(data))
  }

  const logout = () => {
    token.value = ''
    tokenHead.value = 'Bearer '
    userInfo.value = {}
    localStorage.removeItem('portal_token')
    localStorage.removeItem('portal_tokenHead')
    localStorage.removeItem('portal_userInfo')
  }

  const isLoggedIn = () => !!token.value

  return { token, tokenHead, userInfo, setToken, setUserInfo, logout, isLoggedIn }
})
