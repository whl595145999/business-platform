import type { CustomRequestOptions } from '@/http/types'
import { TOKEN_KEY } from '@/constants/app'
import { useTokenStore } from '@/store'
import { getEnvBaseUrl } from '@/utils'
import {
  decryptBase64,
  encryptBase64,
  encryptWithAes,
  generateAesKey,
} from '@/utils/crypto'
import { rsaEncrypt } from '@/utils/jsencrypt'
import { stringifyQuery } from './tools/queryString'

const ENCRYPT_HEADER = 'encrypt-key'
const baseUrl = getEnvBaseUrl()
const clientId = import.meta.env.VITE_CLIENT_ID

const httpInterceptor = {
  invoke(options: CustomRequestOptions) {
    if (options.query) {
      const queryStr = stringifyQuery(options.query)
      if (options.url.includes('?'))
        options.url += `&${queryStr}`
      else
        options.url += `?${queryStr}`
    }

    if (!options.url.startsWith('http')) {
      // #ifdef H5
      if (JSON.parse(import.meta.env.VITE_APP_PROXY_ENABLE)) {
        options.url = import.meta.env.VITE_APP_PROXY_PREFIX + options.url
      }
      else {
        options.url = baseUrl + options.url
      }
      // #endif
      // #ifndef H5
      options.url = baseUrl + options.url
      // #endif
    }

    options.timeout = options.timeout || 60000
    options.header = {
      'Content-Type': 'application/json;charset=utf-8',
      clientid: clientId,
      ...options.header,
    }

    const needToken = options.isToken !== false
    if (needToken) {
      const tokenStore = useTokenStore()
      const token = tokenStore.updateNowTime().validToken || uni.getStorageSync(TOKEN_KEY)
      if (token)
        options.header.Authorization = `Bearer ${token}`
    }

    // 接口加密（与 admin-web 一致，登录等敏感 POST 需 isEncrypt: true）
    const isEncrypt = options.isEncrypt === true
    if (
      import.meta.env.VITE_APP_ENCRYPT === 'true'
      && isEncrypt
      && (options.method === 'POST' || options.method === 'PUT')
      && options.data
    ) {
      const aesKey = generateAesKey()
      options.header[ENCRYPT_HEADER] = rsaEncrypt(encryptBase64(aesKey))
      const body = typeof options.data === 'object' ? JSON.stringify(options.data) : String(options.data)
      options.data = encryptWithAes(body, aesKey)
    }

    return options
  },
}

export const requestInterceptor = {
  install() {
    uni.addInterceptor('request', httpInterceptor)
    uni.addInterceptor('uploadFile', httpInterceptor)
  },
}

export { ENCRYPT_HEADER }
