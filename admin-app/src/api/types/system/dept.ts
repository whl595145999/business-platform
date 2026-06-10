import type { IPageQuery } from '@/api/types/backend'

/** 部门查询（与 admin-web DeptQuery 一致） */
export interface IDeptQuery extends IPageQuery {
  deptName?: string
  status?: string
}

/** 部门（与 admin-web DeptVO 一致，含树形 children） */
export interface IDeptVo {
  deptId: string | number
  parentId?: string | number
  ancestors?: string
  deptName: string
  orderNum?: number
  leader?: string
  phone?: string
  email?: string
  status: string
  createTime?: string
  children?: IDeptVo[]
  [key: string]: any
}

/** 部门表单（新增/修改） */
export interface IDeptForm {
  deptId?: string | number
  parentId?: string | number
  deptName: string
  orderNum?: number
  leader?: string
  phone?: string
  email?: string
  status: string
}
