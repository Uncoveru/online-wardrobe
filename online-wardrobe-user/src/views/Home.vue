<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
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
const pageSize = 12

const pagedClothes = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return clothesList.value.slice(start, start + pageSize)
})

const total = computed(() => clothesList.value.length)

function syncQuery() {
  const query: Record<string, string> = {}
  if (searchKeyword.value) query.keyword = searchKeyword.value
  if (activeTypeId.value !== null) query.typeId = String(activeTypeId.value)
  router.replace({ query })
}

// 搜索时清除分类筛选，确保做全局搜索
watch(searchKeyword, (val) => {
  if (val) activeTypeId.value = null
})

watch([searchKeyword, activeTypeId], async () => {
  currentPage.value = 1
  await fetchClothes()
  syncQuery()
})

onMounted(async () => {
  const qKeyword = (route.query.keyword as string) || ''
  const qTypeId = route.query.typeId ? Number(route.query.typeId) : null
  if (qKeyword) searchKeyword.value = qKeyword
  if (qTypeId !== null) activeTypeId.value = qTypeId
  await Promise.all([fetchClothes(), fetchTypes()])
})

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

async function fetchTypes() {
  try {
    const res = await getTypes()
    types.value = res.data.data || []
  } catch {}
}

function viewDetail(id: number) {
  router.push(`/clothes/${id}`)
}

function getTypeName(typeId: number) {
  return types.value.find((t) => t.id === typeId)?.typeName || ''
}
</script>

<template>
  <div class="home">
    <h2 class="section-title">本季新品</h2>

    <div class="home-body">
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

      <div class="mobile-filter-bar">
        <el-button @click="mobileFilterOpen = true">
          <el-icon><Operation /></el-icon> 筛选
        </el-button>
      </div>

      <div class="content">
        <div v-if="loading" class="skeleton-grid">
          <el-skeleton v-for="n in 8" :key="n" animated>
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

        <el-result v-else-if="error" status="error" title="加载失败" sub-title="请检查网络后重试">
          <template #extra>
            <el-button type="primary" @click="fetchClothes">重试</el-button>
          </template>
        </el-result>

        <div v-else-if="clothesList.length === 0" class="empty">
          <p>暂无服装</p>
          <el-button type="primary" link @click="router.push('/')">去看看其他商品</el-button>
        </div>

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
