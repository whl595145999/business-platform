<script lang="ts" setup>
import type { ILoginForm, ITenantVo } from '@/api/types/backend'
import { getCodeImg, getTenantList } from '@/api/auth'
import { appConfig } from '@/config/app.config'
import { DEFAULT_TENANT_ID } from '@/constants/app'
import { HOME_PAGE } from '@/utils'
import { clearLoginRemember, getLoginRemember, setLoginRemember } from '@/utils/loginRemember'
import { useTokenStore } from '@/store/token'

definePage({
  excludeLoginPath: true,
  style: {
    navigationBarTitleText: '登录',
    navigationStyle: 'custom',
  },
})

const tokenStore = useTokenStore()

const captchaEnabled = ref(true)
const tenantEnabled = ref(false)
const codeUrl = ref('')
const tenantList = ref<ITenantVo[]>([])
const loading = ref(false)
const tenantPickerVisible = ref(false)
const tenantPickerValue = ref<string[]>([DEFAULT_TENANT_ID])

const rememberAccount = ref(true)

const loginForm = ref<ILoginForm>({
  tenantId: DEFAULT_TENANT_ID,
  username: '',
  password: '',
  code: '',
  uuid: '',
})

const tenantColumns = computed(() =>
  tenantList.value.map(item => ({
    label: item.companyName,
    value: item.tenantId,
  })),
)

const tenantLabel = computed(() =>
  tenantColumns.value.find(item => item.value === loginForm.value.tenantId)?.label ?? '',
)

function syncTenantPicker() {
  tenantPickerValue.value = [loginForm.value.tenantId]
}

function openTenantPicker() {
  syncTenantPicker()
  tenantPickerVisible.value = true
}

function onTenantConfirm({ value }: { value: string[] }) {
  loginForm.value.tenantId = value[0]
}

function applyRememberedAccount() {
  const remembered = getLoginRemember()
  if (remembered?.username)
    loginForm.value.username = remembered.username
  if (remembered?.tenantId)
    loginForm.value.tenantId = remembered.tenantId
  loginForm.value.password = ''
  syncTenantPicker()
}

async function loadCaptcha() {
  try {
    const data = await getCodeImg()
    captchaEnabled.value = data.captchaEnabled ?? true
    if (captchaEnabled.value) {
      codeUrl.value = `data:image/gif;base64,${data.img}`
      loginForm.value.uuid = data.uuid
    }
  }
  catch (error) {
    console.error('获取验证码失败', error)
  }
}

async function loadTenantList() {
  try {
    const data = await getTenantList()
    tenantEnabled.value = data.tenantEnabled ?? false
    if (tenantEnabled.value && data.voList?.length) {
      tenantList.value = data.voList
      const remembered = getLoginRemember()
      const rememberedTenant = remembered?.tenantId
      const matched = rememberedTenant
        && data.voList.some(item => item.tenantId === rememberedTenant)
      loginForm.value.tenantId = matched
        ? rememberedTenant
        : data.voList[0].tenantId
      syncTenantPicker()
    }
  }
  catch (error) {
    console.error('获取租户列表失败', error)
  }
}

function validateForm() {
  if (!loginForm.value.username) {
    uni.showToast({ title: '请输入账号', icon: 'none' })
    return false
  }
  if (!loginForm.value.password) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return false
  }
  if (captchaEnabled.value && !loginForm.value.code) {
    uni.showToast({ title: '请输入验证码', icon: 'none' })
    return false
  }
  return true
}

async function handleLogin() {
  if (!validateForm())
    return

  loading.value = true
  try {
    await tokenStore.login(loginForm.value)
    if (rememberAccount.value) {
      setLoginRemember({
        username: loginForm.value.username,
        tenantId: loginForm.value.tenantId,
      })
    }
    else {
      clearLoginRemember()
    }
    uni.switchTab({ url: HOME_PAGE })
  }
  catch {
    if (captchaEnabled.value)
      await loadCaptcha()
  }
  finally {
    loading.value = false
  }
}

onLoad(() => {
  rememberAccount.value = !!getLoginRemember()
  applyRememberedAccount()
  loadCaptcha()
  loadTenantList()
})
</script>

