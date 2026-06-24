interface AxiosErrorLike {
  response?: { data?: { message?: string } }
}

export function getErrorMessage(error: unknown, fallback = '操作失败'): string {
  if (error && typeof error === 'object' && 'response' in error) {
    const msg = (error as AxiosErrorLike).response?.data?.message
    if (msg) return msg
  }
  return fallback
}
