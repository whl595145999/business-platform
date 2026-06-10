/** 业务平台后端（RuoYi-Vue-Plus）接口类型 */

/** 登录请求参数 */
export interface ILoginForm {
  tenantId: string
  username: string
  password: string
  code?: string
  uuid?: string
  clientId?: string
  grantType?: string
}

/** 登录响应（Sa-Token） */
export interface ILoginVo {
  access_token: string
  refresh_token?: string
  expire_in: number
  refresh_expire_in?: number
  client_id?: string
  scope?: string
  openid?: string
}

/** 系统用户 */
export interface ISysUser {
  userId: number
  userName: string
  nickName: string
  avatar?: string
  deptId?: number
  phonenumber?: string
  email?: string
  sex?: string
  [key: string]: any
}

/** 用户信息响应 */
export interface IUserInfoVo {
  user: ISysUser
  roles: string[]
  permissions: string[]
}

/** 验证码响应 */
export interface ICaptchaVo {
  captchaEnabled: boolean
  uuid: string
  img: string
}

/** 租户信息 */
export interface ITenantVo {
  tenantId: string
  companyName: string
}

/** 租户列表响应 */
export interface ITenantListVo {
  tenantEnabled: boolean
  voList: ITenantVo[]
}

/** 分页查询参数 */
export interface IPageQuery {
  pageNum?: number
  pageSize?: number
  orderByColumn?: string
  isAsc?: string
  [key: string]: any
}

/** 分页响应 */
export interface IPageResult<T> {
  rows: T[]
  total: number
}

/** 字典数据 */
export interface IDictData {
  dictCode?: number
  dictLabel: string
  dictValue: string
  dictType?: string
  cssClass?: string
  listClass?: string
  [key: string]: any
}

/** 路由 Meta */
export interface IRouterMeta {
  title: string
  icon?: string
  noCache?: boolean
  link?: string | null
  [key: string]: any
}

/** 动态路由/菜单 */
export interface IRouterVo {
  name?: string
  path: string
  hidden?: boolean
  component?: string
  meta?: IRouterMeta
  children?: IRouterVo[]
  [key: string]: any
}

/** 移动端菜单项 */
export interface IMobileMenuItem {
  title: string
  icon: string
  path: string
  perm?: string
}

/** 移动端菜单分组 */
export interface IMobileMenuGroup {
  title: string
  items: IMobileMenuItem[]
}
