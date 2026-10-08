<template>
  <div class="max-w-7xl mx-auto px-4 py-8">
    <div v-if="product" class="bg-white rounded-2xl shadow-sm overflow-hidden">
      <div class="flex flex-col lg:flex-row">
        <!-- Image Gallery -->
        <div class="lg:w-1/2 p-6">
          <div class="aspect-square bg-muted rounded-xl overflow-hidden">
            <img :src="currentImage || product.pic" class="w-full h-full object-cover" />
          </div>
          <div v-if="product.albumPics" class="flex gap-2 mt-4">
            <button v-for="(pic, idx) in product.albumPics.split(',')" :key="idx" @click="currentImage = pic"
              class="w-16 h-16 rounded-lg overflow-hidden border-2 transition-colors"
              :class="currentImage === pic ? 'border-primary' : 'border-transparent'">
              <img :src="pic" class="w-full h-full object-cover" />
            </button>
          </div>
        </div>

        <!-- Product Info -->
        <div class="lg:w-1/2 p-6 lg:border-l border-border">
          <h1 class="font-heading text-2xl font-bold text-foreground">{{ product.name }}</h1>
          <p class="text-secondary mt-2">{{ product.subtitle }}</p>

          <div class="bg-background rounded-xl p-4 mt-6">
            <div class="flex items-baseline gap-3">
              <span class="text-primary text-3xl font-bold">¥{{ selectedSku.price || product.price }}</span>
              <span v-if="product.originalPrice" class="text-gray-400 line-through">¥{{ product.originalPrice }}</span>
            </div>
            <p class="text-sm text-foreground mt-1">库存: {{ selectedSku.stock || product.stock }}</p>
          </div>

          <!-- SKU Selection -->
          <div v-if="skuList.length" class="mt-6 space-y-4">
            <div v-for="spec in specList" :key="spec.name">
              <h4 class="font-medium text-foreground mb-2">{{ spec.name }}</h4>
              <div class="flex flex-wrap gap-2">
                <button v-for="val in spec.values" :key="val" @click="selectSpec(spec.name, val)"
                  :class="['px-4 py-2 rounded-lg text-sm border transition-colors',
                    selectedSpecs[spec.name] === val
                      ? 'border-primary bg-primary text-white'
                      : 'border-border hover:border-primary text-foreground']">
                  {{ val }}
                </button>
              </div>
            </div>
          </div>

          <!-- Quantity -->
          <div class="mt-6">
            <h4 class="font-medium text-foreground mb-2">数量</h4>
            <div class="flex items-center gap-3">
              <button @click="quantity > 1 && quantity--" class="w-10 h-10 rounded-lg border border-border flex items-center justify-center hover:bg-background">-</button>
              <span class="w-12 text-center font-medium">{{ quantity }}</span>
              <button @click="quantity++" class="w-10 h-10 rounded-lg border border-border flex items-center justify-center hover:bg-background">+</button>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex gap-4 mt-8">
            <button @click="addToCart" class="flex-1 bg-background text-primary border-2 border-primary py-3 rounded-lg font-medium hover:bg-primary hover:text-white transition-colors">
              加入购物车
            </button>
            <button @click="buyNow" class="flex-1 bg-primary text-white py-3 rounded-lg font-medium hover:bg-primary-dark transition-colors">
              立即购买
            </button>
          </div>
        </div>
      </div>

      <!-- Description -->
      <div class="border-t border-border p-6">
        <h3 class="font-heading text-xl font-bold text-foreground mb-4">商品详情</h3>
        <div class="prose max-w-none" v-html="product.description"></div>
      </div>

      <!-- Reviews -->
      <div class="border-t border-border p-6">
        <ProductReviews :product-id="route.params.id" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetail } from '@/api/product'
import { addCart } from '@/api/cart'
import { useCartStore } from '@/stores/cart'
import ProductReviews from '@/components/ProductReviews.vue'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const product = ref(null)
const currentImage = ref('')
const quantity = ref(1)
const skuList = ref([])
const selectedSpecs = reactive({})

const specList = computed(() => {
  const specs = {}
  skuList.value.forEach(sku => {
    if (sku.specs) {
      sku.specs.forEach(spec => {
        if (!specs[spec.name]) specs[spec.name] = new Set()
        specs[spec.name].add(spec.value)
      })
    }
  })
  return Object.entries(specs).map(([name, values]) => ({ name, values: [...values] }))
})

const selectedSku = computed(() => {
  if (!skuList.value.length) return {}
  const match = skuList.value.find(sku => {
    if (!sku.specs) return false
    return sku.specs.every(spec => selectedSpecs[spec.name] === spec.value)
  })
  return match || skuList.value[0] || {}
})

const selectSpec = (name, val) => {
  selectedSpecs[name] = selectedSpecs[name] === val ? '' : val
}

const addToCart = async () => {
  try {
    await addCart({ productId: product.value.id, skuId: selectedSku.value.id, quantity: quantity.value })
    cartStore.addToCart({ productId: product.value.id, skuId: selectedSku.value.id, quantity: quantity.value, productName: product.value.name, productPic: product.value.pic, price: selectedSku.value.price || product.value.price })
    alert('已加入购物车')
  } catch (e) { alert(e.message || '添加失败') }
}

const buyNow = async () => {
  await addToCart()
  router.push('/cart')
}

onMounted(async () => {
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res.data
    currentImage.value = product.value.pic
    skuList.value = res.data.skuList || []
  } catch (e) { console.error(e) }
})
</script>
