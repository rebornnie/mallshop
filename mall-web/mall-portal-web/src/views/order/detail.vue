<template>
  <div class="max-w-3xl mx-auto px-4 py-8">
    <h1 class="font-heading text-2xl font-bold text-foreground mb-6">订单详情</h1>

    <div v-if="order" class="bg-white rounded-xl shadow-sm overflow-hidden">
      <!-- Status -->
      <div class="px-6 py-4 border-b border-border">
        <div class="flex justify-between items-center">
          <span class="text-sm text-secondary">订单编号: {{ order.orderSn }}</span>
          <span :class="['font-medium', statusColor(order.status)]">{{ statusText(order.status) }}</span>
        </div>
        <p class="text-sm text-secondary mt-1">下单时间: {{ order.createTime }}</p>
      </div>

      <!-- Address -->
      <div class="px-6 py-4 border-b border-border">
        <h3 class="font-medium text-foreground mb-2">收货信息</h3>
        <p class="text-sm">{{ order.receiverName }} {{ order.receiverPhone }}</p>
        <p class="text-sm text-secondary mt-1">
          {{ order.receiverProvince }}{{ order.receiverCity }}{{ order.receiverDistrict }}{{ order.receiverDetailAddress }}
        </p>
      </div>

      <!-- Products -->
      <div class="px-6 py-4 border-b border-border">
        <h3 class="font-medium text-foreground mb-2">商品信息</h3>
        <div v-for="item in order.orderItemList" :key="item.id" class="flex items-center gap-4 py-3">
          <img :src="item.productPic" class="w-16 h-16 rounded-lg object-cover" />
          <div class="flex-1">
            <p class="font-medium text-foreground">{{ item.productName }}</p>
            <p class="text-sm text-secondary">{{ item.skuSpec }}</p>
          </div>
          <div class="text-right">
            <p class="text-primary font-bold">¥{{ item.productPrice }}</p>
            <p class="text-sm text-secondary">x{{ item.productQuantity }}</p>
          </div>
        </div>
      </div>

      <!-- Amount -->
      <div class="px-6 py-4 border-b border-border">
        <div class="flex justify-between text-sm mb-2">
          <span class="text-secondary">商品总额</span>
          <span>¥{{ order.totalAmount }}</span>
        </div>
        <div class="flex justify-between text-sm mb-2">
          <span class="text-secondary">运费</span>
          <span>¥0</span>
        </div>
        <div class="flex justify-between items-center pt-2 border-t border-border">
          <span class="font-medium">应付总额</span>
          <span class="text-primary text-xl font-bold">¥{{ order.payAmount }}</span>
        </div>
      </div>

      <!-- Note -->
      <div v-if="order.note" class="px-6 py-4 border-b border-border">
        <h3 class="font-medium text-foreground mb-2">订单备注</h3>
        <p class="text-sm text-secondary">{{ order.note }}</p>
      </div>

      <!-- Actions -->
      <div class="px-6 py-4 flex gap-3 justify-end">
        <router-link to="/orders" class="px-4 py-2 border border-border rounded-lg text-sm hover:bg-background">返回列表</router-link>
        <button v-if="order.status === 0" @click="handlePay(order.id)"
          class="px-4 py-2 bg-primary text-white rounded-lg text-sm hover:bg-primary-dark">立即支付</button>
        <button v-if="order.status === 0" @click="handleCancel(order.id)"
          class="px-4 py-2 border border-border rounded-lg text-sm hover:bg-background">取消订单</button>
        <button v-if="order.status === 2" @click="handleConfirm(order.id)"
          class="px-4 py-2 bg-primary text-white rounded-lg text-sm hover:bg-primary-dark">确认收货</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderDetail, cancelOrder, confirmReceive } from '@/api/order'

const route = useRoute()
const router = useRouter()
const order = ref(null)

const statusMap = { 0: '待支付', 1: '已支付', 2: '已发货', 3: '已完成', 4: '已取消', 5: '退款中', 6: '已退款' }
const statusColorMap = { 0: 'text-warning', 1: 'text-success', 2: 'text-primary', 3: 'text-info', 4: 'text-gray-400', 5: 'text-destructive', 6: 'text-destructive' }
const statusText = (s) => statusMap[s] || '未知'
const statusColor = (s) => statusColorMap[s] || 'text-foreground'

const loadData = async () => {
  try {
    const res = await getOrderDetail(route.params.id)
    order.value = res.data
  } catch (e) { console.error(e) }
}

const handlePay = (id) => router.push(`/pay/${id}`)

const handleCancel = async (id) => {
  const reason = prompt('请输入取消原因')
  if (!reason) return
  try {
    await cancelOrder(id, reason)
    loadData()
  } catch (e) { alert(e.message || '取消失败') }
}

const handleConfirm = async (id) => {
  if (!confirm('确认已收到商品？')) return
  try {
    await confirmReceive(id)
    loadData()
  } catch (e) { alert(e.message || '确认失败') }
}

onMounted(loadData)
</script>
