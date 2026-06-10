<script lang="ts" setup>
import type { IMobileMenuItem } from '@/api/types/backend'
import { appConfig } from '@/config/app.config'
import { LOGIN_PAGE } from '@/router/config'
import { useMenu } from '@/hooks/useMenu'
import { useWorkbenchStats } from '@/hooks/useWorkbenchStats'
import { useTokenStore } from '@/store/token'
import { useUserStore } from '@/store/user'
import { getRecentMenuPaths, resolveRecentMenuItems } from '@/utils/recentMenu'

definePage({
  type: 'home',
  style: {
    navigationBarTitleText: '首页',
    navigationStyle: 'custom',
    enablePullDownRefresh: true,
    backgroundTextStyle: 'dark',
  },
})

const tokenStore = useTokenStore()
const userStore = useUserStore()
const { menuGroups, flatMenus, fetchRoutes, navigateMenu } = useMenu()
const { stats, refreshStats } = useWorkbenchStats()
const refreshing = ref(false)
const recentPaths = ref<string[]>([])

const heroTitle = computed(() =>
  tokenStore.hasLogin
    ? `你好，${userStore.user.nickName || userStore.user.userName}`
    : '欢迎使用',
)

const heroSubtitle = computed(() => {
  if (!tokenStore.hasLogin)
    return appConfig.appInfo.shortName || appConfig.appInfo.name
  const name = appConfig.appInfo.shortName || appConfig.appInfo.name
  return `${name} · 工作台`
})

const totalMenuCount = computed(() =>
  menuGroups.value.reduce((sum, g) => sum + g.items.length, 0),
)

const recentItems = computed(() =>
  resolveRecentMenuItems(recentPaths.value, flatMenus.value),
)

const showRecentSection = computed(() =>
  tokenStore.hasLogin && recentItems.value.length > 0,
)

function loadRecentPaths() {
  if (!tokenStore.hasLogin || !userStore.user.userId) {
    recentPaths.value = []
    return
  }
  recentPaths.value = getRecentMenuPaths(userStore.user.userId)
}

function handleMenuSelect(item: IMobileMenuItem) {
  if (!tokenStore.hasLogin || !userStore.user.userId)
    return
  navigateMenu(item, {
    recordRecent: true,
    userId: userStore.user.userId,
  })
}

async function handleRefresh(showToast = true) {
  if (!tokenStore.hasLogin)
    return
  refreshing.value = true
  try {
    await Promise.all([
      fetchRoutes(true),
      refreshStats(),
    ])
    loadRecentPaths()
    if (showToast)
      uni.showToast({ title: '已刷新', icon: 'none' })
  }
  finally {
    refreshing.value = false
    uni.stopPullDownRefresh()
  }
}

function goLogin() {
  uni.navigateTo({ url: LOGIN_PAGE })
}

onShow(() => {
  if (tokenStore.hasLogin) {
    fetchRoutes().catch(() => {})
    loadRecentPaths()
    refreshStats().catch(() => {})
  }
})

onPullDownRefresh(() => {
  handleRefresh(false)
})
</script>

