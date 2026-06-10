import type { IPageResult } from '@/api/types/backend'
import type { IOrgForm, IOrgQuery, IOrgVo } from '@/api/types/scm/org'
import { http } from '@/http/http'

/** 分页查询组织档案（admin-web: GET /api/scm/wms/orgs） */
export function pageOrg(query: IOrgQuery) {
  return http.get<IPageResult<IOrgVo>>('/api/scm/wms/orgs', query)
}

/** 查询组织档案详情（admin-web: GET /api/scm/wms/orgs/:id） */
export function getOrg(id: string | number, tenantId?: string) {
  return http.get<IOrgVo>(`/api/scm/wms/orgs/${id}`, tenantId ? { tenantId } : undefined)
}

/** 新增组织档案（admin-web: POST /api/scm/wms/orgs） */
export function addOrg(data: IOrgForm) {
  return http.post<void>('/api/scm/wms/orgs', data)
}

/** 修改组织档案（admin-web: PUT /api/scm/wms/orgs/:id） */
export function updateOrg(id: string | number, data: IOrgForm) {
  return http.put<void>(`/api/scm/wms/orgs/${id}`, data)
}

/** 删除组织档案（admin-web: DELETE /api/scm/wms/orgs/:id） */
export function delOrg(id: string | number, tenantId?: string) {
  return http.delete<void>(`/api/scm/wms/orgs/${id}`, tenantId ? { tenantId } : undefined)
}

/** 组织下拉选项（admin-web: GET /api/scm/wms/orgs/options） */
export function listOrgOptions(tenantId?: string, status?: number) {
  const params: Record<string, string | number> = {}
  if (tenantId)
    params.tenantId = tenantId
  if (status !== undefined)
    params.status = status
  return http.get<IOrgVo[]>('/api/scm/wms/orgs/options', Object.keys(params).length ? params : undefined)
}
