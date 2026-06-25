<!-- 运营人员注册申请页（提交后需超管审核） -->
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { registerOperator } from '../api'

const router = useRouter()

const formRef = ref()
const loading = ref(false)

const form = reactive({
    userName: '',
    password: '',
    confirmPassword: '',
    phone: '',
    address: '',
})

// 密码校验：至少6位 + 字母 + 数字
const validatePassword = (_rule: any, value: string, callback: any) => {
    if (!value) {
        callback(new Error('请输入密码'))
        return
    }
    if (value.length < 6) {
        callback(new Error('密码至少6位'))
        return
    }
    if (!/[a-zA-Z]/.test(value) || !/\d/.test(value)) {
        callback(new Error('密码必须同时包含字母和数字'))
        return
    }
    callback()
}

// 确认密码校验
const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
    if (!value) {
        callback(new Error('请确认密码'))
        return
    }
    if (value !== form.password) {
        callback(new Error('两次密码输入不一致'))
        return
    }
    callback()
}

// 手机号校验
const validatePhone = (_rule: any, value: string, callback: any) => {
    if (!value) {
        callback(new Error('请输入手机号'))
        return
    }
    if (!/^\d{11}$/.test(value)) {
        callback(new Error('请输入11位手机号'))
        return
    }
    callback()
}

const rules = {
    userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, validator: validatePassword, trigger: 'blur' }],
    confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }],
    phone: [{ required: true, validator: validatePhone, trigger: 'blur' }],
    address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
}

// 提交注册申请
async function handleRegister() {
    const valid = await formRef.value?.validate().catch(() => false)
    if (!valid) return
    loading.value = true
    try {
        const res = await registerOperator({
            userName: form.userName,
            password: form.password,
            phone: form.phone,
            address: form.address,
        })
        if (res.data.code === 200) {
            ElMessage.success('注册申请已提交，请等待超级管理员审核')
            router.push('/login')
        } else {
            ElMessage.error(res.data.message || '注册失败')
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
                <h2>运营人员注册</h2>
            </template>
            <el-form ref="formRef" :model="form" :rules="rules" label-width="0" @submit.prevent="handleRegister">
                <el-form-item prop="userName">
                    <el-input v-model="form.userName" placeholder="用户名" size="large" />
                </el-form-item>
                <el-form-item prop="password">
                    <el-input v-model="form.password" type="password" placeholder="密码（至少6位，含字母和数字）" size="large" show-password />
                </el-form-item>
                <el-form-item prop="confirmPassword">
                    <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" size="large" show-password />
                </el-form-item>
                <el-form-item prop="phone">
                    <el-input v-model="form.phone" placeholder="手机号（11位）" size="large" />
                </el-form-item>
                <el-form-item prop="address">
                    <el-input v-model="form.address" placeholder="地址" size="large" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" size="large" :loading="loading" @click="handleRegister" style="width:100%">
                        提交注册
                    </el-button>
                </el-form-item>
                <el-form-item class="auth-footer">
                    <router-link to="/login">已有账号？返回登录</router-link>
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
