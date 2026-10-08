<template>
  <div class="min-h-screen flex items-center justify-center bg-background py-12 px-4">
    <div class="max-w-md w-full bg-white rounded-2xl shadow-xl p-8">
      <div class="text-center mb-8">
        <h1 class="font-heading text-3xl font-bold text-foreground">欢迎登录</h1>
        <p class="text-secondary mt-2">登录您的 MallShop 账号</p>
      </div>

      <form @submit.prevent="handleLogin" class="space-y-6">
        <div>
          <label class="block text-sm font-medium text-foreground mb-2">手机号</label>
          <input v-model="form.phone" type="tel" placeholder="请输入手机号" required
            class="w-full px-4 py-3 rounded-lg border border-border focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent" />
        </div>

        <div>
          <label class="block text-sm font-medium text-foreground mb-2">密码</label>
          <input v-model="form.password" type="password" placeholder="请输入密码" required
            class="w-full px-4 py-3 rounded-lg border border-border focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent" />
        </div>

        <button type="submit" :disabled="loading"
          class="w-full bg-primary hover:bg-primary-dark text-white font-medium py-3 rounded-lg transition-colors disabled:opacity-50">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>

      <p class="text-center mt-6 text-sm text-foreground">
        还没有账号？
        <router-link to="/register" class="text-primary hover:text-primary-dark font-medium">立即注册</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login, getCurrentUser } from '@/api/auth'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)

const form = reactive({ phone: '', password: '' })

const handleLogin = async () => {
  loading.value = true
  try {
    const res = await login(form)
    userStore.setToken(res.data)
    const userRes = await getCurrentUser()
    userStore.setUserInfo(userRes.data)
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    alert(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>
