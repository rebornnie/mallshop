<template>
  <div class="max-w-3xl mx-auto px-4 py-8">
    <div class="flex justify-between items-center mb-6">
      <h1 class="font-heading text-2xl font-bold text-foreground">收货地址</h1>
      <button @click="showForm = true"
        class="bg-primary hover:bg-primary-dark text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors">
        新增地址
      </button>
    </div>

    <div class="space-y-4">
      <div v-for="addr in addresses" :key="addr.id"
        class="bg-white rounded-xl p-6 shadow-sm"
        :class="addr.defaultStatus === 1 ? 'border-2 border-primary' : ''">
        <div class="flex justify-between items-start">
          <div>
            <div class="flex items-center gap-3">
              <span class="font-medium text-foreground">{{ addr.receiverName }}</span>
              <span class="text-sm text-secondary">{{ addr.receiverPhone }}</span>
              <span v-if="addr.defaultStatus === 1" class="bg-primary text-white text-xs px-2 py-0.5 rounded">默认</span>
            </div>
            <p class="text-sm text-secondary mt-2">
              {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detailAddress }}
            </p>
          </div>
          <div class="flex gap-2">
            <button @click="editAddress(addr)" class="text-sm text-primary hover:underline">编辑</button>
            <button @click="deleteAddress(addr.id)" class="text-sm text-destructive hover:underline">删除</button>
          </div>
        </div>
        <div v-if="addr.defaultStatus !== 1" class="mt-4 pt-4 border-t border-border">
          <button @click="setDefault(addr.id)" class="text-sm text-primary hover:underline">设为默认</button>
        </div>
      </div>
    </div>

    <div v-if="!addresses.length" class="text-center py-16 bg-white rounded-xl">
      <p class="text-foreground text-lg">暂无收货地址</p>
    </div>

    <!-- Form Dialog -->
    <div v-if="showForm" class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
      <div class="bg-white rounded-xl max-w-md w-full p-6">
        <h3 class="font-heading text-lg font-bold text-foreground mb-4">{{ isEdit ? '编辑地址' : '新增地址' }}</h3>
        <form @submit.prevent="submitForm" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-foreground mb-1">收货人</label>
            <input v-model="form.receiverName" required
              class="w-full px-4 py-2 rounded-lg border border-border focus:outline-none focus:ring-2 focus:ring-primary" />
          </div>
          <div>
            <label class="block text-sm font-medium text-foreground mb-1">手机号</label>
            <input v-model="form.receiverPhone" type="tel" required
              class="w-full px-4 py-2 rounded-lg border border-border focus:outline-none focus:ring-2 focus:ring-primary" />
          </div>
          <div class="grid grid-cols-3 gap-3">
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">省</label>
              <input v-model="form.province" required
                class="w-full px-4 py-2 rounded-lg border border-border focus:outline-none focus:ring-2 focus:ring-primary" />
            </div>
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">市</label>
              <input v-model="form.city" required
                class="w-full px-4 py-2 rounded-lg border border-border focus:outline-none focus:ring-2 focus:ring-primary" />
            </div>
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">区</label>
              <input v-model="form.district" required
                class="w-full px-4 py-2 rounded-lg border border-border focus:outline-none focus:ring-2 focus:ring-primary" />
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-foreground mb-1">详细地址</label>
            <input v-model="form.detailAddress" required
              class="w-full px-4 py-2 rounded-lg border border-border focus:outline-none focus:ring-2 focus:ring-primary" />
          </div>
          <div class="flex items-center gap-2">
            <input v-model="form.defaultStatus" type="checkbox" :true-value="1" :false-value="0" id="default" class="accent-primary" />
            <label for="default" class="text-sm text-foreground">设为默认地址</label>
          </div>
          <div class="flex gap-3 pt-4">
            <button type="button" @click="showForm = false"
              class="flex-1 py-2 border border-border rounded-lg hover:bg-background">取消</button>
            <button type="submit"
              class="flex-1 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/address'

const addresses = ref([])
const showForm = ref(false)
const isEdit = ref(false)
const form = reactive({ id: null, receiverName: '', receiverPhone: '', province: '', city: '', district: '', detailAddress: '', defaultStatus: 0 })

const loadData = async () => {
  try {
    const res = await getAddressList()
    addresses.value = res.data || []
  } catch (e) { console.error(e) }
}

const editAddress = (addr) => {
  isEdit.value = true
  Object.assign(form, addr)
  showForm.value = true
}

const submitForm = async () => {
  try {
    if (isEdit.value) {
      await updateAddress(form)
    } else {
      await addAddress(form)
    }
    showForm.value = false
    resetForm()
    loadData()
  } catch (e) { alert(e.message || '保存失败') }
}

const resetForm = () => {
  Object.assign(form, { id: null, receiverName: '', receiverPhone: '', province: '', city: '', district: '', detailAddress: '', defaultStatus: 0 })
  isEdit.value = false
}

const deleteAddr = async (id) => {
  if (!confirm('确定删除该地址吗？')) return
  try {
    await deleteAddress(id)
    loadData()
  } catch (e) { alert(e.message || '删除失败') }
}

const setDefault = async (id) => {
  try {
    await setDefaultAddress(id)
    loadData()
  } catch (e) { alert(e.message || '设置失败') }
}

onMounted(loadData)
</script>
