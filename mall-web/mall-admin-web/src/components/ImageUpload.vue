<template>
  <div class="flex items-center gap-4">
    <div v-if="modelValue" class="relative group">
      <el-image
        :src="modelValue"
        :preview-src-list="[modelValue]"
        fit="cover"
        class="w-24 h-24 rounded-lg border border-[#FEE2E2]"
      />
      <div
        class="absolute inset-0 bg-black/40 rounded-lg opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center gap-2"
      >
        <el-icon class="text-white cursor-pointer" @click="previewVisible = true">
          <View />
        </el-icon>
        <el-icon class="text-white cursor-pointer" @click="handleRemove">
          <Delete />
        </el-icon>
      </div>
    </div>
    <el-upload
      v-if="!modelValue"
      :http-request="customUpload"
      :show-file-list="false"
      accept="image/*"
      :before-upload="beforeUpload"
    >
      <el-button type="primary" plain>
        <el-icon class="mr-1"><Upload /></el-icon>点击上传
      </el-button>
    </el-upload>
    <el-input
      v-if="!modelValue"
      v-model="urlInput"
      placeholder="或输入图片URL"
      class="w-64"
      clearable
      @blur="handleUrlInput"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { uploadImage } from '@/api/product'

const props = defineProps({
  modelValue: { type: String, default: '' }
})
const emit = defineEmits(['update:modelValue'])

const urlInput = ref('')
const previewVisible = ref(false)

function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

async function customUpload({ file }) {
  const formData = new FormData()
  formData.append('file', file)
  try {
    const res = await uploadImage(formData)
    if (res.data?.url) {
      emit('update:modelValue', res.data.url)
      ElMessage.success('上传成功')
    }
  } catch (e) {
    ElMessage.error('上传失败')
  }
}

function handleRemove() {
  emit('update:modelValue', '')
  urlInput.value = ''
}

function handleUrlInput() {
  if (urlInput.value.trim()) {
    emit('update:modelValue', urlInput.value.trim())
  }
}
</script>
