import { useDictStore } from '@/store/dict'
import { computed, onMounted } from 'vue'

/**
 * 字典 Composable
 * @example const dict = useDict('sys_user_sex'); dict.sys_user_sex.value
 */
export function useDict(...dictTypes: string[]) {
  const dictStore = useDictStore()

  onMounted(() => {
    dictTypes.forEach(type => dictStore.loadDict(type))
  })

  const refs = dictTypes.reduce((acc, type) => {
    acc[type] = computed(() => dictStore.getDict(type))
    return acc
  }, {} as Record<string, ReturnType<typeof computed>>)

  return {
    ...refs,
    getDictLabel: dictStore.getDictLabel,
    loadDict: dictStore.loadDict,
  }
}
