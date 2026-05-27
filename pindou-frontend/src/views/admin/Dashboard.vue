<template>
  <div class="dashboard">
    <h2>数据概览</h2>
    <div class="stat-cards">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #ff6b9d, #ff4757);">
          <el-icon><ShoppingCart /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.totalOrders || 0 }}</span>
          <span class="stat-label">总订单数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #a855f7, #7c3aed);">
          <el-icon><Goods /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.paidOrders || 0 }}</span>
          <span class="stat-label">已支付订单</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #06b6d4, #0891b2);">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ userCount }}</span>
          <span class="stat-label">注册用户</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f59e0b, #d97706);">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ productCount }}</span>
          <span class="stat-label">商品总数</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { orderApi, userApi, productApi } from '../../api'

const stats = ref({})
const userCount = ref(0)
const productCount = ref(0)

onMounted(async () => {
  try {
    const [statsRes, usersRes, productsRes] = await Promise.all([
      orderApi.stats(),
      userApi.list({ page: 1, size: 1 }),
      productApi.adminList({ page: 1, size: 1 })
    ])
    stats.value = statsRes.data || {}
    userCount.value = usersRes.data?.total || 0
    productCount.value = productsRes.data?.total || 0
  } catch (e) { /* ignore */ }
})
</script>

<style scoped>
.dashboard h2 { margin-bottom: 24px; }
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}
.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  display: block;
}
.stat-label {
  font-size: 13px;
  color: #999;
}
</style>
