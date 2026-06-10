import type { IMobileMenuGroup, IRouterVo } from '@/api/types/backend'
import { getRouters } from '@/api/system/menu'
import { buildMobileMenuGroups } from '@/utils/menu'
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

export const usePermissionStore = defineStore(
  'permission',
  () => {
    const routes = ref<IRouterVo[]>([])
    const loaded = ref(false)

    const menuGroups = computed<IMobileMenuGroup[]>(() => buildMobileMenuGroups(routes.value))

    const flatMenus = computed(() => menuGroups.value.flatMap(group => group.items))

    async function fetchRoutes(force = false) {
      if (loaded.value && !force)
        return routes.value

      const list = await getRouters()
      routes.value = list
      loaded.value = true
      return list
    }

    function clearRoutes() {
      routes.value = []
      loaded.value = false
    }

    return {
      routes,
      loaded,
      menuGroups,
      flatMenus,
      fetchRoutes,
      clearRoutes,
    }
  },
  {
    persist: { pick: ['routes', 'loaded'] },
  },
)
