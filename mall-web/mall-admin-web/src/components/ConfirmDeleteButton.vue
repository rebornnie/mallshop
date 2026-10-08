<template>
  <el-button link type="danger" size="small" @click="handleClick">
    <slot>删除</slot>
  </el-button>
</template>

<script setup>
import { ElMessageBox } from 'element-plus'

const props = defineProps({
  message: { type: String, default: '确定要删除吗？此操作不可恢复。' },
  title: { type: String, default: '确认删除' }
})
const emit = defineEmits(['confirm'])

function handleClick() {
  ElMessageBox.confirm(props.message, props.title, {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => emit('confirm'))
    .catch(() => {})
}
</script>
