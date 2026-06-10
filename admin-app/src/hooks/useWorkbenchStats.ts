/**
 * 首页工作台业务指标（v1 占位 0，v2 接待办/单据/异常 API）
 * PRD：docs/prd/HOME_WORKBENCH_PRD.md §3
 */
export interface IWorkbenchStat {
  label: string
  value: string | number
  icon: string
  tone: 'primary' | 'info' | 'warning'
}

export function useWorkbenchStats() {
  const loading = ref(false)

  const stats = ref<IWorkbenchStat[]>([
    {
      label: '待办',
      value: 0,
      icon: 'i-carbon-task',
      tone: 'primary',
    },
    {
      label: '今日单据',
      value: 0,
      icon: 'i-carbon-document',
      tone: 'info',
    },
    {
      label: '异常',
      value: 0,
      icon: 'i-carbon-warning',
      tone: 'warning',
    },
  ])

  /** v2：对接后端后在此 fetch 并更新 stats.value */
  async function refreshStats() {
    loading.value = true
    try {
      // TODO: GET 工作台统计 API（见 docs/prd/HOME_WORKBENCH_PRD.md §3）
    }
    finally {
      loading.value = false
    }
  }

  return {
    stats,
    loading,
    refreshStats,
  }
}
