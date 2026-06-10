<script lang="ts" setup>
import { updateUserPwd } from '@/api/system/user'
import { LOGIN_PAGE } from '@/router/config'
import { useTokenStore } from '@/store/token'
import { toLoginPage } from '@/utils/toLoginPage'

definePage({
  style: {
    navigationBarTitleText: '修改密码',
  },
})

const tokenStore = useTokenStore()
const submitting = ref(false)

const form = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

function validateForm() {
  if (!form.value.oldPassword) {
    uni.showToast({ title: '请输入原密码', icon: 'none' })
    return false
  }
  if (!form.value.newPassword) {
    uni.showToast({ title: '请输入新密码', icon: 'none' })
    return false
  }
  if (form.value.newPassword.length < 5 || form.value.newPassword.length > 20) {
    uni.showToast({ title: '新密码长度为 5～20 位', icon: 'none' })
    return false
  }
  if (form.value.newPassword !== form.value.confirmPassword) {
    uni.showToast({ title: '两次输入的新密码不一致', icon: 'none' })
    return false
  }
  if (form.value.oldPassword === form.value.newPassword) {
    uni.showToast({ title: '新密码不能与原密码相同', icon: 'none' })
    return false
  }
  return true
}

async function handleSubmit() {
  if (!tokenStore.hasLogin) {
    uni.navigateTo({ url: LOGIN_PAGE })
    return
  }
  if (!validateForm())
    return

  submitting.value = true
  try {
    await updateUserPwd(form.value.oldPassword, form.value.newPassword)
    uni.showModal({
      title: '修改成功',
      content: '密码已更新，请使用新密码重新登录',
      showCancel: false,
      success: async () => {
        await tokenStore.logout()
        toLoginPage({ mode: 'reLaunch' })
      },
    })
  }
  catch {
    // http 层已 toast 后端错误信息
  }
  finally {
    submitting.value = false
  }
}

onLoad(() => {
  if (!tokenStore.hasLogin)
    uni.navigateTo({ url: LOGIN_PAGE })
})
</script>

<template>
  <AppPage :safe-top="false">
    <view class="bp-crud-page">
      <view class="bp-crud-section">
        <wd-form :model="form" border title-width="200rpx" custom-class="bp-crud-group">
          <wd-form-item title="原密码">
            <wd-input
              v-model="form.oldPassword"
              compact
              show-password
              clearable
              placeholder="请输入原密码"
            />
          </wd-form-item>
          <wd-form-item title="新密码">
            <wd-input
              v-model="form.newPassword"
              compact
              show-password
              clearable
              placeholder="5～20 位字符"
            />
          </wd-form-item>
          <wd-form-item title="确认密码">
            <wd-input
              v-model="form.confirmPassword"
              compact
              show-password
              clearable
              placeholder="请再次输入新密码"
            />
          </wd-form-item>
        </wd-form>
      </view>

      <text class="bp-crud-hint">
        密码将加密传输。修改成功后将退出当前账号，需使用新密码重新登录（企业系统通用做法）。
      </text>
    </view>

    <view class="bp-crud-footer">
      <wd-button type="primary" block :loading="submitting" @click="handleSubmit">
        确认修改
      </wd-button>
    </view>
  </AppPage>
</template>

<style lang="scss" scoped>
:deep(.wd-form-item__body .wd-input) {
  width: 100%;
}
</style>
