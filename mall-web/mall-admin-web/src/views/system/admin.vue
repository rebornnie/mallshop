<template>
  <div class="admin-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">管理员管理</span>
          <el-button type="primary" @click="handleAdd" style="background: #E11D48; border-color: #E11D48;">
            <el-icon><Plus /></el-icon>新增管理员
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
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
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleIds" multiple placeholder="选择角色">
            <el-option v-for="r in roleList" :key="r.id" :label="r.name" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
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
import { ref, reactive, onMounted } from 'vue'
import { getAdminList, createAdmin, updateAdmin, deleteAdmin, getRoleList } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const roleList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const isEdit = ref(false)

const queryForm = reactive({ pageNum: 1, pageSize: 20 })
const form = reactive({ id: null, username: '', password: '', nickname: '', email: '', status: 1, roleIds: [] })

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAdminList(queryForm)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增管理员'
  Object.assign(form, { id: null, username: '', password: '', nickname: '', email: '', status: 1, roleIds: [] })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑管理员'
  Object.assign(form, { ...row, password: '', roleIds: row.roleIds || [] })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (isEdit.value) { await updateAdmin(form); ElMessage.success('更新成功') }
    else { await createAdmin(form); ElMessage.success('创建成功') }
    dialogVisible.value = false
    loadData()
  } catch (e) { console.error(e) }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该管理员吗？', '提示', { type: 'warning' })
    .then(async () => { await deleteAdmin(row.id); ElMessage.success('删除成功'); loadData() })
    .catch(() => {})
}

onMounted(() => {
  loadData()
  getRoleList().then(res => { roleList.value = res.data || [] })
})
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 16px; font-weight: 600; color: #881337; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
