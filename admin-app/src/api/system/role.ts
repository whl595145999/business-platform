import type { IPageResult } from '@/api/types/backend'
import type { IRoleForm, IRoleQuery, IRoleVo } from '@/api/types/system/role'
import { http } from '@/http/http'

/** 查询角色列表（admin-web: GET /system/role/list） */
export function listRole(query: IRoleQuery) {
  return http.get<IPageResult<IRoleVo>>('/system/role/list', query)
}

/** 查询角色详情（admin-web: GET /system/role/:roleId） */
export function getRole(roleId?: string | number) {
  const id = roleId === undefined || roleId === null || roleId === '' ? '' : String(roleId)
  return http.get<{ data?: IRoleVo, role?: IRoleVo, menuIds?: Array<string | number> }>(`/system/role/${id}`)
}

/** 新增角色（admin-web: POST /system/role） */
export function addRole(data: IRoleForm) {
  return http.post<void>('/system/role', data)
}

/** 修改角色（admin-web: PUT /system/role） */
export function updateRole(data: IRoleForm) {
  return http.put<void>('/system/role', data)
}

/** 删除角色（admin-web: DELETE /system/role/:roleId） */
export function delRole(roleId: string | number | Array<string | number>) {
  const id = Array.isArray(roleId) ? roleId.join(',') : roleId
  return http.delete<void>(`/system/role/${id}`)
}

/** 修改角色状态（admin-web: PUT /system/role/changeStatus） */
export function changeRoleStatus(roleId: string | number, status: string) {
  return http.put<void>('/system/role/changeStatus', { roleId, status })
}
