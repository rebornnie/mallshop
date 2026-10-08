<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <el-icon size="28" color="#E11D48"><ShoppingBag /></el-icon>
        <span class="logo-text">MallShop</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#881337"
        text-color="#FECDD3"
        active-text-color="#FFFFFF"
        :collapse-transition="false"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页概览</span>
        </el-menu-item>
        <el-sub-menu index="/pms">
          <template #title>
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </template>
          <el-menu-item index="/pms/product">商品列表</el-menu-item>
          <el-menu-item index="/pms/category">分类管理</el-menu-item>
          <el-menu-item index="/pms/brand">品牌管理</el-menu-item>
          <el-menu-item index="/pms/review">评价管理</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/oms/order">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/ums/member">
          <el-icon><UserFilled /></el-icon>
          <span>会员管理</span>
        </el-menu-item>
        <el-sub-menu index="/stat">
          <template #title>
            <el-icon><TrendCharts /></el-icon>
            <span>数据统计</span>
          </template>
          <el-menu-item index="/stat/order">订单统计</el-menu-item>
          <el-menu-item index="/stat/product">商品统计</el-menu-item>
          <el-menu-item index="/stat/user">用户统计</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/report">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>数据报表</span>
          </template>
          <el-menu-item index="/report/index">销售报表</el-menu-item>
          <el-menu-item index="/report/export">订单导出</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/sms">
          <template #title>
            <el-icon><Ticket /></el-icon>
            <span>营销管理</span>
          </template>
          <el-menu-item index="/sms/coupon">优惠券管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/sys">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/sys/admin">管理员管理</el-menu-item>
          <el-menu-item index="/sys/role">角色管理</el-menu-item>
          <el-menu-item index="/sys/menu">菜单管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userInfo.avatar" />
              <span class="username">{{ userInfo.nickname || userInfo.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const userInfo = computed(() => userStore.userInfo)

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
      .then(() => {
        userStore.logout()
        ElMessage.success('已退出登录')
        router.push('/login')
      })
      .catch(() => {})
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.sidebar {
  background: #881337;
  color: #fff;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid rgba(254, 205, 211, 0.2);
}

.logo-text {
  font-family: 'Rubik', sans-serif;
  font-size: 20px;
  font-weight: 600;
  color: #FFFFFF;
}

.header {
  background: #FFFFFF;
  border-bottom: 1px solid #FECDD3;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 8px;
  transition: background 0.2s;
}

.user-info:hover {
  background: #FFF1F2;
}

.username {
  font-size: 14px;
  color: #881337;
  font-weight: 500;
}

.main-content {
  background: #FFF1F2;
  padding: 20px;
  overflow-y: auto;
}

:deep(.el-menu) {
  border-right: none;
}

:deep(.el-menu-item.is-active) {
  background: #E11D48 !important;
}
</style>
