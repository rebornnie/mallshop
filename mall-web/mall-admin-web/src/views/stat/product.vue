<template>
  <div class="stat-page">
    <el-card>
      <template #header>
        <span class="card-title">热销商品 TOP10</span>
      </template>
      <v-chart class="chart" :option="chartOption" autoresize />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getProductStat } from '@/api/stat'

use([CanvasRenderer, BarChart, GridComponent, TooltipComponent])

const chartOption = ref({
  tooltip: { trigger: 'axis' },
  xAxis: { type: 'value' },
  yAxis: { type: 'category', data: [] },
  series: [{ type: 'bar', data: [], itemStyle: { color: '#E11D48' } }]
})

const loadData = async () => {
  try {
    const res = await getProductStat()
    if (res.data?.hotProducts) {
      const list = res.data.hotProducts.slice(0, 10).reverse()
      chartOption.value.yAxis.data = list.map(p => p.productName)
      chartOption.value.series[0].data = list.map(p => p.sale)
    }
  } catch (e) { console.error(e) }
}

onMounted(loadData)
</script>

<style scoped>
.card-title { font-size: 16px; font-weight: 600; color: #881337; }
.chart { height: 500px; }
</style>
