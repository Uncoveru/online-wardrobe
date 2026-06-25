/**
 * 订单状态常量（需与后端枚举保持一致）
 */
export const ORDER_STATUS = {
  UNPAID: '0',    // 未支付
  PAID: '1',      // 未发货
  SHIPPED: '2',   // 已发货
  RECEIVED: '3',  // 已收货
} as const

export const ORDER_STATUS_LABELS: Record<string, string> = {
  [ORDER_STATUS.UNPAID]: '未支付',
  [ORDER_STATUS.PAID]: '未发货',
  [ORDER_STATUS.SHIPPED]: '已发货',
  [ORDER_STATUS.RECEIVED]: '已收货',
}
