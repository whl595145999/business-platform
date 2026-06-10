import type { ISysUser, IUserInfoVo } from '@/api/types/backend'
import { getUserInfo } from '@/api/auth'
import { DEFAULT_ROLE } from '@/constants/app'
import { getEnvBaseUrl } from '@/utils'
import { resolveAvatarUrl } from '@/utils/format'
import { defineStore } from 'pinia'
import { ref } from 'vue'

const defaultUser: ISysUser = {
  userId: -1,
  userName: '',
  nickName: '',
  avatar: '/static/images/default-avatar.png',
}

export const useUserStore = defineStore(
  'user',
  () => {
    const user = ref<ISysUser>({ ...defaultUser })
    const roles = ref<string[]>([])
    const permissions = ref<string[]>([])

    const setUserInfo = (data: IUserInfoVo) => {
      const baseUrl = getEnvBaseUrl()
      const sysUser = data.user || { ...defaultUser }
      user.value = {
        ...sysUser,
        avatar: resolveAvatarUrl(sysUser.avatar, baseUrl),
      }
      roles.value = data.roles?.length ? data.roles : [DEFAULT_ROLE]
      permissions.value = data.permissions || []
    }

    const clearUserInfo = () => {
      user.value = { ...defaultUser }
      roles.value = []
      permissions.value = []
    }

    const fetchUserInfo = async () => {
      const res = await getUserInfo()
      setUserInfo(res)
      return res
    }

    return {
      user,
      roles,
      permissions,
      clearUserInfo,
      fetchUserInfo,
      setUserInfo,
    }
  },
  {
    persist: { pick: ['user', 'roles', 'permissions'] },
  },
)
