<template>
  <div class="max-w-7xl mx-auto px-4 py-8">
    <h1 class="font-heading text-2xl font-bold text-foreground mb-6">我的订单</h1>

    <!-- Tabs -->
    <div class="bg-white rounded-xl shadow-sm mb-6">
      <div class="flex border-b border-border">
        <button v-for="tab in tabs" :key="tab.value" @click="activeTab = tab.value; loadData()"
          :class="['px-6 py-4 text-sm font-medium transition-colors relative',
            activeTab === tab.value ? 'text-primary' : 'text-foreground hover:text-primary']">
          {{ tab.label }}
          <div v-if="activeTab === tab.value" class="absolute bottom-0 left-0 right-0 h-0.5 bg-primary"></div>
        </button>
      </div>
    </div>

    <!-- Order List -->
    <div class="space-y-4">
      <div v-for="order in orders" :key="order.id" class="bg-white rounded-xl shadow-sm overflow-hidden">
        <div class="px-6 py-4 border-b border-border flex justify-between items-center">
          <div>
            <span class="text-sm text-secondary">订单编号: {{ order.orderSn }}</span>
            <span class="text-sm text-secondary ml-4">{{ order.createTime }}</span>
          </div>
          <span :class="['text-sm font-medium', statusColor(order.status)]">{{ statusText(order.status) }}</span>
        </div>

        <div class="px-6 py-4">
          <div v-for="item in order.orderItemList" :key="item.id" class="flex items-center gap-4 py-2">
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

        <div class="px-6 py-4 border-t border-border flex justify-between items-center">
          <span class="text-foreground">应付金额: <span class="text-primary font-bold text-lg">¥{{ order.payAmount }}</span></span>
          <div class="flex gap-3">
            <router-link :to="`/order/${order.id}`" class="px-4 py-2 border border-border rounded-lg text-sm hover:bg-background">查看详情</router-link>
            <button v-if="order.status === 0" @click="handlePay(order.id)"
              class="px-4 py-2 bg-primary text-white rounded-lg text-sm hover:bg-primary-dark">立即支付</button>
            <button v-if="order.status === 0" @click="handleCancel(order.id)"
              class="px-4 py-2 border border-border rounded-lg text-sm hover:bg-background">取消订单</button>
            <button v-if="order.status === 2" @click="handleConfirm(order.id)"
              class="px-4 py-2 bg-primary text-white rounded-lg text-sm hover:bg-primary-dark">确认收货</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="!orders.length" class="text-center py-16 bg-white rounded-xl">
      <p class="text-foreground text-lg">暂无订单</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderList, cancelOrder, confirmReceive } from '@/api/order'

const router = useRouter()
const orders = ref([])
const activeTab = ref('')

const tabs = [
  { label: '全部', value: '' },
  { label: '待付款', value: '0' },
  { label: '待发货', value: '1' },
  { label: '待收货', value: '2' },
  { label: '已完成', value: '3' }
]

const statusMap = { 0: '待支付', 1: '已支付', 2: '已发货', 3: '已完成', 4: '已取消', 5: '退款中', 6: '已退款' }
const statusColorMap = { 0: 'text-warning', 1: 'text-success', 2: 'text-primary', 3: 'text-info', 4: 'text-gray-400', 5: 'text-destructive', 6: 'text-destructive' }
const statusText = (s) => statusMap[s] || '未知'
const statusColor = (s) => statusColorMap[s] || 'text-foreground'

const loadData = async () => {
  try {
    const res = await getOrderList({ status: activeTab.value })
    orders.value = res.data?.records || []
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