<template>
  <AppPage :safe-top="false" :gray="true">
    <view class="login-page">
      <view class="login-page__header">
        <view class="login-page__header-bg" />
        <view class="login-page__brand">
          <view class="login-page__logo-wrap">
            <image :src="appConfig.appInfo.logo" class="login-page__logo" mode="aspectFit" />
          </view>
          <text class="login-page__name">{{ appConfig.appInfo.name }}</text>
          <text class="login-page__slogan">供应链业务平台 · 移动端</text>
        </view>
      </view>

      <view class="login-page__form-wrap">
        <view class="login-page__form bp-card">
          <text class="login-page__form-title">账号登录</text>

          <wd-form :model="loginForm" border title-width="160rpx">
            <wd-form-item
              v-if="tenantEnabled && tenantList.length"
              title="租户"
              :value="tenantLabel"
              placeholder="请选择租户"
              is-link
              clickable
              @click="openTenantPicker"
            />
            <wd-form-item title="账号">
              <wd-input
                v-model="loginForm.username"
                compact
                clearable
                placeholder="请输入账号"
              />
            </wd-form-item>
            <wd-form-item title="密码">
              <wd-input
                v-model="loginForm.password"
                compact
                show-password
                clearable
                placeholder="请输入密码"
              />
            </wd-form-item>
            <wd-form-item v-if="captchaEnabled" title="验证码">
              <view class="login-page__captcha">
                <wd-input
                  v-model="loginForm.code"
                  compact
                  clearable
                  placeholder="请输入验证码"
                  class="login-page__captcha-input"
                />
                <image
                  :src="codeUrl"
                  class="login-page__captcha-img"
                  mode="aspectFit"
                  @click="loadCaptcha"
                />
              </view>
            </wd-form-item>
          </wd-form>

          <view class="login-page__remember">
            <wd-checkbox v-model="rememberAccount" shape="square">
              记住账号
            </wd-checkbox>
          </view>

          <wd-button
            type="primary"
            block
            :loading="loading"
            custom-class="login-page__submit"
            @click="handleLogin"
          >
            登 录
          </wd-button>
        </view>

        <view class="login-page__footer">
          登录即代表同意《用户协议》和《隐私政策》
        </view>
      </view>
    </view>

    <wd-picker
      v-if="tenantEnabled && tenantList.length"
      v-model="tenantPickerValue"
      v-model:visible="tenantPickerVisible"
      :columns="tenantColumns"
      title="选择租户"
      @confirm="onTenantConfirm"
    />
  </AppPage>
</template>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;

  &__header {
    position: relative;
    padding: calc(var(--bp-space-xl) + env(safe-area-inset-top)) var(--bp-spacing-page) var(--bp-space-xl);
    overflow: hidden;
  }

  &__header-bg {
    position: absolute;
    inset: 0;
    background: linear-gradient(180deg, var(--bp-color-primary-muted) 0%, var(--bp-bg-page) 100%);
    z-index: 0;
  }

  &__brand {
    position: relative;
    z-index: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  &__logo-wrap {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 128rpx;
    height: 128rpx;
    border-radius: var(--bp-radius-lg);
    background: var(--bp-bg-card);
    border: 1rpx solid var(--bp-border-color);
    box-shadow: var(--bp-shadow-sm);
  }

  &__logo {
    width: 80rpx;
    height: 80rpx;
  }

  &__name {
    margin-top: var(--bp-space-md);
    font-size: var(--bp-font-title);
    font-weight: var(--bp-font-weight-semibold);
    color: var(--bp-text-primary);
  }

  &__slogan {
    margin-top: var(--bp-space-xs);
    font-size: var(--bp-font-caption);
    color: var(--bp-text-tertiary);
  }

  &__form-wrap {
    flex: 1;
    margin-top: calc(-1 * var(--bp-space-lg));
    padding: 0 var(--bp-spacing-page) var(--bp-space-xl);
  }

  &__form {
    padding: var(--bp-space-lg) var(--bp-spacing-card);
  }

  &__form-title {
    display: block;
    margin-bottom: var(--bp-space-md);
    font-size: var(--bp-font-subhead);
    font-weight: var(--bp-font-weight-semibold);
    color: var(--bp-text-primary);
  }

  &__remember {
    margin: var(--bp-space-md) 0;
    padding: 0 var(--bp-space-xs);
  }

  &__captcha {
    display: flex;
    align-items: center;
    gap: var(--bp-space-sm);
    width: 100%;
  }

  &__captcha-input {
    flex: 1;
    min-width: 0;
  }

  &__captcha-img {
    width: 200rpx;
    height: 72rpx;
    flex-shrink: 0;
    border-radius: var(--bp-radius-sm);
    border: 1rpx solid var(--bp-border-color);
    background: var(--bp-bg-page);
  }

  &__submit {
    margin-top: var(--bp-space-lg);
  }

  &__footer {
    margin-top: var(--bp-space-xl);
    text-align: center;
    font-size: var(--bp-font-mini);
    color: var(--bp-text-tertiary);
    line-height: 1.6;
  }
}

:deep(.wd-form-item__body .wd-input) {
  width: 100%;
}
</style>
