import type { IDictData } from '@/api/types/backend'
import { getDicts } from '@/api/system/dict'
import { selectDictLabel } from '@/utils/format'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useDictStore = defineStore(
  'dict',
  () => {
    const dictMap = ref<Record<string, IDictData[]>>({})

    async function loadDict(dictType: string, force = false) {
      if (!force && dictMap.value[dictType]?.length)
        return dictMap.value[dictType]
      const list = await getDicts(dictType)
      dictMap.value[dictType] = list
      return list
    }

    function getDict(dictType: string) {
      return dictMap.value[dictType] || []
    }

    function getDictLabel(dictType: string, value?: string | number) {
      return selectDictLabel(
        getDict(dictType).map(d => ({ label: d.dictLabel, value: d.dictValue })),
        value,
      )
    }

    function clearDict() {
      dictMap.value = {}
    }

    return {
      dictMap,
      loadDict,
      getDict,
      getDictLabel,
      clearDict,
    }
  },
)
