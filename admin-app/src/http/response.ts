import { decryptBase64, decryptWithAes } from '@/utils/crypto'
import { rsaDecrypt } from '@/utils/jsencrypt'

/** 与 admin-web 对齐的响应体提取 */
export function extractResponseData<T = any>(responseData: Record<string, any>): T {
  if (responseData.data !== undefined && responseData.data !== null)
    return responseData.data as T

  const { code, msg, message, ...rest } = responseData
  return rest as T
}

/** 解密加密响应（与 admin-web request.ts 一致） */
export function decryptResponseBody(data: any, encryptKeyHeader?: string) {
  if (import.meta.env.VITE_APP_ENCRYPT !== 'true' || !encryptKeyHeader)
    return data

  if (typeof data !== 'string')
    return data

  const base64Str = rsaDecrypt(encryptKeyHeader)
  const aesKey = decryptBase64(base64Str)
  const decryptData = decryptWithAes(data, aesKey)
  return JSON.parse(decryptData)
}
