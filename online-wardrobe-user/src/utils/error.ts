/**
 * 通用错误消息提取
 */
interface AxiosErrorLike {
  response?: { data?: { message?: string } }
}

// 从 axios 错误对象中提取后端返回的 message，否则返回兜底文本
export function getErrorMessage(error: unknown, fallback = '操作失败'): string {
  if (error && typeof error === 'object' && 'response' in error) {
    const msg = (error as AxiosErrorLike).response?.data?.message
    if (msg) return msg
  }
  return fallback
}
