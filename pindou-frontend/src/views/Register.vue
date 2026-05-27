<template>
  <div class="login-page">
    <img class="deco deco-1" src="https://images.unsplash.com/photo-1612287230202-1ff1d85d1bdf?w=200&h=200&fit=crop" />
    <img class="deco deco-2" src="https://images.unsplash.com/photo-1560343776-97e7d202ff0e?w=180&h=180&fit=crop" />
    <img class="deco deco-3" src="https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=160&h=160&fit=crop" />
    <img class="deco deco-4" src="https://images.unsplash.com/photo-1615486511484-92e172cc4fe0?w=150&h=150&fit=crop" />
    <div class="login-container">
      <div class="login-left">
        <div class="brand-area">
          <h1>🎨 拼豆商城</h1>
          <p>加入拼豆大家庭</p>
          <p class="sub">开启你的创意之旅 🎮</p>
        </div>
      </div>
      <div class="login-right">
        <div class="login-form-wrapper">
          <h2>注册账号</h2>
          <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请输入昵称" prefix-icon="UserFilled" size="large" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" prefix-icon="Lock" size="large" show-password />
            </el-form-item>
            <el-button type="primary" size="large" :loading="loading" @click="handleRegister" style="width:100%;border-radius:10px;">
              注 册
            </el-button>
          </el-form>
          <div class="login-footer">
            已有账号？<router-link to="/login" class="link">立即登录</router-link>
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

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ username: '', nickname: '', password: '', confirmPassword: '' })

const validateConfirm = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' },
             { min: 3, max: 20, message: '用户名长度3-20', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' },
             { min: 6, message: '密码至少6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' },
                    { validator: validateConfirm, trigger: 'blur' }]
}

const handleRegister = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await userApi.register({ username: form.username, password: form.password, nickname: form.nickname })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) { /* handled */ }
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
  min-height: 560px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(255, 107, 157, 0.2);
  overflow: hidden;
}
.login-left {
  flex: 1;
  background: linear-gradient(135deg, #a855f7, #ff6b9d);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}
.brand-area { text-align: center; padding: 40px; }
.brand-area h1 { font-size: 32px; margin-bottom: 12px; }
.brand-area p { font-size: 16px; opacity: 0.9; }
.brand-area .sub { margin-top: 8px; font-size: 14px; opacity: 0.7; }
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}
.login-form-wrapper { width: 100%; max-width: 320px; }
.login-form-wrapper h2 { font-size: 24px; color: #2d1b4e; margin-bottom: 24px; }
.login-footer { margin-top: 20px; text-align: center; font-size: 14px; color: #6b5b7b; }
.link { color: #ff6b9d; font-weight: 500; }
</style>
