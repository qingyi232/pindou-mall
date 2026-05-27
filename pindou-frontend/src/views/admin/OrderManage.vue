<template>
  <div>
    <div class="toolbar">
      <el-input v-model="orderNo" placeholder="搜索订单号" style="width: 200px;" @keyup.enter="loadData" clearable />
      <el-select v-model="statusFilter" placeholder="全部状态" clearable @change="loadData" style="width: 150px;">
        <el-option label="待付款" :value="0" />
        <el-option label="已付款" :value="1" />
        <el-option label="已发货" :value="2" />
        <el-option label="已完成" :value="3" />
        <el-option label="已取消" :value="4" />
      </el-select>
    </div>

    <el-table :data="list" stripe>
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column label="商品" min-width="200">
        <template #default="{ row }">
          <div v-for="item in row.orderItems" :key="item.id" class="order-product">
            {{ item.productName }} x{{ item.quantity }}
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="totalAmount" label="金额" width="100">
        <template #default="{ row }">¥{{ row.totalAmount }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="receiver" label="收货人" width="100" />
      <el-table-column prop="createTime" label="下单时间" width="160" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" link type="primary" @click="shipOrder(row.id)">发货</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination background layout="total, prev, pager, next" :total="total"
                   :page-size="pageSize" v-model:current-page="page" @current-change="loadData" style="margin-top: 16px;" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '../../api'

const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const orderNo = ref('')
const statusFilter = ref(null)

const statusText = (s) => ['待付款', '已付款', '已发货', '已完成', '已取消'][s]
const statusType = (s) => ['warning', 'primary', 'info', 'success', 'danger'][s]

const loadData = async () => {
  try {
    const params = { page: page.value, size: pageSize.value }
    if (orderNo.value) params.orderNo = orderNo.value
    if (statusFilter.value !== null && statusFilter.value !== undefined) params.status = statusFilter.value
    const res = await orderApi.adminList(params)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { /* ignore */ }
}

const shipOrder = async (id) => {
  await ElMessageBox.confirm('确认发货？', '提示')
  await orderApi.ship(id)
  ElMessage.success('发货成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.toolbar { display: flex; gap: 12px; margin-bottom: 16px; }
.order-product { font-size: 13px; line-height: 1.6; }
</style>
