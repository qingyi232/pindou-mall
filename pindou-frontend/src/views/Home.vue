<template>
  <div class="home">
    <!-- 轮播图 -->
    <div class="banner-section">
      <el-carousel height="400px" :interval="4000" arrow="hover">
        <el-carousel-item v-for="item in banners" :key="item.id">
          <div class="banner-item" :style="{ backgroundImage: `url(${item.image})` }">
            <div class="banner-overlay">
              <h2>{{ item.title }}</h2>
            </div>
          </div>
        </el-carousel-item>
        <el-carousel-item v-if="banners.length === 0">
          <div class="banner-item banner-default">
            <div class="banner-overlay">
              <h2>✨ 欢迎来到拼豆商城 ✨</h2>
              <p>二次元拼豆手工艺品 · 让创意拼出精彩</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <div class="page-container">
      <!-- 分类导航 -->
      <div class="category-section">
        <div class="section-title">商品分类</div>
        <div class="category-grid">
          <div v-for="cat in categories" :key="cat.id" class="category-card"
               @click="$router.push({ path: '/products', query: { categoryId: cat.id } })">
            <div class="category-icon">{{ cat.icon || '📦' }}</div>
            <div class="category-name">{{ cat.name }}</div>
          </div>
        </div>
      </div>

      <!-- 热门商品 -->
      <div class="product-section">
        <div class="section-title">🔥 热门推荐</div>
        <div class="product-grid">
          <div v-for="item in hotProducts" :key="item.id" class="product-card"
               @click="$router.push(`/product/${item.id}`)">
            <div class="product-image">
              <img :src="item.mainImage || 'https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=300&h=300&fit=crop'" :alt="item.name" />
              <div class="product-tags">
                <span v-if="item.isHot" class="tag hot">热门</span>
                <span v-if="item.isNew" class="tag new">新品</span>
              </div>
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ item.name }}</h3>
              <p class="product-desc">{{ item.ipSeries }} · {{ item.characterName }}</p>
              <div class="product-bottom">
                <div class="product-price">
                  <span class="price">¥{{ item.price }}</span>
                  <span v-if="item.originalPrice" class="original-price">¥{{ item.originalPrice }}</span>
                </div>
                <span class="sales">已售{{ item.sales }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 新品上线 -->
      <div class="product-section">
        <div class="section-title">🌟 新品上线</div>
        <div class="product-grid">
          <div v-for="item in newProducts" :key="item.id" class="product-card"
               @click="$router.push(`/product/${item.id}`)">
            <div class="product-image">
              <img :src="item.mainImage || 'https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=300&h=300&fit=crop'" :alt="item.name" />
              <div class="product-tags">
                <span v-if="item.isNew" class="tag new">新品</span>
              </div>
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ item.name }}</h3>
              <p class="product-desc">{{ item.ipSeries }} · {{ item.characterName }}</p>
              <div class="product-bottom">
                <div class="product-price">
                  <span class="price">¥{{ item.price }}</span>
                  <span v-if="item.originalPrice" class="original-price">¥{{ item.originalPrice }}</span>
                </div>
                <span class="sales">已售{{ item.sales }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { bannerApi, categoryApi, productApi } from '../api'

const banners = ref([])
const categories = ref([])
const hotProducts = ref([])
const newProducts = ref([])

onMounted(async () => {
  try {
    const [bannerRes, catRes, hotRes, newRes] = await Promise.all([
      bannerApi.list(),
      categoryApi.list(),
      productApi.hot(),
      productApi.newProducts()
    ])
    banners.value = bannerRes.data || []
    categories.value = catRes.data || []
    hotProducts.value = hotRes.data || []
    newProducts.value = newRes.data || []
  } catch (e) { /* ignore */ }
})
</script>

<style scoped>
.banner-section {
  margin-bottom: 40px;
}
.banner-item {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
}
.banner-default {
  background: linear-gradient(135deg, #ff6b9d, #a855f7, #06b6d4);
}
.banner-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 40px;
  background: linear-gradient(transparent, rgba(0,0,0,0.5));
  color: white;
}
.banner-overlay h2 {
  font-size: 32px;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
}
.banner-overlay p {
  font-size: 18px;
  margin-top: 8px;
  opacity: 0.9;
}

.category-section {
  margin-bottom: 50px;
}
.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 16px;
}
.category-card {
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid var(--border-color);
}
.category-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
  border-color: var(--primary-color);
}
.category-icon {
  font-size: 36px;
  margin-bottom: 8px;
}
.category-name {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
}

.product-section {
  margin-bottom: 50px;
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.product-card {
  background: var(--bg-card);
  border-radius: var(--radius);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid var(--border-color);
}
.product-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-lg);
}
.product-image {
  position: relative;
  padding-top: 100%;
  overflow: hidden;
}
.product-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.product-card:hover .product-image img {
  transform: scale(1.05);
}
.product-tags {
  position: absolute;
  top: 8px;
  left: 8px;
  display: flex;
  gap: 4px;
}
.tag {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 11px;
  color: white;
}
.tag.hot {
  background: linear-gradient(135deg, #ff6b6b, #ff4757);
}
.tag.new {
  background: linear-gradient(135deg, #a855f7, #7c3aed);
}
.product-info {
  padding: 14px;
}
.product-name {
  font-size: 14px;
  font-weight: 500;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 6px;
}
.product-desc {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 10px;
}
.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price {
  font-size: 18px;
  font-weight: 700;
  color: var(--primary-dark);
}
.original-price {
  font-size: 12px;
  color: #ccc;
  text-decoration: line-through;
  margin-left: 6px;
}
.sales {
  font-size: 12px;
  color: var(--text-secondary);
}

@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
