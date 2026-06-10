import type { CustomRequestOptions, IResponse } from '@/http/types'
import { useTokenStore } from '@/store/token'
import { decryptBase64, decryptWithAes } from '@/utils/crypto'
import { rsaDecrypt } from '@/utils/jsencrypt'
import { getErrorMessage } from '@/utils/errorCode'
import { toLoginPage } from '@/utils/toLoginPage'
import { ENCRYPT_HEADER } from './interceptor'
import { extractResponseData } from './response'
import { ResultEnum } from './tools/enum'

function handleUnauthorized() {
  uni.showModal({
    title: '系统提示',
    content: '登录状态已过期，您可以继续留在该页面，或者重新登录',
    confirmText: '重新登录',
    cancelText: '取消',
    success: async (res) => {
      if (res.confirm) {
        const tokenStore = useTokenStore()
        await tokenStore.logout()
        toLoginPage({ mode: 'reLaunch' })
      }
    },
  })
}

function decryptBody(data: any, header?: Record<string, any>) {
  if (import.meta.env.VITE_APP_ENCRYPT !== 'true' || !header)
    return data

  const keyStr = header[ENCRYPT_HEADER] || header['encrypt-key']
  if (!keyStr || typeof data !== 'string')
    return data

  const base64Str = rsaDecrypt(String(keyStr))
  const aesKey = decryptBase64(base64Str)
  const decryptData = decryptWithAes(data, aesKey)
  return JSON.parse(decryptData)
}

export function http<T>(options: CustomRequestOptions) {
  return new Promise<T>((resolve, reject) => {
    uni.request({
      ...options,
      dataType: 'json',
      // #ifndef MP-WEIXIN
      responseType: 'json',
      // #endif
      success: async (res) => {
        let responseData = decryptBody(res.data, res.header) as IResponse<T>
        const code = responseData?.code ?? ResultEnum.Success200
        const msg = getErrorMessage(code, (responseData as any)?.msg || (responseData as any)?.message)

        if (code === ResultEnum.Unauthorized || res.statusCode === 401) {
          const tokenStore = useTokenStore()
          if (options.isToken === false) {
            if (!options.hideErrorToast)
              uni.showToast({ icon: 'none', title: msg })
            return reject(new Error(msg))
          }
          if (tokenStore.hasLogin)
            handleUnauthorized()
          return reject(new Error(msg))
        }

        if (code === ResultEnum.InternalServerError) {
          if (!options.hideErrorToast)
            uni.showToast({ icon: 'none', title: msg })
          return reject(new Error(msg))
        }

        if (code !== ResultEnum.Success0 && code !== ResultEnum.Success200) {
          if (!options.hideErrorToast)
            uni.showToast({ icon: 'none', title: msg })
          return reject(responseData)
        }

        return resolve(extractResponseData<T>(responseData as Record<string, any>))
      },
      fail(err) {
        if (!options.hideErrorToast)
          uni.showToast({ icon: 'none', title: '网络错误，请检查网络连接' })
        reject(err)
      },
    })
  })
}

export function httpGet<T>(
  url: string,
  query?: Record<string, any>,
  header?: Record<string, any>,
  options?: Partial<CustomRequestOptions>,
) {
  return http<T>({ url, query, method: 'GET', header, ...options })
}

export function httpPost<T>(
  url: string,
  data?: Record<string, any>,
  query?: Record<string, any>,
  header?: Record<string, any>,
  options?: Partial<CustomRequestOptions>,
) {
  return http<T>({ url, query, data, method: 'POST', header, ...options })
}

export function httpPut<T>(
  url: string,
  data?: Record<string, any>,
  query?: Record<string, any>,
  header?: Record<string, any>,
  options?: Partial<CustomRequestOptions>,
) {
  return http<T>({ url, data, query, method: 'PUT', header, ...options })
}

export function httpDelete<T>(
  url: string,
  query?: Record<string, any>,
  header?: Record<string, any>,
  options?: Partial<CustomRequestOptions>,
) {
  return http<T>({ url, query, method: 'DELETE', header, ...options })
}

http.get = httpGet
http.post = httpPost
http.put = httpPut
http.delete = httpDelete
http.Get = httpGet
http.Post = httpPost
http.Put = httpPut
http.Delete = httpDelete

export function saveTokenExpireTime(expireIn: number) {
  uni.setStorageSync('accessTokenExpireTime', Date.now() + expireIn * 1000)
}

export function clearTokenExpireTime() {
  uni.removeStorageSync('accessTokenExpireTime')
}
