<template>
  <div class="max-w-4xl mx-auto px-4 py-8">
    <h1 class="font-heading text-2xl font-bold text-foreground mb-6">领券中心</h1>

    <div v-if="!availableCoupons.length" class="text-center py-16 bg-white rounded-xl">
      <svg class="w-16 h-16 text-secondary mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 5v2m0 4v2m0 4v2M5 5a2 2 0 00-2 2v3a2 2 0 110 4v3a2 2 0 002 2h14a2 2 0 002-2v-3a2 2 0 110-4V7a2 2 0 00-2-2H5z"/>
      </svg>
      <p class="text-foreground text-lg">暂无可用优惠券</p>
    </div>

    <div class="grid gap-4 md:grid-cols-2">
      <div v-for="coupon in availableCoupons" :key="coupon.id"
        class="bg-white rounded-xl p-5 shadow-sm border-l-4 border-primary relative overflow-hidden">
        <div class="flex justify-between items-start">
          <div>
            <h3 class="font-bold text-lg text-foreground">{{ coupon.name }}</h3>
            <p class="text-sm text-secondary mt-1">{{ coupon.note }}</p>
          </div>
          <div class="text-right">
            <div class="text-2xl font-bold text-primary">
              <span v-if="coupon.type === 0">¥{{ coupon.amount }}</span>
              <span v-else>{{ coupon.amount }}折</span>
            </div>
            <div class="text-xs text-secondary">满¥{{ coupon.minPoint }}可用</div>
          </div>
        </div>
        <div class="mt-4 flex justify-between items-center">
          <div class="text-xs text-secondary">
            <div>有效期: {{ coupon.startTime }} 至 {{ coupon.endTime }}</div>
            <div>剩余: {{ coupon.publishCount - coupon.receiveCount }}张</div>
          </div>
          <button @click="handleReceive(coupon.id)"
            :disabled="coupon.publishCount <= coupon.receiveCount || receiving === coupon.id"
            class="bg-primary hover:bg-primary-dark disabled:bg-gray-300 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors">
            {{ receiving === coupon.id ? '领取中...' : (coupon.publishCount <= coupon.receiveCount ? '已领完' : '立即领取') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Toast -->
    <div v-if="toastMsg" class="fixed top-20 left-1/2 -translate-x-1/2 z-50 px-6 py-3 rounded-lg text-white text-sm font-medium shadow-lg transition-opacity"
      :class="toastType === 'error' ? 'bg-red-500' : 'bg-green-500'"
    >
      {{ toastMsg }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAvailableCoupons, receiveCoupon } from '@/api/coupon'

const availableCoupons = ref([])
const receiving = ref(null)
const toastMsg = ref('')
const toastType = ref('')

const showToast = (msg, type = 'success') => {
  toastMsg.value = msg
  toastType.value = type
  setTimeout(() => { toastMsg.value = '' }, 3000)
}

const loadCoupons = async () => {
  try {
    const res = await getAvailableCoupons()
    availableCoupons.value = res.data || []
  } catch (e) { console.error(e) }
}

const handleReceive = async (couponId) => {
  receiving.value = couponId
  try {
    const res = await receiveCoupon(couponId)
    if (res.code === 200) {
      showToast('领取成功')
      loadCoupons()
    } else {
      showToast(res.message || '领取失败', 'error')
    }
  } catch (e) {
    showToast('领取失败', 'error')
  } finally {
    receiving.value = null
  }
}

onMounted(loadCoupons)
</script>
