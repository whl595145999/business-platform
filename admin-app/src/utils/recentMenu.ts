import type { IMobileMenuItem } from '@/api/types/backend'
import { RECENT_MENU_MAX } from '@/constants/app'

function storageKey(userId: string | number) {
  return `bp-recent-menus-${userId}`
}

/** 读取最近访问的菜单 path 列表（新→旧） */
export function getRecentMenuPaths(userId?: string | number): string[] {
  if (userId === undefined || userId === null || userId === '')
    return []
  try {
    const raw = uni.getStorageSync(storageKey(userId))
    if (!raw)
      return []
    const list = typeof raw === 'string' ? JSON.parse(raw) : raw
    return Array.isArray(list) ? list.filter((p): p is string => typeof p === 'string') : []
  }
  catch {
    return []
  }
}

/** 记录菜单访问（LRU，最多 RECENT_MENU_MAX 条） */
export function pushRecentMenu(userId: string | number, item: IMobileMenuItem) {
  if (!item.path)
    return
  const prev = getRecentMenuPaths(userId).filter(p => p !== item.path)
  const next = [item.path, ...prev].slice(0, RECENT_MENU_MAX)
  uni.setStorageSync(storageKey(userId), next)
}

/** 将 path 列表解析为当前仍有效的菜单项 */
export function resolveRecentMenuItems(
  paths: string[],
  available: IMobileMenuItem[],
): IMobileMenuItem[] {
  const map = new Map(available.map(item => [item.path, item]))
  return paths.map(p => map.get(p)).filter((item): item is IMobileMenuItem => !!item)
}
