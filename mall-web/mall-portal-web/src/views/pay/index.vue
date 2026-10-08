<template>
  <div class="max-w-md mx-auto px-4 py-16 text-center">
    <div class="bg-white rounded-2xl shadow-xl p-8">
      <h1 class="font-heading text-2xl font-bold text-foreground mb-4">订单支付</h1>
      <p class="text-secondary mb-2">订单编号: {{ orderId }}</p>
      <p class="text-primary text-3xl font-bold mb-8">¥{{ amount }}</p>

      <div class="space-y-4">
        <button @click="handlePay" :disabled="paying"
          class="w-full bg-primary hover:bg-primary-dark text-white py-3 rounded-lg font-medium transition-colors disabled:opacity-50">
          {{ paying ? '支付中...' : '支付宝支付' }}
        </button>
        <router-link to="/orders"
          class="block w-full py-3 border border-border rounded-lg text-foreground hover:bg-background transition-colors">
          稍后支付
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { alipay, getPayStatus } from '@/api/pay'
import { getOrderDetail } from '@/api/order'

const route = useRoute()
const router = useRouter()
const orderId = ref(route.params.orderId)
const amount = ref('0.00')
const paying = ref(false)

const loadOrder = async () => {
  try {
    const res = await getOrderDetail(orderId.value)
    amount.value = res.data?.payAmount || '0.00'
  } catch (e) { console.error(e) }
}

const handlePay = async () => {
  paying.value = true
  try {
    const res = await alipay(orderId.value)
    if (res.data?.form) {
      const div = document.createElement('div')
      div.innerHTML = res.data.form
      document.body.appendChild(div)
      div.querySelector('form')?.submit()
    } else {
      alert('支付成功')
      router.push('/orders')
    }
  } catch (e) {
    alert(e.message || '支付失败')
  } finally {
    paying.value = false
  }
}

onMounted(loadOrder)
</script>
