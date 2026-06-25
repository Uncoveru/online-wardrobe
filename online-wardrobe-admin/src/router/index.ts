/**
 * 路由配置：hash 模式 + 导航守卫（登录校验 + 用户管理权限）
 */
import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        {
            path: '/login',
            name: 'Login',
            component: () => import('../views/Login.vue'),
        },
        {
            path: '/register',
            name: 'Register',
            component: () => import('../views/RegisterOperator.vue'),
        },
        {
            path: '/',
            component: () => import('../components/AdminLayout.vue'),
            redirect: '/dashboard',
            children: [
                {
                    path: 'dashboard',
                    name: 'Dashboard',
                    component: () => import('../views/Dashboard.vue'),
                    meta: { requiresAuth: true },
                },
                {
                    path: 'clothes',
                    name: 'Clothes',
                    component: () => import('../views/Clothes.vue'),
                    meta: { requiresAuth: true },
                },
                {
                    path: 'orders',
                    name: 'Orders',
                    component: () => import('../views/Orders.vue'),
                    meta: { requiresAuth: true },
                },
                {
                    path: 'users',
                    name: 'Users',
                    component: () => import('../views/Users.vue'),
                    meta: { requiresAuth: true },
                },
            ],
        },
    ],
})

// 导航守卫：未登录 → /login；非超管访问 /users → /dashboard
router.beforeEach((to, _from, next) => {
    const token = localStorage.getItem('token')
    if (to.meta.requiresAuth && !token) {
        next('/login')
        return
    }
    if (to.path.startsWith('/users')) {
        const raw = localStorage.getItem('user')
        const user = raw ? JSON.parse(raw) : null
        if (user?.role !== 1) {
            next('/dashboard')
            return
        }
    }
    next()
})

export default router
