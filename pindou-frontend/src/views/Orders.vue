<template>
  <div class="page-container orders-page">
    <h2 class="section-title">📋 我的订单</h2>

    <el-tabs v-model="activeTab" @tab-change="loadOrders">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="待付款" name="0" />
      <el-tab-pane label="已付款" name="1" />
      <el-tab-pane label="已发货" name="2" />
      <el-tab-pane label="已完成" name="3" />
    </el-tabs>

    <div v-if="orders.length === 0" class="empty">
      <el-empty description="暂无订单" />
    </div>

    <div v-for="order in orders" :key="order.id" class="order-card">
      <div class="order-header">
        <span class="order-no">订单号：{{ order.orderNo }}</span>
        <span class="order-time">{{ order.createTime }}</span>
        <el-tag :type="statusType(order.status)" size="small">{{ statusText(order.status) }}</el-tag>
      </div>

      <div v-for="item in order.orderItems" :key="item.id" class="order-item"
           @click="$router.push(`/product/${item.productId}`)">
        <img :src="item.productImage || 'https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=200&h=200&fit=crop'" class="item-img" />
        <div class="item-info">
          <span class="item-name">{{ item.productName }}</span>
          <span class="item-qty">x{{ item.quantity }}</span>
        </div>
        <span class="item-price">¥{{ item.price }}</span>
      </div>

      <div class="order-footer">
        <span class="order-total">
          共 {{ order.orderItems?.reduce((s, i) => s + i.quantity, 0) }} 件，合计：
          <strong>¥{{ order.totalAmount }}</strong>
        </span>
        <div class="order-actions">
          <el-button v-if="order.status === 0" type="primary" size="small" @click="payOrder(order.id)">去支付</el-button>
          <el-button v-if="order.status === 0" size="small" @click="cancelOrder(order.id)">取消订单</el-button>
          <el-button v-if="order.status === 2" type="success" size="small" @click="confirmOrder(order.id)">确认收货</el-button>
          <el-button v-if="order.status === 3" size="small" @click="reviewOrder(order)">去评价</el-button>
        </div>
      </div>
    </div>

    <!-- 评价弹窗 -->
    <el-dialog v-model="reviewVisible" title="商品评价" width="500px">
      <el-form>
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" />
        </el-form-item>
        <el-form-item label="评价">
          <el-input v-model="reviewForm.content" type="textarea" :rows="4" placeholder="分享你的使用感受" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi, reviewApi } from '../api'

const activeTab = ref('all')
const orders = ref([])
const reviewVisible = ref(false)
const reviewForm = ref({ rating: 5, content: '', productId: null, orderId: null })

const statusText = (s) => ['待付款', '已付款', '已发货', '已完成', '已取消'][s]
const statusType = (s) => ['warning', 'primary', 'info', 'success', 'danger'][s]

const loadOrders = async () => {
  try {
    const params = { page: 1, size: 50 }
    if (activeTab.value !== 'all') params.status = Number(activeTab.value)
    const res = await orderApi.list(params)
    orders.value = res.data?.records || []
  } catch (e) { /* ignore */ }
}

const payOrder = async (id) => {
  await ElMessageBox.confirm('确认支付该订单？', '支付确认')
  await orderApi.pay(id)
  ElMessage.success('支付成功')
  loadOrders()
}

const cancelOrder = async (id) => {
  await ElMessageBox.confirm('确定取消订单？', '提示')
  await orderApi.cancel(id)
  ElMessage.success('已取消')
  loadOrders()
}

const confirmOrder = async (id) => {
  await ElMessageBox.confirm('确认收货？', '提示')
  await orderApi.confirm(id)
  ElMessage.success('已确认收货')
  loadOrders()
}

const reviewOrder = (order) => {
  if (order.orderItems && order.orderItems.length > 0) {
    reviewForm.value.productId = order.orderItems[0].productId
    reviewForm.value.orderId = order.id
    reviewForm.value.rating = 5
    reviewForm.value.content = ''
    reviewVisible.value = true
  }
}

const submitReview = async () => {
  if (!reviewForm.value.content) {
    ElMessage.warning('请输入评价内容')
    return
  }
  try {
    await reviewApi.add(reviewForm.value)
    ElMessage.success('评价成功')
    reviewVisible.value = false
  } catch (e) { /* handled */ }
}

onMounted(loadOrders)
</script>

<style scoped>
.orders-page { padding-top: 30px; }
.order-card {
  background: white;
  border-radius: var(--radius);
  margin-bottom: 16px;
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}
.order-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 14px 20px;
  background: #faf5ff;
  border-bottom: 1px solid var(--border-color);
  font-size: 13px;
}
.order-no { font-weight: 500; }
.order-time { color: var(--text-secondary); margin-left: auto; }
.order-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  cursor: pointer;
  border-bottom: 1px solid #f9f0f9;
}
.order-item:hover { background: #fef7ff; }
.item-img {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
}
.item-info { flex: 1; display: flex; justify-content: space-between; }
.item-name { font-size: 14px; }
.item-qty { color: var(--text-secondary); }
.item-price { font-weight: 600; color: var(--primary-dark); }
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  background: #faf5ff;
}
.order-total { font-size: 14px; }
.order-total strong {
  font-size: 18px;
  color: var(--primary-dark);
}
.empty { padding: 60px 0; }
</style>
