<script lang="ts" setup>
import { usePageLayout } from '@/hooks/usePageLayout'

const props = withDefaults(defineProps<{
  /** 是否包含顶部安全区（默认 true；custom 导航页传 false，由 PageHero 处理） */
  safeTop?: boolean
  /** 是否包含底部安全区 */
  safeBottom?: boolean
  /** 页面背景，默认灰底 */
  gray?: boolean
  /** TabBar 主页面，统一预留底部导航 + 安全区 */
  tabbar?: boolean
  /** 注入跨端布局 CSS 变量（custom 导航 / TabBar 页建议开启） */
  layoutVars?: boolean
}>(), {
  safeTop: true,
  safeBottom: false,
  gray: true,
  tabbar: false,
  layoutVars: true,
})

const { pageStyle } = usePageLayout({ tabbar: props.tabbar })
</script>

<template>
  <view
    class="app-page"
    :class="{
      'app-page--gray': gray,
      'app-page--tabbar': tabbar,
      'pt-safe': safeTop,
      'pb-safe': safeBottom,
    }"
    :style="layoutVars ? pageStyle : undefined"
  >
    <slot />
  </view>
</template>

<style lang="scss" scoped>
.app-page {
  min-height: 100vh;
  box-sizing: border-box;

  &--gray {
    background-color: var(--bp-bg-page);
  }

  &:not(.app-page--gray) {
    background-color: var(--bp-bg-card);
  }

  &--tabbar {
    padding-bottom: var(--bp-page-bottom, calc(var(--bp-tabbar-height, 50px) + env(safe-area-inset-bottom)));
  }
}
</style>
