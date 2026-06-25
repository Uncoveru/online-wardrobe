<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const formRef = ref()
const loading = ref(false)

const form = reactive({
    account: '',
    password: '',
})

const rules = {
    account: [{ required: true, message: '请输入用户名或手机号', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
    const valid = await formRef.value?.validate().catch(() => false)
    if (!valid) return
    loading.value = true
    try {
        const res = await login(form)
        if (res.data.code === 200) {
            auth.setUser(res.data.data.user)
            ElMessage.success('登录成功')
            router.push('/dashboard')
        } else {
            ElMessage.error(res.data.message || '登录失败')
        }
    } catch (err: any) {
        const msg = err?.response?.data?.message || err?.message || '网络错误，请稍后重试'
        ElMessage.error(msg)
    } finally {
        loading.value = false
    }
}
</script>

<template>
    <div class="auth-container">
        <el-card class="auth-card" shadow="always">
            <template #header>
                <h2>网上衣橱 - 后台管理</h2>
            </template>
            <el-form ref="formRef" :model="form" :rules="rules" label-width="0" @submit.prevent="handleLogin">
                <el-form-item prop="account">
                    <el-input v-model="form.account" placeholder="用户名或手机号" size="large" />
                </el-form-item>
                <el-form-item prop="password">
                    <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" size="large" :loading="loading" @click="handleLogin" style="width:100%">
                        登录
                    </el-button>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" link style="width:100%">
                        <router-link to="/register">注册运营人员账号</router-link>
                    </el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<style scoped>
.auth-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.auth-card {
    width: 420px;
}
.auth-card h2 {
    text-align: center;
    margin: 0;
    color: #303133;
    font-size: 18px;
}
.auth-footer {
    text-align: center;
}
</style>
