<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <div>
            <span>菜品管理</span>
            <el-select v-model="queryCategory" placeholder="全部分类" clearable style="margin-left:16px;width:160px" @change="loadList">
              <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </div>
          <el-button type="primary" @click="openDialog()">新增菜品</el-button>
        </div>
      </template>
      <el-table :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图片" width="100">
          <template #default="{ row }">
            <el-image v-if="row.image" :src="getImageUrl(row.image)" style="width:60px;height:60px" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="菜品名称" />
        <el-table-column label="价格" width="100">
          <template #default="{ row }">{{ (row.price / 100).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="操作" width="260">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" background layout="prev, pager, next"
        :total="total" :page-size="pageSize" v-model:current-page="currentPage" @current-change="loadList" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑菜品' : '新增菜品'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="菜品名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="所属分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格(元)">
          <el-input-number v-model="formPrice" :min="0" :precision="2" :step="1" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload action="/api/admin/upload" :show-file-list="false" :headers="uploadHeaders"
            :on-success="handleUploadSuccess">
            <el-image v-if="form.image" :src="getImageUrl(form.image)" style="width:100px;height:100px" fit="cover" />
            <el-button v-else size="small">上传图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const list = ref([])
const categories = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 20
const queryCategory = ref(null)
const dialogVisible = ref(false)
const form = ref({ id: null, name: '', categoryId: null, price: 0, image: '', description: '', sortOrder: 0, status: 1 })
const formPrice = computed({
  get: () => form.value.price / 100,
  set: (val) => { form.value.price = Math.round(val * 100) }
})
const uploadHeaders = computed(() => ({ Authorization: 'Bearer ' + localStorage.getItem('token') }))

const getImageUrl = (imagePath) => {
  if (!imagePath) return ''
  // 如果已经是完整URL，直接返回
  if (imagePath.startsWith('http')) return imagePath
  // 拼接后端服务器地址
  return `http://localhost:8080${imagePath}`
}

const loadCategories = async () => {
  const res = await request.get('/category/list')
  categories.value = res.data
}

const loadList = async () => {
  const params = { page: currentPage.value, size: pageSize }
  if (queryCategory.value) params.categoryId = queryCategory.value
  const res = await request.get('/dish/list', { params })
  list.value = res.data.records
  total.value = res.data.total
}

const openDialog = (row) => {
  if (row) {
    form.value = { ...row }
  } else {
    form.value = { id: null, name: '', categoryId: null, price: 0, image: '', description: '', sortOrder: 0, status: 1 }
  }
  dialogVisible.value = true
}

const handleUploadSuccess = (res) => {
  if (res.code === 200) {
    form.value.image = res.data
  }
}

const handleSave = async () => {
  await request.post('/dish/save', form.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadList()
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  await request.post('/dish/status', null, { params: { id: row.id, status: newStatus } })
  ElMessage.success('操作成功')
  loadList()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该菜品？', '提示')
  try {
    await request.delete('/dish/delete/' + id)
    ElMessage.success('删除成功')
    loadList()
  } catch (e) { /* handled */ }
}

onMounted(() => { loadCategories(); loadList() })
</script>
