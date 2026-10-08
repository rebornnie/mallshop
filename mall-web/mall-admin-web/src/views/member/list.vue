<template>
  <div class="member-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">会员管理</span>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item>
          <el-input v-model="queryForm.keyword" placeholder="手机号/昵称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData" style="background: #E11D48; border-color: #E11D48;">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.avatar" />
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="160" />
        <el-table-column prop="loginTime" label="最近登录" width="160" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total"
          layout="total, prev, pager, next" @current-change="loadData" />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="会员详情" width="600px">
      <el-descriptions :column="2" border v-if="memberDetail">
        <el-descriptions-item label="ID">{{ memberDetail.id }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ memberDetail.phone }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ memberDetail.nickname || '-' }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ memberDetail.gender === 1 ? '男' : memberDetail.gender === 2 ? '女' : '未知' }}</el-descriptions-item>
        <el-descriptions-item label="生日">{{ memberDetail.birthday || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="memberDetail.status === 1 ? 'success' : 'danger'">{{ memberDetail.status === 1 ? '正常' : '禁用' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ memberDetail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="订单数">{{ memberDetail.orderCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="消费金额">¥{{ memberDetail.totalConsume || 0 }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMemberList, getMemberDetail } from '@/api/member'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const memberDetail = ref(null)

const queryForm = reactive({ pageNum: 1, pageSize: 20, keyword: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMemberList(queryForm)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const resetQuery = () => { queryForm.keyword = ''; queryForm.pageNum = 1; loadData() }

const handleDetail = async (row) => {
  try {
    const res = await getMemberDetail(row.id)
    memberDetail.value = res.data
    detailVisible.value = true
  } catch (e) { console.error(e) }
}

onMounted(loadData)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 16px; font-weight: 600; color: #881337; }
.search-form { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
