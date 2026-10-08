<template>
  <el-tag :type="computedType" size="small">{{ computedLabel }}</el-tag>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  status: { type: [Number, String], required: true },
  options: { type: Object, required: true }
})

const computedType = computed(() => props.options[props.status]?.type || 'info')
const computedLabel = computed(() => props.options[props.status]?.label || '未知')
</script>
