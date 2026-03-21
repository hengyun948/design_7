<template>
  <div>
    <el-card>
      <template #header><span>评价管理</span></template>
      <el-table :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderId" label="订单ID" width="100" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column label="评分" width="100">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled />
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" />
        <el-table-column prop="createTime" label="评价时间" width="180" />
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" background layout="prev, pager, next"
        :total="total" :page-size="pageSize" v-model:current-page="currentPage" @current-change="loadList" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 20

const loadList = async () => {
  const res = await request.get('/review/list', { params: { page: currentPage.value, size: pageSize } })
  list.value = res.data.records
  total.value = res.data.total
}

onMounted(loadList)
</script>
