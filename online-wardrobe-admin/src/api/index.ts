/**
 * API 封装：axios 实例 + 请求/响应拦截器 + 所有接口
 */
import axios from 'axios'
import router from '../router'

// 创建 axios 实例
const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
    timeout: 10000,
})

// 请求拦截：自动携带 Bearer Token
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

// 响应拦截：401 时清除登录态并跳转登录页
api.interceptors.response.use(
    (res) => res,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('token')
            localStorage.removeItem('user')
            if (router.currentRoute.value.path !== '/login') {
                router.push('/login').catch(() => {})
            }
        }
        return Promise.reject(error)
    },
)

// --- 类型定义 ---

export interface LoginParams {
    account: string
    password: string
}

export interface RegisterParams {
    userName: string
    password: string
    phone: string
    address: string
}

export interface UserInfo {
    id: number
    userName: string
    phone: string
    address: string
    role: number       // 1=超管, 2=普通用户, 3=运营
    status: number     // 0=待审核, 1=正常, 2=拒绝
}

export interface ApiResult<T> {
    code: number
    message: string
    data: T
}

// --- 认证接口 ---

// 管理员登录
export async function login(params: LoginParams) {
    const res = await api.post<ApiResult<{ user: UserInfo; token: string }>>('/admin/login', params)
    if (res.data.code === 200) {
        localStorage.setItem('token', res.data.data.token)
        localStorage.setItem('user', JSON.stringify(res.data.data.user))
    }
    return res
}

// 普通用户注册
export async function register(params: RegisterParams) {
    const res = await api.post<ApiResult<{ user: UserInfo; token: string }>>('/user/register', params)
    if (res.data.code === 200) {
        localStorage.setItem('token', res.data.data.token)
        localStorage.setItem('user', JSON.stringify(res.data.data.user))
    }
    return res
}

// --- 商品相关类型与接口 ---

export interface ClothesInfo {
    id: number
    clothName: string
    image: string
    typeId: number
    style: string
    price: number
}

export interface TypeInfo {
    id: number
    typeName: string
}

export interface SizeInfo {
    id: number
    sizeName: string
    typeId: number
}

// 商品列表
export function getClothesList() {
    return api.get<ApiResult<ClothesInfo[]>>('/clothes')
}

// 商品搜索
export function searchClothes(params: { clothName?: string; style?: string; typeId?: number }) {
    return api.get<ApiResult<ClothesInfo[]>>('/clothes/search', { params })
}

// 新增商品（FormData 上传）
export function addClothes(form: FormData) {
    return api.post<ApiResult<ClothesInfo>>('/clothes', form, {
        headers: { 'Content-Type': 'multipart/form-data' },
    })
}

// 更新商品
export function updateClothes(id: number, form: FormData) {
    return api.post<ApiResult<ClothesInfo>>(`/clothes/${id}`, form, {
        headers: { 'Content-Type': 'multipart/form-data' },
    })
}

// 删除商品
export function deleteClothes(id: number) {
    return api.delete<ApiResult<null>>(`/clothes/${id}`)
}

// 商品类型列表
export function getTypes() {
    return api.get<ApiResult<TypeInfo[]>>('/types')
}

// 按类型查尺码
export function getSizes(typeId: number) {
    return api.get<ApiResult<SizeInfo[]>>('/sizes', { params: { typeId } })
}

// --- 订单相关类型与接口 ---

export interface OrderItem {
    id: number
    orderId: number
    clothId: number
    clothName: string
    clothSize: string
    amount: number
    price: number
    operatorId: number
}

export interface OrderInfo {
    id: number
    clothesDetails: string
    price: number
    status: string     // 0=未支付, 1=未发货, 2=已发货, 3=已收货
    userId: number
    address: string
    time: string
    orderItems?: OrderItem[]
}

// 订单列表
export function getOrders(params?: { userName?: string; status?: string }) {
    return api.get<ApiResult<OrderInfo[]>>('/orders', { params })
}

// 发货
export function shipOrder(id: number) {
    return api.put<ApiResult<null>>(`/orders/${id}/ship`)
}

// --- 用户管理接口 ---

// 用户列表
export function getUsers(params?: { userName?: string; phone?: string }) {
    return api.get<ApiResult<UserInfo[]>>('/admin/users', { params })
}

// 新增用户
export function addUser(data: Partial<UserInfo> & { password: string }) {
    return api.post<ApiResult<UserInfo>>('/admin/users', data)
}

// 编辑用户
export function updateUser(id: number, data: Partial<UserInfo> & { password?: string }) {
    return api.put<ApiResult<UserInfo>>(`/admin/users/${id}`, data)
}

// 删除用户
export function deleteUser(id: number) {
    return api.delete<ApiResult<null>>(`/admin/users/${id}`)
}

// 运营人员注册
export function registerOperator(data: { userName: string; password: string; phone: string; address: string }) {
    return api.post<ApiResult<{ message: string }>>('/user/register-operator', data)
}

// 审核通过
export function approveUser(id: number) {
    return api.put<ApiResult<null>>(`/admin/users/${id}/approve`)
}

// 审核拒绝
export function rejectUser(id: number) {
    return api.put<ApiResult<null>>(`/admin/users/${id}/reject`)
}

// 撤销拒绝
export function undoRejectUser(id: number) {
    return api.put<ApiResult<null>>(`/admin/users/${id}/undo-reject`)
}

export default api
