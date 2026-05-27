<template>
  <div class="login-page">
    <img class="deco deco-1" src="https://images.unsplash.com/photo-1566576912321-d58ddd7a6088?w=200&h=200&fit=crop" />
    <img class="deco deco-2" src="https://images.unsplash.com/photo-1551103782-8ab07afd45c1?w=180&h=180&fit=crop" />
    <img class="deco deco-3" src="https://images.unsplash.com/photo-1608889825103-eb5ed706fc64?w=160&h=160&fit=crop" />
    <img class="deco deco-4" src="https://images.unsplash.com/photo-1613771404784-3a5686aa2be3?w=150&h=150&fit=crop" />
    <div class="login-container">
      <div class="login-left">
        <div class="brand-area">
          <h1>🎨 拼豆商城</h1>
          <p>二次元拼豆手工艺品商城</p>
          <p class="sub">让创意拼出精彩 ✨</p>
        </div>
      </div>
      <div class="login-right">
        <div class="login-form-wrapper">
          <h2>欢迎登录</h2>
          <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password @keyup.enter="handleLogin" />
            </el-form-item>
            <el-button type="primary" size="large" :loading="loading" @click="handleLogin" style="width:100%;border-radius:10px;">
              登 录
            </el-button>
          </el-form>
          <div class="login-footer">
            还没有账号？<router-link to="/register" class="link">立即注册</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userApi } from '../api'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await userApi.login(form)
    userStore.setLogin(res.data.token, res.data.user)
    ElMessage.success('登录成功')
    if (res.data.user.role === 'ADMIN') {
      router.push('/admin')
    } else {
      router.push('/')
    }
  } catch (e) { /* handled by interceptor */ }
  finally { loading.value = false }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fef7ff 0%, #f0e6ff 50%, #ffe0f0 100%);
  position: relative;
  overflow: hidden;
}
.deco {
  position: absolute;
  border-radius: 16px;
  opacity: 0.2;
  pointer-events: none;
  object-fit: cover;
  filter: blur(1px);
}
.deco-1 {
  top: 40px;
  left: 60px;
  width: 160px;
  height: 160px;
  transform: rotate(-10deg);
}
.deco-2 {
  bottom: 50px;
  right: 80px;
  width: 140px;
  height: 140px;
  transform: rotate(8deg);
}
.deco-3 {
  top: 60px;
  right: 120px;
  width: 120px;
  height: 120px;
  transform: rotate(-5deg);
}
.deco-4 {
  bottom: 80px;
  left: 100px;
  width: 130px;
  height: 130px;
  transform: rotate(12deg);
}
.login-page::before {
  content: '🎨 🎮 🧩 ✨ 🎭 🌸 🎯 💎 🦊 🐱 ⭐ 🎪 🎨 🧩 ✨ 🎮 🎭 🌸';
  position: absolute;
  top: 30px;
  left: 0;
  right: 0;
  font-size: 32px;
  text-align: center;
  letter-spacing: 20px;
  opacity: 0.15;
  pointer-events: none;
}
.login-page::after {
  content: '💜 🎀 🔮 🌈 🎵 🍭 🦄 💫 🎪 🧸 🌟 💖 🎀 🔮 🌈 🎵 💜 🍭';
  position: absolute;
  bottom: 30px;
  left: 0;
  right: 0;
  font-size: 32px;
  text-align: center;
  letter-spacing: 20px;
  opacity: 0.15;
  pointer-events: none;
}
.login-container {
  display: flex;
  width: 800px;
  min-height: 500px;
  background: white;
  border-radius: 20px;
  box-shadow: var(--shadow-lg);
  overflow: hidden;
}
.login-left {
  flex: 1;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}
.brand-area {
  text-align: center;
  padding: 40px;
}
.brand-area h1 {
  font-size: 32px;
  margin-bottom: 12px;
}
.brand-area p {
  font-size: 16px;
  opacity: 0.9;
}
.brand-area .sub {
  margin-top: 8px;
  font-size: 14px;
  opacity: 0.7;
}
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}
.login-form-wrapper {
  width: 100%;
  max-width: 320px;
}
.login-form-wrapper h2 {
  font-size: 24px;
  color: var(--text-primary);
  margin-bottom: 30px;
}
.login-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: var(--text-secondary);
}
.link {
  color: var(--primary-color);
  font-weight: 500;
}
</style>
