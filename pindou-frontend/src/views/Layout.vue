<template>
  <div class="layout">
    <header class="header">
      <div class="header-inner">
        <router-link to="/" class="logo">
          <span class="logo-icon">🎨</span>
          <span class="logo-text">拼豆商城</span>
        </router-link>

        <nav class="nav-menu">
          <router-link to="/" class="nav-item" :class="{ active: $route.path === '/' }">首页</router-link>
          <router-link to="/products" class="nav-item" :class="{ active: $route.path === '/products' }">全部商品</router-link>
          <router-link to="/cart" class="nav-item" :class="{ active: $route.path === '/cart' }">
            <el-badge :value="cartCount" :hidden="cartCount === 0" :max="99">购物车</el-badge>
          </router-link>
        </nav>

        <div class="header-right">
          <div class="search-box">
            <el-input v-model="searchKeyword" placeholder="搜索拼豆商品..." @keyup.enter="handleSearch" clearable>
              <template #prefix><el-icon><Search /></el-icon></template>
            </el-input>
          </div>

          <template v-if="userStore.isLogin">
            <el-dropdown @command="handleCommand">
              <div class="user-info">
                <el-avatar :size="32" :src="userStore.user?.avatar || ''">
                  {{ userStore.user?.nickname?.charAt(0) || 'U' }}
                </el-avatar>
                <span class="username">{{ userStore.user?.nickname }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                  <el-dropdown-item v-if="userStore.isAdmin" command="admin" divided>后台管理</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login">
              <el-button type="primary" round>登录</el-button>
            </router-link>
          </template>
        </div>
      </div>
    </header>

    <main class="main-content">
      <router-view />
    </main>

    <footer class="footer">
      <div class="footer-inner">
        <div class="footer-brand">
          <span class="logo-icon">🎨</span> 拼豆商城
        </div>
        <p class="footer-desc">二次元拼豆手工艺品商城 | 让创意拼出精彩</p>
        <p class="footer-copyright">© 2026 拼豆商城 All Rights Reserved</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { cartApi } from '../api'

const router = useRouter()
const userStore = useUserStore()
const searchKeyword = ref('')
const cartCount = ref(0)

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/products', query: { keyword: searchKeyword.value } })
  }
}

const handleCommand = (cmd) => {
  if (cmd === 'logout') {
    userStore.logout()
    router.push('/')
  } else if (cmd === 'admin') {
    router.push('/admin')
  } else {
    router.push('/' + cmd)
  }
}

const loadCartCount = async () => {
  if (userStore.isLogin) {
    try {
      const res = await cartApi.list()
      cartCount.value = res.data?.length || 0
    } catch (e) { /* ignore */ }
  }
}

onMounted(loadCartCount)
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid var(--border-color);
}
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 22px;
  font-weight: 700;
  cursor: pointer;
}
.logo-icon {
  font-size: 28px;
}
.logo-text {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.nav-menu {
  display: flex;
  gap: 32px;
}
.nav-item {
  font-size: 15px;
  color: var(--text-secondary);
  padding: 8px 0;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}
.nav-item:hover, .nav-item.active {
  color: var(--primary-color);
  border-bottom-color: var(--primary-color);
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.search-box {
  width: 220px;
}
.search-box .el-input {
  --el-input-border-radius: 20px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.username {
  font-size: 14px;
  color: var(--text-primary);
}
.main-content {
  flex: 1;
}
.footer {
  background: linear-gradient(135deg, #2d1b4e, #1a0e2e);
  color: #ccc;
  padding: 40px 20px;
  text-align: center;
  margin-top: 60px;
}
.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
}
.footer-brand {
  font-size: 24px;
  font-weight: 700;
  color: white;
  margin-bottom: 12px;
}
.footer-desc {
  font-size: 14px;
  margin-bottom: 8px;
  color: #aaa;
}
.footer-copyright {
  font-size: 12px;
  color: #888;
}
</style>
