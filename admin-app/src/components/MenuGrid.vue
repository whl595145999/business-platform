<script lang="ts" setup>
import type { IMobileMenuGroup, IMobileMenuItem } from '@/api/types/backend'

/** 工作台宫格列数：钉钉/企业微信/飞书移动端均为 4 列，全端统一 */
const GRID_COLUMN = 4

defineProps<{
  groups: IMobileMenuGroup[]
  /** 仅一组时隐藏分组内标题，避免与外层区块重复 */
  hideSingleGroupHead?: boolean
}>()

const emit = defineEmits<{
  select: [item: IMobileMenuItem]
}>()

function onItemClick(item: IMobileMenuItem) {
  emit('select', item)
}
</script>

<template>
  <view class="menu-grid-wrap">
    <view
      v-for="group in groups"
      :key="group.title"
      class="menu-grid-group bp-card"
    >
      <view
        v-if="!(hideSingleGroupHead && groups.length === 1)"
        class="menu-grid-group__head"
      >
        <text class="menu-grid-group__title">{{ group.title }}</text>
      </view>
      <!-- wot-ui 宫格：跨 App / H5 / 小程序统一渲染 -->
      <wd-grid
        :column="GRID_COLUMN"
        clickable
        :border="false"
        :gutter="8"
        custom-class="menu-grid-group__grid"
      >
        <wd-grid-item
          v-for="item in group.items"
          :key="item.path"
          custom-class="menu-grid-item"
          @click="onItemClick(item)"
        >
          <template #icon>
            <view class="bp-icon-box menu-grid-item__icon-box">
              <view :class="item.icon" class="menu-grid-item__icon" />
            </view>
          </template>
          <template #text>
            <text class="menu-grid-item__text">{{ item.title }}</text>
          </template>
        </wd-grid-item>
      </wd-grid>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.menu-grid-wrap {
  display: flex;
  flex-direction: column;
  gap: var(--bp-space-md);
}

.menu-grid-group {
  overflow: hidden;

  &__head {
    padding: var(--bp-space-md) var(--bp-spacing-card) var(--bp-space-sm);
    border-bottom: 1rpx solid var(--bp-divider-color);
  }

  &__title {
    font-size: var(--bp-font-subhead);
    font-weight: var(--bp-font-weight-semibold);
    color: var(--bp-text-primary);
  }

  :deep(.menu-grid-group__grid) {
    padding: var(--bp-space-sm) 0 var(--bp-space-xs);
  }

  :deep(.menu-grid-item) {
    padding: var(--bp-space-sm) 0;
  }

  :deep(.wd-grid-item__text) {
    margin-top: var(--bp-space-sm);
  }
}

.menu-grid-item {
  &__icon-box {
    width: 88rpx;
    height: 88rpx;
    border-radius: 20rpx;
    margin: 0 auto;
  }

  &__icon {
    font-size: 44rpx;
    color: var(--bp-color-primary);
  }

  &__text {
    font-size: var(--bp-font-mini);
    color: var(--bp-text-secondary);
    text-align: center;
    line-height: 1.35;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    word-break: break-all;
  }
}
</style>
