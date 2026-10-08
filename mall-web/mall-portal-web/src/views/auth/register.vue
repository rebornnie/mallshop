<template>
  <div class="min-h-screen flex items-center justify-center bg-background py-12 px-4">
    <div class="max-w-md w-full bg-white rounded-2xl shadow-xl p-8">
      <div class="text-center mb-8">
        <h1 class="font-heading text-3xl font-bold text-foreground">注册账号</h1>
        <p class="text-secondary mt-2">创建您的 MallShop 账号</p>
      </div>

      <form @submit.prevent="handleRegister" class="space-y-6">
        <div>
          <label class="block text-sm font-medium text-foreground mb-2">手机号</label>
          <input v-model="form.phone" type="tel" placeholder="请输入手机号" required
            class="w-full px-4 py-3 rounded-lg border border-border focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent" />
        </div>

        <div>
          <label class="block text-sm font-medium text-foreground mb-2">密码</label>
          <input v-model="form.password" type="password" placeholder="8-20位字母+数字" required
            class="w-full px-4 py-3 rounded-lg border border-border focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent" />
        </div>

        <div>
          <label class="block text-sm font-medium text-foreground mb-2">验证码</label>
          <div class="flex gap-3">
            <input v-model="form.authCode" type="text" placeholder="请输入验证码" required
              class="flex-1 px-4 py-3 rounded-lg border border-border focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent" />
            <button type="button" @click="sendCode" :disabled="codeTimer > 0"
              class="px-4 py-3 bg-muted text-foreground rounded-lg font-medium whitespace-nowrap disabled:opacity-50">
              {{ codeTimer > 0 ? `${codeTimer}s` : '获取验证码' }}
            </button>
          </div>
        </div>

        <button type="submit" :disabled="loading"
          class="w-full bg-primary hover:bg-primary-dark text-white font-medium py-3 rounded-lg transition-colors disabled:opacity-50">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>

      <p class="text-center mt-6 text-sm text-foreground">
        已有账号？
        <router-link to="/login" class="text-primary hover:text-primary-dark font-medium">立即登录</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { register, getCurrentUser } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const codeTimer = ref(0)

const form = reactive({ phone: '', password: '', authCode: '' })

const sendCode = () => {
  if (!form.phone) { alert('请输入手机号'); return }
  codeTimer.value = 60
  const timer = setInterval(() => {
    codeTimer.value--
    if (codeTimer.value <= 0) clearInterval(timer)
  }, 1000)
}

const handleRegister = async () => {
  loading.value = true
  try {
    const res = await register(form)
    userStore.setToken(res.data)
    const userRes = await getCurrentUser()
    userStore.setUserInfo(userRes.data)
    router.push('/')
  } catch (e) {
    alert(e.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>
