<template>
  <div class="page-header">
    <h1 class="page-title">{{ title }}</h1>
    <div class="flex items-center gap-3">
      <slot name="extra" />
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, required: true }
})
</script>
