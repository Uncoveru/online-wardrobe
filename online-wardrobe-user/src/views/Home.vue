<!-- 首页：分类筛选 + 商品网格 + 响应式分页 -->
<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Operation } from '@element-plus/icons-vue'
import { getClothesList, searchClothes, getTypes, type ClothesInfo, type TypeInfo } from '../api'
import { useFilter } from '../composables/useFilter'
import ProductCard from '../components/ProductCard.vue'

const router = useRouter()
const route = useRoute()

const { searchKeyword } = useFilter()

const activeTypeId = ref<number | null>(null)
const clothesList = ref<ClothesInfo[]>([])
const loading = ref(false)
const error = ref(false)
const types = ref<TypeInfo[]>([])
const mobileFilterOpen = ref(false)

const currentPage = ref(1)

// 响应式列数
const columnsPerRow = ref(4)
const ROWS_PER_PAGE = 3

function updateColumns() {
  const width = window.innerWidth
  if (width >= 1400) columnsPerRow.value = 5
  else if (width < 769) columnsPerRow.value = 2
  else if (width < 1025) columnsPerRow.value = 3
  else columnsPerRow.value = 4
}

const pageSize = computed(() => columnsPerRow.value * ROWS_PER_PAGE)

// 列数变化时重置页码
watch(pageSize, () => {
  currentPage.value = 1
})

// 前端分页
const pagedClothes = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return clothesList.value.slice(start, start + pageSize.value)
})

const total = computed(() => clothesList.value.length)

// 同步筛选状态到 URL query
function syncQuery() {
  const query: Record<string, string> = {}
  if (searchKeyword.value) query.keyword = searchKeyword.value
  if (activeTypeId.value !== null) query.typeId = String(activeTypeId.value)
  router.replace({ query })
}

// 搜索时清除分类筛选
watch(searchKeyword, (val) => {
  if (val) activeTypeId.value = null
})

// 关键词或分类变化时重新加载
watch([searchKeyword, activeTypeId], async () => {
  currentPage.value = 1
  await fetchClothes()
  syncQuery()
})

onMounted(async () => {
  updateColumns()
  window.addEventListener('resize', updateColumns)

  // 从 URL 恢复筛选状态
  const qKeyword = (route.query.keyword as string) || ''
  const qTypeId = route.query.typeId ? Number(route.query.typeId) : null
  if (qKeyword) searchKeyword.value = qKeyword
  if (qTypeId !== null) activeTypeId.value = qTypeId
  await Promise.all([fetchClothes(), fetchTypes()])
})

onUnmounted(() => {
  window.removeEventListener('resize', updateColumns)
})

// 获取商品列表（有筛选条件时走搜索接口）
async function fetchClothes() {
  loading.value = true
  error.value = false
  try {
    const keyword = searchKeyword.value || undefined
    const typeId = activeTypeId.value ?? undefined
    if (keyword || typeId) {
      const res = await searchClothes({ clothName: keyword, typeId })
      clothesList.value = res.data.data || []
    } else {
      const res = await getClothesList()
      clothesList.value = res.data.data || []
    }
  } catch {
    error.value = true
    clothesList.value = []
  } finally {
    loading.value = false
  }
}

// 获取商品类型
async function fetchTypes() {
  try {
    const res = await getTypes()
    types.value = res.data.data || []
  } catch {}
}

// 跳转详情
function viewDetail(id: number) {
  router.push(`/clothes/${id}`)
}

// 类型 ID → 名称
function getTypeName(typeId: number) {
  return types.value.find((t) => t.id === typeId)?.typeName || ''
}
</script>

