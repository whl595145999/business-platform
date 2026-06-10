import type { IPageQuery } from '@/api/types/backend'

/** 角色列表查询（与 admin-web RoleQuery 一致） */
export interface IRoleQuery extends IPageQuery {
  roleName?: string
  roleKey?: string
  status?: string
}

/** 角色列表项（与 admin-web RoleVO 一致） */
export interface IRoleVo {
  roleId: string | number
  roleName: string
  roleKey: string
  roleSort?: number
  status: string
  dataScope?: string
  menuCheckStrictly?: boolean
  deptCheckStrictly?: boolean
  remark?: string
  createTime?: string
  [key: string]: any
}

/** 角色表单（新增/修改，与 admin-web RoleForm 对齐） */
export interface IRoleForm {
  roleId?: string | number
  roleName: string
  roleKey: string
  roleSort?: number
  status: string
  menuIds?: Array<string | number>
  deptIds?: Array<string | number>
  menuCheckStrictly?: boolean
  deptCheckStrictly?: boolean
  remark?: string
  dataScope?: string
}
