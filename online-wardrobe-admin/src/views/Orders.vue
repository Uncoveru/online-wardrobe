<script setup lang="ts">
import { reactive, ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrders, shipOrder, type OrderInfo } from '../api'
import { ORDER_STATUS, ORDER_STATUS_LABELS } from '../constants/order'

const orders = ref<OrderInfo[]>([])
const loading = ref(false)

const currentPage = ref(1)
const pageSize = 10

const pagedData = computed(() => {
    const start = (currentPage.value - 1) * pageSize
    return orders.value.slice(start, start + pageSize)
})

const searchForm = reactive({
    userName: '',
    status: '',
})

function getStatusType(status: string) {
    const map: Record<string, string> = {
        '0': 'danger',
        '1': 'warning',
        '2': 'primary',
        '3': 'success',
    }
    return map[status] || 'info'
}

onMounted(() => {
    fetchOrders()
})

async function fetchOrders() {
    loading.value = true
    try {
        const res = await getOrders()
        orders.value = res.data.data || []
    } finally {
        loading.value = false
    }
}

async function handleSearch() {
    currentPage.value = 1
    loading.value = true
    try {
        const res = await getOrders({
            userName: searchForm.userName || undefined,
            status: searchForm.status || undefined,
        })
        orders.value = res.data.data || []
    } finally {
        loading.value = false
    }
}

function handleReset() {
    searchForm.userName = ''
    searchForm.status = ''
    currentPage.value = 1
    fetchOrders()
}

async function handleShip(row: OrderInfo) {
    try {
        await shipOrder(row.id)
        ElMessage.success('发货成功')
        fetchOrders()
    } catch (err: any) {
        const msg = err?.response?.data?.message || err?.message || '发货失败'
        ElMessage.error(msg)
    }
}
</script>

<template>
    <el-card>
        <el-form :inline="true" :model="searchForm" @submit.prevent="handleSearch">
            <el-form-item label="下单用户">
                <el-input v-model="searchForm.userName" placeholder="请输入" clearable />
            </el-form-item>
            <el-form-item label="订单状态">
                <el-select v-model="searchForm.status" placeholder="全部" clearable style="width:140px">
                    <el-option label="未支付" value="0" />
                    <el-option label="未发货" value="1" />
                    <el-option label="已发货" value="2" />
                    <el-option label="已收货" value="3" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="handleSearch">查询</el-button>
                <el-button @click="handleReset">重置</el-button>
            </el-form-item>
        </el-form>
    </el-card>

    <el-card style="margin-top:16px">
        <el-table :data="pagedData" v-loading="loading" border stripe>
            <template #empty>
                <el-empty description="暂无数据" />
            </template>
            <el-table-column prop="id" label="编号" width="80" />
            <el-table-column prop="clothesDetails" label="商品详情" min-width="200" />
            <el-table-column label="收货信息" min-width="180">
                <template #default="{ row }">{{ row.address }}</template>
            </el-table-column>
            <el-table-column label="订单状态" width="100">
                <template #default="{ row }">
                    <el-tag :type="getStatusType(row.status)">
                        {{ ORDER_STATUS_LABELS[row.status] || row.status }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="订单金额" width="100">
                <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column prop="time" label="下单时间" width="170" />
            <el-table-column label="操作" width="100" fixed="right">
                <template #default="{ row }">
                    <el-popconfirm v-if="row.status === ORDER_STATUS.PAID" title="确定发货吗？" @confirm="handleShip(row)">
                        <template #reference>
                            <el-button type="primary" size="small">发货</el-button>
                        </template>
                    </el-popconfirm>
                    <span v-else>-</span>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="orders.length"
            layout="total, prev, pager, next"
            background
            style="margin-top:16px;justify-content:flex-end"
        />
    </el-card>
</template>
