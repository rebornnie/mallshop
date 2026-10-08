<template>
  <div class="logistics-section">
    <div class="flex items-center gap-2 mb-4">
      <svg class="w-5 h-5 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16V6a1 1 0 00-1-1H4a1 1 0 00-1 1v10a1 1 0 001 1h1m8-1a1 1 0 01-1 1H9m4-1V8a1 1 0 011-1h2.586a1 1 0 01.707.293l3.414 3.414a1 1 0 01.293.707V16a1 1 0 01-1 1h-1m-6-1a1 1 0 001 1h1M5 17a2 2 0 104 0m-4 0a2 2 0 114 0m6 0a2 2 0 104 0m-4 0a2 2 0 114 0"/>
      </svg>
      <h3 class="font-bold text-foreground">物流信息</h3>
    </div>

    <div v-if="loading" class="text-center py-8">
      <div class="text-secondary">加载中...</div>
    </div>

    <div v-else-if="!logisticsInfo" class="text-center py-8 bg-background rounded-lg">
      <p class="text-secondary">暂无物流信息</p>
    </div>

    <div v-else>
      <div class="flex items-center gap-4 mb-4 p-3 bg-background rounded-lg">
        <div>
          <div class="text-sm text-secondary">物流公司</div>
          <div class="font-medium text-foreground">{{ logisticsInfo.company || '未知' }}</div>
        </div>
        <div class="w-px h-8 bg-border"></div>
        <div>
          <div class="text-sm text-secondary">运单号</div>
          <div class="font-medium text-foreground">{{ logisticsInfo.trackingNo || '未知' }}</div>
        </div>
        <div class="w-px h-8 bg-border"></div>
        <div>
          <div class="text-sm text-secondary">物流状态</div>
          <div class="font-medium" :class="getStatusColor(logisticsInfo.status)">
            {{ getStatusText(logisticsInfo.status) }}
          </div>
        </div>
      </div>

      <div v-if="logisticsInfo.traces?.length" class="relative pl-6">
        <div class="absolute left-2 top-2 bottom-2 w-0.5 bg-border"></div>
        <div v-for="(trace, index) in logisticsInfo.traces" :key="index" class="relative mb-4 last:mb-0">
          <div class="absolute -left-4 top-1 w-2.5 h-2.5 rounded-full border-2"
            :class="index === 0 ? 'bg-primary border-primary' : 'bg-white border-secondary'"></div>
          <div class="text-sm text-foreground" :class="index === 0 ? 'font-medium' : ''">
            {{ trace.context }}
          </div>
          <div class="text-xs text-secondary mt-1">{{ trace.time }}</div>
        </div>
      </div>

      <div v-else class="text-center py-4 text-secondary">
        暂无物流轨迹
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getLogisticsByOrderId } from '@/api/logistics'

const props = defineProps({
  orderId: {
    type: [String, Number],
    required: true
  }
})

const loading = ref(false)
const logisticsInfo = ref(null)

const getStatusColor = (status) => {
  const map = {
    0: 'text-secondary',
    1: 'text-primary',
    2: 'text-primary',
    3: 'text-green-500',
    4: 'text-destructive'
  }
  return map[status] || 'text-secondary'
}

const getStatusText = (status) => {
  const map = {
    0: '待发货',
    1: '已发货',
    2: '运输中',
    3: '已签收',
    4: '异常'
  }
  return map[status] || '未知'
}

const loadLogistics = async () => {
  loading.value = true
  try {
    const res = await getLogisticsByOrderId(props.orderId)
    logisticsInfo.value = res.data || null
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

onMounted(loadLogistics)
</script>
