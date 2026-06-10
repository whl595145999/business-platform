import { computed } from 'vue'
import { pageLayoutMetrics, toPageLayoutVars } from '@/utils/layout'

/** 跨端页面布局 Hook（App / H5 / 小程序统一 CSS 变量） */
export function usePageLayout(options?: { tabbar?: boolean }) {
  const metrics = pageLayoutMetrics

  const pageStyle = computed(() => {
    const vars = toPageLayoutVars(metrics)
    if (options?.tabbar) {
      vars['--bp-page-bottom'] = 'calc(var(--bp-tabbar-height) + env(safe-area-inset-bottom))'
    }
    return vars
  })

  return { metrics, pageStyle }
}
