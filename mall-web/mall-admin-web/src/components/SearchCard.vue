<template>
  <el-card class="mb-4" shadow="never">
    <el-form :inline="true" :model="model" class="flex flex-wrap gap-2 items-center">
      <slot />
      <el-form-item class="ml-auto">
        <el-button type="primary" @click="$emit('search')">
          <el-icon class="mr-1"><Search /></el-icon>查询
        </el-button>
        <el-button @click="$emit('reset')">
          <el-icon class="mr-1"><RefreshRight /></el-icon>重置
        </el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
defineProps({
  model: { type: Object, default: () => ({}) }
})
defineEmits(['search', 'reset'])
</script>
