import type { IMobileMenuGroup, IMobileMenuItem, IRouterVo } from '@/api/types/backend'
import {
  COMPONENT_ICON_MAP,
  MENU_DEFAULT_ICON,
  RUOYI_ICON_MAP,
} from '@/constants/menu-icons'
import { pushRecentMenu } from '@/utils/recentMenu'

/** 后台 component 与移动端 admin 分包页面映射 */
export const ADMIN_PAGE_MAP: Record<string, string> = {
  'system/user/index': '/pages-sub/admin/user/index',
  'system/role/index': '/pages-sub/admin/role/index',
  'system/menu/index': '/pages-sub/admin/menu/index',
  'system/dept/index': '/pages-sub/admin/dept/index',
  'system/post/index': '/pages-sub/admin/post/index',
  'system/dict/index': '/pages-sub/admin/dict/index',
  'system/config/index': '/pages-sub/admin/config/index',
  'system/notice/index': '/pages-sub/admin/notice/index',
  'monitor/operlog/index': '/pages-sub/admin/operlog/index',
  'scm/wms/org/index': '/pages-sub/scm/org/index',
}

export { MENU_ICON_SAFELIST } from '@/constants/menu-icons'

function normalizeIconKey(icon?: string) {
  if (!icon)
    return ''
  return icon
    .replace(/^el-icon-/, '')
    .replace(/^icon-/, '')
    .trim()
}

function resolveIcon(icon?: string, component?: string) {
  if (component && COMPONENT_ICON_MAP[component])
    return COMPONENT_ICON_MAP[component]

  if (!icon)
    return MENU_DEFAULT_ICON

  if (icon.startsWith('i-'))
    return icon

  const key = normalizeIconKey(icon)
  return RUOYI_ICON_MAP[key] || MENU_DEFAULT_ICON
}

function isLayoutComponent(component?: string) {
  return !component || component === 'Layout' || component === 'ParentView'
}

/** 将后台路由树转为移动端宫格菜单 */
export function buildMobileMenuGroups(routes: IRouterVo[]): IMobileMenuGroup[] {
  const groups: IMobileMenuGroup[] = []

  for (const route of routes) {
    if (route.hidden)
      continue

    const groupTitle = route.meta?.title || route.name || '功能菜单'
    const items: IMobileMenuItem[] = []

    const walk = (node: IRouterVo, parentPath = '') => {
      if (node.hidden)
        return

      const children = node.children?.filter(c => !c.hidden) || []
      const fullPath = [parentPath, node.path].filter(Boolean).join('/').replace(/\/+/g, '/')

      if (children.length > 0) {
        children.forEach(child => walk(child, fullPath))
        return
      }

      if (isLayoutComponent(node.component))
        return

      const component = node.component || fullPath
      const mobilePath = ADMIN_PAGE_MAP[component]
      if (!mobilePath)
        return

      items.push({
        title: node.meta?.title || node.name || component,
        icon: resolveIcon(node.meta?.icon, component),
        path: mobilePath,
      })
    }

    if (route.children?.length) {
      route.children.forEach(child => walk(child, route.path))
    }
    else {
      walk(route)
    }

    if (items.length)
      groups.push({ title: groupTitle, items })
  }

  return groups
}

/** 点击菜单项跳转；可选记录最近使用（首页工作台） */
export function navigateMenuItem(item: IMobileMenuItem, options?: { recordRecent?: boolean, userId?: string | number }) {
  if (!item.path) {
    uni.showToast({ title: '功能建设中', icon: 'none' })
    return
  }
  if (options?.recordRecent && options.userId !== undefined && options.userId !== null && options.userId !== '')
    pushRecentMenu(options.userId, item)
  uni.navigateTo({
    url: item.path,
    fail: () => {
      uni.showToast({ title: '页面暂未开放', icon: 'none' })
    },
  })
}
