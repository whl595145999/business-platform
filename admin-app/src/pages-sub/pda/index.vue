<script lang="ts" setup>
/**
 * PDA 现场工作台 — B8+ 占位
 * UI PRD：docs/prd/PDA_WORKBENCH_PRD.md
 * 业务 PRD：待 B8 重建（business-platform/docs/domains/README.md）
 */
definePage({
  style: {
    navigationBarTitleText: '现场作业',
    navigationStyle: 'custom',
  },
})

function handleScanPlaceholder() {
  uni.showToast({
    title: 'B8+ 扫码作业待实现',
    icon: 'none',
  })
}

function handleFeature(name: string) {
  uni.showToast({
    title: `${name}（B8+ 排期）`,
    icon: 'none',
  })
}
</script>

<template>
  <AppPage :safe-top="false">
    <PageHero
      gradient
      title="现场作业"
      subtitle="PDA 工作台 · B8+ 排期"
      :show-avatar="false"
    />

    <view class="pda-body">
      <view class="pda-scan bp-card">
        <wd-button type="primary" block size="large" @click="handleScanPlaceholder">
          <view class="pda-scan__inner">
            <view class="i-carbon-barcode pda-scan__icon" />
            <text>扫码作业</text>
          </view>
        </wd-button>
        <text class="pda-scan__hint">收货、复核、盘点将从扫码开始</text>
      </view>

      <view class="pda-grid">
        <view class="pda-grid__item bp-card" @click="handleFeature('收货确认')">
          <view class="bp-icon-box pda-grid__icon-box">
            <view class="i-carbon-delivery pda-grid__icon" />
          </view>
          <text class="pda-grid__label">收货确认</text>
        </view>
        <view class="pda-grid__item bp-card" @click="handleFeature('出库复核')">
          <view class="bp-icon-box pda-grid__icon-box">
            <view class="i-carbon-export pda-grid__icon" />
          </view>
          <text class="pda-grid__label">出库复核</text>
        </view>
        <view class="pda-grid__item bp-card" @click="handleFeature('盘点录入')">
          <view class="bp-icon-box pda-grid__icon-box">
            <view class="i-carbon-inventory-management pda-grid__icon" />
          </view>
          <text class="pda-grid__label">盘点录入</text>
        </view>
        <view class="pda-grid__item bp-card" @click="handleFeature('今日完成')">
          <view class="bp-icon-box pda-grid__icon-box">
            <view class="i-carbon-chart-line pda-grid__icon" />
          </view>
          <text class="pda-grid__label">今日完成</text>
        </view>
      </view>

      <view class="pda-notice bp-card">
        <text class="pda-notice__title">开发说明</text>
        <text class="pda-notice__text">
          本页为 B8 PDA 分包占位。业务 API 使用 /api/pda/wms/...，业务 PRD 待 B8 重建（见 domains/README.md）。
        </text>
      </view>
    </view>
  </AppPage>
</template>

<style lang="scss" scoped>
.pda-body {
  padding: var(--bp-space-md) var(--bp-spacing-page) var(--bp-page-bottom);
}

.pda-scan {
  padding: var(--bp-space-lg) var(--bp-spacing-card);

  &__inner {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: var(--bp-space-sm);
  }

  &__icon {
    font-size: 40rpx;
  }

  &__hint {
    display: block;
    margin-top: var(--bp-space-md);
    text-align: center;
    font-size: var(--bp-font-caption);
    color: var(--bp-text-tertiary);
  }
}

.pda-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--bp-space-md);
  margin-top: var(--bp-space-lg);

  &__item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: var(--bp-space-xl) var(--bp-space-md);
    min-height: 200rpx;
  }

  &__icon-box {
    width: 96rpx;
    height: 96rpx;
    border-radius: var(--bp-radius-lg);
  }

  &__icon {
    font-size: 48rpx;
    color: var(--bp-color-primary);
  }

  &__label {
    margin-top: var(--bp-space-md);
    font-size: var(--bp-font-subhead);
    font-weight: var(--bp-font-weight-medium);
    color: var(--bp-text-primary);
  }
}

.pda-notice {
  margin-top: var(--bp-space-lg);
  padding: var(--bp-space-md) var(--bp-spacing-card);

  &__title {
    display: block;
    font-size: var(--bp-font-subhead);
    font-weight: var(--bp-font-weight-semibold);
    color: var(--bp-text-primary);
  }

  &__text {
    display: block;
    margin-top: var(--bp-space-sm);
    font-size: var(--bp-font-caption);
    color: var(--bp-text-secondary);
    line-height: 1.6;
  }
}
</style>
