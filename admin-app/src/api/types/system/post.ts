import type { IPageQuery } from '@/api/types/backend'

/** 岗位列表查询（与 admin-web PostQuery 一致） */
export interface IPostQuery extends IPageQuery {
  postCode?: string
  postName?: string
  status?: string
}

/** 岗位列表项（与 admin-web PostVO 一致） */
export interface IPostVo {
  postId: string | number
  postCode: string
  postName: string
  postSort?: number
  status: string
  remark?: string
  createTime?: string
  [key: string]: any
}

/** 岗位表单（新增/修改） */
export interface IPostForm {
  postId?: string | number
  postCode: string
  postName: string
  postSort?: number
  status: string
  remark?: string
}
