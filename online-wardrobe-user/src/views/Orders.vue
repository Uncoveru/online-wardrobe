<!-- 我的订单：列表 + 支付 + 确认收货 + 分页 -->
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrders, payOrder, confirmReceived, type OrderInfo } from '../api'
import { ORDER_STATUS } from '../constants/order'
import OrderCard from '../components/OrderCard.vue'
import { getErrorMessage } from '../utils/error'

const router = useRouter()

const orders = ref<OrderInfo[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = 10

// 前端分页
const pagedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return orders.value.slice(start, start + pageSize)
})

const total = computed(() => orders.value.length)

onMounted(async () => {
  await fetchOrders()
})

// 获取订单列表
async function fetchOrders() {
  loading.value = true
  try {
    const res = await getOrders()
    orders.value = res.data.data || []
  } catch (e) {
    ElMessage.error(getErrorMessage(e, '加载订单失败'))
  } finally {
    loading.value = false
  }
}

// 支付（二次确认 + 乐观更新状态）
async function handlePay(order: OrderInfo) {
  try {
    await ElMessageBox.confirm(`确认支付订单 #${order.id}，金额 ¥${order.price}？`, '确认支付', { type: 'warning' })
  } catch {
    return
  }
  try {
    await payOrder(order.id)
    ElMessage.success('支付成功')
    order.status = ORDER_STATUS.PAID
  } catch (e) {
    ElMessage.error(getErrorMessage(e, '支付失败'))
  }
}

// 确认收货（二次确认 + 乐观更新状态）
async function handleConfirm(order: OrderInfo) {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '确认收货', { type: 'warning' })
  } catch {
    return
  }
  try {
    await confirmReceived(order.id)
    ElMessage.success('已确认收货')
    order.status = ORDER_STATUS.RECEIVED
  } catch (e) {
    ElMessage.error(getErrorMessage(e, '确认收货失败'))
  }
}
</script>

<template>
  <div class="orders">
    <header class="orders-header">
      <h1>我的订单</h1>
    </header>

    <!-- 加载态 -->
    <el-card v-if="loading && orders.length === 0">
      <el-skeleton :rows="4" animated />
    </el-card>

    <!-- 空态 -->
    <el-card v-else-if="orders.length === 0">
      <div style="text-align:center;padding:40px;color:#999">
        <p>暂无订单</p>
        <el-button type="primary" link @click="router.push('/')">去逛逛</el-button>
      </div>
    </el-card>

    <!-- 订单列表 -->
    <template v-else>
      <TransitionGroup name="order-list" tag="div">
        <OrderCard
          v-for="order in pagedOrders"
          :key="order.id"
          :order="order"
          @pay="handlePay(order)"
          @confirm="handleConfirm(order)"
        />
      </TransitionGroup>

      <div class="pagination-wrap" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          background
        />
      </div>
    </template>
  </div>
</template>

<style scoped>
.orders {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.orders-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}
</style>
