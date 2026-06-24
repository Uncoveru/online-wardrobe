/** 与 online-wardrobe-user/src/constants/order.ts 保持同步 */
export const ORDER_STATUS = {
  UNPAID: '0',
  PAID: '1',
  SHIPPED: '2',
  RECEIVED: '3',
} as const

export const ORDER_STATUS_LABELS: Record<string, string> = {
  [ORDER_STATUS.UNPAID]: '未支付',
  [ORDER_STATUS.PAID]: '未发货',
  [ORDER_STATUS.SHIPPED]: '已发货',
  [ORDER_STATUS.RECEIVED]: '已收货',
}
