<template>
  <div>
    <!-- Hero Banner -->
    <div class="bg-primary text-white py-16">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <h1 class="font-heading text-4xl md:text-5xl font-bold mb-4">MallShop 商城</h1>
        <p class="text-lg opacity-90 mb-8">发现优质好物，享受品质生活</p>
        <router-link to="/products"
          class="inline-block bg-white text-primary px-8 py-3 rounded-lg font-medium hover:bg-background transition-colors">
          立即选购
        </router-link>
      </div>
    </div>

    <!-- Categories -->
    <div class="max-w-7xl mx-auto px-4 py-12">
      <h2 class="font-heading text-2xl font-bold text-foreground mb-6">商品分类</h2>
      <div class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-8 gap-4">
        <router-link v-for="cat in categories" :key="cat.id" :to="`/products?categoryId=${cat.id}`"
          class="bg-white rounded-xl p-4 text-center hover:shadow-lg transition-shadow">
          <img v-if="cat.icon" :src="cat.icon" class="w-12 h-12 mx-auto mb-2 rounded-full object-cover" />
          <div v-else class="w-12 h-12 mx-auto mb-2 rounded-full bg-primary/10 flex items-center justify-center">
            <svg class="w-6 h-6 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
            </svg>
          </div>
          <span class="text-sm text-foreground font-medium">{{ cat.name }}</span>
        </router-link>
      </div>
    </div>

    <!-- Hot Products -->
    <div class="max-w-7xl mx-auto px-4 py-12">
      <h2 class="font-heading text-2xl font-bold text-foreground mb-6">热销推荐</h2>
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        <router-link v-for="product in hotProducts" :key="product.id" :to="`/product/${product.id}`"
          class="bg-white rounded-xl overflow-hidden hover:shadow-xl transition-shadow group">
          <div class="aspect-square bg-muted overflow-hidden">
            <img :src="product.pic || 'https://via.placeholder.com/300x300/FECDD3/E11D48?text=No+Image'"
              class="w-full h-full object-cover group-hover:scale-105 transition-transform" />
          </div>
          <div class="p-4">
            <h3 class="font-medium text-foreground truncate">{{ product.name }}</h3>
            <p class="text-secondary text-sm mt-1 truncate">{{ product.subtitle }}</p>
            <div class="flex items-center gap-2 mt-3">
              <span class="text-primary font-bold text-lg">¥{{ product.price }}</span>
              <span v-if="product.originalPrice" class="text-gray-400 text-sm line-through">¥{{ product.originalPrice }}</span>
            </div>
          </div>
        </router-link>
      </div>
    </div>

    <!-- Brands -->
    <div class="max-w-7xl mx-auto px-4 py-12">
      <h2 class="font-heading text-2xl font-bold text-foreground mb-6">品牌专区</h2>
      <div class="grid grid-cols-3 md:grid-cols-6 gap-4">
        <div v-for="brand in brands" :key="brand.id"
          class="bg-white rounded-xl p-4 flex items-center justify-center h-20 hover:shadow-lg transition-shadow">
          <img v-if="brand.logo" :src="brand.logo" class="max-h-12 max-w-full object-contain" />
          <span v-else class="text-foreground font-medium">{{ brand.name }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCategoryList, getBrandList, getProductList } from '@/api/product'

const categories = ref([])
const brands = ref([])
const hotProducts = ref([])

onMounted(async () => {
  try {
    const [catRes, brandRes, prodRes] = await Promise.all([
      getCategoryList(),
      getBrandList(),
      getProductList({ pageSize: 8, sort: 'sale' })
    ])
    const cats = catRes.data || []
    categories.value = cats.flatMap(c => [c, ...(c.children || [])]).slice(0, 8)
    brands.value = (brandRes.data?.records || []).filter(b => b.recommendStatus === 1).slice(0, 6)
    hotProducts.value = prodRes.data?.records || []
  } catch (e) { console.error(e) }
})
</script>
