<template>
  <div class="order-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">订单管理</span>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item>
          <el-input v-model="queryForm.orderSn" placeholder="订单编号" clearable />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryForm.status" placeholder="订单状态" clearable>
            <el-option label="待支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="已发货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
            <el-option label="退款中" :value="5" />
            <el-option label="已退款" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryForm.receiverPhone" placeholder="收货人手机号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData" style="background: #E11D48; border-color: #E11D48;">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="orderSn" label="订单编号" min-width="160" />
        <el-table-column prop="receiverName" label="收货人" width="100" />
        <el-table-column prop="receiverPhone" label="手机号" width="120" />
        <el-table-column prop="totalAmount" label="商品总额" width="100">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="payAmount" label="应付金额" width="100">
          <template #default="{ row }">¥{{ row.payAmount }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row)">详情</el-button>
            <el-button size="small" type="primary" v-if="row.status === 1" @click="handleDeliver(row)">发货</el-button>
            <el-button size="small" type="warning" v-if="row.status === 5" @click="handleRefund(row)">退款</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total"
          layout="total, prev, pager, next" @current-change="loadData" />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <el-descriptions :column="2" border v-if="orderDetail">
        <el-descriptions-item label="订单编号">{{ orderDetail.orderSn }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="statusType(orderDetail.status)">{{ statusText(orderDetail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="收货人">{{ orderDetail.receiverName }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ orderDetail.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">
          {{ orderDetail.receiverProvince }}{{ orderDetail.receiverCity }}{{ orderDetail.receiverDistrict }}{{ orderDetail.receiverDetailAddress }}
        </el-descriptions-item>
        <el-descriptions-item label="商品总额">¥{{ orderDetail.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="应付金额">¥{{ orderDetail.payAmount }}</el-descriptions-item>
        <el-descriptions-item label="订单备注" :span="2">{{ orderDetail.note || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="orderDetail?.orderItemList" size="small" class="mt-16">
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="productPrice" label="单价" width="100">
          <template #default="{ row }">¥{{ row.productPrice }}</template>
        </el-table-column>
        <el-table-column prop="productQuantity" label="数量" width="80" />
      </el-table>
    </el-dialog>

    <el-dialog v-model="deliverVisible" title="订单发货" width="400px">
      <el-form :model="deliverForm" ref="deliverRef" label-width="100px">
        <el-form-item label="物流公司" prop="deliveryCompany">
          <el-input v-model="deliverForm.deliveryCompany" placeholder="请输入物流公司" />
        </el-form-item>
        <el-form-item label="运单号" prop="deliverySn">
          <el-input v-model="deliverForm.deliverySn" placeholder="请输入运单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deliverVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDeliver" style="background: #E11D48; border-color: #E11D48;">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="refundVisible" title="退款审批" width="400px">
      <el-form :model="refundForm" label-width="100px">
        <el-form-item label="审批结果">
          <el-radio-group v-model="refundForm.approved">
            <el-radio :label="true">通过</el-radio>
            <el-radio :label="false">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="refundForm.note" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="refundVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRefund" style="background: #E11D48; border-color: #E11D48;">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOrderList, getOrderDetail, deliverOrder, refundOrder } from '@/api/order'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const deliverVisible = ref(false)
const refundVisible = ref(false)
const orderDetail = ref(null)
const currentOrderId = ref(null)

const queryForm = reactive({ pageNum: 1, pageSize: 20, orderSn: '', status: '', receiverPhone: '' })
const deliverForm = reactive({ orderId: '', deliveryCompany: '', deliverySn: '' })
const refundForm = reactive({ orderId: '', approved: true, note: '' })

const statusMap = { 0: '待支付', 1: '已支付', 2: '已发货', 3: '已完成', 4: '已取消', 5: '退款中', 6: '已退款' }
const statusTypeMap = { 0: 'warning', 1: 'success', 2: 'primary', 3: 'info', 4: 'info', 5: 'danger', 6: 'danger' }
const statusText = (s) => statusMap[s] || '未知'
const statusType = (s) => statusTypeMap[s] || 'info'

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOrderList(queryForm)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const resetQuery = () => { Object.assign(queryForm, { pageNum: 1, pageSize: 20, orderSn: '', status: '', receiverPhone: '' }); loadData() }

const handleDetail = async (row) => {
  try {
    const res = await getOrderDetail(row.id)
    orderDetail.value = res.data
    detailVisible.value = true
  } catch (e) { console.error(e) }
}

const handleDeliver = (row) => { currentOrderId.value = row.id; deliverForm.orderId = row.id; deliverVisible.value = true }

const submitDeliver = async () => {
  try {
    await deliverOrder(deliverForm)
    ElMessage.success('发货成功')
    deliverVisible.value = false
    loadData()
  } catch (e) { console.error(e) }
}

const handleRefund = (row) => { currentOrderId.value = row.id; refundForm.orderId = row.id; refundVisible.value = true }

const submitRefund = async () => {
  try {
    await refundOrder(refundForm)
    ElMessage.success('审批完成')
    refundVisible.value = false
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
.mt-16 { margin-top: 16px; }
</style>
