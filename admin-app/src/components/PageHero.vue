<script lang="ts" setup>
withDefaults(defineProps<{
  title: string
  subtitle?: string
  avatar?: string
  showAvatar?: boolean
  /** 紧凑模式（我的页） */
  compact?: boolean
  /** 首页工作台：轻渐变底（对齐登录页色系） */
  gradient?: boolean
}>(), {
  showAvatar: true,
  compact: false,
  gradient: false,
})
</script>

<template>
  <view
    class="page-hero"
    :class="{ 'page-hero--compact': compact, 'page-hero--gradient': gradient }"
  >
    <view v-if="gradient" class="page-hero__bg" />
    <view class="page-hero__bar" />
    <view class="page-hero__inner">
      <view class="page-hero__text">
        <text class="page-hero__title">{{ title }}</text>
        <text v-if="subtitle" class="page-hero__subtitle">{{ subtitle }}</text>
      </view>
      <image
        v-if="showAvatar"
        :src="avatar || '/static/images/default-avatar.png'"
        class="page-hero__avatar"
        mode="aspectFill"
      />
    </view>
  </view>
</template>

<style lang="scss" scoped>
.page-hero {
  position: relative;
  box-sizing: border-box;
  overflow: hidden;
  background: var(--bp-bg-card);

  &--gradient {
    border-bottom-color: transparent;
  }

  &__bg {
    position: absolute;
    inset: 0;
    background: linear-gradient(180deg, var(--bp-color-primary-muted) 0%, var(--bp-bg-card) 100%);
    z-index: 0;
    pointer-events: none;
  }
  /* 跨端：由 AppPage 注入 --bp-custom-nav-padding-top */
  padding-top: var(--bp-custom-nav-padding-top, calc(env(safe-area-inset-top) + 44px + 8px));
  padding-left: var(--bp-spacing-page);
  padding-right: var(--bp-spacing-page);
  padding-bottom: var(--bp-space-lg);
  border-bottom: 1rpx solid var(--bp-border-color);

  &--compact {
    padding-bottom: var(--bp-space-md);
  }

  &__bar {
    position: absolute;
    top: var(--bp-status-bar-height, env(safe-area-inset-top));
    left: 0;
    right: 0;
    z-index: 2;
    height: 6rpx;
    background: linear-gradient(90deg, var(--bp-color-primary) 0%, var(--bp-color-primary-hover) 100%);
  }

  &__inner {
    position: relative;
    z-index: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: var(--bp-space-md);
    max-width: 100%;
  }

  &__text {
    flex: 1;
    min-width: 0;
    max-width: var(--bp-content-max-width, 100%);
  }

  &__title {
    display: block;
    font-size: var(--bp-font-title);
    font-weight: var(--bp-font-weight-semibold);
    color: var(--bp-text-primary);
    line-height: 1.35;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__subtitle {
    display: block;
    margin-top: var(--bp-space-xs);
    font-size: var(--bp-font-caption);
    color: var(--bp-text-tertiary);
    line-height: 1.5;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__avatar {
    width: 96rpx;
    height: 96rpx;
    border-radius: var(--bp-radius-round);
    border: 4rpx solid var(--bp-color-primary-light);
    background: var(--bp-bg-page);
    flex-shrink: 0;
  }

  &--compact &__avatar {
    width: 88rpx;
    height: 88rpx;
  }

  &--compact &__title {
    font-size: var(--bp-font-subhead);
  }
}
</style>
