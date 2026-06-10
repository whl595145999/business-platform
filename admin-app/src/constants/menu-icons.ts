/** 移动端菜单 Carbon 图标（与 src/utils/menu.ts 同步维护） */

export const MENU_DEFAULT_ICON = 'i-carbon-application'

export const COMPONENT_ICON_MAP: Record<string, string> = {
  'system/user/index': 'i-carbon-user-multiple',
  'system/role/index': 'i-carbon-user-role',
  'system/menu/index': 'i-carbon-menu',
  'system/dept/index': 'i-carbon-enterprise',
  'system/post/index': 'i-carbon-badge',
  'system/dict/index': 'i-carbon-book',
  'system/config/index': 'i-carbon-settings-adjust',
  'system/notice/index': 'i-carbon-notification',
  'monitor/operlog/index': 'i-carbon-document',
  'scm/wms/org/index': 'i-carbon-tree-view',
}

/** RuoYi / Element Plus sys_menu.icon → Carbon */
export const RUOYI_ICON_MAP: Record<string, string> = {
  system: 'i-carbon-settings',
  user: 'i-carbon-user-multiple',
  peoples: 'i-carbon-user-role',
  role: 'i-carbon-user-role',
  tree: 'i-carbon-tree-view',
  'tree-table': 'i-carbon-tree-view',
  menu: 'i-carbon-menu',
  cascader: 'i-carbon-tree-view',
  dept: 'i-carbon-enterprise',
  'office-building': 'i-carbon-building',
  post: 'i-carbon-badge',
  dict: 'i-carbon-book',
  config: 'i-carbon-settings-adjust',
  notice: 'i-carbon-notification',
  message: 'i-carbon-notification',
  log: 'i-carbon-document',
  monitor: 'i-carbon-dashboard',
  tool: 'i-carbon-tools',
  guide: 'i-carbon-help',
  chart: 'i-carbon-chart-line',
  list: 'i-carbon-list',
  form: 'i-carbon-document',
  table: 'i-carbon-table',
  search: 'i-carbon-search',
  edit: 'i-carbon-edit',
  job: 'i-carbon-time',
  server: 'i-carbon-data-center',
  redis: 'i-carbon-db2-database',
  build: 'i-carbon-tool-box',
}

/** 去重后的图标 class 列表（供 uno safelist） */
export const MENU_ICON_SAFELIST: string[] = Array.from(
  new Set([
    MENU_DEFAULT_ICON,
    ...Object.values(COMPONENT_ICON_MAP),
    ...Object.values(RUOYI_ICON_MAP),
  ]),
)
