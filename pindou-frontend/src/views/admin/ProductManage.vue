<template>
  <div>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索商品" style="width: 200px;" @keyup.enter="loadData" clearable />
      <el-select v-model="categoryFilter" placeholder="全部分类" clearable @change="loadData" style="width: 150px;">
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-button type="primary" @click="showDialog()">添加商品</el-button>
    </div>

    <el-table :data="list" stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="图片" width="80">
        <template #default="{ row }">
          <el-image :src="row.mainImage" style="width: 50px; height: 50px; border-radius: 6px;" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" min-width="180" show-overflow-tooltip />
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column prop="price" label="价格" width="80" />
      <el-table-column prop="stock" label="库存" width="70" />
      <el-table-column prop="sales" label="销量" width="70" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-switch :model-value="row.status === 1" @change="toggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="showDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="deleteProduct(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination background layout="total, prev, pager, next" :total="total"
                   :page-size="pageSize" v-model:current-page="page" @current-change="loadData" style="margin-top: 16px;" />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑商品' : '添加商品'" width="700px" top="5vh">
      <el-form :model="form" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="商品名称"><el-input v-model="form.name" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类">
              <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%;">
                <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="售价"><el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%;" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="原价"><el-input-number v-model="form.originalPrice" :min="0" :precision="2" style="width: 100%;" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" style="width: 100%;" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="IP系列"><el-input v-model="form.ipSeries" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色名称"><el-input v-model="form.characterName" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="商品主图">
          <div class="upload-area">
            <el-upload action="/api/file/upload" :show-file-list="false" :on-success="handleImageSuccess" accept="image/*">
              <el-button size="small" type="primary">上传图片</el-button>
            </el-upload>
            <el-input v-model="form.mainImage" placeholder="图片URL（可上传或粘贴链接）" style="margin-top: 8px;" />
            <el-image v-if="form.mainImage" :src="form.mainImage" style="width: 100px; height: 100px; margin-top: 8px; border-radius: 6px;" fit="cover" />
          </div>
        </el-form-item>
        <el-form-item label="简介"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="详情(HTML)"><el-input v-model="form.detail" type="textarea" :rows="4" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="8"><el-form-item label="热门"><el-switch v-model="form.isHot" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="新品"><el-switch v-model="form.isNew" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProduct">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { productApi, categoryApi } from '../../api'

const list = ref([])
const categories = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const categoryFilter = ref(null)
const dialogVisible = ref(false)
const form = ref({})

const loadData = async () => {
  try {
    const res = await productApi.adminList({ page: page.value, size: pageSize.value, keyword: keyword.value, categoryId: categoryFilter.value })
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { /* ignore */ }
}

const handleImageSuccess = (res) => {
  if (res.code === 200) {
    form.value.mainImage = res.data
    ElMessage.success('图片上传成功')
  }
}

const showDialog = (row) => {
  form.value = row ? { ...row } : { price: 0, originalPrice: 0, stock: 0, isHot: 0, isNew: 0 }
  dialogVisible.value = true
}

const saveProduct = async () => {
  try {
    if (form.value.id) {
      await productApi.update(form.value)
    } else {
      await productApi.add(form.value)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (e) { /* handled */ }
}

const deleteProduct = async (id) => {
  await ElMessageBox.confirm('确定删除该商品？', '提示')
  await productApi.delete(id)
  ElMessage.success('已删除')
  loadData()
}

const toggleStatus = async (row) => {
  await productApi.updateStatus(row.id, { status: row.status === 1 ? 0 : 1 })
  ElMessage.success('状态已更新')
  loadData()
}

onMounted(async () => {
  try {
    const res = await categoryApi.all()
    categories.value = res.data || []
  } catch (e) { /* ignore */ }
  loadData()
})
</script>

<style scoped>
.toolbar { display: flex; gap: 12px; margin-bottom: 16px; }
.upload-area { width: 100%; }
</style>
