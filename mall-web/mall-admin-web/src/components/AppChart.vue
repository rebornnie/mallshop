<template>
  <div ref="chartRef" :style="{ height: height + 'px' }"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  option: { type: Object, required: true },
  height: { type: Number, default: 300 }
})

const chartRef = ref(null)
let chartInstance = null

function init() {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  chartInstance.setOption(props.option)
}

function resize() {
  chartInstance?.resize()
}

function dispose() {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
}

onMounted(() => {
  nextTick(init)
  window.addEventListener('resize', resize)
})

onUnmounted(() => {
  dispose()
  window.removeEventListener('resize', resize)
})

watch(() => props.option, (opt) => {
  if (chartInstance) {
    chartInstance.setOption(opt, true)
  }
}, { deep: true })
</script>
