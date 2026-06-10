import type { IPageQuery } from '@/api/types/backend'

/** 组织档案查询（admin-web: OrgQuery） */
export interface IOrgQuery extends IPageQuery {
  tenantId?: string
  orgCode?: string
  orgName?: string
  orgType?: number
  status?: number
}

/** 组织档案 VO（admin-web: OrgVO） */
export interface IOrgVo {
  id: number
  orgCode: string
  orgName: string
  orgType: number
  parentOrgId?: number
  parentOrgName?: string
  linkedDeptId?: number
  status: number
  sortOrder?: number
  remark?: string
  updateTime?: string
}

/** 组织档案表单（admin-web: OrgForm） */
export interface IOrgForm {
  tenantId?: string
  orgCode?: string
  orgName?: string
  orgType?: number
  parentOrgId?: number
  linkedDeptId?: number
  status?: number
  sortOrder?: number
  remark?: string
}
