<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import { updatePassword, updateProfile } from '../api'
import { getErrorMessage } from '../utils/error'

const auth = useAuthStore()

const profileForm = reactive({ phone: auth.user?.phone || '', address: auth.user?.address || '' })
const passwordForm = reactive({ oldPassword: '', newPassword: '' })
const profileLoading = ref(false)
const passwordLoading = ref(false)

async function handleUpdateProfile() {
  profileLoading.value = true
  try {
    await updateProfile(profileForm)
    if (auth.user) {
      auth.user.phone = profileForm.phone
      auth.user.address = profileForm.address
    }
    ElMessage.success('更新成功')
  } catch (e) {
    ElMessage.error(getErrorMessage(e, '更新失败'))
  } finally {
    profileLoading.value = false
  }
}

async function handleUpdatePassword() {
  if (!passwordForm.oldPassword || !passwordForm.newPassword) {
    ElMessage.warning('请输入密码')
    return
  }
  passwordLoading.value = true
  try {
    await updatePassword(passwordForm)
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
  } catch (e) {
    ElMessage.error(getErrorMessage(e, '密码修改失败'))
  } finally {
    passwordLoading.value = false
  }
}
</script>

<template>
  <div class="profile">
    <header class="profile-header">
      <h1>个人中心</h1>
    </header>
    <el-card style="margin-bottom:16px">
      <template #header>个人信息</template>
      <el-form label-width="80px">
        <el-form-item label="用户名">
          <el-input :model-value="auth.user?.userName" disabled />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="profileForm.phone" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="profileForm.address" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="profileLoading" @click="handleUpdateProfile">更新</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <template #header>修改密码</template>
      <el-form label-width="80px">
        <el-form-item label="原密码">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="passwordLoading" @click="handleUpdatePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.profile {
  max-width: 900px;
  margin: 20px auto;
  padding: 0 20px;
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
</style>
