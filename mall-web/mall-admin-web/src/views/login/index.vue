<template>
  <div class="login-page">
    <div class="login-box">
      <div class="login-header">
        <el-icon size="48" color="#E11D48"><ShoppingBag /></el-icon>
        <h1 class="login-title">MallShop 后台管理</h1>
        <p class="login-subtitle">欢迎登录后台管理系统</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login, getAdminInfo } from '@/api/admin'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await login(form)
    userStore.setToken(res.data)
    const infoRes = await getAdminInfo()
    userStore.setUserInfo(infoRes.data)
    userStore.setMenuList(infoRes.data.menuList || [])
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #881337;
  background-size: cover;
}

.login-box {
  width: 420px;
  padding: 48px 40px;
  background: #FFFFFF;
  border-radius: 16px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-title {
  font-family: 'Rubik', sans-serif;
  font-size: 28px;
  font-weight: 600;
  color: #881337;
  margin-top: 16px;
  margin-bottom: 8px;
}

.login-subtitle {
  font-size: 14px;
  color: #FB7185;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 8px;
}

.login-btn {
  width: 100%;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  background: #E11D48;
  border-color: #E11D48;
}

.login-btn:hover {
  background: #BE123C;
  border-color: #BE123C;
}
</style>
