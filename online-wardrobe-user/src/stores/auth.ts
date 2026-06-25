/**
 * 认证状态管理：用户信息存储 + 登录/登出
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserInfo } from '../api'

export const useAuthStore = defineStore('auth', () => {
    const user = ref<UserInfo | null>(loadUser())

    // 从 localStorage 加载用户信息
    function loadUser(): UserInfo | null {
        const raw = localStorage.getItem('user')
        return raw ? JSON.parse(raw) : null
    }

    // 设置用户信息（同步写入 localStorage）
    function setUser(u: UserInfo) {
        user.value = u
        localStorage.setItem('user', JSON.stringify(u))
    }

    // 登出：清除用户信息
    function logout() {
        user.value = null
        localStorage.removeItem('token')
        localStorage.removeItem('user')
    }

    // 是否已登录
    function isLoggedIn() {
        return user.value !== null && localStorage.getItem('token') !== null
    }

    return { user, setUser, logout, isLoggedIn }
})
