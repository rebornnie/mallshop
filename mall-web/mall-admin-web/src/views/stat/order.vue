<template>
  <div class="stat-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">订单统计</span>
          <el-radio-group v-model="type" @change="loadData">
            <el-radio-button label="day">日</el-radio-button>
            <el-radio-button label="week">周</el-radio-button>
            <el-radio-button label="month">月</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <v-chart class="chart" :option="chartOption" autoresize />
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
import { getOrderStat } from '@/api/stat'

use([CanvasRenderer, LineChart, BarChart, GridComponent, TooltipComponent, LegendComponent])

const type = ref('day')
const chartOption = ref({
  tooltip: { trigger: 'axis' },
  legend: { data: ['订单量', '销售额'] },
  xAxis: { type: 'category', data: [] },
  yAxis: [{ type: 'value', name: '订单量' }, { type: 'value', name: '销售额' }],
  series: [
    { name: '订单量', type: 'bar', data: [], itemStyle: { color: '#E11D48' } },
    { name: '销售额', type: 'line', yAxisIndex: 1, data: [], itemStyle: { color: '#2563EB' } }
  ]
})

const loadData = async () => {
  try {
    const res = await getOrderStat({ type: type.value })
    if (res.data?.trend) {
      chartOption.value.xAxis.data = res.data.trend.map(t => t.date)
      chartOption.value.series[0].data = res.data.trend.map(t => t.count)
      chartOption.value.series[1].data = res.data.trend.map(t => t.amount)
    }
  } catch (e) { console.error(e) }
}

onMounted(loadData)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 16px; font-weight: 600; color: #881337; }
.chart { height: 400px; }
</style>
