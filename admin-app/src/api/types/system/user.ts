import type { IPageQuery } from '@/api/types/backend'

/** 用户列表查询（与 admin-web UserQuery 一致） */
export interface IUserQuery extends IPageQuery {
  userName?: string
  nickName?: string
  phonenumber?: string
  status?: string
  deptId?: string | number
  roleId?: string | number
}

/** 用户列表项（与 admin-web UserVO 一致） */
export interface IUserVo {
  userId: string | number
  tenantId?: string
  deptId?: number
  userName: string
  nickName: string
  phonenumber?: string
  email?: string
  sex?: string
  avatar?: string
  status: string
  deptName?: string
  loginIp?: string
  loginDate?: string
  remark?: string
  createTime?: string
  [key: string]: any
}

/** 用户表单（新增/修改，与 admin-web UserForm 对齐） */
export interface IUserForm {
  userId?: string | number
  deptId?: number
  userName: string
  nickName?: string
  password?: string
  phonenumber?: string
  email?: string
  sex?: string
  status: string
  remark?: string
  postIds?: string[]
  roleIds?: string[]
}
