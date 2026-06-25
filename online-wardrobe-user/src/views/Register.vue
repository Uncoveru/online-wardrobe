<!-- 用户注册页（注册成功后自动登录） -->
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { register, login } from '../api'
import { useAuthStore } from '../stores/auth'
import { getErrorMessage } from '../utils/error'

const router = useRouter()
const auth = useAuthStore()

const formRef = ref<FormInstance>()
const form = reactive({ userName: '', password: '', confirmPassword: '', phone: '', address: '' })
const loading = ref(false)

// 确认密码校验
const validateConfirmPassword = (_rule: any, value: string, callback: (error?: Error) => void) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次密码不一致'))
  } else {
    callback()
  }
}

// 手机号校验
const validatePhone = (_rule: any, value: string, callback: (error?: Error) => void) => {
  if (value && !/^\d{11}$/.test(value)) {
    callback(new Error('手机号格式不正确'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度为 2-20 位', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' },
  ],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }],
  phone: [{ validator: validatePhone, trigger: 'blur' }],
}

// 注册 → 自动登录 → 跳转首页
async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const regRes = await register(form)
    if (regRes.data.code === 200) {
      const loginRes = await login(form.userName, form.password)
      if (loginRes.data.code === 200) {
        auth.setUser(loginRes.data.data.user)
        ElMessage.success('注册成功')
        router.push('/')
      } else {
        ElMessage.warning('注册成功，请登录')
        router.push('/login')
      }
    } else {
      ElMessage.error(regRes.data.message || '注册失败')
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
      <template #header><h2>用户注册</h2></template>
      <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleRegister" label-width="0">
        <el-form-item prop="userName">
          <el-input v-model="form.userName" placeholder="用户名" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" size="large" show-password />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.address" placeholder="地址" size="large" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleRegister" style="width:100%">
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <div class="footer">
        <el-button type="primary" link @click="router.push('/login')">已有账号？返回登录</el-button>
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
