<template>
  <div class="min-h-screen flex flex-col">
    <!-- Header -->
    <header class="bg-foreground text-white sticky top-0 z-50 shadow-lg">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16">
          <!-- Logo -->
          <router-link to="/" class="flex items-center gap-2">
            <svg class="w-8 h-8 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"/>
            </svg>
            <span class="font-heading text-xl font-bold">MallShop</span>
          </router-link>

          <!-- Search -->
          <div class="hidden md:flex flex-1 max-w-lg mx-8">
            <div class="relative w-full">
              <input
                v-model="searchKeyword"
                @keyup.enter="handleSearch"
                type="text"
                placeholder="搜索商品..."
                class="w-full px-4 py-2 rounded-lg text-foreground bg-white focus:outline-none focus:ring-2 focus:ring-primary"
              />
              <button @click="handleSearch" class="absolute right-2 top-1/2 -translate-y-1/2 text-primary">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
                </svg>
              </button>
            </div>
          </div>

          <!-- Right Actions -->
          <div class="flex items-center gap-4">
            <router-link to="/seckill" class="hidden sm:block text-sm hover:bg-white/10 rounded-lg px-3 py-2 transition-colors">
              秒杀
            </router-link>
            <router-link to="/coupons" class="hidden sm:block text-sm hover:bg-white/10 rounded-lg px-3 py-2 transition-colors">
              领券
            </router-link>
            <router-link v-if="userStore.isLoggedIn()" to="/messages" class="relative p-2 hover:bg-white/10 rounded-lg transition-colors">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
              </svg>
              <span v-if="unreadCount > 0" class="absolute -top-1 -right-1 bg-primary text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                {{ unreadCount > 99 ? '99+' : unreadCount }}
              </span>
            </router-link>

            <router-link to="/cart" class="relative p-2 hover:bg-white/10 rounded-lg transition-colors">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"/>
              </svg>
              <span v-if="cartStore.cartCount > 0" class="absolute -top-1 -right-1 bg-primary text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                {{ cartStore.cartCount }}
              </span>
            </router-link>

            <div v-if="userStore.isLoggedIn()" class="relative">
              <button @click="showUserMenu = !showUserMenu" class="flex items-center gap-2 hover:bg-white/10 rounded-lg px-3 py-2 transition-colors">
                <img :src="userStore.userInfo.avatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + userStore.userInfo.phone" class="w-8 h-8 rounded-full bg-white"/>
                <span class="hidden sm:inline text-sm">{{ userStore.userInfo.nickname || userStore.userInfo.phone }}</span>
              </button>
              <div v-if="showUserMenu" class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg py-2 text-foreground z-50">
                <router-link to="/user" @click="showUserMenu = false" class="block px-4 py-2 hover:bg-background">个人中心</router-link>
                <router-link to="/orders" @click="showUserMenu = false" class="block px-4 py-2 hover:bg-background">我的订单</router-link>
                <router-link to="/my-coupons" @click="showUserMenu = false" class="block px-4 py-2 hover:bg-background">我的优惠券</router-link>
                <router-link to="/addresses" @click="showUserMenu = false" class="block px-4 py-2 hover:bg-background">收货地址</router-link>
                <div class="border-t border-border my-1"></div>
                <button @click="handleLogout" class="block w-full text-left px-4 py-2 hover:bg-background text-destructive">退出登录</button>
              </div>
            </div>
            <router-link v-else to="/login" class="bg-primary hover:bg-primary-dark text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors">
              登录
            </router-link>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="flex-1">
      <router-view />
    </main>

    <!-- Footer -->
    <footer class="bg-foreground text-white py-8 mt-auto">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <p class="text-sm opacity-70"> MallShop 商城 - 技术学习与实战项目</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { getUnreadCount } from '@/api/message'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const searchKeyword = ref('')
const showUserMenu = ref(false)
const unreadCount = ref(0)
let unreadTimer = null

const loadUnreadCount = async () => {
  if (!userStore.isLoggedIn()) {
    unreadCount.value = 0
    return
  }
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data || 0
  } catch (e) { console.error(e) }
}

const startUnreadPolling = () => {
  loadUnreadCount()
  unreadTimer = setInterval(loadUnreadCount, 30000)
}

const stopUnreadPolling = () => {
  if (unreadTimer) {
    clearInterval(unreadTimer)
    unreadTimer = null
  }
}

watch(() => userStore.isLoggedIn(), (loggedIn) => {
  if (loggedIn) {
    startUnreadPolling()
  } else {
    stopUnreadPolling()
    unreadCount.value = 0
  }
})

onMounted(() => {
  if (userStore.isLoggedIn()) {
    startUnreadPolling()
  }
})

onUnmounted(() => {
  stopUnreadPolling()
})

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/products', query: { keyword: searchKeyword.value } })
  }
}

const handleLogout = () => {
  userStore.logout()
  showUserMenu.value = false
  router.push('/')
}
</script>