<template>
  <AppPage :safe-top="false" tabbar>
    <PageHero
      gradient
      :title="heroTitle"
      :subtitle="heroSubtitle"
      :avatar="userStore.user.avatar"
      :show-avatar="tokenStore.hasLogin"
    />

    <view class="index-body">
      <view v-if="tokenStore.hasLogin" class="index-stats">
        <view
          v-for="item in stats"
          :key="item.label"
          class="index-stats__item bp-card"
        >
          <view class="bp-icon-box bp-icon-box--sm" :class="`index-stats__icon--${item.tone}`">
            <view :class="item.icon" class="index-stats__icon" />
          </view>
          <text class="index-stats__value">{{ item.value }}</text>
          <text class="index-stats__label">{{ item.label }}</text>
        </view>
      </view>

      <view v-if="showRecentSection" class="index-section">
        <view class="index-section__head index-section__head--simple">
          <text class="bp-section-title">最近使用</text>
        </view>
        <MenuGrid
          :groups="[{ title: '最近使用', items: recentItems }]"
          hide-single-group-head
          @select="handleMenuSelect"
        />
      </view>

      <view v-if="tokenStore.hasLogin && menuGroups.length" class="index-section">
        <view class="index-section__head">
          <view v-if="menuGroups.length > 1" class="index-section__titles">
            <text class="bp-section-title">全部功能</text>
            <text class="bp-section-desc">共 {{ totalMenuCount }} 项 · 按权限展示</text>
          </view>
          <view v-else class="index-section__titles">
            <text class="bp-section-desc">共 {{ totalMenuCount }} 项功能 · 按权限展示</text>
          </view>
          <view
            class="index-section__refresh"
            :class="{ 'index-section__refresh--spin': refreshing }"
            hover-class="index-section__refresh--hover"
            @click="handleRefresh()"
          >
            <view class="i-carbon-renew index-section__refresh-icon" />
            <text class="index-section__refresh-text">刷新</text>
          </view>
        </view>
        <MenuGrid
          :groups="menuGroups"
          hide-single-group-head
          @select="handleMenuSelect"
        />
      </view>

      <view v-else-if="tokenStore.hasLogin" class="index-empty bp-card">
        <wd-empty tip="暂无可用菜单，请联系管理员分配权限">
          <template #image>
            <view class="i-carbon-menu index-empty__icon" />
          </template>
          <template #bottom>
            <wd-button type="primary" size="small" plain @click="handleRefresh()">
              刷新菜单
            </wd-button>
          </template>
        </wd-empty>
      </view>

      <view v-else class="index-empty bp-card">
        <wd-empty tip="登录后查看功能菜单与业务入口">
          <template #image>
            <view class="i-carbon-login index-empty__icon" />
          </template>
          <template #bottom>
            <wd-button type="primary" size="small" @click="goLogin">
              去登录
            </wd-button>
          </template>
        </wd-empty>
      </view>
    </view>
  </AppPage>
</template>

<style lang="scss" scoped>
.index-body {
  padding: var(--bp-space-md) var(--bp-spacing-page) var(--bp-page-bottom);
}

.index-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--bp-space-sm);

  &__item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: var(--bp-space-lg) var(--bp-space-sm);
  }

  &__icon {
    font-size: 36rpx;
    color: var(--bp-color-primary);
  }

  &__icon--primary {
    background: var(--bp-color-primary-light);
    .index-stats__icon { color: var(--bp-color-primary); }
  }

  &__icon--info {
    background: var(--bp-color-primary-muted);
    .index-stats__icon { color: var(--bp-color-primary-hover); }
  }

  &__icon--warning {
    background: var(--bp-color-warning-light);
    .index-stats__icon { color: var(--bp-color-warning); }
  }

  &__value {
    margin-top: var(--bp-space-sm);
    font-size: 40rpx;
    font-weight: var(--bp-font-weight-semibold);
    color: var(--bp-text-primary);
    line-height: 1.2;
  }

  &__label {
    margin-top: 6rpx;
    font-size: var(--bp-font-mini);
    color: var(--bp-text-tertiary);
  }
}

.index-section {
  margin-top: var(--bp-space-lg);

  &__head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: var(--bp-space-md);
    margin-bottom: var(--bp-space-md);

    &--simple {
      margin-bottom: var(--bp-space-sm);
    }
  }

  &__titles {
    flex: 1;
    min-width: 0;
  }

  &__refresh {
    display: flex;
    flex-direction: row;
    align-items: center;
    flex-shrink: 0;
    gap: 6rpx;
    padding: var(--bp-space-xs) var(--bp-space-sm);
    border-radius: var(--bp-radius-sm);
    color: var(--bp-text-tertiary);

    &--hover {
      background: var(--bp-bg-hover);
    }

    &--spin .index-section__refresh-icon {
      animation: index-spin 0.8s linear infinite;
    }
  }

  &__refresh-icon {
    font-size: 32rpx;
    color: var(--bp-color-primary);
  }

  &__refresh-text {
    font-size: var(--bp-font-mini);
    color: var(--bp-text-tertiary);
  }
}

.index-empty {
  margin-top: var(--bp-space-lg);
  padding: var(--bp-space-xl) var(--bp-space-md);

  &__icon {
    font-size: 120rpx;
    color: var(--bp-text-disabled);
  }
}

@keyframes index-spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
