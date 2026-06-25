/**
 * 路由配置：history 模式 + 导航守卫（需登录页面的跳转）
 */
import { createRouter, createWebHistory } from 'vue-router'
import AppLayout from '../components/AppLayout.vue'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            component: AppLayout,
            children: [
                {
                    path: '',
                    name: 'Home',
                    component: () => import('../views/Home.vue'),
                },
                {
                    path: 'clothes/:id',
                    name: 'ClothDetail',
                    component: () => import('../views/ClothDetail.vue'),
                },
                {
                    path: 'cart',
                    name: 'Cart',
                    component: () => import('../views/Cart.vue'),
                    meta: { requiresAuth: true },
                },
                {
                    path: 'orders',
                    name: 'Orders',
                    component: () => import('../views/Orders.vue'),
                    meta: { requiresAuth: true },
                },
                {
                    path: 'profile',
                    name: 'Profile',
                    component: () => import('../views/Profile.vue'),
                    meta: { requiresAuth: true },
                },
            ],
        },
        {
            path: '/login',
            name: 'Login',
            component: () => import('../views/Login.vue'),
        },
        {
            path: '/register',
            name: 'Register',
            component: () => import('../views/Register.vue'),
        },
        {
            path: '/:pathMatch(.*)*',
            name: 'NotFound',
            component: () => import('../views/NotFound.vue'),
        },
    ],
})

// 导航守卫：需要登录的页面未登录则跳转 /login，并携带 redirect 参数
router.beforeEach((to, _from, next) => {
    const token = localStorage.getItem('token')
    if (to.meta.requiresAuth && !token) {
        next({ path: '/login', query: { redirect: to.fullPath } })
    } else {
        next()
    }
})

export default router
