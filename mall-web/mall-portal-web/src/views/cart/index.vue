<template>
  <div class="max-w-7xl mx-auto px-4 py-8">
    <h1 class="font-heading text-2xl font-bold text-foreground mb-6">购物车</h1>

    <div v-if="cartItems.length" class="space-y-4">
      <div v-for="item in cartItems" :key="item.id"
        class="bg-white rounded-xl p-4 flex items-center gap-4 shadow-sm">
        <input type="checkbox" v-model="item.selected" class="w-5 h-5 accent-primary" />

        <img :src="item.productPic || 'https://via.placeholder.com/80x80/FECDD3/E11D48?text=No+Image'"
          class="w-20 h-20 rounded-lg object-cover" />

        <div class="flex-1">
          <h3 class="font-medium text-foreground">{{ item.productName }}</h3>
          <p v-if="item.skuSpec" class="text-sm text-secondary">{{ item.skuSpec }}</p>
          <p class="text-primary font-bold mt-1">¥{{ item.price }}</p>
        </div>

        <div class="flex items-center gap-2">
          <button @click="updateQty(item, item.quantity - 1)"
            class="w-8 h-8 rounded-lg border border-border flex items-center justify-center hover:bg-background">-</button>
          <span class="w-10 text-center">{{ item.quantity }}</span>
          <button @click="updateQty(item, item.quantity + 1)"
            class="w-8 h-8 rounded-lg border border-border flex items-center justify-center hover:bg-background">+</button>
        </div>

        <button @click="removeItem(item.id)" class="text-destructive hover:text-destructive/80 p-2">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
          </svg>
        </button>
      </div>

      <div class="bg-white rounded-xl p-4 flex items-center justify-between shadow-sm sticky bottom-4">
        <div class="flex items-center gap-4">
          <label class="flex items-center gap-2 cursor-pointer">
            <input type="checkbox" :checked="allSelected" @change="toggleAll" class="w-5 h-5 accent-primary" />
            <span class="text-sm">全选</span>
          </label>
          <button @click="removeSelected" class="text-sm text-destructive hover:underline">删除选中</button>
        </div>
        <div class="flex items-center gap-6">
          <div>
            <span class="text-sm text-foreground">合计: </span>
            <span class="text-primary text-xl font-bold">¥{{ totalAmount }}</span>
          </div>
          <button @click="checkout" :disabled="!selectedItems.length"
            class="bg-primary hover:bg-primary-dark text-white px-8 py-3 rounded-lg font-medium transition-colors disabled:opacity-50">
            结算 ({{ selectedItems.length }})
          </button>
        </div>
      </div>
    </div>

    <div v-else class="text-center py-16 bg-white rounded-xl">
      <p class="text-foreground text-lg">购物车是空的</p>
      <router-link to="/products" class="inline-block mt-4 text-primary hover:underline">去选购商品</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCartList, updateCart, deleteCartItem } from '@/api/cart'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const cartStore = useCartStore()
const cartItems = ref([])

const selectedItems = computed(() => cartItems.value.filter(i => i.selected))
const allSelected = computed(() => cartItems.value.length > 0 && cartItems.value.every(i => i.selected))
const totalAmount = computed(() => selectedItems.value.reduce((sum, i) => sum + i.price * i.quantity, 0).toFixed(2))

const loadData = async () => {
  try {
    const res = await getCartList()
    cartItems.value = (res.data || []).map(item => ({ ...item, selected: true }))
  } catch (e) { console.error(e) }
}

const updateQty = async (item, qty) => {
  if (qty < 1) return
  try {
    await updateCart({ id: item.id, quantity: qty })
    item.quantity = qty
  } catch (e) { console.error(e) }
}

const removeItem = async (id) => {
  try {
    await deleteCartItem(id)
    cartItems.value = cartItems.value.filter(i => i.id !== id)
  } catch (e) { console.error(e) }
}

const toggleAll = () => {
  const val = !allSelected.value
  cartItems.value.forEach(i => i.selected = val)
}

const removeSelected = async () => {
  for (const item of selectedItems.value) {
    await removeItem(item.id)
  }
}

const checkout = () => {
  const ids = selectedItems.value.map(i => i.id)
  router.push({ path: '/order-confirm', query: { cartIds: ids.join(',') } })
}

onMounted(loadData)
</script>
