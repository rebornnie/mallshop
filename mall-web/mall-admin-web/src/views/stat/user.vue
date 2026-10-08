<template>
  <div class="stat-page">
    <el-card>
      <template #header>
        <span class="card-title">用户增长趋势</span>
      </template>
      <v-chart class="chart" :option="chartOption" autoresize />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getUserStat } from '@/api/stat'

use([CanvasRenderer, LineChart, GridComponent, TooltipComponent, LegendComponent])

const chartOption = ref({
  tooltip: { trigger: 'axis' },
  legend: { data: ['新增用户', '活跃用户'] },
  xAxis: { type: 'category', data: [] },
  yAxis: { type: 'value' },
  series: [
    { name: '新增用户', type: 'line', data: [], itemStyle: { color: '#E11D48' }, smooth: true },
    { name: '活跃用户', type: 'line', data: [], itemStyle: { color: '#2563EB' }, smooth: true }
  ]
})

const loadData = async () => {
  try {
    const res = await getUserStat()
    if (res.data?.trend) {
      chartOption.value.xAxis.data = res.data.trend.map(t => t.date)
      chartOption.value.series[0].data = res.data.trend.map(t => t.newCount)
      chartOption.value.series[1].data = res.data.trend.map(t => t.activeCount)
    }
  } catch (e) { console.error(e) }
}

onMounted(loadData)
</script>

<style scoped>
.card-title { font-size: 16px; font-weight: 600; color: #881337; }
.chart { height: 400px; }
</style>
