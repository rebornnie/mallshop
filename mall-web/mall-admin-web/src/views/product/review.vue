<template>
  <div class="review-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">评价管理</span>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item>
          <el-input v-model="queryForm.keyword" placeholder="商品名称/评价内容" clearable />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryForm.showStatus" placeholder="状态" clearable>
            <el-option label="显示" :value="1" />
            <el-option label="隐藏" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData" style="background: #E11D48; border-color: #E11D48;">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="用户" width="120">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <el-avatar :size="32" :src="row.memberAvatar" />
              <span class="text-sm">{{ row.memberNickName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="100">
          <template #default="{ row }">
            <el-rate :model-value="row.star" disabled />
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" min-width="200" show-overflow-tooltip />
        <el-table-column label="图片" width="120">
          <template #default="{ row }">
            <el-image v-if="row.pics" :src="row.pics.split(',')[0]" style="width: 40px; height: 40px; border-radius: 4px;" fit="cover" :preview-src-list="row.pics.split(',')" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.showStatus === 1 ? 'success' : 'info'">
              {{ row.showStatus === 1 ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleReply(row)">回复</el-button>
            <el-button size="small" type="primary" v-if="row.showStatus === 0" @click="handleToggleStatus(row, 1)">显示</el-button>
            <el-button size="small" type="warning" v-if="row.showStatus === 1" @click="handleToggleStatus(row, 0)">隐藏</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total"
          layout="total, prev, pager, next" @current-change="loadData" />
      </div>
    </el-card>

    <!-- 回复弹窗 -->
    <el-dialog v-model="replyVisible" title="回复评价" width="500px">
      <el-form :model="replyForm" label-width="80px">
        <el-form-item label="评价内容">
          <el-input v-model="replyForm.commentContent" type="textarea" :rows="3" disabled />
        </el-form-item>
        <el-form-item label="回复内容" prop="reply">
          <el-input v-model="replyForm.content" type="textarea" :rows="4" placeholder="请输入回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply" style="background: #E11D48; border-color: #E11D48;">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getReviewList, updateReviewStatus, replyReview } from '@/api/review'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const replyVisible = ref(false)
const replyForm = reactive({ commentId: null, commentContent: '', content: '' })

const queryForm = reactive({ pageNum: 1, pageSize: 20, keyword: '', showStatus: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getReviewList(queryForm)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const resetQuery = () => {
  Object.assign(queryForm, { pageNum: 1, pageSize: 20, keyword: '', showStatus: '' })
  loadData()
}

const handleReply = (row) => {
  replyForm.commentId = row.id
  replyForm.commentContent = row.content
  replyForm.content = ''
  replyVisible.value = true
}

const submitReply = async () => {
  try {
    await replyReview({ commentId: replyForm.commentId, content: replyForm.content })
    ElMessage.success('回复成功')
    replyVisible.value = false
    loadData()
  } catch (e) { console.error(e) }
}

const handleToggleStatus = async (row, status) => {
  try {
    await updateReviewStatus(row.id, status)
    ElMessage.success(status === 1 ? '已显示' : '已隐藏')
    loadData()
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
