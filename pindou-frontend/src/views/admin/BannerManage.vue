<template>
  <div>
    <div class="toolbar">
      <el-button type="primary" @click="showDialog()">添加轮播图</el-button>
    </div>

    <el-table :data="list" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="图片" width="120">
        <template #default="{ row }">
          <el-image :src="row.image" style="width: 80px; height: 40px; border-radius: 4px;" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="url" label="链接" show-overflow-tooltip />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button link type="primary" @click="showDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="deleteBanner(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑轮播图' : '添加轮播图'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="轮播图片">
          <div class="upload-area">
            <el-upload action="/api/file/upload" :show-file-list="false" :on-success="handleBannerUpload" accept="image/*">
              <el-button size="small" type="primary">上传图片</el-button>
            </el-upload>
            <el-input v-model="form.image" placeholder="图片URL（可上传或粘贴链接）" style="margin-top: 8px;" />
            <el-image v-if="form.image" :src="form.image" style="width: 200px; height: 80px; margin-top: 8px; border-radius: 4px;" fit="cover" />
          </div>
        </el-form-item>
        <el-form-item label="链接"><el-input v-model="form.url" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveBanner">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { bannerApi } from '../../api'

const list = ref([])
const dialogVisible = ref(false)
const form = ref({})

const loadData = async () => {
  try {
    const res = await bannerApi.all()
    list.value = res.data || []
  } catch (e) { /* ignore */ }
}

const handleBannerUpload = (res) => {
  if (res.code === 200) {
    form.value.image = res.data
    ElMessage.success('图片上传成功')
  }
}

const showDialog = (row) => {
  form.value = row ? { ...row } : { sortOrder: 0, status: 1 }
  dialogVisible.value = true
}

const saveBanner = async () => {
  try {
    if (form.value.id) {
      await bannerApi.update(form.value)
    } else {
      await bannerApi.add(form.value)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (e) { /* handled */ }
}

const deleteBanner = async (id) => {
  await ElMessageBox.confirm('确定删除？', '提示')
  await bannerApi.delete(id)
  ElMessage.success('已删除')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
.upload-area { width: 100%; }
</style>
