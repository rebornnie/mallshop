<template>
  <div class="max-w-7xl mx-auto px-4 py-8">
    <div class="flex flex-col lg:flex-row gap-8">
      <!-- Sidebar Filters -->
      <aside class="lg:w-64 flex-shrink-0">
        <div class="bg-white rounded-xl p-6 shadow-sm">
          <h3 class="font-heading font-bold text-foreground mb-4">筛选</h3>

          <div class="mb-6">
            <h4 class="font-medium text-foreground mb-2">分类</h4>
            <div class="space-y-2">
              <button v-for="cat in categories" :key="cat.id" @click="toggleCategory(cat.id)"
                :class="['block w-full text-left px-3 py-2 rounded-lg text-sm transition-colors', query.categoryId == cat.id ? 'bg-primary text-white' : 'hover:bg-background text-foreground']">
                {{ cat.name }}
              </button>
            </div>
          </div>

          <div class="mb-6">
            <h4 class="font-medium text-foreground mb-2">品牌</h4>
            <div class="space-y-2">
              <button v-for="brand in brands" :key="brand.id" @click="toggleBrand(brand.id)"
                :class="['block w-full text-left px-3 py-2 rounded-lg text-sm transition-colors', query.brandId == brand.id ? 'bg-primary text-white' : 'hover:bg-background text-foreground']">
                {{ brand.name }}
              </button>
            </div>
          </div>
        </div>
      </aside>

      <!-- Product Grid -->
      <div class="flex-1">
        <!-- Sort Bar -->
        <div class="bg-white rounded-xl p-4 mb-6 flex items-center justify-between shadow-sm">
          <div class="flex gap-4">
            <button v-for="s in sortOptions" :key="s.value" @click="query.sort = s.value; loadData()"
              :class="['text-sm font-medium transition-colors', query.sort === s.value ? 'text-primary' : 'text-foreground hover:text-primary']">
              {{ s.label }}
            </button>
          </div>
          <span class="text-sm text-foreground">共 {{ total }} 件商品</span>
        </div>

        <!-- Products -->
        <div v-if="products.length" class="grid grid-cols-2 md:grid-cols-3 gap-6">
          <router-link v-for="product in products" :key="product.id" :to="`/product/${product.id}`"
            class="bg-white rounded-xl overflow-hidden hover:shadow-xl transition-shadow group">
            <div class="aspect-square bg-muted overflow-hidden">
              <img :src="product.pic || 'https://via.placeholder.com/300x300/FECDD3/E11D48?text=No+Image'"
                class="w-full h-full object-cover group-hover:scale-105 transition-transform" />
            </div>
            <div class="p-4">
              <h3 class="font-medium text-foreground truncate">{{ product.name }}</h3>
              <div class="flex items-center gap-2 mt-2">
                <span class="text-primary font-bold">¥{{ product.price }}</span>
                <span v-if="product.originalPrice" class="text-gray-400 text-sm line-through">¥{{ product.originalPrice }}</span>
              </div>
              <p class="text-secondary text-xs mt-1">已售 {{ product.sale || 0 }}</p>
            </div>
          </router-link>
        </div>

        <div v-else class="text-center py-16">
          <p class="text-foreground text-lg">暂无商品</p>
        </div>

        <!-- Pagination -->
        <div v-if="total > query.pageSize" class="flex justify-center mt-8 gap-2">
          <button v-for="p in Math.ceil(total / query.pageSize)" :key="p" @click="query.pageNum = p; loadData()"
            :class="['px-4 py-2 rounded-lg text-sm font-medium transition-colors', query.pageNum === p ? 'bg-primary text-white' : 'bg-white text-foreground hover:bg-background']">
            {{ p }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getProductList, getCategoryList, getBrandList } from '@/api/product'

const route = useRoute()
const products = ref([])
const categories = ref([])
const brands = ref([])
const total = ref(0)

const query = reactive({
  pageNum: 1, pageSize: 20, categoryId: '', brandId: '', keyword: '', sort: ''
})

const sortOptions = [
  { label: '综合', value: '' },
  { label: '销量', value: 'sale' },
  { label: '价格', value: 'price' },
  { label: '新品', value: 'createTime' }
]

const loadData = async () => {
  try {
    const res = await getProductList(query)
    products.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { console.error(e) }
}

const toggleCategory = (id) => {
  query.categoryId = query.categoryId == id ? '' : id
  query.pageNum = 1
  loadData()
}

const toggleBrand = (id) => {
  query.brandId = query.brandId == id ? '' : id
  query.pageNum = 1
  loadData()
}

watch(() => route.query, (newQuery) => {
  query.keyword = newQuery.keyword || ''
  query.categoryId = newQuery.categoryId || ''
  query.pageNum = 1
  loadData()
}, { immediate: true })

onMounted(async () => {
  try {
    const [catRes, brandRes] = await Promise.all([getCategoryList(), getBrandList()])
    const cats = catRes.data || []
    categories.value = cats.flatMap(c => [c, ...(c.children || [])])
    brands.value = brandRes.data?.records || []
  } catch (e) { console.error(e) }
})
</script>
