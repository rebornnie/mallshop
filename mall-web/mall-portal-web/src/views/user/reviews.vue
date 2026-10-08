<template>
  <div class="max-w-3xl mx-auto px-4 py-8">
    <h1 class="font-heading text-2xl font-bold text-foreground mb-6">我的评价</h1>

    <!-- 待评价订单 -->
    <div class="bg-white rounded-xl shadow-sm overflow-hidden mb-6">
      <div class="px-6 py-4 border-b border-border">
        <h2 class="font-medium text-foreground">待评价</h2>
      </div>
      <div v-if="reviewableOrders.length">
        <div v-for="order in reviewableOrders" :key="order.id" class="px-6 py-4 border-b border-border last:border-0">
          <div v-for="item in order.orderItemList" :key="item.id" class="flex items-center gap-4">
            <img :src="item.productPic" class="w-16 h-16 rounded-lg object-cover" />
            <div class="flex-1">
              <p class="font-medium text-foreground text-sm">{{ item.productName }}</p>
              <p class="text-xs text-secondary">{{ item.skuSpec }}</p>
            </div>
            <button @click="openReviewForm(order, item)"
              class="bg-primary hover:bg-primary-dark text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors">
              去评价
            </button>
          </div>
        </div>
      </div>
      <div v-else class="text-center py-8">
        <p class="text-secondary text-sm">暂无待评价订单</p>
      </div>
    </div>

    <!-- 已评价列表 -->
    <div class="bg-white rounded-xl shadow-sm overflow-hidden">
      <div class="px-6 py-4 border-b border-border">
        <h2 class="font-medium text-foreground">已评价</h2>
      </div>
      <div v-if="myReviews.length">
        <div v-for="review in myReviews" :key="review.id" class="px-6 py-4 border-b border-border last:border-0">
          <div class="flex items-center gap-3 mb-2">
            <img :src="review.productPic" class="w-12 h-12 rounded-lg object-cover" />
            <div class="flex-1">
              <p class="font-medium text-foreground text-sm">{{ review.productName }}</p>
              <div class="flex gap-0.5 mt-1">
                <svg v-for="i in 5" :key="i" class="w-3 h-3" :class="i <= review.star ? 'text-primary' : 'text-gray-300'" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"/>
                </svg>
              </div>
            </div>
            <span class="text-xs text-secondary">{{ review.createTime }}</span>
          </div>
          <p class="text-sm text-foreground mt-2">{{ review.content }}</p>
          <div v-if="review.pics" class="flex gap-2 mt-2">
            <img v-for="(pic, idx) in review.pics.split(',')" :key="idx" :src="pic" class="w-16 h-16 rounded-lg object-cover" />
          </div>
        </div>
      </div>
      <div v-else class="text-center py-8">
        <p class="text-secondary text-sm">暂无已评价内容</p>
      </div>
    </div>

    <!-- 评价弹窗 -->
    <div v-if="showForm" class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
      <div class="bg-white rounded-xl max-w-md w-full p-6 max-h-[90vh] overflow-auto">
        <h3 class="font-heading text-lg font-bold text-foreground mb-4">发表评价</h3>
        <div class="flex items-center gap-3 mb-4">
          <img :src="currentItem.productPic" class="w-12 h-12 rounded-lg object-cover" />
          <p class="font-medium text-foreground text-sm">{{ currentItem.productName }}</p>
        </div>
        <form @submit.prevent="submitReview" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-foreground mb-2">评分</label>
            <div class="flex gap-2">
              <button v-for="i in 5" :key="i" type="button" @click="form.star = i"
                class="transition-transform hover:scale-110">
                <svg class="w-8 h-8" :class="i <= form.star ? 'text-primary' : 'text-gray-300'" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"/>
                </svg>
              </button>
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-foreground mb-2">评价内容</label>
            <textarea v-model="form.content" placeholder="请输入您的评价..." required rows="4"
              class="w-full px-4 py-3 rounded-lg border border-border focus:outline-none focus:ring-2 focus:ring-primary resize-none"
            ></textarea>
          </div>
          <div>
            <label class="block text-sm font-medium text-foreground mb-2">上传图片（最多6张）</label>
            <div class="flex flex-wrap gap-2">
              <div v-for="(pic, idx) in form.pics" :key="idx" class="relative">
                <img :src="pic" class="w-20 h-20 rounded-lg object-cover" />
                <button type="button" @click="removePic(idx)" class="absolute -top-1 -right-1 w-5 h-5 bg-destructive text-white rounded-full text-xs">×</button>
              </div>
              <button v-if="form.pics.length < 6" type="button" @click="uploadPic"
                class="w-20 h-20 rounded-lg border-2 border-dashed border-border flex items-center justify-center text-secondary hover:border-primary hover:text-primary transition-colors">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
                </svg>
              </button>
            </div>
          </div>
          <div class="flex gap-3 pt-4">
            <button type="button" @click="showForm = false"
              class="flex-1 py-2 border border-border rounded-lg hover:bg-background">取消</button>
            <button type="submit" :disabled="submitting"
              class="flex-1 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark disabled:opacity-50">
              {{ submitting ? '提交中...' : '提交评价' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { submitReview, getReviewableOrders } from '@/api/review'

const reviewableOrders = ref([])
const myReviews = ref([])
const showForm = ref(false)
const submitting = ref(false)
const currentOrder = ref(null)
const currentItem = ref({})

const form = reactive({ orderId: '', orderItemId: '', productId: '', star: 5, content: '', pics: [] })

const loadData = async () => {
  try {
    const res = await getReviewableOrders()
    // 过滤出已完成的订单（status=3）且未评价的
    reviewableOrders.value = (res.data?.records || []).filter(o => o.status === 3)
  } catch (e) { console.error(e) }
}

const openReviewForm = (order, item) => {
  currentOrder.value = order
  currentItem.value = item
  form.orderId = order.id
  form.orderItemId = item.id
  form.productId = item.productId
  form.star = 5
  form.content = ''
  form.pics = []
  showForm.value = true
}

const removePic = (idx) => {
  form.pics.splice(idx, 1)
}

const uploadPic = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = (e) => {
    const file = e.target.files[0]
    if (file) {
      const url = URL.createObjectURL(file)
      form.pics.push(url)
    }
  }
  input.click()
}

const submitReviewForm = async () => {
  submitting.value = true
  try {
    await submitReview(form)
    showForm.value = false
    loadData()
    alert('评价成功！')
  } catch (e) {
    alert(e.message || '评价失败')
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>
