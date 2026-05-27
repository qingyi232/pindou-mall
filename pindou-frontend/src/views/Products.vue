<template>
  <div class="page-container products-page">
    <div class="filter-bar">
      <div class="category-tabs">
        <span class="tab" :class="{ active: !currentCategory }" @click="currentCategory = null; loadProducts()">全部</span>
        <span v-for="cat in categories" :key="cat.id" class="tab"
              :class="{ active: currentCategory === cat.id }"
              @click="currentCategory = cat.id; loadProducts()">
          {{ cat.icon }} {{ cat.name }}
        </span>
      </div>
    </div>

    <div class="product-grid">
      <div v-for="item in products" :key="item.id" class="product-card"
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

    <div v-if="products.length === 0" class="empty-state">
      <el-empty description="暂无商品" />
    </div>

    <div class="pagination-wrap">
      <el-pagination background layout="prev, pager, next" :total="total"
                     :page-size="pageSize" v-model:current-page="currentPage" @current-change="loadProducts" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { productApi, categoryApi } from '../api'

const route = useRoute()
const categories = ref([])
const products = ref([])
const currentCategory = ref(null)
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const loadProducts = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    if (currentCategory.value) params.categoryId = currentCategory.value
    if (route.query.keyword) params.keyword = route.query.keyword
    const res = await productApi.list(params)
    products.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { /* ignore */ }
}

onMounted(async () => {
  try {
    const catRes = await categoryApi.list()
    categories.value = catRes.data || []
  } catch (e) { /* ignore */ }

  if (route.query.categoryId) {
    currentCategory.value = Number(route.query.categoryId)
  }
  loadProducts()
})

watch(() => route.query, () => {
  if (route.query.categoryId) {
    currentCategory.value = Number(route.query.categoryId)
  }
  currentPage.value = 1
  loadProducts()
})
</script>

<style scoped>
.products-page { padding-top: 30px; }
.filter-bar {
  margin-bottom: 24px;
  background: white;
  border-radius: var(--radius);
  padding: 16px;
  box-shadow: var(--shadow-sm);
}
.category-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.tab {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
  background: #f8f0f8;
  color: var(--text-secondary);
}
.tab:hover, .tab.active {
  background: var(--primary-color);
  color: white;
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.product-card {
  background: white;
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
  top: 0; left: 0;
  width: 100%; height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.product-card:hover .product-image img {
  transform: scale(1.05);
}
.product-tags {
  position: absolute;
  top: 8px; left: 8px;
  display: flex;
  gap: 4px;
}
.tag {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 11px;
  color: white;
}
.tag.hot { background: linear-gradient(135deg, #ff6b6b, #ff4757); }
.tag.new { background: linear-gradient(135deg, #a855f7, #7c3aed); }
.product-info { padding: 14px; }
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
  color: #e84d80;
}
.original-price {
  font-size: 12px;
  color: #ccc;
  text-decoration: line-through;
  margin-left: 6px;
}
.sales { font-size: 12px; color: var(--text-secondary); }
.pagination-wrap {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
.empty-state {
  padding: 60px 0;
}

@media (max-width: 768px) {
  .product-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
