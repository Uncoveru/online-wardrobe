import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 10000,
})

api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

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
    role: number
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
    clothName: string | null
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
    status: string
    userId: number
    address: string
    time: string
    orderItems?: OrderItemInfo[]
}

export async function login(account: string, password: string) {
    const res = await api.post<ApiResult<{ user: UserInfo; token: string }>>('/user/login', { account, password })
    if (res.data.code === 200) {
        localStorage.setItem('token', res.data.data.token)
        localStorage.setItem('user', JSON.stringify(res.data.data.user))
    }
    return res
}

export async function register(data: { userName: string; password: string; phone: string; address: string }) {
    return api.post<ApiResult<{ user: UserInfo; token: string }>>('/user/register', data)
}

export function getClothesList() {
    return api.get<ApiResult<ClothesInfo[]>>('/clothes')
}

export function searchClothes(params: { clothName?: string; style?: string; typeId?: number }) {
    return api.get<ApiResult<ClothesInfo[]>>('/clothes/search', { params })
}

export function getClothesById(id: number) {
    return api.get<ApiResult<ClothesInfo>>(`/clothes/${id}`)
}

export function getTypes() {
    return api.get<ApiResult<TypeInfo[]>>('/types')
}

export function getSizes(typeId: number) {
    return api.get<ApiResult<SizeInfo[]>>('/sizes', { params: { typeId } })
}

export function getCart() {
    return api.get<ApiResult<CartInfo[]>>('/cart')
}

export function addToCart(data: { clothId: number; clothSize: string; amount: number }) {
    return api.post<ApiResult<CartInfo>>('/cart', data)
}

export function updateCartItem(id: number, amount: number) {
    return api.put<ApiResult<null>>(`/cart/${id}`, { amount })
}

export function deleteCartItem(id: number) {
    return api.delete<ApiResult<null>>(`/cart/${id}`)
}

export function checkout(ids: number[]) {
    return api.post<ApiResult<OrderInfo>>('/cart/checkout', { ids })
}

export function getOrders() {
    return api.get<ApiResult<OrderInfo[]>>('/user/orders')
}

export function payOrder(id: number) {
    return api.put<ApiResult<null>>(`/user/orders/${id}/pay`)
}

export function confirmReceived(id: number) {
    return api.put<ApiResult<null>>(`/user/orders/${id}/confirm`)
}

export function updatePassword(data: { oldPassword: string; newPassword: string }) {
    return api.put<ApiResult<null>>('/user/password', data)
}

export function updateProfile(data: { phone: string; address: string }) {
    return api.put<ApiResult<UserInfo>>('/user/profile', data)
}

export default api
