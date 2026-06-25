/**
 * API 封装：axios 实例 + 请求/响应拦截器 + 所有接口
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
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

// 响应拦截：401 时清除登录态并跳转
api.interceptors.response.use(
    (res) => res,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('token')
            localStorage.removeItem('user')
            router.push('/login')
        }
        return Promise.reject(error)
    },
)

// --- 类型定义 ---

export interface ApiResult<T> {
    code: number
    message: string
    data: T
}

export interface UserInfo {
    id: number
    userName: string
    phone: string
    address: string
    role: number    // 1=超管, 2=普通用户, 3=运营
}

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

export interface CartInfo {
    id: number
    clothId: number
    clothSize: string
    amount: number
    userId: number
    date: string
    clothName: string | null   // null 表示商品已下架
    image: string
    price: number
}

export interface OrderItemInfo {
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
    orderItems?: OrderItemInfo[]
}

// --- 认证接口 ---

// 登录
export async function login(account: string, password: string) {
    const res = await api.post<ApiResult<{ user: UserInfo; token: string }>>('/user/login', { account, password })
    if (res.data.code === 200) {
        localStorage.setItem('token', res.data.data.token)
        localStorage.setItem('user', JSON.stringify(res.data.data.user))
    }
    return res
}

// 注册
export async function register(data: { userName: string; password: string; phone: string; address: string }) {
    return api.post<ApiResult<{ user: UserInfo; token: string }>>('/user/register', data)
}

// --- 商品接口 ---

// 商品列表
export function getClothesList() {
    return api.get<ApiResult<ClothesInfo[]>>('/clothes')
}

// 商品搜索
export function searchClothes(params: { clothName?: string; style?: string; typeId?: number }) {
    return api.get<ApiResult<ClothesInfo[]>>('/clothes/search', { params })
}

// 商品详情
export function getClothesById(id: number) {
    return api.get<ApiResult<ClothesInfo>>(`/clothes/${id}`)
}

// 类型列表
export function getTypes() {
    return api.get<ApiResult<TypeInfo[]>>('/types')
}

// 按类型查尺码
export function getSizes(typeId: number) {
    return api.get<ApiResult<SizeInfo[]>>('/sizes', { params: { typeId } })
}

// --- 购物车接口 ---

// 获取购物车
export function getCart() {
    return api.get<ApiResult<CartInfo[]>>('/cart')
}

// 加入购物车
export function addToCart(data: { clothId: number; clothSize: string; amount: number }) {
    return api.post<ApiResult<CartInfo>>('/cart', data)
}

// 修改数量
export function updateCartItem(id: number, amount: number) {
    return api.put<ApiResult<null>>(`/cart/${id}`, { amount })
}

// 删除条目
export function deleteCartItem(id: number) {
    return api.delete<ApiResult<null>>(`/cart/${id}`)
}

// 结算下单
export function checkout(ids: number[]) {
    return api.post<ApiResult<OrderInfo>>('/cart/checkout', { ids })
}

// --- 订单接口 ---

// 我的订单
export function getOrders() {
    return api.get<ApiResult<OrderInfo[]>>('/user/orders')
}

// 支付
export function payOrder(id: number) {
    return api.put<ApiResult<null>>(`/user/orders/${id}/pay`)
}

// 确认收货
export function confirmReceived(id: number) {
    return api.put<ApiResult<null>>(`/user/orders/${id}/confirm`)
}

// --- 个人中心接口 ---

// 修改密码
export function updatePassword(data: { oldPassword: string; newPassword: string }) {
    return api.put<ApiResult<null>>('/user/password', data)
}

// 修改资料
export function updateProfile(data: { phone: string; address: string }) {
    return api.put<ApiResult<UserInfo>>('/user/profile', data)
}

export default api
