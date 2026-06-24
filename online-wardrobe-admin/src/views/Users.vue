<script setup lang="ts">
import { reactive, ref, onMounted, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import { getUsers, addUser, updateUser, deleteUser, approveUser, rejectUser, undoRejectUser, type UserInfo } from '../api'

const auth = useAuthStore()

function maskPhone(phone?: string) {
    if (!phone || phone.length < 7) return phone || '-'
    return phone.slice(0, 3) + '****' + phone.slice(7)
}

const users = ref<UserInfo[]>([])
const loading = ref(false)
const deletingIds = ref(new Set<number>())
const approveLoading = ref<Set<number>>(new Set())

const currentPage = ref(1)
const pageSize = 10

const searchForm = reactive({
    userName: '',
    phone: '',
    status: null as number | null,
})

const dialogVisible = ref(false)
const dialogTitle = ref('添加用户')
const isEdit = ref(false)
const editId = ref(0)
const submitLoading = ref(false)

const formRef = ref()
const form = reactive({
    userName: '',
    password: '',
    role: 2,
})

const roleOptions = [
    { label: '超级管理员', value: 1 },
    { label: '普通用户', value: 2 },
    { label: '运营人员', value: 3 },
]

const roleLabel = computed(() => {
    const map: Record<number, string> = { 1: '超级管理员', 2: '普通用户', 3: '运营人员' }
    return (role: number) => map[role] ?? '未知'
})

const rules = {
    userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
    role: [{ required: true, message: '请选择角色', trigger: 'change' }],
}

onMounted(() => {
    fetchUsers()
})

watch(() => searchForm.status, () => {
    currentPage.value = 1
})

async function fetchUsers() {
    loading.value = true
    try {
        const res = await getUsers()
        users.value = res.data.data || []
    } finally {
        loading.value = false
    }
}

async function handleSearch() {
    currentPage.value = 1
    loading.value = true
    try {
        const res = await getUsers({
            userName: searchForm.userName || undefined,
            phone: searchForm.phone || undefined,
        })
        users.value = res.data.data || []
    } finally {
        loading.value = false
    }
}

function handleReset() {
    searchForm.userName = ''
    searchForm.phone = ''
    searchForm.status = null
    currentPage.value = 1
    fetchUsers()
}

function openAddDialog() {
    dialogTitle.value = '添加用户'
    isEdit.value = false
    editId.value = 0
    form.userName = ''
    form.password = ''
    form.role = 2
    dialogVisible.value = true
}

function openEditDialog(row: UserInfo) {
    dialogTitle.value = '编辑用户'
    isEdit.value = true
    editId.value = row.id
    form.userName = row.userName
    form.password = ''
    form.role = row.role
    dialogVisible.value = true
}

async function handleSubmit() {
    const valid = await formRef.value?.validate().catch(() => false)
    if (!valid) return
    submitLoading.value = true
    try {
        if (isEdit.value) {
            const data: Record<string, unknown> = { role: form.role }
            if (form.password) {
                data.password = form.password
            }
            await updateUser(editId.value, data as any)
            ElMessage.success('修改成功')
        } else {
            await addUser({
                userName: form.userName,
                password: form.password,
                role: form.role,
            })
            ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        fetchUsers()
    } catch (err: any) {
        const msg = err?.response?.data?.message || err?.message || '操作失败'
        ElMessage.error(msg)
    } finally {
        submitLoading.value = false
    }
}

async function handleDelete(row: UserInfo) {
    deletingIds.value.add(row.id)
    try {
        await deleteUser(row.id)
        ElMessage.success('删除成功')
        fetchUsers()
    } catch (err: any) {
        const msg = err?.response?.data?.message || err?.message || '删除失败'
        ElMessage.error(msg)
    } finally {
        deletingIds.value.delete(row.id)
    }
}

async function handleApprove(row: UserInfo) {
    approveLoading.value.add(row.id)
    try {
        await approveUser(row.id)
        ElMessage.success('已通过审批')
        fetchUsers()
    } catch (err: any) {
        const msg = err?.response?.data?.message || err?.message || '操作失败'
        ElMessage.error(msg)
    } finally {
        approveLoading.value.delete(row.id)
    }
}

async function handleReject(row: UserInfo) {
    approveLoading.value.add(row.id)
    try {
        await rejectUser(row.id)
        ElMessage.success('已拒绝')
        fetchUsers()
    } catch (err: any) {
        const msg = err?.response?.data?.message || err?.message || '操作失败'
        ElMessage.error(msg)
    } finally {
        approveLoading.value.delete(row.id)
    }
}

async function handleUndoReject(row: UserInfo) {
    approveLoading.value.add(row.id)
    try {
        await undoRejectUser(row.id)
        ElMessage.success('已撤销拒绝')
        fetchUsers()
    } catch (err: any) {
        const msg = err?.response?.data?.message || err?.message || '操作失败'
        ElMessage.error(msg)
    } finally {
        approveLoading.value.delete(row.id)
    }
}

const filteredUsers = computed(() => {
    if (searchForm.status === null || searchForm.status === undefined) return users.value
    return users.value.filter(u => u.status === searchForm.status)
})

const pagedData = computed(() => {
    const start = (currentPage.value - 1) * pageSize
    return filteredUsers.value.slice(start, start + pageSize)
})
</script>

<template>
    <el-card>
        <el-form :inline="true" :model="searchForm" @submit.prevent="handleSearch">
            <el-form-item label="用户名">
                <el-input v-model="searchForm.userName" placeholder="请输入" clearable />
            </el-form-item>
            <el-form-item label="手机号">
                <el-input v-model="searchForm.phone" placeholder="请输入" clearable />
            </el-form-item>
            <el-form-item label="状态">
                <el-select v-model="searchForm.status" placeholder="全部" clearable style="width:120px">
                    <el-option label="全部" :value="null" />
                    <el-option label="待审核" :value="0" />
                    <el-option label="已通过" :value="1" />
                    <el-option label="已拒绝" :value="2" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="handleSearch">查询</el-button>
                <el-button @click="handleReset">重置</el-button>
            </el-form-item>
        </el-form>
    </el-card>

    <el-card style="margin-top:16px">
        <div class="toolbar">
            <el-button type="primary" @click="openAddDialog">添加用户</el-button>
        </div>
        <el-table :data="pagedData" v-loading="loading" border stripe style="margin-top:12px">
            <template #empty>
                <el-empty description="暂无数据" />
            </template>
            <el-table-column prop="id" label="编号" width="80" />
            <el-table-column prop="userName" label="用户名" min-width="120" />
            <el-table-column label="手机号" width="140">
                <template #default="{ row }">{{ maskPhone(row.phone) }}</template>
            </el-table-column>
            <el-table-column label="角色" width="140">
                <template #default="{ row }">
                    <el-tag :type="row.role === 1 ? 'danger' : row.role === 3 ? 'warning' : ''" size="small">
                        {{ roleLabel(row.role) }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
                <template #default="{ row }">
                    <el-tag
                        :type="row.status === 0 ? 'warning' : row.status === 2 ? 'danger' : 'success'"
                        size="small"
                    >
                        {{ row.status === 0 ? '待审核' : row.status === 2 ? '已拒绝' : '已通过' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
                <template #default="{ row }">
                    <template v-if="row.status === 0">
                        <el-popconfirm title="确定通过该运营人员的注册申请吗？" @confirm="handleApprove(row)">
                            <template #reference>
                                <el-button type="primary" link :loading="approveLoading.has(row.id)">通过</el-button>
                            </template>
                        </el-popconfirm>
                        <el-popconfirm title="确定拒绝该运营人员的注册申请吗？" @confirm="handleReject(row)">
                            <template #reference>
                                <el-button type="warning" link :loading="approveLoading.has(row.id)">拒绝</el-button>
                            </template>
                        </el-popconfirm>
                    </template>
                    <template v-else-if="row.status === 2">
                        <el-popconfirm title="确定撤销拒绝该用户吗？" @confirm="handleUndoReject(row)">
                            <template #reference>
                                <el-button type="info" link :loading="approveLoading.has(row.id)">撤销拒绝</el-button>
                            </template>
                        </el-popconfirm>
                        <el-popconfirm title="确定删除该用户吗？" @confirm="handleDelete(row)">
                            <template #reference>
                                <el-button type="danger" link :disabled="row.id === auth.user?.id" :loading="deletingIds.has(row.id)">删除</el-button>
                            </template>
                        </el-popconfirm>
                    </template>
                    <template v-else>
                        <el-button type="primary" link @click="openEditDialog(row)" :disabled="row.id === auth.user?.id">编辑</el-button>
                        <el-popconfirm title="确定删除该用户吗？" @confirm="handleDelete(row)">
                            <template #reference>
                                <el-button type="danger" link :disabled="row.id === auth.user?.id" :loading="deletingIds.has(row.id)">删除</el-button>
                            </template>
                        </el-popconfirm>
                    </template>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="filteredUsers.length"
            layout="total, prev, pager, next"
            background
            style="margin-top:16px;justify-content:flex-end"
        />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="用户名" prop="userName">
                <el-input v-model="form.userName" placeholder="请输入" :disabled="isEdit" />
            </el-form-item>
            <el-form-item v-if="!isEdit" label="密码" prop="password">
                <el-input v-model="form.password" type="password" placeholder="请输入" show-password />
            </el-form-item>
            <el-form-item label="角色" prop="role">
                <el-select v-model="form.role" placeholder="请选择角色" style="width:100%">
                    <el-option v-for="opt in roleOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
                </el-select>
            </el-form-item>
            <el-form-item v-if="isEdit" label="新密码">
                <el-input v-model="form.password" type="password" placeholder="留空则不修改密码" show-password />
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交</el-button>
        </template>
    </el-dialog>
</template>

<style scoped>
.toolbar {
    display: flex;
    justify-content: flex-end;
}
</style>
