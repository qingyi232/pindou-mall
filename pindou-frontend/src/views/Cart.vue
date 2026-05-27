<template>
  <div class="page-container cart-page">
    <h2 class="section-title">🛒 我的购物车</h2>

    <div v-if="cartList.length === 0" class="empty-cart">
      <el-empty description="购物车是空的">
        <el-button type="primary" @click="$router.push('/products')">去逛逛</el-button>
      </el-empty>
    </div>

    <template v-else>
      <div class="cart-list">
        <div v-for="item in cartList" :key="item.id" class="cart-item">
          <el-checkbox v-model="item.checked" :true-value="1" :false-value="0" @change="updateItem(item)" />
          <div class="item-image" @click="$router.push(`/product/${item.productId}`)">
            <img :src="item.productImage || 'https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=200&h=200&fit=crop'" />
          </div>
          <div class="item-info">
            <h3 @click="$router.push(`/product/${item.productId}`)">{{ item.productName }}</h3>
            <p class="item-price">¥{{ item.productPrice }}</p>
          </div>
          <div class="item-quantity">
            <el-input-number v-model="item.quantity" :min="1" :max="item.productStock" size="small" @change="updateItem(item)" />
          </div>
          <div class="item-subtotal">
            ¥{{ (item.productPrice * item.quantity).toFixed(2) }}
          </div>
          <el-button type="danger" link @click="removeItem(item.id)">
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>

      <div class="cart-footer">
        <div class="footer-left">
          <el-checkbox v-model="allChecked" @change="toggleAll">全选</el-checkbox>
          <el-button type="danger" link @click="clearCart">清空购物车</el-button>
        </div>
        <div class="footer-right">
          <span class="total-info">
            已选 <strong>{{ checkedCount }}</strong> 件，合计：
            <span class="total-price">¥{{ totalPrice }}</span>
          </span>
          <el-button type="primary" size="large" :disabled="checkedCount === 0" @click="goCheckout">
            去结算
          </el-button>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cartApi } from '../api'

const router = useRouter()
const cartList = ref([])

const allChecked = computed({
  get: () => cartList.value.length > 0 && cartList.value.every(i => i.checked === 1),
  set: () => {}
})

const checkedCount = computed(() => cartList.value.filter(i => i.checked === 1).reduce((s, i) => s + i.quantity, 0))
const totalPrice = computed(() => cartList.value.filter(i => i.checked === 1).reduce((s, i) => s + i.productPrice * i.quantity, 0).toFixed(2))

const loadCart = async () => {
  try {
    const res = await cartApi.list()
    cartList.value = res.data || []
  } catch (e) { /* ignore */ }
}

const updateItem = async (item) => {
  try {
    await cartApi.update({ id: item.id, quantity: item.quantity, checked: item.checked })
  } catch (e) { /* ignore */ }
}

const removeItem = async (id) => {
  await ElMessageBox.confirm('确定删除该商品吗？', '提示')
  await cartApi.delete(id)
  ElMessage.success('已删除')
  loadCart()
}

const clearCart = async () => {
  await ElMessageBox.confirm('确定清空购物车吗？', '提示')
  await cartApi.clear()
  ElMessage.success('已清空')
  loadCart()
}

const toggleAll = (val) => {
  cartList.value.forEach(i => {
    i.checked = val ? 1 : 0
    updateItem(i)
  })
}

const goCheckout = () => router.push('/checkout')

onMounted(loadCart)
</script>

<style scoped>
.cart-page { padding-top: 30px; }
.cart-list {
  background: white;
  border-radius: var(--radius-lg);
  padding: 20px;
  box-shadow: var(--shadow-sm);
}
.cart-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid var(--border-color);
}
.cart-item:last-child { border-bottom: none; }
.item-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  flex-shrink: 0;
}
.item-image img { width: 100%; height: 100%; object-fit: cover; }
.item-info {
  flex: 1;
}
.item-info h3 {
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  margin-bottom: 8px;
}
.item-info h3:hover { color: var(--primary-color); }
.item-price {
  font-size: 16px;
  font-weight: 600;
  color: var(--primary-dark);
}
.item-subtotal {
  font-size: 16px;
  font-weight: 700;
  color: var(--primary-dark);
  min-width: 80px;
  text-align: right;
}
.cart-footer {
  margin-top: 20px;
  background: white;
  border-radius: var(--radius);
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--shadow-sm);
}
.footer-left { display: flex; align-items: center; gap: 16px; }
.footer-right { display: flex; align-items: center; gap: 20px; }
.total-info { font-size: 14px; }
.total-price {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary-dark);
}
.empty-cart { padding: 60px 0; }
</style>
