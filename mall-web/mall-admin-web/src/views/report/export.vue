<template>
  <div class="export-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">订单数据导出</span>
        </div>
      </template>

      <!-- 筛选条件 -->
      <el-form :model="queryForm" label-width="100px" class="query-form">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :lg="8">
            <el-form-item label="订单编号">
              <el-input v-model="queryForm.orderSn" placeholder="请输入订单编号" clearable />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :lg="8">
            <el-form-item label="订单状态">
              <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 100%">
                <el-option label="待付款" :value="0" />
                <el-option label="待发货" :value="1" />
                <el-option label="已发货" :value="2" />
                <el-option label="已完成" :value="3" />
                <el-option label="已关闭" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :lg="8">
            <el-form-item label="收货人电话">
              <el-input v-model="queryForm.receiverPhone" placeholder="请输入收货人电话" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :lg="8">
            <el-form-item label="下单时间">
              <el-date-picker
                v-model="queryForm.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :lg="8" class="flex items-end">
            <el-form-item>
              <el-button type="primary" @click="handleExport" :loading="exporting" :icon="Download">
                导出Excel
              </el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <!-- 导出说明 -->
      <el-alert type="info" :closable="false" class="export-tip">
        <template #title>
          <span>导出说明</span>
        </template>
        <ul>
          <li>支持按订单编号、订单状态、收货人电话、下单时间等条件筛选导出</li>
          <li>导出文件格式为 Excel (.xlsx)，包含订单基本信息、商品信息、收货信息</li>
          <li>数据量较大时可能需要等待片刻，请勿重复点击导出按钮</li>
        </ul>
      </el-alert>
    </el-card>

    <!-- 导出历史 -->
    <el-card class="history-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">导出历史</span>
          <el-button type="danger" link @click="clearHistory" :icon="Delete">清空历史</el-button>
        </div>
      </template>

      <el-empty v-if="!exportHistory.length" description="暂无导出记录" />

      <el-timeline v-else>
        <el-timeline-item
          v-for="(item, index) in exportHistory"
          :key="index"
          :type="item.status === 'success' ? 'success' : 'danger'"
          :timestamp="item.time"
        >
          <div class="history-item">
            <span class="history-filename">{{ item.filename }}</span>
            <el-tag :type="item.status === 'success' ? 'success' : 'danger'" size="small">
              {{ item.status === 'success' ? '成功' : '失败' }}
            </el-tag>
            <span v-if="item.status === 'success'" class="history-size">{{ item.size }}</span>
            <span v-else class="history-error">{{ item.error }}</span>
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Download, Delete } from '@element-plus/icons-vue'
import { exportOrder } from '@/api/report'
import { ElMessage, ElMessageBox } from 'element-plus'

const exporting = ref(false)

const queryForm = reactive({
  orderSn: '',
  status: null,
  receiverPhone: '',
  dateRange: []
})

const exportHistory = ref([])

const loadHistory = () => {
  const stored = localStorage.getItem('orderExportHistory')
  if (stored) {
    try {
      exportHistory.value = JSON.parse(stored)
    } catch (e) {
      exportHistory.value = []
    }
  }
}

const saveHistory = () => {
  localStorage.setItem('orderExportHistory', JSON.stringify(exportHistory.value.slice(0, 20)))
}

const addHistory = (item) => {
  exportHistory.value.unshift(item)
  saveHistory()
}

const handleExport = async () => {
  if (exporting.value) return

  const params = {}
  if (queryForm.orderSn) params.orderSn = queryForm.orderSn
  if (queryForm.status !== null && queryForm.status !== undefined) params.status = queryForm.status
  if (queryForm.receiverPhone) params.receiverPhone = queryForm.receiverPhone
  if (queryForm.dateRange && queryForm.dateRange.length === 2) {
    params.startTime = queryForm.dateRange[0]
    params.endTime = queryForm.dateRange[1]
  }

  exporting.value = true
  const startTime = Date.now()

  try {
    const res = await exportOrder(params)

    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')

    const timestamp = new Date().toISOString().slice(0, 19).replace(/[:T]/g, '-')
    const filename = `订单导出_${timestamp}.xlsx`

    link.href = url
    link.download = filename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    const size = formatFileSize(blob.size)
    const elapsed = ((Date.now() - startTime) / 1000).toFixed(1)

    addHistory({
      filename,
      status: 'success',
      time: new Date().toLocaleString(),
      size,
      elapsed: `${elapsed}s`
    })

    ElMessage.success(`导出成功，文件大小 ${size}，耗时 ${elapsed}s`)
  } catch (e) {
    console.error(e)

    addHistory({
      filename: `订单导出_${new Date().toLocaleString()}.xlsx`,
      status: 'error',
      time: new Date().toLocaleString(),
      error: e.message || '导出失败'
    })

    ElMessage.error('导出失败，请稍后重试')
  } finally {
    exporting.value = false
  }
}

const handleReset = () => {
  queryForm.orderSn = ''
  queryForm.status = null
  queryForm.receiverPhone = ''
  queryForm.dateRange = []
}

const clearHistory = () => {
  ElMessageBox.confirm('确定要清空所有导出历史吗？', '提示', { type: 'warning' })
    .then(() => {
      exportHistory.value = []
      localStorage.removeItem('orderExportHistory')
      ElMessage.success('已清空导出历史')
    })
    .catch(() => {})
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

onMounted(() => {
  loadHistory()
})
</script>

<style scoped>
.export-page {
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

.export-tip {
  margin-top: 10px;
}

.export-tip ul {
  margin: 0;
  padding-left: 20px;
}

.export-tip li {
  line-height: 1.8;
  color: #666;
}

.history-card {
  margin-top: 20px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.history-filename {
  font-weight: 500;
  color: #333;
}

.history-size {
  color: #666;
  font-size: 12px;
}

.history-error {
  color: #E11D48;
  font-size: 12px;
}
</style>
