import type { IRouterVo } from '@/api/types/backend'
import { http } from '@/http/http'

/** 获取当前用户路由/菜单 */
export function getRouters() {
  return http.get<IRouterVo[]>('/system/menu/getRouters')
}
