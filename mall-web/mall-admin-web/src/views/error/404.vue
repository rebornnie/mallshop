<template>
  <div class="min-h-screen flex flex-col items-center justify-center bg-[#FEF2F2]">
    <div class="text-center">
      <div class="text-9xl font-bold text-[#DC2626] mb-4">404</div>
      <h1 class="text-2xl font-bold text-[#450A0A] mb-2">页面不存在</h1>
      <p class="text-gray-500 mb-8">您访问的页面已被移除或不存在</p>
      <el-button type="primary" size="large" @click="$router.push('/dashboard')">
        <el-icon class="mr-1"><HomeFilled /></el-icon>返回首页
      </el-button>
    </div>
  </div>
</template>
