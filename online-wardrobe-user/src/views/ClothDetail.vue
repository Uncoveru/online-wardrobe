<!-- 商品详情页：图片 + 信息 + 尺码/数量选择 + 加入购物车 -->
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import { getClothesById, getSizes, addToCart, type ClothesInfo, type SizeInfo } from '../api'
import { getImageUrl } from '../utils/image'
import { getErrorMessage } from '../utils/error'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const clothes = ref<ClothesInfo | null>(null)
const sizes = ref<SizeInfo[]>([])
const selectedSize = ref('')
const quantity = ref(1)
const loading = ref(true)
const error = ref(false)
const notFound = ref(false)
const addingToCart = ref(false)

onMounted(async () => {
  const id = Number(route.params.id)
  try {
    const res = await getClothesById(id)
    if (res.data.data) {
      clothes.value = res.data.data
      // 加载该类型的可选尺码
      const sizesRes = await getSizes(clothes.value.typeId)
      sizes.value = sizesRes.data.data || []
    } else {
      notFound.value = true
    }
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
})

// 加入购物车
async function handleAddToCart() {
  if (!auth.isLoggedIn()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  if (!selectedSize.value) {
    ElMessage.warning('请选择尺码')
    return
  }
  addingToCart.value = true
  try {
    const res = await addToCart({
      clothId: clothes.value!.id,
      clothSize: selectedSize.value,
      amount: quantity.value,
    })
    if (res.data.code === 200) {
      ElMessage.success('已加入购物车')
    }
  } catch (e) {
    ElMessage.error(getErrorMessage(e, '操作失败'))
  } finally {
    addingToCart.value = false
  }
}
</script>

<template>
  <div class="detail">
    <router-link to="/" class="back-link">← 返回首页</router-link>

    <!-- 加载态 -->
    <div v-if="loading">
      <el-skeleton animated>
        <template #template>
          <div style="display:flex;gap:40px">
            <el-skeleton-item variant="image" style="flex:1;height:400px" />
            <div style="flex:1">
              <el-skeleton-item variant="text" style="width:60%;margin-bottom:16px" />
              <el-skeleton-item variant="text" style="width:30%;margin-bottom:8px" />
              <el-skeleton-item variant="text" style="width:20%;margin-bottom:20px" />
              <el-skeleton-item variant="rect" style="height:40px;width:200px" />
            </div>
          </div>
        </template>
      </el-skeleton>
    </div>

    <!-- 错误态 -->
    <el-result v-else-if="error" status="error" title="加载失败" sub-title="请检查网络后重试">
      <template #extra>
        <el-button type="primary" @click="router.go(0)">刷新</el-button>
      </template>
    </el-result>

    <!-- 不存在 -->
    <el-result v-else-if="notFound" status="warning" title="商品不存在" sub-title="该商品可能已下架">
      <template #extra>
        <el-button type="primary" @click="router.push('/')">返回首页</el-button>
      </template>
    </el-result>

    <!-- 商品详情内容 -->
    <div v-else-if="clothes" class="detail-body">
      <div class="image-section">
        <el-image
          v-if="clothes.image"
          :src="getImageUrl(clothes.image)"
          :alt="clothes.clothName"
          style="width:100%;max-height:500px"
          fit="contain"
        />
        <div v-else class="no-image">暂无图片</div>
      </div>
      <div class="info-section">
        <h1>{{ clothes.clothName }}</h1>
        <p class="style">风格：{{ clothes.style }}</p>
        <p class="price">¥{{ clothes.price }}</p>

        <!-- 尺码选择 -->
        <div class="size-section">
          <label>尺码：</label>
          <el-radio-group v-model="selectedSize">
            <el-radio-button v-for="s in sizes" :key="s.id" :value="s.sizeName">{{ s.sizeName }}</el-radio-button>
          </el-radio-group>
        </div>

        <!-- 数量 -->
        <div class="quantity-section">
          <label>数量：</label>
          <el-input-number v-model="quantity" :min="1" :max="99" />
        </div>

        <el-button type="danger" size="large" :loading="addingToCart" @click="handleAddToCart" class="add-btn">
          加入购物车
        </el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.detail {
  max-width: 1400px;
  margin: 20px auto;
  padding: 0 20px;
}

.back-link {
  color: var(--color-primary);
  text-decoration: none;
  font-size: 14px;
  display: inline-block;
  margin-bottom: 16px;
}

.detail-body {
  display: flex;
  gap: 40px;
  background: var(--color-bg-card);
  padding: 30px;
  border-radius: 8px;
}

.image-section {
  flex: 1;
  min-width: 300px;
}

.info-section {
  flex: 1;
}

.info-section h1 {
  font-size: 24px;
  margin-bottom: 12px;
}

.style {
  color: var(--color-text-muted);
  margin-bottom: 12px;
}

.price {
  color: var(--color-primary);
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 20px;
}

.size-section {
  margin-top: 20px;
}

.size-section label {
  display: block;
  margin-bottom: 8px;
  color: var(--color-text-secondary);
}

.quantity-section {
  margin-top: 20px;
}

.quantity-section label {
  display: block;
  margin-bottom: 8px;
  color: var(--color-text-secondary);
}

.add-btn {
  margin-top: 24px;
}

.no-image {
  height: 400px;
  background: var(--color-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-placeholder);
}

@media (max-width: 768px) {
  .detail-body {
    flex-direction: column;
    gap: 20px;
    padding: 16px;
  }

  .image-section {
    min-width: unset;
  }
}
</style>
