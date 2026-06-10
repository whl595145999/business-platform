import type { IDeptForm, IDeptQuery, IDeptVo } from '@/api/types/system/dept'
import { http } from '@/http/http'

/** 查询部门列表（admin-web: GET /system/dept/list，树形） */
export function listDept(query?: IDeptQuery) {
  return http.get<IDeptVo[]>('/system/dept/list', query)
}

/** 查询部门详情（admin-web: GET /system/dept/:deptId） */
export function getDept(deptId?: string | number) {
  const id = deptId === undefined || deptId === null || deptId === '' ? '' : String(deptId)
  return http.get<IDeptVo>(`/system/dept/${id}`)
}

/** 查询部门列表（排除节点，用于上级部门选择） */
export function listDeptExcludeChild(deptId: string | number) {
  return http.get<IDeptVo[]>(`/system/dept/list/exclude/${deptId}`)
}

/** 新增部门（admin-web: POST /system/dept） */
export function addDept(data: IDeptForm) {
  return http.post<void>('/system/dept', data)
}

/** 修改部门（admin-web: PUT /system/dept） */
export function updateDept(data: IDeptForm) {
  return http.put<void>('/system/dept', data)
}

/** 删除部门（admin-web: DELETE /system/dept/:deptId） */
export function delDept(deptId: string | number) {
  return http.delete<void>(`/system/dept/${deptId}`)
}
