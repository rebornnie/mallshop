import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('adminUser', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const tokenHead = ref(localStorage.getItem('admin_tokenHead') || 'Bearer ')
  const userInfo = ref(JSON.parse(localStorage.getItem('admin_userInfo') || '{}'))
  const menuList = ref(JSON.parse(localStorage.getItem('admin_menuList') || '[]'))

  const setToken = (data) => {
    token.value = data.token
    tokenHead.value = data.tokenHead || 'Bearer '
    localStorage.setItem('admin_token', data.token)
    localStorage.setItem('admin_tokenHead', data.tokenHead || 'Bearer ')
  }

  const setUserInfo = (data) => {
    userInfo.value = data
    localStorage.setItem('admin_userInfo', JSON.stringify(data))
  }

  const setMenuList = (menus) => {
    menuList.value = menus
    localStorage.setItem('admin_menuList', JSON.stringify(menus))
  }

  const logout = () => {
    token.value = ''
    tokenHead.value = 'Bearer '
    userInfo.value = {}
    menuList.value = []
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_tokenHead')
    localStorage.removeItem('admin_userInfo')
    localStorage.removeItem('admin_menuList')
  }

  const isLoggedIn = () => {
    return !!token.value
  }

  return { token, tokenHead, userInfo, menuList, setToken, setUserInfo, setMenuList, logout, isLoggedIn }
})
