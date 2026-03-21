<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;align-items:center;gap:16px">
          <span>订单管理</span>
          <el-select v-model="queryStatus" placeholder="全部状态" clearable style="width:140px" @change="loadList">
            <el-option label="待支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="制作中" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </div>
      </template>
      <el-table :data="list" border stripe>
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="tableNo" label="桌号" width="80" />
        <el-table-column label="金额" width="100">
          <template #default="{ row }">{{ (row.totalAmount / 100).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row.id)">详情</el-button>
            <el-button v-if="row.status === 1" size="small" type="primary" @click="changeStatus(row.id, 2)">开始制作</el-button>
            <el-button v-if="row.status === 2" size="small" type="success" @click="changeStatus(row.id, 3)">完成</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" background layout="prev, pager, next"
        :total="total" :page-size="pageSize" v-model:current-page="currentPage" @current-change="loadList" />
    </el-card>

    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
      <div v-if="detail">
        <p><b>订单号：</b>{{ detail.order.orderNo }}</p>
        <p><b>桌号：</b>{{ detail.order.tableNo }}</p>
        <p><b>总金额：</b>{{ (detail.order.totalAmount / 100).toFixed(2) }} 元</p>
        <p><b>备注：</b>{{ detail.order.remark || '无' }}</p>
        <el-table :data="detail.items" border size="small" style="margin-top:12px">
          <el-table-column prop="dishName" label="菜品" />
          <el-table-column label="单价" width="100">
            <template #default="{ row }">{{ (row.dishPrice / 100).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column label="小计" width="100">
            <template #default="{ row }">{{ (row.dishPrice * row.quantity / 100).toFixed(2) }}</template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 20
const queryStatus = ref(null)
const detailVisible = ref(false)
const detail = ref(null)

const statusText = (s) => ['待支付','已支付','制作中','已完成','已取消'][s] || '未知'
const statusType = (s) => ['warning','primary','','success','info'][s] || 'info'

const loadList = async () => {
  const params = { page: currentPage.value, size: pageSize }
  if (queryStatus.value !== null && queryStatus.value !== '') params.status = queryStatus.value
  const res = await request.get('/order/list', { params })
  list.value = res.data.records
  total.value = res.data.total
}

const viewDetail = async (id) => {
  const res = await request.get('/order/detail/' + id)
  detail.value = res.data
  detailVisible.value = true
}

const changeStatus = async (orderId, status) => {
  await request.post('/order/status', null, { params: { orderId, status } })
  ElMessage.success('操作成功')
  loadList()
}

onMounted(loadList)
</script>
