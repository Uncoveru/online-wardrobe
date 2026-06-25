import axios from 'axios'
import router from '../router'

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
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
            if (router.currentRoute.value.path !== '/login') {
                router.push('/login').catch(() => {})
            }
        }
        return Promise.reject(error)
    },
)

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
    role: number
    status: number
}

export interface ApiResult<T> {
    code: number
    message: string
    data: T
}

export async function login(params: LoginParams) {
    const res = await api.post<ApiResult<{ user: UserInfo; token: string }>>('/admin/login', params)
    if (res.data.code === 200) {
        localStorage.setItem('token', res.data.data.token)
        localStorage.setItem('user', JSON.stringify(res.data.data.user))
    }
    return res
}

export async function register(params: RegisterParams) {
    const res = await api.post<ApiResult<{ user: UserInfo; token: string }>>('/user/register', params)
    if (res.data.code === 200) {
        localStorage.setItem('token', res.data.data.token)
        localStorage.setItem('user', JSON.stringify(res.data.data.user))
    }
    return res
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

export function getClothesList() {
    return api.get<ApiResult<ClothesInfo[]>>('/clothes')
}

export function searchClothes(params: { clothName?: string; style?: string; typeId?: number }) {
    return api.get<ApiResult<ClothesInfo[]>>('/clothes/search', { params })
}

export function addClothes(form: FormData) {
    return api.post<ApiResult<ClothesInfo>>('/clothes', form, {
        headers: { 'Content-Type': 'multipart/form-data' },
    })
}

export function updateClothes(id: number, form: FormData) {
    return api.post<ApiResult<ClothesInfo>>(`/clothes/${id}`, form, {
        headers: { 'Content-Type': 'multipart/form-data' },
    })
}

export function deleteClothes(id: number) {
    return api.delete<ApiResult<null>>(`/clothes/${id}`)
}

export function getTypes() {
    return api.get<ApiResult<TypeInfo[]>>('/types')
}

export function getSizes(typeId: number) {
    return api.get<ApiResult<SizeInfo[]>>('/sizes', { params: { typeId } })
}

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
    status: string
    userId: number
    address: string
    time: string
    orderItems?: OrderItem[]
}

export function getOrders(params?: { userName?: string; status?: string }) {
    return api.get<ApiResult<OrderInfo[]>>('/orders', { params })
}

export function shipOrder(id: number) {
    return api.put<ApiResult<null>>(`/orders/${id}/ship`)
}

export function getUsers(params?: { userName?: string; phone?: string }) {
    return api.get<ApiResult<UserInfo[]>>('/admin/users', { params })
}

export function addUser(data: Partial<UserInfo> & { password: string }) {
    return api.post<ApiResult<UserInfo>>('/admin/users', data)
}

export function updateUser(id: number, data: Partial<UserInfo> & { password?: string }) {
    return api.put<ApiResult<UserInfo>>(`/admin/users/${id}`, data)
}

export function deleteUser(id: number) {
    return api.delete<ApiResult<null>>(`/admin/users/${id}`)
}

export function registerOperator(data: { userName: string; password: string; phone: string; address: string }) {
    return api.post<ApiResult<{ message: string }>>('/user/register-operator', data)
}

export function approveUser(id: number) {
    return api.put<ApiResult<null>>(`/admin/users/${id}/approve`)
}

export function rejectUser(id: number) {
    return api.put<ApiResult<null>>(`/admin/users/${id}/reject`)
}

export function undoRejectUser(id: number) {
    return api.put<ApiResult<null>>(`/admin/users/${id}/undo-reject`)
}

export default api
