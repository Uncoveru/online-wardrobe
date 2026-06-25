<!-- 订单卡片：商品明细 + 状态 + 操作按钮 -->
<script setup lang="ts">
import { ORDER_STATUS, ORDER_STATUS_LABELS, getOrderTagType } from '../constants/order'
import type { OrderInfo } from '../api'

defineProps<{
  order: OrderInfo
}>()

defineEmits<{
  pay: []      // 支付
  confirm: []  // 确认收货
}>()

// 尺码显示格式化
const sizeLabel = (size: string) => size === '均码' ? '均码' : `${size}码`
</script>

<template>
  <el-card class="order-card">
    <div class="order-row">
      <div class="order-info">
        <p class="order-id">订单编号：{{ order.id }}</p>
        <!-- 有明细数据时展示列表，否则展示冗余文本 -->
        <ul v-if="order.orderItems?.length" class="order-items">
          <li v-for="item in order.orderItems" :key="item.id">
            {{ item.clothName }}
            <span class="item-size">{{ sizeLabel(item.clothSize) }}</span>
            <span class="item-meta">x{{ item.amount }}  ¥{{ item.price }}</span>
          </li>
        </ul>
        <p v-else>{{ order.clothesDetails }}</p>
        <p class="order-time">{{ order.time }}</p>
      </div>
      <div class="order-status">
        <el-tag :type="getOrderTagType(order.status)">
          {{ ORDER_STATUS_LABELS[order.status] || order.status }}
        </el-tag>
        <p class="order-price">¥{{ order.price }}</p>
      </div>
      <div class="order-actions">
        <!-- 未支付 → 支付 -->
        <el-button v-if="order.status === ORDER_STATUS.UNPAID" type="danger" size="small" @click="$emit('pay')">
          支付
        </el-button>
        <!-- 已发货 → 确认收货 -->
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
  align-items: flex-start;
}

.order-info {
  flex: 1;
  min-width: 0;
}

.order-id {
  font-weight: 600;
  margin-bottom: 8px;
}

.order-items {
  list-style: none;
  margin: 0 0 8px;
  padding: 0;
}

.order-items li {
  padding: 4px 0;
  font-size: 14px;
  color: var(--color-text-primary);
  border-bottom: 1px dashed var(--color-border-light, #ebeef5);
}

.order-items li:last-child {
  border-bottom: none;
}

.item-size {
  color: var(--color-text-secondary);
  margin-left: 4px;
}

.item-meta {
  color: var(--color-text-muted);
  margin-left: 8px;
  font-size: 13px;
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
