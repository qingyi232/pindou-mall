<template>
  <div class="page-container checkout-page">
    <h2 class="section-title">📦 确认订单</h2>

    <div class="checkout-section">
      <h3>收货信息</h3>
      <el-form :model="orderForm" label-width="80px">
        <el-form-item label="收货人">
          <el-input v-model="orderForm.receiver" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="orderForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="收货地址">
          <el-input v-model="orderForm.address" placeholder="请输入收货地址" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="orderForm.remark" placeholder="选填" />
        </el-form-item>
      </el-form>
    </div>

    <div class="checkout-section">
      <h3>商品清单</h3>
      <div v-for="item in checkedItems" :key="item.id" class="order-item">
        <img :src="item.productImage || 'https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=200&h=200&fit=crop'" class="order-image" />
        <div class="order-info">
          <span class="order-name">{{ item.productName }}</span>
          <span class="order-qty">x{{ item.quantity }}</span>
        </div>
        <span class="order-price">¥{{ (item.productPrice * item.quantity).toFixed(2) }}</span>
      </div>
    </div>

    <div class="checkout-footer">
      <div class="total">
        合计：<span class="total-price">¥{{ totalPrice }}</span>
      </div>
      <el-button type="primary" size="large" @click="submitOrder" :loading="loading">
        提交订单
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { cartApi, orderApi } from '../api'

const router = useRouter()
const loading = ref(false)
const checkedItems = ref([])

const orderForm = ref({
  receiver: '',
  phone: '',
  address: '',
  remark: ''
})

const totalPrice = computed(() =>
  checkedItems.value.reduce((s, i) => s + i.productPrice * i.quantity, 0).toFixed(2)
)

const submitOrder = async () => {
  if (!orderForm.value.receiver || !orderForm.value.phone || !orderForm.value.address) {
    ElMessage.warning('请填写完整的收货信息')
    return
  }
  loading.value = true
  try {
    const res = await orderApi.create(orderForm.value)
    ElMessage.success('下单成功')
    router.push('/orders')
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

onMounted(async () => {
  try {
    const res = await cartApi.list()
    checkedItems.value = (res.data || []).filter(i => i.checked === 1)
    if (checkedItems.value.length === 0) {
      ElMessage.warning('请先选择商品')
      router.push('/cart')
    }
  } catch (e) { router.push('/cart') }
})
</script>

<style scoped>
.checkout-page { padding-top: 30px; max-width: 800px; }
.checkout-section {
  background: white;
  border-radius: var(--radius);
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: var(--shadow-sm);
}
.checkout-section h3 {
  font-size: 16px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-color);
}
.order-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}
.order-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
}
.order-info {
  flex: 1;
  display: flex;
  justify-content: space-between;
}
.order-name { font-size: 14px; }
.order-qty { color: var(--text-secondary); font-size: 14px; }
.order-price {
  font-weight: 600;
  color: var(--primary-dark);
  min-width: 80px;
  text-align: right;
}
.checkout-footer {
  background: white;
  border-radius: var(--radius);
  padding: 20px 24px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 20px;
  box-shadow: var(--shadow-sm);
}
.total-price {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary-dark);
}
</style>
