/** 后端业务错误码映射 */
const errorCode: Record<number | string, string> = {
  401: '认证失败，无法访问系统资源',
  403: '当前操作没有权限',
  404: '访问资源不存在',
  default: '系统未知错误，请反馈给管理员',
}

export default errorCode

/** 优先使用后端返回的 msg，再回退到错误码映射 */
export function getErrorMessage(code: number | string, msg?: string) {
  if (msg)
    return msg
  return errorCode[code] || errorCode.default
}

/** 从 catch 的 error 中解析可读文案（http reject 的 responseData / Error） */
export function resolveErrorMessage(error: unknown, fallback = '操作失败，请重试') {
  if (!error)
    return fallback
  if (typeof error === 'string')
    return error
  if (error instanceof Error && error.message)
    return error.message
  if (typeof error === 'object') {
    const e = error as Record<string, any>
    return e.msg || e.message || fallback
  }
  return fallback
}
