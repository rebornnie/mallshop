<template>
  <div class="category-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">分类管理</span>
          <el-button type="primary" @click="handleAdd" style="background: #E11D48; border-color: #E11D48;">
            <el-icon><Plus /></el-icon>新增分类
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" row-key="id" default-expand-all>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column label="图标" width="80">
          <template #default="{ row }">
            <el-image v-if="row.icon" :src="row.icon" style="width: 32px; height: 32px;" fit="cover" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.showStatus === 1 ? 'success' : 'info'">
              {{ row.showStatus === 1 ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="父分类">
          <el-select v-model="form.parentId" placeholder="不选则为一级分类" clearable>
            <el-option v-for="c in topCategories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="图标URL">
          <el-input v-model="form.icon" placeholder="图标地址" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="显示状态">
          <el-radio-group v-model="form.showStatus">
            <el-radio :label="1">显示</el-radio>
            <el-radio :label="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" style="background: #E11D48; border-color: #E11D48;">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getCategoryList, createCategory, updateCategory, deleteCategory } from '@/api/product'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const isEdit = ref(false)

const form = reactive({ id: null, parentId: 0, name: '', icon: '', sort: 0, showStatus: 1 })

const formRules = { name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }] }

const topCategories = computed(() => tableData.value.filter(c => c.parentId === 0))

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCategoryList()
    tableData.value = res.data || []
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增分类'
  Object.assign(form, { id: null, parentId: 0, name: '', icon: '', sort: 0, showStatus: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑分类'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (isEdit.value) { await updateCategory(form); ElMessage.success('更新成功') }
    else { await createCategory(form); ElMessage.success('创建成功') }
    dialogVisible.value = false
    loadData()
  } catch (e) { console.error(e) }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该分类吗？', '提示', { type: 'warning' })
    .then(async () => { await deleteCategory(row.id); ElMessage.success('删除成功'); loadData() })
    .catch(() => {})
}

onMounted(loadData)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 16px; font-weight: 600; color: #881337; }
</style>
