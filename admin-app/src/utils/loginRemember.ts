import { LOGIN_REMEMBER_KEY } from '@/constants/app'

export interface ILoginRemember {
  username: string
  tenantId?: string
}

export function getLoginRemember(): ILoginRemember | null {
  try {
    const raw = uni.getStorageSync(LOGIN_REMEMBER_KEY)
    if (!raw)
      return null
    const data = typeof raw === 'string' ? JSON.parse(raw) : raw
    if (!data?.username)
      return null
    return {
      username: String(data.username),
      tenantId: data.tenantId ? String(data.tenantId) : undefined,
    }
  }
  catch {
    return null
  }
}

export function setLoginRemember(data: ILoginRemember) {
  if (!data.username?.trim())
    return
  uni.setStorageSync(LOGIN_REMEMBER_KEY, {
    username: data.username.trim(),
    tenantId: data.tenantId,
  })
}

export function clearLoginRemember() {
  uni.removeStorageSync(LOGIN_REMEMBER_KEY)
}
