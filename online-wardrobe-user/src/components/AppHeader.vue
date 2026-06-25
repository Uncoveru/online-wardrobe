<!-- 顶部导航栏：搜索 + 用户菜单（响应式：桌面横向 / 移动侧边抽屉） -->
<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Menu } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'
import { useFilter } from '../composables/useFilter'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const { searchKeyword, setSearch } = useFilter()

const mobileMenuOpen = ref(false)

const isLoggedIn = computed(() => auth.isLoggedIn())
const userName = computed(() => auth.user?.userName || '')

// 搜索：设置关键词并跳转到首页
function doSearch() {
  setSearch(searchKeyword.value)
  if (route.path !== '/') {
    router.push('/')
  }
}

// 退出登录
function handleLogout() {
  auth.logout()
  router.push('/login')
}

// 导航跳转（同时关闭移动菜单）
function navigateTo(path: string) {
  mobileMenuOpen.value = false
  router.push(path)
}
</script>

<template>
  <header class="app-header">
    <div class="header-inner">
      <div class="header-left">
        <router-link to="/" class="logo">网上衣橱</router-link>
        <router-link to="/" class="home-link">首页</router-link>
      </div>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索服装..." @keyup.enter="doSearch" />
        <el-button type="primary" @click="doSearch">搜索</el-button>
      </div>

      <!-- 桌面端导航 -->
      <nav class="nav-links desktop-nav">
        <template v-if="!isLoggedIn">
          <el-button type="primary" link @click="navigateTo('/login')">登录</el-button>
          <el-button link @click="navigateTo('/register')">注册</el-button>
        </template>
        <template v-else>
          <span class="user-name">{{ userName }}</span>
          <el-button link @click="navigateTo('/cart')">购物车</el-button>
          <el-button link @click="navigateTo('/orders')">我的订单</el-button>
          <el-button link @click="navigateTo('/profile')">个人中心</el-button>
          <el-button link @click="handleLogout">退出</el-button>
        </template>
      </nav>

      <!-- 移动端菜单按钮 -->
      <el-button class="mobile-toggle" @click="mobileMenuOpen = true">
        <el-icon><Menu /></el-icon>
      </el-button>
    </div>

    <!-- 移动端侧边抽屉 -->
    <el-drawer v-model="mobileMenuOpen" direction="rtl" size="260px" title="导航菜单">
      <div class="mobile-nav">
        <template v-if="!isLoggedIn">
          <el-button type="primary" block @click="navigateTo('/login')">登录</el-button>
          <el-button block @click="navigateTo('/register')">注册</el-button>
        </template>
        <template v-else>
          <p class="mobile-user">{{ userName }}</p>
          <el-button block @click="navigateTo('/cart')">购物车</el-button>
          <el-button block @click="navigateTo('/orders')">我的订单</el-button>
          <el-button block @click="navigateTo('/profile')">个人中心</el-button>
          <el-button type="danger" block @click="handleLogout">退出</el-button>
        </template>
      </div>
    </el-drawer>
  </header>
</template>

<style scoped>
.app-header {
  background: var(--color-bg-card);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  max-width: 1600px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  padding: 12px 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.logo {
  font-size: 22px;
  color: var(--color-primary);
  text-decoration: none;
  white-space: nowrap;
  font-weight: bold;
}

.home-link {
  font-size: 14px;
  color: var(--color-text-primary);
  text-decoration: none;
}

.home-link:hover {
  color: var(--color-primary);
}

.search-bar {
  display: flex;
  gap: 8px;
  width: 500px;
  max-width: 100%;
}

.desktop-nav {
  display: flex;
  gap: 4px;
  align-items: center;
  justify-self: end;
}

.user-name {
  color: var(--color-text-primary);
  font-size: 14px;
  margin-right: 8px;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.mobile-toggle {
  display: none;
}

.mobile-nav {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 12px;
}

.mobile-user {
  font-size: 16px;
  font-weight: bold;
  color: var(--color-text-primary);
  padding: 8px 0;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .header-inner {
    display: flex;
    justify-content: space-between;
  }

  .header-left .home-link {
    display: none;
  }

  .desktop-nav {
    display: none;
  }

  .mobile-toggle {
    display: inline-flex;
  }

  .search-bar {
    flex: 1;
    width: auto;
  }
}
</style>
