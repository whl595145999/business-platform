import type { IDictData } from '@/api/types/backend'
import { http } from '@/http/http'

/** 根据字典类型查询字典数据 */
export function getDicts(dictType: string) {
  return http.get<IDictData[]>(`/system/dict/data/type/${dictType}`)
}
