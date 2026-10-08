<template>
  <div class="product-reviews">
    <!-- 评价统计 -->
    <div class="bg-white rounded-xl p-6 shadow-sm mb-6">
      <div class="flex items-center gap-8">
        <div class="text-center">
          <div class="text-4xl font-bold text-primary">{{ stats.avgStar || '5.0' }}</div>
          <div class="flex gap-1 mt-2">
            <svg v-for="i in 5" :key="i" class="w-5 h-5" :class="i <= Math.round(stats.avgStar || 5) ? 'text-primary' : 'text-gray-300'" fill="currentColor" viewBox="0 0 20 20">
              <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"/>
            </svg>
          </div>
          <p class="text-sm text-secondary mt-1">{{ stats.total || 0 }} 条评价</p>
        </div>
        <div class="flex-1 space-y-2">
          <div v-for="star in [5,4,3,2,1]" :key="star" class="flex items-center gap-3">
            <span class="text-sm text-foreground w-8">{{ star }}星</span>
            <div class="flex-1 h-2 bg-muted rounded-full overflow-hidden">
              <div class="h-full bg-primary rounded-full transition-all" :style="{ width: getStarPercent(star) + '%' }"></div>
            </div>
            <span class="text-sm text-secondary w-10">{{ getStarCount(star) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 评价筛选 -->
    <div class="bg-white rounded-xl p-4 shadow-sm mb-6">
      <div class="flex gap-3">
        <button v-for="filter in filters" :key="filter.value" @click="activeFilter = filter.value; loadReviews()"
          :class="['px-4 py-2 rounded-lg text-sm transition-colors', activeFilter === filter.value ? 'bg-primary text-white' : 'bg-background text-foreground hover:bg-border']">
          {{ filter.label }}
        </button>
      </div>
    </div>

    <!-- 评价列表 -->
    <div class="space-y-4">
      <div v-for="review in reviews" :key="review.id" class="bg-white rounded-xl p-6 shadow-sm">
        <div class="flex items-center gap-3 mb-3">
          <img :src="review.memberAvatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + review.memberNickName" class="w-10 h-10 rounded-full bg-background" />
          <div>
            <p class="font-medium text-foreground text-sm">{{ review.memberNickName }}</p>
            <div class="flex gap-0.5">
              <svg v-for="i in 5" :key="i" class="w-3 h-3" :class="i <= review.star ? 'text-primary' : 'text-gray-300'" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"/>
              </svg>
            </div>
          </div>
          <span class="text-xs text-secondary ml-auto">{{ review.createTime }}</span>
        </div>
        <p class="text-foreground text-sm mb-3">{{ review.content }}</p>
        <div v-if="review.pics" class="flex gap-2 mb-3">
          <img v-for="(pic, idx) in review.pics.split(',')" :key="idx" :src="pic" class="w-20 h-20 rounded-lg object-cover cursor-pointer hover:opacity-80" @click="previewImage = pic" />
        </div>
        <!-- 商家回复 -->
        <div v-if="review.replyList && review.replyList.length" class="bg-background rounded-lg p-3 mt-3">
          <p v-for="reply in review.replyList" :key="reply.id" class="text-sm text-foreground">
            <span class="text-primary font-medium">商家回复：</span>{{ reply.content }}
          </p>
        </div>
      </div>
    </div>

    <div v-if="!reviews.length" class="text-center py-12 bg-white rounded-xl">
      <p class="text-foreground text-lg">暂无评价</p>
    </div>

    <!-- 分页 -->
    <div v-if="total > pageSize" class="flex justify-center mt-6 gap-2">
      <button v-for="p in Math.ceil(total / pageSize)" :key="p" @click="pageNum = p; loadReviews()"
        :class="['px-4 py-2 rounded-lg text-sm transition-colors', pageNum === p ? 'bg-primary text-white' : 'bg-white text-foreground hover:bg-background']">
        {{ p }}
      </button>
    </div>

    <!-- 图片预览 -->
    <div v-if="previewImage" class="fixed inset-0 bg-black/80 z-50 flex items-center justify-center p-4" @click="previewImage = ''">
      <img :src="previewImage" class="max-w-full max-h-full rounded-lg" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProductReviews, getProductReviewStats } from '@/api/review'

const props = defineProps({ productId: { type: [String, Number], required: true } })

const reviews = ref([])
const stats = ref({})
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const activeFilter = ref('')
const previewImage = ref('')

const filters = [
  { label: '全部', value: '' },
  { label: '好评', value: '5' },
  { label: '中评', value: '3' },
  { label: '差评', value: '1' },
  { label: '有图', value: 'pic' }
]

const getStarPercent = (star) => {
  const count = getStarCount(star)
  return stats.value.total ? Math.round((count / stats.value.total) * 100) : 0
}

const getStarCount = (star) => {
  return stats.value.starCounts?.[star] || 0
}

const loadReviews = async () => {
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (activeFilter.value === 'pic') {
      params.hasPic = true
    } else if (activeFilter.value) {
      params.star = activeFilter.value
    }
    const [reviewsRes, statsRes] = await Promise.all([
      getProductReviews(props.productId, params),
      getProductReviewStats(props.productId)
    ])
    reviews.value = reviewsRes.data?.records || []
    total.value = reviewsRes.data?.total || 0
    stats.value = statsRes.data || {}
  } catch (e) { console.error(e) }
}

onMounted(loadReviews)
</script>
