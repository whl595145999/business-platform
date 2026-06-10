/** 业务平台移动端全局常量 */

/** Token 本地存储 Key */
export const TOKEN_KEY = 'App-Token'

/** 登录页记住账号（仅用户名/租户，不存密码） */
export const LOGIN_REMEMBER_KEY = 'bp-login-remember'

/** 首页最近使用菜单（按 userId 分桶，见 recentMenu.ts） */
export const RECENT_MENU_MAX = 4

/** 超级管理员角色标识 */
export const SUPER_ADMIN_ROLE = 'admin'

/** 超级权限标识 */
export const ALL_PERMISSION = '*:*:*'

/** 默认角色（无角色时兜底） */
export const DEFAULT_ROLE = 'ROLE_DEFAULT'

/** 默认租户 ID */
export const DEFAULT_TENANT_ID = '000000'

/** 密码登录授权类型 */
export const GRANT_TYPE_PASSWORD = 'password'
