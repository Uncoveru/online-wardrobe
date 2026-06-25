<script setup lang="ts">
import { ref, computed, nextTick, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { TableInstance } from 'element-plus'
import { getCart, updateCartItem, deleteCartItem, checkout, type CartInfo } from '../api'
import { getErrorMessage } from '../utils/error'

const router = useRouter()
const cartItems = ref<CartInfo[]>([])
const checkedIds = ref<number[]>([])
const loading = ref(false)
const tableRef = ref<TableInstance>()

onMounted(() => fetchCart())

async function fetchCart() {
  try {
    const res = await getCart()
    const savedIds = [...checkedIds.value]
    cartItems.value = res.data.data || []
    await nextTick()
    // Re-apply selections that still exist after refresh
    cartItems.value.forEach((item) => {
      if (savedIds.includes(item.id)) {
        tableRef.value?.toggleRowSelection(item, true)
      }
    })
    // Remove any stale IDs (items that no longer exist in cart)
    const currentIds = new Set(cartItems.value.map((i) => i.id))
    checkedIds.value = checkedIds.value.filter((id) => currentIds.has(id))
  } catch {}
}

function handleSelectionChange(rows: CartInfo[]) {
  checkedIds.value = rows.map((r) => r.id)
}

const allChecked = computed({
  get: () => cartItems.value.length > 0 && checkedIds.value.length === cartItems.value.length,
  set: (val: boolean) => {
    const table = tableRef.value
    if (!table) return
    cartItems.value.forEach((item) => {
      table.toggleRowSelection(item, val)
    })
  },
})

const totalPrice = computed(() =>
  cartItems.value
    .filter((i) => checkedIds.value.includes(i.id))
    .reduce((sum, i) => sum + (i.price || 0) * i.amount, 0),
)

function subtotal(item: CartInfo) {
  return (item.price || 0) * item.amount
}

async function handleAmountChange(item: CartInfo, amount: number) {
  if (amount < 1) return
  const previousAmount = item.amount
  item.amount = amount
  try {
    await updateCartItem(item.id, amount)
  } catch (e) {
    item.amount = previousAmount
    ElMessage.error(getErrorMessage(e, '修改数量失败'))
  }
}

async function handleDelete(item: CartInfo) {
  await deleteCartItem(item.id)
  ElMessage.success('已删除')
  checkedIds.value = checkedIds.value.filter((id) => id !== item.id)
  fetchCart()
}

async function handleCheckout() {
  if (checkedIds.value.length === 0) {
    ElMessage.warning('请选择结算商品')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确认结算选中的 ${checkedIds.value.length} 件商品，合计 ¥${totalPrice.value}？`,
      '确认结算',
      { type: 'warning' },
    )
  } catch {
    return
  }
  loading.value = true
  try {
    await checkout(checkedIds.value)
    ElMessage.success('结算成功，请到我的订单中查看')
    checkedIds.value = []
    fetchCart()
  } catch (e) {
    ElMessage.error(getErrorMessage(e, '结算失败'))
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="cart">
    <header class="cart-header">
      <h1>购物车</h1>
      <el-button link @click="router.push('/')">继续购物</el-button>
    </header>

    <el-card v-if="cartItems.length === 0">
      <div class="empty-cart">
        <p>购物车是空的</p>
        <el-button type="primary" @click="router.push('/')">去逛逛</el-button>
      </div>
    </el-card>

    <template v-else>
      <el-card>
        <el-table ref="tableRef" :data="cartItems" class="cart-table" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" :selectable="(row: CartInfo) => !!row.clothName" />
          <el-table-column label="商品" min-width="180">
            <template #default="{ row }">
              <span v-if="row.clothName">{{ row.clothName }}</span>
              <span v-else class="offline">商品已下架（服装编号{{ row.clothId }}）</span>
            </template>
          </el-table-column>
          <el-table-column label="尺码" width="100">
            <template #default="{ row }">
              <span>{{ row.clothSize === '均码' ? '均码' : `${row.clothSize || '--'}码` }}</span>
            </template>
          </el-table-column>
          <el-table-column label="单价" width="100">
            <template #default="{ row }">¥{{ row.price || '--' }}</template>
          </el-table-column>
          <el-table-column label="小计" width="100">
            <template #default="{ row }">¥{{ subtotal(row) }}</template>
          </el-table-column>
          <el-table-column label="数量" width="160">
            <template #default="{ row }">
              <el-input-number v-model="row.amount" :min="1" size="small" :disabled="!row.clothName" @change="handleAmountChange(row, $event)" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ row }">
              <el-popconfirm title="确定删除？" @confirm="handleDelete(row)">
                <template #reference>
                  <el-button type="danger" link>删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <div class="cart-footer">
        <el-checkbox v-model="allChecked">全选</el-checkbox>
        <div class="cart-summary">
          <span>已选 <strong>{{ checkedIds.length }}</strong> 件</span>
          <span class="cart-total">合计：<strong>¥{{ totalPrice }}</strong></span>
        </div>
        <el-button type="danger" :loading="loading" @click="handleCheckout">结算</el-button>
      </div>
    </template>
  </div>
</template>

<style scoped>
.cart {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.cart-table {
  overflow-x: auto;
}

.empty-cart {
  text-align: center;
  padding: 40px;
  color: var(--color-text-muted);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
}

.cart-summary {
  display: flex;
  align-items: center;
  gap: 16px;
  color: var(--color-text-secondary);
  font-size: 14px;
}

.cart-total {
  font-size: 16px;
  color: var(--color-primary);
}

.offline {
  color: #909399;
  font-style: italic;
}

@media (max-width: 768px) {
  .cart-footer {
    flex-wrap: wrap;
  }

  .cart-summary {
    order: 3;
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
