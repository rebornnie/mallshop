<template>
  <div class="report-page">
    <!-- 数据概览卡片 -->
    <el-row :gutter="20" class="summary-cards">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="summary-card">
          <div class="summary-label">总销售额</div>
          <div class="summary-value">¥{{ formatAmount(salesData.totalAmount) }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="summary-card">
          <div class="summary-label">总订单数</div>
          <div class="summary-value">{{ salesData.totalOrderCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="summary-card">
          <div class="summary-label">平均客单价</div>
          <div class="summary-value">¥{{ formatAmount(salesData.avgOrderAmount) }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="summary-card">
          <div class="summary-label">商品销量</div>
          <div class="summary-value">{{ salesData.totalProductCount || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 销售报表 -->
    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">销售报表</span>
          <div class="header-actions">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              @change="loadSalesReport"
            />
            <el-button type="primary" @click="loadSalesReport" :icon="Refresh">刷新</el-button>
          </div>
        </div>
      </template>
      <v-chart class="chart" :option="salesChartOption" autoresize />
    </el-card>

    <!-- 商品销售排行 -->
    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">商品销售排行 TOP10</span>
          <div class="header-actions">
            <el-date-picker
              v-model="rankDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              @change="loadProductRank"
            />
            <el-button type="primary" @click="loadProductRank" :icon="Refresh">刷新</el-button>
          </div>
        </div>
      </template>
      <v-chart class="chart" :option="rankChartOption" autoresize />
    </el-card>

    <!-- 销售明细表格 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">销售明细</span>
        </div>
      </template>
      <el-table :data="salesData.dailyList || []" stripe border>
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="orderCount" label="订单数" width="100" />
        <el-table-column prop="productCount" label="商品销量" width="100" />
        <el-table-column prop="amount" label="销售额">
          <template #default="scope">
            ¥{{ formatAmount(scope.row.amount) }}
          </template>
        </el-table-column>
        <el-table-column prop="avgAmount" label="客单价">
          <template #default="scope">
            ¥{{ formatAmount(scope.row.avgAmount) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { Refresh } from '@element-plus/icons-vue'
import { getSalesReport, getProductRank } from '@/api/report'
import { ElMessage } from 'element-plus'

use([CanvasRenderer, LineChart, BarChart, GridComponent, TooltipComponent, LegendComponent])

const dateRange = ref([])
const rankDateRange = ref([])
const salesData = ref({})

const salesChartOption = ref({
  tooltip: { trigger: 'axis' },
  legend: { data: ['销售额', '订单数'] },
  xAxis: { type: 'category', data: [] },
  yAxis: [
    { type: 'value', name: '销售额(元)' },
    { type: 'value', name: '订单数' }
  ],
  series: [
    {
      name: '销售额',
      type: 'bar',
      data: [],
      itemStyle: { color: '#E11D48' }
    },
    {
      name: '订单数',
      type: 'line',
      yAxisIndex: 1,
      data: [],
      itemStyle: { color: '#2563EB' }
    }
  ]
})

const rankChartOption = ref({
  tooltip: { trigger: 'axis', formatter: '{b}: 销量 {c}' },
  xAxis: { type: 'value', name: '销量' },
  yAxis: { type: 'category', data: [] },
  series: [{
    type: 'bar',
    data: [],
    itemStyle: { color: '#E11D48' },
    label: { show: true, position: 'right' }
  }]
})

const formatAmount = (val) => {
  if (!val && val !== 0) return '0.00'
  return Number(val).toFixed(2)
}

const loadSalesReport = async () => {
  try {
    const params = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }
    const res = await getSalesReport(params)
    if (res.data) {
      salesData.value = res.data
      const dailyList = res.data.dailyList || []
      salesChartOption.value.xAxis.data = dailyList.map(d => d.date)
      salesChartOption.value.series[0].data = dailyList.map(d => d.amount)
      salesChartOption.value.series[1].data = dailyList.map(d => d.orderCount)
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('加载销售报表失败')
  }
}

const loadProductRank = async () => {
  try {
    const params = { topN: 10 }
    if (rankDateRange.value && rankDateRange.value.length === 2) {
      params.startTime = rankDateRange.value[0]
      params.endTime = rankDateRange.value[1]
    }
    const res = await getProductRank(params)
    if (res.data?.rankList) {
      const list = res.data.rankList.slice(0, 10).reverse()
      rankChartOption.value.yAxis.data = list.map(p => p.productName)
      rankChartOption.value.series[0].data = list.map(p => p.saleCount)
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('加载商品排行失败')
  }
}

onMounted(() => {
  loadSalesReport()
  loadProductRank()
})
</script>

<style scoped>
.report-page {
  padding-bottom: 20px;
}

.summary-cards {
  margin-bottom: 20px;
}

.summary-card {
  text-align: center;
  margin-bottom: 20px;
}

.summary-card :deep(.el-card__body) {
  padding: 20px;
}

.summary-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.summary-value {
  font-size: 24px;
  font-weight: 600;
  color: #E11D48;
}

.chart-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #881337;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.chart {
  height: 400px;
}
</style>
