<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in statCards" :key="item.title">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" :style="{ background: item.bg }">
            <el-icon size="28" color="#fff"><component :is="item.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-title">{{ item.title }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span class="card-title">销售趋势</span>
          </template>
          <v-chart class="chart" :option="salesOption" autoresize />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span class="card-title">热销商品 TOP5</span>
          </template>
          <el-table :data="hotProducts" size="small" :show-header="false">
            <el-table-column type="index" width="40" />
            <el-table-column prop="productName" show-overflow-tooltip />
            <el-table-column prop="sale" width="60" align="right" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getOrderStat, getProductStat } from '@/api/stat'

use([CanvasRenderer, LineChart, BarChart, GridComponent, TooltipComponent, LegendComponent])

const statCards = ref([
  { title: '今日订单', value: 0, icon: 'List', bg: '#E11D48' },
  { title: '今日销售额', value: '¥0', icon: 'Money', bg: '#2563EB' },
  { title: '商品总数', value: 0, icon: 'Goods', bg: '#059669' },
  { title: '会员总数', value: 0, icon: 'UserFilled', bg: '#D97706' }
])

const hotProducts = ref([])

const salesOption = ref({
  tooltip: { trigger: 'axis' },
  xAxis: { type: 'category', data: [] },
  yAxis: { type: 'value' },
  series: [{
    data: [],
    type: 'line',
    smooth: true,
    areaStyle: { color: 'rgba(225, 29, 72, 0.1)' },
    itemStyle: { color: '#E11D48' }
  }]
})

onMounted(async () => {
  try {
    const orderRes = await getOrderStat({ type: 'day' })
    const productRes = await getProductStat()
    if (orderRes.data) {
      statCards.value[0].value = orderRes.data.orderCount || 0
      statCards.value[1].value = '¥' + (orderRes.data.totalAmount || 0)
      if (orderRes.data.trend) {
        salesOption.value.xAxis.data = orderRes.data.trend.map(t => t.date)
        salesOption.value.series[0].data = orderRes.data.trend.map(t => t.amount)
      }
    }
    if (productRes.data) {
      hotProducts.value = productRes.data.hotProducts || []
      statCards.value[2].value = productRes.data.totalProduct || 0
    }
  } catch (e) {
    console.error(e)
  }
})
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  padding: 8px;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #881337;
  font-family: 'Rubik', sans-serif;
}

.stat-title {
  font-size: 13px;
  color: #FB7185;
  margin-top: 4px;
}

.mt-20 {
  margin-top: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #881337;
}

.chart {
  height: 320px;
}
</style>
