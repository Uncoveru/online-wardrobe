<script setup lang="ts">
import { reactive, ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { UploadFile } from 'element-plus'
import {
    getClothesList,
    searchClothes,
    addClothes,
    updateClothes,
    deleteClothes,
    getTypes,
    type ClothesInfo,
    type TypeInfo,
} from '../api'

const clothesList = ref<ClothesInfo[]>([])
const types = ref<TypeInfo[]>([])
const loading = ref(false)

const currentPage = ref(1)
const pageSize = 10
const imageBaseUrl = import.meta.env.VITE_IMAGE_BASE_URL || 'http://localhost:8080/images'

const pagedData = computed(() => {
    const start = (currentPage.value - 1) * pageSize
    return clothesList.value.slice(start, start + pageSize)
})

const styleOptions = ['时尚', '休闲', '商务', '运动', '正式', '复古', '户外']

const searchForm = reactive({
    clothName: '',
    style: '',
    typeId: undefined as number | undefined,
})

const dialogVisible = ref(false)
const dialogTitle = ref('上架服装')
const isEdit = ref(false)
const editId = ref(0)
const submitLoading = ref(false)
const fileList = ref<UploadFile[]>([])

const formRef = ref()
const form = reactive({
    clothName: '',
    typeId: undefined as number | undefined,
    style: '',
    price: undefined as number | undefined,
})

const rules = {
    clothName: [{ required: true, message: '请输入服装名称', trigger: 'blur' }],
    typeId: [{ required: true, message: '请选择服装类别', trigger: 'change' }],
    style: [{ required: true, message: '请输入服装风格', trigger: 'blur' }],
    price: [{ required: true, message: '请输入服装价格', trigger: 'blur' }],
}

onMounted(() => {
    fetchClothes()
    fetchTypes()
})

async function fetchClothes() {
    loading.value = true
    try {
        const res = await getClothesList()
        clothesList.value = res.data.data || []
    } finally {
        loading.value = false
    }
}

async function fetchTypes() {
    const res = await getTypes()
    types.value = res.data.data || []
}

async function handleSearch() {
    currentPage.value = 1
    loading.value = true
    try {
        const res = await searchClothes({
            clothName: searchForm.clothName || undefined,
            style: searchForm.style || undefined,
            typeId: searchForm.typeId,
        })
        clothesList.value = res.data.data || []
    } finally {
        loading.value = false
    }
}

function handleReset() {
    searchForm.clothName = ''
    searchForm.style = ''
    searchForm.typeId = undefined
    currentPage.value = 1
    fetchClothes()
}

function openAddDialog() {
    dialogTitle.value = '上架服装'
    isEdit.value = false
    editId.value = 0
    form.clothName = ''
    form.typeId = undefined
    form.style = ''
    form.price = undefined
    fileList.value = []
    dialogVisible.value = true
}

function openEditDialog(row: ClothesInfo) {
    dialogTitle.value = '修改服装'
    isEdit.value = true
    editId.value = row.id
    form.clothName = row.clothName
    form.typeId = row.typeId
    form.style = row.style
    form.price = row.price
    fileList.value = []
    dialogVisible.value = true
}

async function handleSubmit() {
    const valid = await formRef.value?.validate().catch(() => false)
    if (!valid) return
    submitLoading.value = true
    try {
        const fd = new FormData()
        fd.append('clothName', form.clothName!)
        fd.append('typeId', String(form.typeId))
        fd.append('style', form.style!)
        fd.append('price', String(form.price))
        if (fileList.value.length > 0) {
            fd.append('file', fileList.value[0].raw!)
        }

        if (isEdit.value) {
            await updateClothes(editId.value, fd)
            ElMessage.success('修改成功')
        } else {
            await addClothes(fd)
            ElMessage.success('上架成功')
        }
        dialogVisible.value = false
        fetchClothes()
    } catch (err: any) {
        const msg = err?.response?.data?.message || err?.message || '操作失败'
        ElMessage.error(msg)
    } finally {
        submitLoading.value = false
    }
}

async function handleDelete(row: ClothesInfo) {
    try {
        await deleteClothes(row.id)
        ElMessage.success('删除成功')
        fetchClothes()
    } catch (err: any) {
        const msg = err?.response?.data?.message || err?.message || '删除失败'
        ElMessage.error(msg)
    }
}

function getTypeName(typeId: number) {
    return types.value.find(t => t.id === typeId)?.typeName || ''
}
</script>

<template>
    <el-card>
        <el-form :inline="true" :model="searchForm" @submit.prevent="handleSearch">
            <el-form-item label="服装名称">
                <el-input v-model="searchForm.clothName" placeholder="请输入" clearable />
            </el-form-item>
            <el-form-item label="服装类别">
                <el-select v-model="searchForm.typeId" placeholder="全部" clearable style="width:140px">
                    <el-option v-for="t in types" :key="t.id" :label="t.typeName" :value="t.id" />
                </el-select>
            </el-form-item>
            <el-form-item label="服装风格">
                <el-select v-model="searchForm.style" placeholder="全部" clearable filterable allow-create style="width:140px">
                    <el-option v-for="s in styleOptions" :key="s" :label="s" :value="s" />
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
            <el-button type="primary" @click="openAddDialog">上架服装</el-button>
        </div>
        <el-table :data="pagedData" v-loading="loading" border stripe style="margin-top:12px">
            <template #empty>
                <el-empty description="暂无数据" />
            </template>
            <el-table-column prop="id" label="编号" width="80" />
            <el-table-column label="服装图片" width="100">
                <template #default="{ row }">
                    <el-image
                        v-if="row.image"
                        :src="`${imageBaseUrl}/${row.image}`"
                        style="width:60px;height:60px"
                        fit="cover"
                    />
                    <span v-else>无图片</span>
                </template>
            </el-table-column>
            <el-table-column prop="clothName" label="服装名称" />
            <el-table-column label="服装类别" width="100">
                <template #default="{ row }">{{ getTypeName(row.typeId) }}</template>
            </el-table-column>
            <el-table-column prop="style" label="服装风格" width="120" />
            <el-table-column prop="price" label="价格" width="100">
                <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column label="操作" width="160" fixed="right">
                <template #default="{ row }">
                    <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
                    <el-popconfirm title="确定删除该服装吗？" @confirm="handleDelete(row)">
                        <template #reference>
                            <el-button type="danger" link>删除</el-button>
                        </template>
                    </el-popconfirm>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="clothesList.length"
            layout="total, prev, pager, next"
            background
            style="margin-top:16px;justify-content:flex-end"
        />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="服装名称" prop="clothName">
                <el-input v-model="form.clothName" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="服装类别" prop="typeId">
                <el-select v-model="form.typeId" placeholder="请选择" style="width:100%">
                    <el-option v-for="t in types" :key="t.id" :label="t.typeName" :value="t.id" />
                </el-select>
            </el-form-item>
            <el-form-item label="服装风格" prop="style">
                <el-select v-model="form.style" placeholder="例如：时尚、休闲、商务" filterable allow-create style="width:100%">
                    <el-option v-for="s in styleOptions" :key="s" :label="s" :value="s" />
                </el-select>
            </el-form-item>
            <el-form-item label="图片上传">
                <el-upload
                    v-model:file-list="fileList"
                    :auto-upload="false"
                    list-type="picture"
                    :limit="1"
                    accept="image/jpeg,image/png,image/gif,image/webp"
                >
                    <el-button type="primary">选择图片</el-button>
                    <template #tip>
                        <div class="el-upload__tip">支持 jpg/png/gif/webp 格式，不选择则保留原图</div>
                    </template>
                </el-upload>
            </el-form-item>
            <el-form-item label="服装价格" prop="price">
                <el-input-number v-model="form.price" :precision="2" :min="0" style="width:100%" />
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

:deep(.el-upload-list__item-name) {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 280px;
}
</style>
