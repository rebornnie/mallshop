<template>
  <div class="menu-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">菜单管理</span>
          <el-button type="primary" @click="handleAdd" style="background: #E11D48; border-color: #E11D48;">
            <el-icon><Plus /></el-icon>新增菜单
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" row-key="id" default-expand-all>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="菜单名称" />
        <el-table-column prop="url" label="路由路径" />
        <el-table-column prop="component" label="组件路径" />
        <el-table-column prop="icon" label="图标" width="80" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === 0 ? 'primary' : row.type === 1 ? 'success' : 'warning'">
              {{ row.type === 0 ? '目录' : row.type === 1 ? '菜单' : '按钮' }}
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
        <el-form-item label="父菜单">
          <el-select v-model="form.parentId" placeholder="不选则为顶级" clearable>
            <el-option v-for="m in topMenus" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="路由路径">
          <el-input v-model="form.url" />
        </el-form-item>
        <el-form-item label="组件路径">
          <el-input v-model="form.component" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="form.type">
            <el-radio :label="0">目录</el-radio>
            <el-radio :label="1">菜单</el-radio>
            <el-radio :label="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input v-model="form.permission" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { getMenuList, createMenu, updateMenu, deleteMenu } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const isEdit = ref(false)

const form = reactive({ id: null, parentId: 0, name: '', url: '', component: '', icon: '', sort: 0, type: 1, permission: '', status: 1 })
const formRules = { name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }] }

const topMenus = computed(() => tableData.value.filter(m => m.parentId === 0))

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMenuList()
    tableData.value = res.data || []
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增菜单'
  Object.assign(form, { id: null, parentId: 0, name: '', url: '', component: '', icon: '', sort: 0, type: 1, permission: '', status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑菜单'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (isEdit.value) { await updateMenu(form); ElMessage.success('更新成功') }
    else { await createMenu(form); ElMessage.success('创建成功') }
    dialogVisible.value = false
    loadData()
  } catch (e) { console.error(e) }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该菜单吗？', '提示', { type: 'warning' })
    .then(async () => { await deleteMenu(row.id); ElMessage.success('删除成功'); loadData() })
    .catch(() => {})
}

onMounted(loadData)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 16px; font-weight: 600; color: #881337; }
</style>
