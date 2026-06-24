<script setup lang="ts">
import { ORDER_STATUS, ORDER_STATUS_LABELS, getOrderTagType } from '../constants/order'
import type { OrderInfo } from '../api'

defineProps<{
  order: OrderInfo
}>()

defineEmits<{
  pay: []
  confirm: []
}>()
</script>

<template>
  <el-card class="order-card">
    <div class="order-row">
      <div class="order-info">
        <p>订单编号：{{ order.id }}</p>
        <p>{{ order.clothesDetails }}</p>
        <p class="order-time">{{ order.time }}</p>
      </div>
      <div class="order-status">
        <el-tag :type="getOrderTagType(order.status)">
          {{ ORDER_STATUS_LABELS[order.status] || order.status }}
        </el-tag>
        <p class="order-price">¥{{ order.price }}</p>
      </div>
      <div class="order-actions">
        <el-button v-if="order.status === ORDER_STATUS.UNPAID" type="danger" size="small" @click="$emit('pay')">
          支付
        </el-button>
        <el-button v-if="order.status === ORDER_STATUS.SHIPPED" type="success" size="small" @click="$emit('confirm')">
          确认收货
        </el-button>
      </div>
    </div>
  </el-card>
</template>

<style scoped>
.order-card {
  margin-bottom: 12px;
}

.order-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-info {
  flex: 1;
}

.order-time {
  color: var(--color-text-muted);
  font-size: 12px;
  margin-top: 8px;
}

.order-status {
  text-align: center;
}

.order-price {
  margin-top: 4px;
  color: var(--color-primary);
  font-weight: bold;
}

.order-actions {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
</style>
