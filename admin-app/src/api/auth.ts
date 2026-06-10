import type {
  ICaptchaVo,
  ILoginForm,
  ILoginVo,
  ITenantListVo,
  IUserInfoVo,
} from '@/api/types/backend'
import { DEFAULT_TENANT_ID, GRANT_TYPE_PASSWORD } from '@/constants/app'
import { http } from '@/http/http'

const clientId = import.meta.env.VITE_CLIENT_ID

export function login(data: ILoginForm) {
  return http.post<ILoginVo>('/auth/login', {
    ...data,
    tenantId: data.tenantId || DEFAULT_TENANT_ID,
    clientId: data.clientId || clientId,
    grantType: data.grantType || GRANT_TYPE_PASSWORD,
  }, undefined, undefined, { isToken: false, isEncrypt: true })
}

export function logout() {
  return http.post<void>('/auth/logout')
}

export function getUserInfo() {
  return http.get<IUserInfoVo>('/system/user/getInfo')
}

export function getCodeImg() {
  return http.get<ICaptchaVo>('/auth/code', undefined, undefined, {
    isToken: false,
    timeout: 20000,
  })
}

export function getTenantList() {
  return http.get<ITenantListVo>('/auth/tenant/list', undefined, undefined, { isToken: false })
}

export function register(data: Record<string, any>) {
  return http.post<void>('/auth/register', {
    ...data,
    clientId,
    grantType: GRANT_TYPE_PASSWORD,
  }, undefined, undefined, { isToken: false, isEncrypt: true })
}
