/**
 * 跨端页面布局度量（uni-app 行业标准）
 *
 * - App / H5 / 各类小程序：统一用 uni.getWindowInfo().statusBarHeight
 * - 自定义导航内容区默认 44px（iOS 人机指南 / Android Material 常用高度）
 * - 仅微信小程序额外读取胶囊位置（官方推荐，#ifdef MP-WEIXIN）
 * - 底部 TabBar、安全区用 CSS env(safe-area-inset-*) 配合常量
 *
 * @see https://uniapp.dcloud.net.cn/tutorial/syntax-css.html#css-%E5%8F%98%E9%87%8F
 */

/** 导航栏内容区标准高度（px） */
export const NAV_CONTENT_HEIGHT = 44

/** 与 tabbar/config.ts height 保持一致（px） */
export const TABBAR_HEIGHT = 50

/** 自定义顶栏内容区与导航区的间距（px） */
export const NAV_CONTENT_GAP = 8

export interface PageLayoutMetrics {
  statusBarHeight: number
  navContentHeight: number
  /** 自定义导航栏总高度（状态栏 + 导航区），px */
  navBarTotalHeight: number
  /** PageHero 等内容起始 padding-top，px */
  customNavPaddingTop: number
  /** 主内容区最大宽度右边界（避免与 MP 胶囊重叠），px */
  contentMaxWidth: number
  windowWidth: number
}

function readPageLayoutMetrics(): PageLayoutMetrics {
  const windowInfo = uni.getWindowInfo?.() ?? uni.getSystemInfoSync()
  const statusBarHeight = windowInfo.statusBarHeight ?? 0
  const windowWidth = windowInfo.windowWidth ?? 375

  let navContentHeight = NAV_CONTENT_HEIGHT
  let navBarTotalHeight = statusBarHeight + NAV_CONTENT_HEIGHT
  let customNavPaddingTop = navBarTotalHeight + NAV_CONTENT_GAP
  let contentMaxWidth = windowWidth

  // #ifdef MP-WEIXIN
  try {
    const rect = uni.getMenuButtonBoundingClientRect()
    // 与胶囊垂直居中对齐的导航栏高度（微信官方社区通用算法）
    navContentHeight = (rect.top - statusBarHeight) * 2 + rect.height
    navBarTotalHeight = statusBarHeight + navContentHeight
    customNavPaddingTop = rect.bottom + NAV_CONTENT_GAP
    contentMaxWidth = rect.left - 12
  }
  catch {
    /* 降级为通用算法 */
  }
  // #endif

  return {
    statusBarHeight,
    navContentHeight,
    navBarTotalHeight,
    customNavPaddingTop,
    contentMaxWidth,
    windowWidth,
  }
}

export const pageLayoutMetrics = readPageLayoutMetrics()

/** 转为 AppPage 根节点 CSS 变量，供全页子组件消费 */
export function toPageLayoutVars(metrics = pageLayoutMetrics) {
  return {
    '--bp-status-bar-height': `${metrics.statusBarHeight}px`,
    '--bp-nav-content-height': `${metrics.navContentHeight}px`,
    '--bp-nav-total-height': `${metrics.navBarTotalHeight}px`,
    '--bp-custom-nav-padding-top': `${metrics.customNavPaddingTop}px`,
    '--bp-content-max-width': `${metrics.contentMaxWidth}px`,
    '--bp-tabbar-height': `${TABBAR_HEIGHT}px`,
  } as Record<string, string>
}
