import { usePermissionStore } from '@/store/permission'
import { useTokenStore } from '@/store/token'
import { navigateMenuItem } from '@/utils/menu'
import { computed, onMounted } from 'vue'

/** 动态菜单 Composable */
export function useMenu(autoLoad = true) {
  const permissionStore = usePermissionStore()
  const tokenStore = useTokenStore()

  onMounted(() => {
    if (autoLoad && tokenStore.hasLogin && !permissionStore.loaded)
      permissionStore.fetchRoutes().catch(() => {})
  })

  return {
    menuGroups: computed(() => permissionStore.menuGroups),
    flatMenus: computed(() => permissionStore.flatMenus),
    fetchRoutes: permissionStore.fetchRoutes,
    navigateMenu: navigateMenuItem,
  }
}
