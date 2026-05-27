<template>
  <div>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索用户" style="width: 200px;" @keyup.enter="loadData" clearable />
    </div>

    <el-table :data="list" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="头像" width="70">
        <template #default="{ row }">
          <el-avatar :size="36">{{ row.nickname?.charAt(0) || 'U' }}</el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column label="角色" width="80">
        <template #default="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : ''">{{ row.role === 'ADMIN' ? '管理员' : '用户' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-switch :model-value="row.status === 1" @change="toggleStatus(row)" :disabled="row.role === 'ADMIN'" />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="160" />
    </el-table>

    <el-pagination background layout="total, prev, pager, next" :total="total"
                   :page-size="pageSize" v-model:current-page="page" @current-change="loadData" style="margin-top: 16px;" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi } from '../../api'

const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const keyword = ref('')

const loadData = async () => {
  try {
    const res = await userApi.list({ page: page.value, size: pageSize.value, keyword: keyword.value })
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { /* ignore */ }
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  await userApi.updateStatus(row.id, { status: newStatus })
  ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>
