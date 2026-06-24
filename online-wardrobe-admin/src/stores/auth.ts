import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserInfo } from '../api'

export const useAuthStore = defineStore('auth', () => {
    const user = ref<UserInfo | null>(loadUser())

    function loadUser(): UserInfo | null {
        const raw = localStorage.getItem('user')
        return raw ? JSON.parse(raw) : null
    }

    function setUser(u: UserInfo) {
        user.value = u
        localStorage.setItem('user', JSON.stringify(u))
    }

    function logout() {
        user.value = null
        localStorage.removeItem('token')
        localStorage.removeItem('user')
    }

    function isLoggedIn() {
        return user.value !== null && localStorage.getItem('token') !== null
    }

    return { user, setUser, logout, isLoggedIn }
})
