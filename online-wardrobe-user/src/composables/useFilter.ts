/**
 * 全局搜索关键词状态（跨组件共享）
 */
import { ref } from 'vue'

const searchKeyword = ref('')

export function useFilter() {
  function setSearch(keyword: string) {
    searchKeyword.value = keyword
  }

  function resetAll() {
    searchKeyword.value = ''
  }

  return { searchKeyword, setSearch, resetAll }
}
