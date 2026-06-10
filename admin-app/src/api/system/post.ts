import type { IPageResult } from '@/api/types/backend'
import type { IPostForm, IPostQuery, IPostVo } from '@/api/types/system/post'
import { http } from '@/http/http'

/** 查询岗位列表（admin-web: GET /system/post/list） */
export function listPost(query: IPostQuery) {
  return http.get<IPageResult<IPostVo>>('/system/post/list', query)
}

/** 查询岗位详情（admin-web: GET /system/post/:postId） */
export function getPost(postId?: string | number) {
  const id = postId === undefined || postId === null || postId === '' ? '' : String(postId)
  return http.get<IPostVo>(`/system/post/${id}`)
}

/** 新增岗位（admin-web: POST /system/post） */
export function addPost(data: IPostForm) {
  return http.post<void>('/system/post', data)
}

/** 修改岗位（admin-web: PUT /system/post） */
export function updatePost(data: IPostForm) {
  return http.put<void>('/system/post', data)
}

/** 删除岗位（admin-web: DELETE /system/post/:postId） */
export function delPost(postId: string | number | Array<string | number>) {
  const id = Array.isArray(postId) ? postId.join(',') : postId
  return http.delete<void>(`/system/post/${id}`)
}
