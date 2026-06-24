export function getImageUrl(filename: string | undefined | null): string {
  if (!filename) return ''
  return `${import.meta.env.VITE_IMAGE_BASE_URL}/${filename}`
}
