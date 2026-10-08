<template>
  <div class="max-w-3xl mx-auto px-4 py-8">
    <h1 class="font-heading text-2xl font-bold text-foreground mb-6">确认订单</h1>

    <!-- Address -->
    <div class="bg-white rounded-xl p-6 shadow-sm mb-6">
      <h2 class="font-medium text-foreground mb-4">收货地址</h2>
      <div v-if="selectedAddress" class="border border-primary rounded-lg p-4 bg-primary/5">
        <div class="flex justify-between items-start">
          <div>
            <p class="font-medium">{{ selectedAddress.receiverName }} {{ selectedAddress.receiverPhone }}</p>
            <p class="text-sm text-secondary mt-1">
              {{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }}{{ selectedAddress.detailAddress }}
            </p>
          </div>
          <button @click="showAddressList = true" class="text-primary text-sm hover:underline">更换</button>
        </div>
      </div>
      <div v-else class="text-center py-4">
        <router-link to="/addresses" class="text-primary hover:underline">添加收货地址</router-link>
      </div>
    </div>

    <!-- Products -->
    <div class="bg-white rounded-xl p-6 shadow-sm mb-6">
      <h2 class="font-medium text-foreground mb-4">商品清单</h2>
      <div v-for="item in orderItems" :key="item.id" class="flex items-center gap-4 py-4 border-b border-border last:border-0">
        <img :src="item.productPic" class="w-16 h-16 rounded-lg object-cover" />
        <div class="flex-1">
          <p class="font-medium text-foreground">{{ item.productName }}</p>
          <p class="text-sm text-secondary">{{ item.skuSpec }}</p>
        </div>
        <div class="text-right">
          <p class="text-primary font-bold">¥{{ item.price }}</p>
          <p class="text-sm text-secondary">x{{ item.quantity }}</p>
        </div>
      </div>
    </div>

    <!-- Note -->
    <div class="bg-white rounded-xl p-6 shadow-sm mb-6">
      <h2 class="font-medium text-foreground mb-4">订单备注</h2>
      <textarea v-model="note" placeholder="选填，请输入订单备注" maxlength="200"
        class="w-full px-4 py-3 rounded-lg border border-border focus:outline-none focus:ring-2 focus:ring-primary resize-none h-24"></textarea>
    </div>

    <!-- Summary -->
    <div class="bg-white rounded-xl p-6 shadow-sm">
      <div class="flex justify-between items-center mb-4">
        <span class="text-foreground">商品总价</span>
        <span class="font-bold">¥{{ totalAmount }}</span>
      </div>
      <div class="flex justify-between items-center mb-4">
        <span class="text-foreground">运费</span>
        <span class="font-bold">¥0</span>
      </div>
      <div class="border-t border-border pt-4 flex justify-between items-center">
        <span class="text-foreground font-medium">应付总额</span>
        <span class="text-primary text-2xl font-bold">¥{{ totalAmount }}</span>
      </div>
      <button @click="submitOrder" :disabled="!selectedAddress || submitting"
        class="w-full mt-6 bg-primary hover:bg-primary-dark text-white py-3 rounded-lg font-medium transition-colors disabled:opacity-50">
        {{ submitting ? '提交中...' : '提交订单' }}
      </button>
    </div>

    <!-- Address Dialog -->
    <div v-if="showAddressList" class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
      <div class="bg-white rounded-xl max-w-md w-full max-h-[80vh] overflow-auto p-6">
        <h3 class="font-heading text-lg font-bold text-foreground mb-4">选择地址</h3>
        <div v-for="addr in addressList" :key="addr.id" @click="selectAddress(addr)"
          class="border rounded-lg p-4 mb-3 cursor-pointer hover:border-primary transition-colors"
          :class="selectedAddress?.id === addr.id ? 'border-primary bg-primary/5' : 'border-border'">
          <p class="font-medium">{{ addr.receiverName }} {{ addr.receiverPhone }}</p>
          <p class="text-sm text-secondary mt-1">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detailAddress }}</p>
        </div>
        <button @click="showAddressList = false" class="w-full mt-4 py-2 border border-border rounded-lg hover:bg-background">取消</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAddressList } from '@/api/address'
import { generateConfirm, createOrder } from '@/api/order'

const route = useRoute()
const router = useRouter()
const orderItems = ref([])
const addressList = ref([])
const selectedAddress = ref(null)
const showAddressList = ref(false)
const note = ref('')
const submitting = ref(false)

const totalAmount = computed(() => orderItems.value.reduce((sum, i) => sum + i.price * i.quantity, 0).toFixed(2))

const loadData = async () => {
  try {
    const cartIds = route.query.cartIds
    const [confirmRes, addrRes] = await Promise.all([
      generateConfirm({ cartIds: cartIds ? cartIds.split(',') : [] }),
      getAddressList()
    ])
    orderItems.value = confirmRes.data?.cartItems || []
    addressList.value = addrRes.data || []
    selectedAddress.value = addressList.value.find(a => a.defaultStatus === 1) || addressList.value[0]
  } catch (e) { console.error(e) }
}

const selectAddress = (addr) => {
  selectedAddress.value = addr
  showAddressList.value = false
}

const submitOrder = async () => {
  submitting.value = true
  try {
    const res = await createOrder({
      addressId: selectedAddress.value.id,
      cartIds: orderItems.value.map(i => i.id),
      note: note.value
    })
    router.push(`/pay/${res.data.orderId}`)
  } catch (e) {
    alert(e.message || '创建订单失败')
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>
