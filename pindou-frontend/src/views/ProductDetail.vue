<template>
  <div class="page-container detail-page" v-if="product">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/products' }">全部商品</el-breadcrumb-item>
      <el-breadcrumb-item>{{ product.name }}</el-breadcrumb-item>
    </el-breadcrumb>

    <div class="detail-main">
      <div class="detail-left">
        <div class="main-image">
          <img :src="currentImage || product.mainImage || 'https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=400&h=400&fit=crop'" :alt="product.name" />
        </div>
        <div class="image-list" v-if="imageList.length > 1">
          <div v-for="(img, idx) in imageList" :key="idx" class="thumb"
               :class="{ active: currentImage === img }" @click="currentImage = img">
            <img :src="img" />
          </div>
        </div>
      </div>

      <div class="detail-right">
        <h1 class="product-title">{{ product.name }}</h1>
        <p class="product-desc">{{ product.description }}</p>

        <div class="product-meta">
          <span v-if="product.ipSeries" class="meta-tag">📺 {{ product.ipSeries }}</span>
          <span v-if="product.characterName" class="meta-tag">🎭 {{ product.characterName }}</span>
        </div>

        <div class="price-section">
          <span class="current-price">¥{{ product.price }}</span>
          <span v-if="product.originalPrice" class="origin-price">¥{{ product.originalPrice }}</span>
          <span v-if="product.originalPrice" class="discount">
            {{ Math.round((1 - product.price / product.originalPrice) * 100) }}% OFF
          </span>
        </div>

        <div class="info-row">
          <span>销量：<strong>{{ product.sales }}</strong></span>
          <span>库存：<strong>{{ product.stock }}</strong></span>
          <span>浏览：<strong>{{ product.viewCount }}</strong></span>
        </div>

        <div class="quantity-section">
          <span class="label">数量</span>
          <el-input-number v-model="quantity" :min="1" :max="product.stock" size="large" />
        </div>

        <div class="action-buttons">
          <el-button type="primary" size="large" @click="addToCart" :disabled="product.stock === 0">
            <el-icon><ShoppingCart /></el-icon> 加入购物车
          </el-button>
          <el-button size="large" @click="buyNow" :disabled="product.stock === 0" type="warning">
            立即购买
          </el-button>
        </div>
      </div>
    </div>

    <!-- 商品详情 -->
    <div class="detail-tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="商品详情" name="detail">
          <div class="rich-content" v-html="product.detail || '<p>暂无详情</p>'"></div>
        </el-tab-pane>
        <el-tab-pane :label="`商品评价(${reviews.length})`" name="reviews">
          <div v-if="reviews.length === 0" class="empty-reviews">
            <el-empty description="暂无评价" />
          </div>
          <div v-else class="review-list">
            <div v-for="r in reviews" :key="r.id" class="review-item">
              <div class="review-header">
                <el-avatar :size="36">{{ r.username?.charAt(0) || 'U' }}</el-avatar>
                <div class="review-user">
                  <span class="review-name">{{ r.username || '匿名用户' }}</span>
                  <el-rate v-model="r.rating" disabled size="small" />
                </div>
                <span class="review-time">{{ r.createTime }}</span>
              </div>
              <p class="review-content">{{ r.content }}</p>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { productApi, cartApi, reviewApi } from '../api'
import { useUserStore } from '../store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const product = ref(null)
const quantity = ref(1)
const currentImage = ref('')
const activeTab = ref('detail')
const reviews = ref([])

const imageList = computed(() => {
  if (!product.value) return []
  const list = [product.value.mainImage]
  if (product.value.images) {
    list.push(...product.value.images.split(',').filter(Boolean))
  }
  return list
})

const addToCart = async () => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await cartApi.add({ productId: product.value.id, quantity: quantity.value })
    ElMessage.success('已加入购物车')
  } catch (e) { /* handled */ }
}

const buyNow = async () => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  await addToCart()
  router.push('/cart')
}

onMounted(async () => {
  try {
    const res = await productApi.detail(route.params.id)
    product.value = res.data
    currentImage.value = res.data.mainImage
  } catch (e) { /* handled */ }

  try {
    const revRes = await reviewApi.list(route.params.id, { page: 1, size: 20 })
    reviews.value = revRes.data?.records || []
  } catch (e) { /* ignore */ }
})
</script>

<style scoped>
.detail-page { padding-top: 20px; }
.el-breadcrumb { margin-bottom: 20px; }
.detail-main {
  display: flex;
  gap: 40px;
  background: white;
  border-radius: var(--radius-lg);
  padding: 30px;
  box-shadow: var(--shadow-sm);
  margin-bottom: 30px;
}
.detail-left { width: 450px; flex-shrink: 0; }
.main-image {
  width: 100%;
  border-radius: var(--radius);
  overflow: hidden;
  background: #f8f0f8;
}
.main-image img {
  width: 100%;
  height: 450px;
  object-fit: cover;
}
.image-list {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}
.thumb {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid transparent;
  cursor: pointer;
}
.thumb.active { border-color: var(--primary-color); }
.thumb img { width: 100%; height: 100%; object-fit: cover; }

.detail-right { flex: 1; }
.product-title {
  font-size: 22px;
  font-weight: 700;
  line-height: 1.4;
  margin-bottom: 10px;
}
.product-desc {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 16px;
  line-height: 1.6;
}
.product-meta {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}
.meta-tag {
  padding: 4px 12px;
  background: #f8f0ff;
  border-radius: 16px;
  font-size: 13px;
  color: var(--secondary-color);
}
.price-section {
  background: linear-gradient(135deg, #fff5f8, #fef0ff);
  padding: 20px;
  border-radius: var(--radius);
  margin-bottom: 20px;
}
.current-price {
  font-size: 32px;
  font-weight: 700;
  color: var(--primary-dark);
}
.origin-price {
  font-size: 16px;
  color: #ccc;
  text-decoration: line-through;
  margin-left: 12px;
}
.discount {
  margin-left: 12px;
  padding: 2px 8px;
  background: var(--primary-color);
  color: white;
  border-radius: 4px;
  font-size: 12px;
}
.info-row {
  display: flex;
  gap: 24px;
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 20px;
}
.info-row strong { color: var(--text-primary); }
.quantity-section {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 30px;
}
.quantity-section .label {
  font-size: 14px;
  color: var(--text-secondary);
}
.action-buttons {
  display: flex;
  gap: 16px;
}
.action-buttons .el-button {
  padding: 12px 32px;
  border-radius: 10px;
  font-size: 16px;
}

.detail-tabs {
  background: white;
  border-radius: var(--radius-lg);
  padding: 30px;
  box-shadow: var(--shadow-sm);
}
.rich-content { line-height: 1.8; }
.rich-content :deep(img) { max-width: 100%; border-radius: 8px; }

.review-item {
  padding: 16px 0;
  border-bottom: 1px solid var(--border-color);
}
.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}
.review-name { font-weight: 500; font-size: 14px; }
.review-time { margin-left: auto; font-size: 12px; color: #999; }
.review-content { font-size: 14px; line-height: 1.6; color: var(--text-secondary); }
</style>
