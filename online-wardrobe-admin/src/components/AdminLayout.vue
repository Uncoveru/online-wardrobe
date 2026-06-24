<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { DataAnalysis, Goods, Document, User } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

function handleLogout() {
    auth.logout()
    router.push('/login')
}
</script>

<template>
    <el-container class="admin-layout">
        <el-header class="admin-header">
            <span class="logo">网上衣橱 - 后台管理</span>
            <div class="header-right">
                <span>欢迎，{{ auth.user?.userName }}</span>
                <el-tag
                    :type="auth.user?.role === 1 ? 'danger' : 'warning'"
                    size="small"
                    effect="dark"
                >
                    {{ auth.user?.role === 1 ? '超级管理员' : '运营人员' }}
                </el-tag>
                <el-button text @click="handleLogout">退出登录</el-button>
            </div>
        </el-header>
        <el-container>
            <el-aside width="200px" class="admin-aside">
                <el-menu :default-active="route.path" router>
                    <el-menu-item index="/dashboard">
                        <el-icon><DataAnalysis /></el-icon>
                        <span>仪表盘</span>
                    </el-menu-item>
                    <el-menu-item index="/clothes">
                        <el-icon><Goods /></el-icon>
                        <span>服装管理</span>
                    </el-menu-item>
                    <el-menu-item index="/orders">
                        <el-icon><Document /></el-icon>
                        <span>订单管理</span>
                    </el-menu-item>
                    <el-menu-item v-if="auth.user?.role === 1" index="/users">
                        <el-icon><User /></el-icon>
                        <span>用户管理</span>
                    </el-menu-item>
                </el-menu>
            </el-aside>
            <el-main class="admin-main">
                <router-view />
            </el-main>
        </el-container>
    </el-container>
</template>

<style scoped>
.admin-layout {
    min-height: 100vh;
}
.admin-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #304156;
    color: #fff;
    padding: 0 20px;
}
.logo {
    font-size: 18px;
    font-weight: bold;
}
.header-right {
    display: flex;
    align-items: center;
    gap: 12px;
}
.admin-aside {
    background: #fff;
}
.admin-main {
    background: #f0f2f5;
}
</style>
