<template>
  <div class="max-w-4xl mx-auto px-4 py-8">
    <div class="flex justify-between items-center mb-6">
      <h1 class="font-heading text-2xl font-bold text-foreground">我的优惠券</h1>
      <router-link to="/coupons" class="text-primary hover:text-primary-dark text-sm">去领券中心 →</router-link>
    </div>

    <!-- 状态筛选 -->
    <div class="bg-white rounded-xl shadow-sm mb-6">
      <div class="flex border-b border-border">
        <button v-for="tab in tabs" :key="tab.value" @click="activeTab = tab.value; loadCoupons()"
          :class="['px-6 py-4 text-sm font-medium transition-colors relative',
            activeTab === tab.value ? 'text-primary' : 'text-foreground hover:text-primary']">
          {{ tab.label }}
          <div v-if="activeTab === tab.value" class="absolute bottom-0 left-0 right-0 h-0.5 bg-primary"></div>
        </button>
      </div>
    </div>

    <div v-if="!myCoupons.length" class="text-center py-16 bg-white rounded-xl">
      <svg class="w-16 h-16 text-secondary mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 5v2m0 4v2m0 4v2M5 5a2 2 0 00-2 2v3a2 2 0 110 4v3a2 2 0 002 2h14a2 2 0 002-2v-3a2 2 0 110-4V7a2 2 0 00-2-2H5z"/>
      </svg>
      <p class="text-foreground text-lg">暂无优惠券</p>
    </div>

    <div class="grid gap-4 md:grid-cols-2">
      <div v-for="item in myCoupons" :key="item.memberCoupon.id"
        class="bg-white rounded-xl p-5 shadow-sm border-l-4 relative overflow-hidden"
        :class="getCouponBorderClass(item.memberCoupon.status)">
        <div class="flex justify-between items-start">
          <div>
            <h3 class="font-bold text-lg text-foreground">{{ item.coupon.name }}</h3>
            <p class="text-sm text-secondary mt-1">{{ item.coupon.note }}</p>
          </div>
          <div class="text-right">
            <div class="text-2xl font-bold" :class="getCouponTextClass(item.memberCoupon.status)">
              <span v-if="item.coupon.type === 0">¥{{ item.coupon.amount }}</span>
              <span v-else>{{ item.coupon.amount }}折</span>
            </div>
            <div class="text-xs text-secondary">满¥{{ item.coupon.minPoint }}可用</div>
          </div>
        </div>
        <div class="mt-4 flex justify-between items-center">
          <div class="text-xs text-secondary">
            <div>有效期至: {{ item.memberCoupon.endTime || item.coupon.endTime }}</div>
            <div>领取时间: {{ item.memberCoupon.createTime }}</div>
          </div>
          <span class="text-xs px-2 py-1 rounded-full" :class="getStatusClass(item.memberCoupon.status)">
            {{ getStatusLabel(item.memberCoupon.status) }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyCoupons } from '@/api/coupon'

const myCoupons = ref([])
const activeTab = ref(0)

const tabs = [
  { label: '未使用', value: 0 },
  { label: '已使用', value: 1 },
  { label: '已过期', value: 2 }
]

const getCouponBorderClass = (status) => {
  if (status === 0) return 'border-primary'
  if (status === 1) return 'border-green-500'
  return 'border-gray-300'
}

const getCouponTextClass = (status) => {
  if (status === 0) return 'text-primary'
  if (status === 1) return 'text-green-500'
  return 'text-gray-400'
}

const getStatusClass = (status) => {
  if (status === 0) return 'bg-primary/10 text-primary'
  if (status === 1) return 'bg-green-100 text-green-600'
  return 'bg-gray-100 text-gray-500'
}

const getStatusLabel = (status) => {
  if (status === 0) return '未使用'
  if (status === 1) return '已使用'
  return '已过期'
}

const loadCoupons = async () => {
  try {
    const res = await getMyCoupons(activeTab.value)
    myCoupons.value = res.data || []
  } catch (e) { console.error(e) }
}

onMounted(loadCoupons)
</script>
