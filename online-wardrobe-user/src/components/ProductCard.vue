<!-- 商品卡片：图片 + 名称 + 分类 · 风格 + 价格 -->
<script setup lang="ts">
import { getImageUrl } from '../utils/image'
import type { ClothesInfo } from '../api'

defineProps<{
  product: ClothesInfo
  typeName: string    // 所属类型名称
}>()

defineEmits<{
  click: []           // 点击跳转详情
}>()
</script>

<template>
  <el-card class="product-card" shadow="hover" @click="$emit('click')">
    <el-image
      v-if="product.image"
      :src="getImageUrl(product.image)"
      :alt="product.clothName"
      style="height:200px"
      fit="cover"
    />
    <div v-else class="no-image">暂无图片</div>
    <div class="card-info">
      <h3>{{ product.clothName }}</h3>
      <p class="style">{{ typeName }} · {{ product.style }}</p>
      <p class="price">¥{{ product.price }}</p>
    </div>
  </el-card>
</template>

<style scoped>
.product-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.product-card:hover {
  transform: translateY(-4px);
}

.card-info {
  padding: 12px 4px 4px;
}

.card-info h3 {
  font-size: 16px;
  margin-bottom: 4px;
}

.style {
  color: var(--color-text-muted);
  font-size: 13px;
}

.price {
  color: var(--color-primary);
  font-size: 18px;
  font-weight: bold;
  margin-top: 4px;
}

.no-image {
  height: 200px;
  background: var(--color-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-placeholder);
}
</style>
