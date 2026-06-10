<script lang="ts" setup>
import { storeToRefs } from 'pinia'
import { appConfig } from '@/config/app.config'
import { LOGIN_PAGE } from '@/router/config'
import { useTokenStore } from '@/store/token'
import { useUserStore } from '@/store/user'

definePage({
  style: {
    navigationBarTitleText: '我的',
    navigationStyle: 'custom',
  },
})

const tokenStore = useTokenStore()
const userStore = useUserStore()
const { user, roles } = storeToRefs(userStore)

const heroTitle = computed(() =>
  tokenStore.hasLogin ? (user.value.nickName || user.value.userName) : '未登录',
)

const heroSubtitle = computed(() => {
  if (!tokenStore.hasLogin)
    return '点击登录账户'
  const roleText = roles.value.join('、') || '未分配角色'
  return `${roleText} · 点击管理账户`
})

function handleLogin() {
  uni.navigateTo({ url: LOGIN_PAGE })
}

function handleHeroClick() {
  if (!tokenStore.hasLogin)
    handleLogin()
}

function goPassword() {
  if (!tokenStore.hasLogin) {
    handleLogin()
    return
  }
  uni.navigateTo({ url: '/pages/me/password' })
}

function handleMenuClick(title: string) {
  if (!tokenStore.hasLogin) {
    handleLogin()
    return
  }
  if (title === '修改密码') {
    goPassword()
    return
  }
  uni.showToast({ title: `${title}功能开发中`, icon: 'none' })
}

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定退出登录吗？',
    success: async (res) => {
      if (res.confirm) {
        await tokenStore.logout()
        uni.showToast({ title: '已退出登录', icon: 'success' })
      }
    },
  })
}
</script>

<template>
  <AppPage :safe-top="false" tabbar>
    <view @click="handleHeroClick">
      <PageHero
        compact
        :title="heroTitle"
        :subtitle="heroSubtitle"
        :avatar="user.avatar"
      />
    </view>

    <view class="me-body">
      <wd-cell-group title="账户" border>
        <wd-cell title="编辑资料" is-link clickable @click="handleMenuClick('编辑资料')">
          <template #prefix>
            <view class="bp-icon-box bp-icon-box--sm me-cell-icon-box">
              <view class="i-carbon-user-profile me-cell-icon" />
            </view>
          </template>
        </wd-cell>
        <wd-cell title="修改密码" is-link clickable @click="goPassword">
          <template #prefix>
            <view class="bp-icon-box bp-icon-box--sm me-cell-icon-box">
              <view class="i-carbon-password me-cell-icon" />
            </view>
          </template>
        </wd-cell>
      </wd-cell-group>

      <wd-cell-group title="通用" border custom-class="me-body__gap">
        <wd-cell title="关于我们" is-link clickable @click="handleMenuClick('关于我们')">
          <template #prefix>
            <view class="bp-icon-box bp-icon-box--sm me-cell-icon-box">
              <view class="i-carbon-information me-cell-icon" />
            </view>
          </template>
        </wd-cell>
        <wd-cell title="应用设置" is-link clickable @click="handleMenuClick('应用设置')">
          <template #prefix>
            <view class="bp-icon-box bp-icon-box--sm me-cell-icon-box">
              <view class="i-carbon-settings me-cell-icon" />
            </view>
          </template>
        </wd-cell>
      </wd-cell-group>

      <view v-if="tokenStore.hasLogin" class="me-body__logout">
        <wd-button type="error" block plain hairline @click="handleLogout">
          退出登录
        </wd-button>
      </view>

      <view v-else class="me-body__login">
        <wd-button type="primary" block @click="handleLogin">
          登录
        </wd-button>
      </view>

      <view class="me-footer">
        {{ appConfig.appInfo.name }} v{{ appConfig.appInfo.version }}
      </view>
    </view>
  </AppPage>
</template>

<style lang="scss" scoped>
.me-body {
  padding: var(--bp-space-md) var(--bp-spacing-page) var(--bp-space-xl);

  &__gap {
    margin-top: var(--bp-space-md);
  }

  &__logout,
  &__login {
    margin-top: var(--bp-space-xl);
  }
}

.me-cell-icon-box {
  margin-right: var(--bp-space-sm);
}

.me-cell-icon {
  font-size: 36rpx;
  color: var(--bp-color-primary);
}

.me-footer {
  margin-top: var(--bp-space-xl);
  text-align: center;
  font-size: var(--bp-font-mini);
  color: var(--bp-text-disabled);
}
</style>
