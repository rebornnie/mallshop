<template>
  <div class="brand-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">品牌管理</span>
          <el-button type="primary" @click="handleAdd" style="background: #E11D48; border-color: #E11D48;">
            <el-icon><Plus /></el-icon>新增品牌
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item>
          <el-input v-model="queryForm.keyword" placeholder="品牌名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData" style="background: #E11D48; border-color: #E11D48;">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="Logo" width="80">
          <template #default="{ row }">
            <el-image v-if="row.logo" :src="row.logo" style="width: 50px; height: 50px; border-radius: 4px;" fit="cover" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="品牌名称" />
        <el-table-column prop="firstLetter" label="首字母" width="80" />
        <el-table-column label="推荐" width="80">
          <template #default="{ row }">
            <el-tag :type="row.recommendStatus === 1 ? 'success' : 'info'">
              {{ row.recommendStatus === 1 ? '是' : '否' }}
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

      <div class="pagination">
        <el-pagination v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total"
          layout="total, prev, pager, next" @current-change="loadData" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="品牌名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="首字母">
          <el-input v-model="form.firstLetter" maxlength="1" />
        </el-form-item>
        <el-form-item label="Logo">
          <el-input v-model="form.logo" placeholder="Logo URL" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="推荐">
          <el-radio-group v-model="form.recommendStatus">
            <el-radio :label="1">是</el-radio>
            <el-radio :label="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
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
import { ref, reactive, onMounted } from 'vue'
import { getBrandList, createBrand, updateBrand, deleteBrand } from '@/api/product'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const isEdit = ref(false)

const queryForm = reactive({ pageNum: 1, pageSize: 20, keyword: '' })
const form = reactive({ id: null, name: '', firstLetter: '', logo: '', description: '', recommendStatus: 0, sort: 0 })
const formRules = { name: [{ required: true, message: '请输入品牌名称', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await getBrandList(queryForm)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const resetQuery = () => { queryForm.keyword = ''; queryForm.pageNum = 1; loadData() }

const handleAdd = () => {
  isEdit.value = false; dialogTitle.value = '新增品牌'
  Object.assign(form, { id: null, name: '', firstLetter: '', logo: '', description: '', recommendStatus: 0, sort: 0 })
  dialogVisible.value = true
}

const handleEdit = (row) => { isEdit.value = true; dialogTitle.value = '编辑品牌'; Object.assign(form, row); dialogVisible.value = true }

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (isEdit.value) { await updateBrand(form); ElMessage.success('更新成功') }
    else { await createBrand(form); ElMessage.success('创建成功') }
    dialogVisible.value = false; loadData()
  } catch (e) { console.error(e) }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该品牌吗？', '提示', { type: 'warning' })
    .then(async () => { await deleteBrand(row.id); ElMessage.success('删除成功'); loadData() })
    .catch(() => {})
}

onMounted(loadData)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 16px; font-weight: 600; color: #881337; }
.search-form { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
