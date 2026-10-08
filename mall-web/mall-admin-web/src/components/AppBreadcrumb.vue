<template>
  <el-breadcrumb separator="/">
    <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path" :to="item.to">
      {{ item.title }}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const titleMap = {
  Dashboard: '首页概览',
  ProductList: '商品列表',
  Category: '分类管理',
  Brand: '品牌管理',
  OrderList: '订单管理',
  MemberList: '会员管理',
  Stat: '数据统计'
}

const breadcrumbs = computed(() => {
  if (!route.name || route.name === 'Dashboard') return []
  return [{ title: titleMap[route.name] || route.name, path: route.path, to: { path: route.path } }]
})
</script>
