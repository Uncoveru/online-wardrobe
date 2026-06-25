<!-- 用户登录页 -->
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { login } from '../api'
import { useAuthStore } from '../stores/auth'
import { getErrorMessage } from '../utils/error'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const formRef = ref<FormInstance>()
const form = reactive({ account: '', password: '' })
const loading = ref(false)

const rules: FormRules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' },
  ],
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login(form.account, form.password)
    if (res.data.code === 200) {
      auth.setUser(res.data.data.user)
      ElMessage.success('登录成功')
      // 登录后回到之前的页面
      const redirect = route.query.redirect as string | undefined
      router.push(redirect || '/')
    } else {
      ElMessage.error(res.data.message || '登录失败')
    }
  } catch (e) {
    ElMessage.error(getErrorMessage(e, '网络错误'))
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <template #header><h2>用户登录</h2></template>
      <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleLogin" label-width="0">
        <el-form-item prop="account">
          <el-input v-model="form.account" placeholder="用户名/手机号" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleLogin" style="width:100%">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="footer">
        <el-button type="primary" link @click="router.push('/register')">还没有账号？立即注册</el-button>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, var(--el-color-primary-light-5), var(--el-color-primary-dark-2));
}

.auth-card {
  width: min(400px, 90vw);
}

.auth-card h2 {
  text-align: center;
  margin: 0;
}

.footer {
  text-align: center;
}
</style>
