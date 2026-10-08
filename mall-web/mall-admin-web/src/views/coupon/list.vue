<template>
  <div class="coupon-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">优惠券管理</span>
          <el-button type="primary" @click="showDialog = true" :icon="Plus">新增优惠券</el-button>
        </div>
      </template>

      <el-form :model="queryForm" inline class="query-form">
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="优惠券名称" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryForm.type" placeholder="全部类型" clearable style="width: 120px">
            <el-option label="满减券" :value="0" />
            <el-option label="折扣券" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="未开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已结束" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList" :icon="Search">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="couponList" stripe border v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="优惠券名称" min-width="150" />
        <el-table-column label="类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.type === 0 ? 'primary' : 'warning'">
              {{ scope.row.type === 0 ? '满减券' : '折扣券' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="优惠" width="120">
          <template #default="scope">
            <span v-if="scope.row.type === 0">¥{{ scope.row.amount }}</span>
            <span v-else>{{ scope.row.amount }}折</span>
          </template>
        </el-table-column>
        <el-table-column prop="minPoint" label="最低消费" width="100">
          <template #default="scope">¥{{ scope.row.minPoint }}</template>
        </el-table-column>
        <el-table-column prop="publishCount" label="发行量" width="100" />
        <el-table-column prop="receiveCount" label="已领取" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="有效期" min-width="200">
          <template #default="scope">
            {{ scope.row.startTime }} ~ {{ scope.row.endTime }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link :type="scope.row.status === 1 ? 'danger' : 'success'" @click="handleToggleStatus(scope.row)">
              {{ scope.row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @change="loadList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑优惠券' : '新增优惠券'" width="600px">
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="优惠券名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入优惠券名称" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio-button :label="0">满减券</el-radio-button>
            <el-radio-button :label="1">折扣券</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="form.type === 0 ? '减免金额' : '折扣比例'" prop="amount">
          <el-input-number v-model="form.amount" :min="0" :precision="form.type === 0 ? 2 : 1" :max="form.type === 0 ? 99999 : 9.9" />
          <span class="ml-2 text-secondary">{{ form.type === 0 ? '元' : '折' }}</span>
        </el-form-item>
        <el-form-item label="最低消费" prop="minPoint">
          <el-input-number v-model="form.minPoint" :min="0" :precision="2" />
          <span class="ml-2 text-secondary">元</span>
        </el-form-item>
        <el-form-item label="发行量" prop="publishCount">
          <el-input-number v-model="form.publishCount" :min="1" :max="999999" />
          <span class="ml-2 text-secondary">张</span>
        </el-form-item>
        <el-form-item label="每人限领" prop="perLimit">
          <el-input-number v-model="form.perLimit" :min="1" :max="99" />
          <span class="ml-2 text-secondary">张</span>
        </el-form-item>
        <el-form-item label="有效期" prop="timeRange">
          <el-date-picker
            v-model="form.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="备注" prop="note">
          <el-input v-model="form.note" type="textarea" rows="2" placeholder="优惠券使用说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus, Search } from '@element-plus/icons-vue'
import { getCouponList, createCoupon, updateCoupon, deleteCoupon, updateCouponStatus } from '@/api/coupon'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const showDialog = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const couponList = ref([])
const total = ref(0)
const formRef = ref()

const queryForm = reactive({
  keyword: '',
  type: null,
  status: null,
  pageNum: 1,
  pageSize: 20
})

const form = reactive({
  id: null,
  name: '',
  type: 0,
  amount: 10,
  minPoint: 100,
  publishCount: 1000,
  perLimit: 1,
  timeRange: [],
  note: ''
})

const rules = {
  name: [{ required: true, message: '请输入优惠券名称', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入优惠金额', trigger: 'blur' }],
  minPoint: [{ required: true, message: '请输入最低消费金额', trigger: 'blur' }],
  publishCount: [{ required: true, message: '请输入发行量', trigger: 'blur' }],
  timeRange: [{ required: true, message: '请选择有效期', trigger: 'change' }]
}

const getStatusType = (status) => {
  if (status === 0) return 'info'
  if (status === 1) return 'success'
  return 'danger'
}

const getStatusLabel = (status) => {
  if (status === 0) return '未开始'
  if (status === 1) return '进行中'
  return '已结束'
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await getCouponList(queryForm)
    couponList.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const handleReset = () => {
  queryForm.keyword = ''
  queryForm.type = null
  queryForm.status = null
  queryForm.pageNum = 1
  loadList()
}

const resetForm = () => {
  form.id = null
  form.name = ''
  form.type = 0
  form.amount = 10
  form.minPoint = 100
  form.publishCount = 1000
  form.perLimit = 1
  form.timeRange = []
  form.note = ''
  isEdit.value = false
}

const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.name = row.name
  form.type = row.type
  form.amount = row.amount
  form.minPoint = row.minPoint
  form.publishCount = row.publishCount
  form.perLimit = row.perLimit || 1
  form.timeRange = [row.startTime, row.endTime]
  form.note = row.note || ''
  showDialog.value = true
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 2 : 1
  try {
    await updateCouponStatus(row.id, newStatus)
    ElMessage.success('状态更新成功')
    loadList()
  } catch (e) {
    ElMessage.error('状态更新失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该优惠券吗？', '提示', { type: 'warning' })
    .then(async () => {
      try {
        await deleteCoupon(row.id)
        ElMessage.success('删除成功')
        loadList()
      } catch (e) {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {})
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const data = {
      name: form.name,
      type: form.type,
      amount: form.amount,
      minPoint: form.minPoint,
      publishCount: form.publishCount,
      perLimit: form.perLimit,
      startTime: form.timeRange[0],
      endTime: form.timeRange[1],
      note: form.note
    }

    if (isEdit.value) {
      await updateCoupon(form.id, data)
      ElMessage.success('更新成功')
    } else {
      await createCoupon(data)
      ElMessage.success('创建成功')
    }
    showDialog.value = false
    resetForm()
    loadList()
  } catch (e) {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  } finally {
    submitting.value = false
  }
}

onMounted(loadList)
</script>

<style scoped>
.coupon-page {
  padding-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #881337;
}

.query-form {
  margin-bottom: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
