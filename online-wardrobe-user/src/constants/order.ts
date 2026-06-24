export const ORDER_STATUS = {
  UNPAID: '0',
  PAID: '1',
  SHIPPED: '2',
  RECEIVED: '3',
} as const

export type OrderStatus = (typeof ORDER_STATUS)[keyof typeof ORDER_STATUS]

export const ORDER_STATUS_LABELS: Record<string, string> = {
  [ORDER_STATUS.UNPAID]: '未支付',
  [ORDER_STATUS.PAID]: '未发货',
  [ORDER_STATUS.SHIPPED]: '已发货',
  [ORDER_STATUS.RECEIVED]: '已收货',
}

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
