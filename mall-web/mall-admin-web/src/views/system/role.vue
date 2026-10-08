<template>
  <div class="role-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">角色管理</span>
          <el-button type="primary" @click="handleAdd" style="background: #E11D48; border-color: #E11D48;">
            <el-icon><Plus /></el-icon>新增角色
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="角色名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="primary" @click="handleAllocMenu(row)">分配权限</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
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

    <el-dialog v-model="menuDialogVisible" title="分配菜单权限" width="400px">
      <el-tree
        ref="menuTreeRef"
        :data="menuList"
        show-checkbox
        node-key="id"
        :props="{ label: 'name', children: 'children' }"
        default-expand-all
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAllocMenu" style="background: #E11D48; border-color: #E11D48;">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getRoleList, createRole, updateRole, allocMenu, getMenuList } from '@/api/admin'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const menuList = ref([])
const dialogVisible = ref(false)
const menuDialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const menuTreeRef = ref()
const isEdit = ref(false)
const currentRoleId = ref(null)

const form = reactive({ id: null, name: '', description: '', sort: 0, status: 1 })
const formRules = { name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRoleList()
    tableData.value = res.data || []
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增角色'
  Object.assign(form, { id: null, name: '', description: '', sort: 0, status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑角色'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (isEdit.value) { await updateRole(form); ElMessage.success('更新成功') }
    else { await createRole(form); ElMessage.success('创建成功') }
    dialogVisible.value = false
    loadData()
  } catch (e) { console.error(e) }
}

const handleAllocMenu = (row) => {
  currentRoleId.value = row.id
  menuDialogVisible.value = true
  setTimeout(() => {
    menuTreeRef.value?.setCheckedKeys(row.menuIds || [])
  }, 100)
}

const submitAllocMenu = async () => {
  try {
    const checkedKeys = menuTreeRef.value.getCheckedKeys()
    const halfKeys = menuTreeRef.value.getHalfCheckedKeys()
    await allocMenu({ roleId: currentRoleId.value, menuIds: [...halfKeys, ...checkedKeys] })
    ElMessage.success('分配成功')
    menuDialogVisible.value = false
  } catch (e) { console.error(e) }
}

onMounted(() => {
  loadData()
  getMenuList().then(res => { menuList.value = res.data || [] })
})
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 16px; font-weight: 600; color: #881337; }
</style>
