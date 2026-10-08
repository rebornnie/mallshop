<template>
  <div class="max-w-4xl mx-auto px-4 py-8">
    <h1 class="font-heading text-2xl font-bold text-foreground mb-6">限时秒杀</h1>

    <div v-if="!seckillList.length" class="text-center py-16 bg-white rounded-xl">
      <svg class="w-16 h-16 text-secondary mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
      </svg>
      <p class="text-foreground text-lg">暂无秒杀活动</p>
    </div>

    <div class="grid gap-4 md:grid-cols-2">
      <div v-for="item in seckillList" :key="item.id"
        class="bg-white rounded-xl p-5 shadow-sm border-2 border-primary/20 relative overflow-hidden">
        <div class="absolute top-0 right-0 bg-primary text-white text-xs px-3 py-1 rounded-bl-lg">
          限时秒杀
        </div>
        <div class="flex gap-4">
          <img :src="item.productPic" class="w-24 h-24 object-cover rounded-lg bg-background"/>
          <div class="flex-1">
            <h3 class="font-bold text-foreground">{{ item.productName }}</h3>
            <p class="text-sm text-secondary mt-1 line-clamp-2">{{ item.productSubTitle }}</p>
            <div class="mt-2 flex items-baseline gap-2">
              <span class="text-xl font-bold text-primary">¥{{ item.flashPrice }}</span>
              <span class="text-sm text-secondary line-through">¥{{ item.originalPrice }}</span>
            </div>
          </div>
        </div>
        <div class="mt-4 flex justify-between items-center">
          <div class="text-sm text-secondary">
            <div>剩余库存: {{ item.stock }}件</div>
            <div>限购: {{ item.limitNum }}件/人</div>
          </div>
          <button @click="handleSeckill(item)"
            :disabled="item.stock <= 0 || seckilling === item.id"
            class="bg-primary hover:bg-primary-dark disabled:bg-gray-300 text-white px-6 py-2 rounded-lg text-sm font-medium transition-colors">
            {{ seckilling === item.id ? '抢购中...' : (item.stock <= 0 ? '已售罄' : '立即抢购') }}
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
import { seckillOrder } from '@/api/seckill'

const seckillList = ref([])
const seckilling = ref(null)
const toastMsg = ref('')
const toastType = ref('')

const showToast = (msg, type = 'success') => {
  toastMsg.value = msg
  toastType.value = type
  setTimeout(() => { toastMsg.value = '' }, 3000)
}

const loadSeckillList = async () => {
  try {
    // 秒杀列表需要从商品接口获取，这里使用模拟数据
    // 实际项目中应该有一个获取秒杀活动列表的接口
    seckillList.value = []
  } catch (e) { console.error(e) }
}

const handleSeckill = async (item) => {
  seckilling.value = item.id
  try {
    const res = await seckillOrder(item.flashPromotionId, item.skuId)
    if (res.code === 200) {
      showToast('秒杀成功')
    } else {
      showToast(res.message || '秒杀失败', 'error')
    }
  } catch (e) {
    showToast('秒杀失败', 'error')
  } finally {
    seckilling.value = null
  }
}

onMounted(loadSeckillList)
</script>
