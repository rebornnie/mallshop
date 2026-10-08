import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getAdminInfo } from '@/api/admin'
import { setToken, setTokenHead, removeToken } from '@/utils/auth'

export const useAdminStore = defineStore('admin', () => {
  const adminInfo = ref(null)
  const menuList = ref([])
  const isLoggedIn = ref(false)

  async function login(username, password) {
    const res = await loginApi({ username, password })
    setToken(res.data.token)
    setTokenHead(res.data.tokenHead)
    isLoggedIn.value = true
    await fetchAdminInfo()
    return res
  }

  async function fetchAdminInfo() {
    try {
      const res = await getAdminInfo()
      adminInfo.value = res.data
      menuList.value = res.data?.menuList || []
      isLoggedIn.value = true
    } catch {
      adminInfo.value = null
      menuList.value = []
      isLoggedIn.value = false
    }
  }

  function logout() {
    removeToken()
    adminInfo.value = null
    menuList.value = []
    isLoggedIn.value = false
  }

  return { adminInfo, menuList, isLoggedIn, login, fetchAdminInfo, logout }
})
