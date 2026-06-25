/**
 * 订单状态常量 + Element Tag 类型映射
 */
export const ORDER_STATUS = {
  UNPAID: '0',    // 未支付
  PAID: '1',      // 未发货
  SHIPPED: '2',   // 已发货
  RECEIVED: '3',  // 已收货
} as const

export type OrderStatus = (typeof ORDER_STATUS)[keyof typeof ORDER_STATUS]

export const ORDER_STATUS_LABELS: Record<string, string> = {
  [ORDER_STATUS.UNPAID]: '未支付',
  [ORDER_STATUS.PAID]: '未发货',
  [ORDER_STATUS.SHIPPED]: '已发货',
  [ORDER_STATUS.RECEIVED]: '已收货',
}

// 状态 → el-tag type 映射
export function getOrderTagType(status: string): 'danger' | 'warning' | '' | 'success' | 'info' {
  switch (status) {
    case ORDER_STATUS.UNPAID:
      return 'danger'
    case ORDER_STATUS.PAID:
      return 'warning'
    case ORDER_STATUS.SHIPPED:
      return ''
    case ORDER_STATUS.RECEIVED:
      return 'success'
    default:
      return 'info'
  }
}
