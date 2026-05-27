<template>
  <div class="page-container profile-page">
    <h2 class="section-title">👤 个人中心</h2>

    <div class="profile-card">
      <div class="profile-header">
        <el-avatar :size="80" :src="userStore.user?.avatar || ''">
          {{ userStore.user?.nickname?.charAt(0) || 'U' }}
        </el-avatar>
        <div class="profile-info">
          <h3>{{ userStore.user?.nickname }}</h3>
          <p>@{{ userStore.user?.username }}</p>
        </div>
      </div>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="info">
          <el-form :model="form" label-width="80px" style="max-width: 500px;">
            <el-form-item label="昵称">
              <el-input v-model="form.nickname" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateInfo">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="修改密码" name="password">
          <el-form :model="pwdForm" label-width="100px" style="max-width: 500px;">
            <el-form-item label="旧密码">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="pwdForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认新密码">
              <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updatePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="收货地址" name="address">
          <el-button type="primary" size="small" @click="showAddressDialog()" style="margin-bottom: 16px;">
            添加地址
          </el-button>
          <div v-for="addr in addresses" :key="addr.id" class="address-item">
            <div class="addr-info">
              <span class="addr-name">{{ addr.receiver }}</span>
              <span class="addr-phone">{{ addr.phone }}</span>
              <el-tag v-if="addr.isDefault" type="success" size="small">默认</el-tag>
            </div>
            <p class="addr-detail">{{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detail }}</p>
            <div class="addr-actions">
              <el-button link size="small" @click="showAddressDialog(addr)">编辑</el-button>
              <el-button link size="small" type="danger" @click="deleteAddress(addr.id)">删除</el-button>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <el-dialog v-model="addrVisible" :title="addrForm.id ? '编辑地址' : '新增地址'" width="500px">
      <el-form :model="addrForm" label-width="80px">
        <el-form-item label="收货人">
          <el-input v-model="addrForm.receiver" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="addrForm.phone" />
        </el-form-item>
        <el-form-item label="省">
          <el-input v-model="addrForm.province" />
        </el-form-item>
        <el-form-item label="市">
          <el-input v-model="addrForm.city" />
        </el-form-item>
        <el-form-item label="区">
          <el-input v-model="addrForm.district" />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input v-model="addrForm.detail" type="textarea" />
        </el-form-item>
        <el-form-item label="默认地址">
          <el-switch v-model="addrForm.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addrVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../store/user'
import { userApi, addressApi } from '../api'

const userStore = useUserStore()
const activeTab = ref('info')
const addresses = ref([])
const addrVisible = ref(false)

const form = reactive({
  nickname: userStore.user?.nickname || '',
  phone: userStore.user?.phone || '',
  email: userStore.user?.email || ''
})

const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const addrForm = ref({ receiver: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: 0 })

const updateInfo = async () => {
  try {
    await userApi.update(form)
    const res = await userApi.getInfo()
    userStore.updateUser(res.data)
    ElMessage.success('修改成功')
  } catch (e) { /* handled */ }
}

const updatePassword = async () => {
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    ElMessage.error('两次密码不一致')
    return
  }
  try {
    await userApi.updatePassword({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    ElMessage.success('密码修改成功')
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
  } catch (e) { /* handled */ }
}

const loadAddresses = async () => {
  try {
    const res = await addressApi.list()
    addresses.value = res.data || []
  } catch (e) { /* ignore */ }
}

const showAddressDialog = (addr) => {
  if (addr) {
    addrForm.value = { ...addr }
  } else {
    addrForm.value = { receiver: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: 0 }
  }
  addrVisible.value = true
}

const saveAddress = async () => {
  try {
    if (addrForm.value.id) {
      await addressApi.update(addrForm.value)
    } else {
      await addressApi.add(addrForm.value)
    }
    ElMessage.success('保存成功')
    addrVisible.value = false
    loadAddresses()
  } catch (e) { /* handled */ }
}

const deleteAddress = async (id) => {
  await ElMessageBox.confirm('确定删除该地址？', '提示')
  await addressApi.delete(id)
  ElMessage.success('已删除')
  loadAddresses()
}

onMounted(loadAddresses)
</script>

<style scoped>
.profile-page { padding-top: 30px; }
.profile-card {
  background: white;
  border-radius: var(--radius-lg);
  padding: 30px;
  box-shadow: var(--shadow-sm);
}
.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--border-color);
}
.profile-info h3 { font-size: 20px; }
.profile-info p { color: var(--text-secondary); font-size: 14px; }
.address-item {
  padding: 14px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius);
  margin-bottom: 12px;
}
.addr-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}
.addr-name { font-weight: 600; }
.addr-phone { color: var(--text-secondary); font-size: 14px; }
.addr-detail { font-size: 14px; color: var(--text-secondary); margin-bottom: 8px; }
</style>
