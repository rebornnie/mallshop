<template>
  <router-link
    :to="`/product/${product.id}`"
    class="bg-white rounded-xl overflow-hidden shadow-sm hover:shadow-md hover:-translate-y-1 transition-all border border-[#FEE2E2] group"
  >
    <div class="aspect-square bg-[#FEF2F2] overflow-hidden">
      <img
        v-if="product.pic"
        :src="product.pic"
        :alt="product.name"
        class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
      />
      <div v-else class="w-full h-full flex items-center justify-center">
        <svg class="w-12 h-12 text-[#FECACA]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
      </div>
    </div>
    <div class="p-3">
      <h3 class="text-sm font-medium text-[#450A0A] line-clamp-2 min-h-[2.5rem]">{{ product.name }}</h3>
      <p v-if="product.subtitle" class="text-xs text-gray-500 mt-1 line-clamp-1">{{ product.subtitle }}</p>
      <div class="flex items-baseline mt-2 space-x-2">
        <span class="text-lg font-bold text-[#DC2626]">¥{{ product.price }}</span>
        <span v-if="product.originalPrice" class="text-xs text-gray-400 line-through">¥{{ product.originalPrice }}</span>
      </div>
      <div class="flex items-center justify-between mt-2">
        <span class="text-xs text-gray-500">已售 {{ product.sale || 0 }}</span>
        <span v-if="product.newStatus === 1" class="px-2 py-0.5 bg-[#DC2626] text-white text-xs rounded-full">新品</span>
      </div>
    </div>
  </router-link>
</template>

<script setup>
defineProps({
  product: { type: Object, required: true }
})
</script>
