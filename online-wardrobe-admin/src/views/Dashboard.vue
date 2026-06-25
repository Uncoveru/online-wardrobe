<!-- 仪表盘：统计概览（用户数/商品数/订单数） -->
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getClothesList, getOrders, getUsers } from '../api'
import { ORDER_STATUS } from '../constants/order'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()

const totalClothes = ref(0)
const pendingOrders = ref(0)   // 待发货订单数
const todayOrders = ref(0)     // 今日订单数
const userCount = ref(0)       // 用户总数（仅超管）
const loading = ref(true)

onMounted(async () => {
    try {
        const [clothesRes, ordersRes] = await Promise.all([
            getClothesList(),
            getOrders(),
        ])
        const clothes = clothesRes.data.data || []
        const orders = ordersRes.data.data || []
        totalClothes.value = clothes.length

        // 待发货 = 状态为 PAID 的订单
        pendingOrders.value = orders.filter(
            o => o.status === ORDER_STATUS.PAID
        ).length

        // 今日订单
        const today = new Date().toISOString().slice(0, 10)
        todayOrders.value = orders.filter(o => o.time?.startsWith(today)).length

        // 超管额外获取用户总数
        if (auth.user?.role === 1) {
            try {
                const usersRes = await getUsers()
                const users = usersRes.data.data || []
                userCount.value = users.length
            } catch {
                // 获取失败时保持 0
            }
        }
    } catch {
        // 获取失败时保持 0
    } finally {
        loading.value = false
    }
})
</script>

<template>
    <el-card v-loading="loading">
        <template #header>仪表盘</template>
        <p>欢迎使用网上衣橱后台管理系统</p>
        <!-- 超管看板 -->
        <template v-if="auth.user?.role === 1">
            <el-row :gutter="20">
                <el-col :span="8">
                    <el-statistic title="用户总数" :value="userCount" />
                </el-col>
                <el-col :span="8">
                    <el-statistic title="商品总数" :value="totalClothes" />
                </el-col>
                <el-col :span="8">
                    <el-statistic title="待发货订单" :value="pendingOrders" />
                </el-col>
            </el-row>
        </template>
        <!-- 运营人员看板 -->
        <template v-else>
            <el-row :gutter="20">
                <el-col :span="8">
                    <el-statistic title="待发货订单" :value="pendingOrders" />
                </el-col>
                <el-col :span="8">
                    <el-statistic title="今日订单" :value="todayOrders" />
                </el-col>
                <el-col :span="8">
                    <el-statistic title="商品总数" :value="totalClothes" />
                </el-col>
            </el-row>
        </template>
    </el-card>
</template>
