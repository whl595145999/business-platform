import type { IPageResult } from '@/api/types/backend'
import type { IUserForm, IUserQuery, IUserVo } from '@/api/types/system/user'
import { TOKEN_KEY } from '@/constants/app'
import { http } from '@/http/http'

/** 查询用户列表（admin-web: GET /system/user/list） */
export function listUser(query: IUserQuery) {
  return http.get<IPageResult<IUserVo>>('/system/user/list', query)
}

/** 查询用户详情（admin-web: GET /system/user/:userId） */
export function getUser(userId?: string | number) {
  const id = userId === undefined || userId === null || userId === '' ? '' : String(userId)
  return http.get<{ data?: IUserVo, user?: IUserVo, roles?: any[], posts?: any[] }>(`/system/user/${id}`)
}

/** 新增用户（admin-web: POST /system/user） */
export function addUser(data: IUserForm) {
  return http.post<void>('/system/user', data)
}

/** 修改用户（admin-web: PUT /system/user） */
export function updateUser(data: IUserForm) {
  return http.put<void>('/system/user', data)
}

/** 删除用户（admin-web: DELETE /system/user/:userId） */
export function delUser(userId: string | number | Array<string | number>) {
  const id = Array.isArray(userId) ? userId.join(',') : userId
  return http.delete<void>(`/system/user/${id}`)
}

/** 重置密码（admin-web: PUT /system/user/resetPwd，加密） */
export function resetUserPwd(userId: string | number, password: string) {
  return http.put<void>('/system/user/resetPwd', { userId, password }, undefined, undefined, { isEncrypt: true })
}

/** 修改用户状态（admin-web: PUT /system/user/changeStatus） */
export function changeUserStatus(userId: string | number, status: string) {
  return http.put<void>('/system/user/changeStatus', { userId, status })
}

/** 查询用户个人信息（admin-web: GET /system/user/profile） */
export function getUserProfile() {
  return http.get<{ user: IUserVo }>('/system/user/profile')
}

/** 修改用户个人信息（admin-web: PUT /system/user/profile） */
export function updateUserProfile(data: Partial<IUserForm>) {
  return http.put<void>('/system/user/profile', data)
}

/** 修改个人密码（admin-web: PUT /system/user/profile/updatePwd，加密） */
export function updateUserPwd(oldPassword: string, newPassword: string) {
  return http.put<void>(
    '/system/user/profile/updatePwd',
    { oldPassword, newPassword },
    undefined,
    undefined,
    { isEncrypt: true },
  )
}

/** 上传头像（admin-web: POST /system/user/profile/avatar） */
export function uploadAvatar(filePath: string) {
  return new Promise<any>((resolve, reject) => {
    uni.uploadFile({
      url: `${import.meta.env.VITE_SERVER_BASEURL}/system/user/profile/avatar`,
      filePath,
      name: 'avatarfile',
      header: {
        Authorization: `Bearer ${uni.getStorageSync(TOKEN_KEY)}`,
        clientid: import.meta.env.VITE_CLIENT_ID,
      },
      success: (res) => {
        const data = JSON.parse(res.data)
        if (data.code === 200)
          resolve(data.data)
        else
          reject(data)
      },
      fail: reject,
    })
  })
}
