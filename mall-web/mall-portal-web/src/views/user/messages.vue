<template>
  <div class="max-w-3xl mx-auto px-4 py-8">
    <div class="flex justify-between items-center mb-6">
      <h1 class="font-heading text-2xl font-bold text-foreground">消息中心</h1>
      <button v-if="unreadCount > 0" @click="handleMarkAllRead"
        class="text-sm text-primary hover:text-primary-dark">
        一键已读
      </button>
    </div>

    <!-- 消息类型筛选 -->
    <div class="bg-white rounded-xl shadow-sm mb-6">
      <div class="flex border-b border-border">
        <button v-for="tab in tabs" :key="tab.value" @click="activeTab = tab.value; loadMessages()"
          :class="['px-6 py-4 text-sm font-medium transition-colors relative',
            activeTab === tab.value ? 'text-primary' : 'text-foreground hover:text-primary']">
          {{ tab.label }}
          <span v-if="tab.value === '' && unreadCount > 0"
            class="ml-1 bg-primary text-white text-xs px-1.5 py-0.5 rounded-full">
            {{ unreadCount }}
          </span>
          <div v-if="activeTab === tab.value" class="absolute bottom-0 left-0 right-0 h-0.5 bg-primary"></div>
        </button>
      </div>
    </div>

    <!-- 消息列表 -->
    <div class="space-y-3">
      <div v-for="msg in messages" :key="msg.id"
        class="bg-white rounded-xl p-5 shadow-sm cursor-pointer transition-colors"
        :class="msg.isRead === 0 ? 'border-l-4 border-primary bg-primary/5' : ''"
        @click="handleRead(msg)">
        <div class="flex items-start gap-3">
          <div class="w-2 h-2 rounded-full mt-2 flex-shrink-0"
            :class="msg.isRead === 0 ? 'bg-primary' : 'bg-gray-300'"></div>
          <div class="flex-1">
            <div class="flex justify-between items-start">
              <h3 class="font-medium text-foreground" :class="msg.isRead === 0 ? 'font-bold' : ''">
                {{ msg.title }}
              </h3>
              <span class="text-xs text-secondary">{{ msg.createTime }}</span>
            </div>
            <p class="text-sm text-secondary mt-1">{{ msg.content }}</p>
            <div class="mt-2">
              <span class="text-xs px-2 py-1 rounded-full"
                :class="getTypeClass(msg.type)">
                {{ getTypeLabel(msg.type) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="!messages.length" class="text-center py-16 bg-white rounded-xl">
      <svg class="w-16 h-16 text-secondary mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
          d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
      </svg>
      <p class="text-foreground text-lg">暂无消息</p>
    </div>

    <!-- 分页 -->
    <div v-if="total > pageSize" class="flex justify-center mt-6 gap-2">
      <button v-for="p in Math.ceil(total / pageSize)" :key="p" @click="pageNum = p; loadMessages()"
        :class="['px-4 py-2 rounded-lg text-sm transition-colors', pageNum === p ? 'bg-primary text-white' : 'bg-white text-foreground hover:bg-background']">
        {{ p }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMessageList, getUnreadCount, markRead, markAllRead } from '@/api/message'

const messages = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)
const unreadCount = ref(0)
const activeTab = ref('')

const tabs = [
  { label: '全部', value: '' },
  { label: '订单', value: '1' },
  { label: '系统', value: '2' },
  { label: '活动', value: '3' }
]

const typeMap = {
  1: { label: '订单', class: 'bg-blue-100 text-blue-600' },
  2: { label: '系统', class: 'bg-gray-100 text-gray-600' },
  3: { label: '活动', class: 'bg-orange-100 text-orange-600' }
}

const getTypeLabel = (type) => typeMap[type]?.label || '其他'
const getTypeClass = (type) => typeMap[type]?.class || 'bg-gray-100 text-gray-600'

const loadMessages = async () => {
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (activeTab.value) params.type = activeTab.value
    const res = await getMessageList(params)
    messages.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { console.error(e) }
}

const loadUnreadCount = async () => {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data || 0
  } catch (e) { console.error(e) }
}

const handleRead = async (msg) => {
  if (msg.isRead === 0) {
    try {
      await markRead(msg.id)
      msg.isRead = 1
      loadUnreadCount()
    } catch (e) { console.error(e) }
  }
}

const handleMarkAllRead = async () => {
  try {
    await markAllRead()
    messages.value.forEach(m => m.isRead = 1)
    unreadCount.value = 0
  } catch (e) { console.error(e) }
}

onMounted(() => {
  loadMessages()
  loadUnreadCount()
})
</script>