<template>
  <div class="home">
    <h2 class="section-title">本季新品</h2>

    <div class="home-body">
      <!-- 桌面端左侧分类 -->
      <aside class="sidebar">
        <h3 class="filter-title">分类</h3>
        <ul class="filter-list">
          <li :class="{ active: activeTypeId === null }" @click="activeTypeId = null">全部</li>
          <li
            v-for="t in types"
            :key="t.id"
            :class="{ active: activeTypeId === t.id }"
            @click="activeTypeId = t.id"
          >{{ t.typeName }}</li>
        </ul>
      </aside>

      <!-- 移动端筛选按钮 -->
      <div class="mobile-filter-bar">
        <el-button @click="mobileFilterOpen = true">
          <el-icon><Operation /></el-icon> 筛选
        </el-button>
      </div>

      <div class="content">
        <!-- 加载态 -->
        <div v-if="loading" class="skeleton-grid">
          <el-skeleton v-for="n in pageSize" :key="n" animated>
            <template #template>
              <el-skeleton-item variant="image" style="height:200px" />
              <div style="padding:14px 4px 4px">
                <el-skeleton-item variant="text" style="width:60%" />
                <el-skeleton-item variant="text" style="width:40%" />
                <el-skeleton-item variant="text" style="width:30%" />
              </div>
            </template>
          </el-skeleton>
        </div>

        <!-- 错误态 -->
        <el-result v-else-if="error" status="error" title="加载失败" sub-title="请检查网络后重试">
          <template #extra>
            <el-button type="primary" @click="fetchClothes">重试</el-button>
          </template>
        </el-result>

        <!-- 空态 -->
        <div v-else-if="clothesList.length === 0" class="empty">
          <p>暂无服装</p>
          <el-button type="primary" link @click="router.push('/')">去看看其他商品</el-button>
        </div>

        <!-- 商品网格 -->
        <template v-else>
          <div class="clothes-grid">
            <ProductCard
              v-for="item in pagedClothes"
              :key="item.id"
              :product="item"
              :type-name="getTypeName(item.typeId)"
              @click="viewDetail(item.id)"
            />
          </div>
          <!-- 分页 -->
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
    </div>

    <!-- 移动端筛选抽屉 -->
    <el-drawer v-model="mobileFilterOpen" direction="ltr" size="260px" title="筛选">
      <h3 class="filter-title">分类</h3>
      <ul class="filter-list">
        <li :class="{ active: activeTypeId === null }" @click="activeTypeId = null">全部</li>
        <li
          v-for="t in types"
          :key="t.id"
          :class="{ active: activeTypeId === t.id }"
          @click="activeTypeId = t.id"
        >{{ t.typeName }}</li>
      </ul>
    </el-drawer>
  </div>
</template>

<style scoped>
.home {
  max-width: 1600px;
  margin: 20px auto;
  padding: 0 20px;
}

.section-title {
  font-size: 20px;
  margin-bottom: 16px;
}

.home-body {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.sidebar {
  width: 180px;
  flex-shrink: 0;
}

.mobile-filter-bar {
  display: none;
}

.content {
  flex: 1;
  min-width: 0;
}

.filter-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--color-border);
}

.filter-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.filter-list li {
  padding: 10px 12px;
  cursor: pointer;
  color: var(--color-text-body);
  font-size: 14px;
  border-radius: 6px;
  transition: all 0.15s;
  user-select: none;
}

.filter-list li:hover {
  background: var(--color-bg);
  color: var(--color-text-primary);
}

.filter-list li.active {
  color: var(--color-primary);
  font-weight: 600;
  background: var(--color-primary-light);
}

.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.empty {
  text-align: center;
  color: var(--color-text-muted);
  padding: 60px 0;
}

.clothes-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

@media (min-width: 1400px) {
  .clothes-grid,
  .skeleton-grid {
    grid-template-columns: repeat(5, 1fr);
  }
}

@media (max-width: 1024px) {
  .clothes-grid,
  .skeleton-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .sidebar {
    display: none;
  }

  .mobile-filter-bar {
    display: block;
    margin-bottom: 12px;
  }

  .home-body {
    flex-direction: column;
  }

  .clothes-grid,
  .skeleton-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
