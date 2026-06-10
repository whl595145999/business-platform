<script lang="ts" setup>
import type { IUserForm } from '@/api/types/system/user'
import { addUser, delUser, getUser, updateUser } from '@/api/system/user'
import { useAuth } from '@/hooks/useAuth'
import { useDict } from '@/hooks/useDict'

definePage({
  style: {
    navigationBarTitleText: '用户表单',
  },
})

const { hasPermi } = useAuth()
const { sys_normal_disable, sys_user_sex } = useDict(['sys_normal_disable', 'sys_user_sex'])

const userId = ref<string | number>()
const loading = ref(false)
const submitting = ref(false)
const statusPickerVisible = ref(false)
const sexPickerVisible = ref(false)
const statusPickerValue = ref<string[]>(['0'])
const sexPickerValue = ref<string[]>(['0'])

const isEdit = computed(() => userId.value !== undefined && userId.value !== '')

const form = ref<IUserForm>({
  userName: '',
  nickName: '',
  password: '',
  phonenumber: '',
  email: '',
  sex: '0',
  status: '0',
  remark: '',
})

const statusColumns = computed(() =>
  (sys_normal_disable.value || []).map(item => ({
    label: item.dictLabel,
    value: item.dictValue,
  })),
)

const sexColumns = computed(() =>
  (sys_user_sex.value || []).map(item => ({
    label: item.dictLabel,
    value: item.dictValue,
  })),
)

const statusLabel = computed(() =>
  statusColumns.value.find(item => item.value === form.value.status)?.label ?? '',
)

const sexLabel = computed(() =>
  sexColumns.value.find(item => item.value === form.value.sex)?.label ?? '',
)

const canSubmit = computed(() =>
  isEdit.value ? hasPermi('system:user:edit') : hasPermi('system:user:add'),
)

const canDelete = computed(() => isEdit.value && hasPermi('system:user:remove'))

function syncPickerValues() {
  statusPickerValue.value = [form.value.status ?? '0']
  sexPickerValue.value = [form.value.sex ?? '0']
}

function openStatusPicker() {
  if (!canSubmit.value)
    return
  syncPickerValues()
  statusPickerVisible.value = true
}

function openSexPicker() {
  if (!canSubmit.value)
    return
  syncPickerValues()
  sexPickerVisible.value = true
}

function onStatusConfirm({ value }: { value: string[] }) {
  form.value.status = value[0]
}

function onSexConfirm({ value }: { value: string[] }) {
  form.value.sex = value[0]
}

async function loadDetail() {
  if (!isEdit.value)
    return
  loading.value = true
  try {
    const res = await getUser(userId.value)
    const user = res.user || res.data
    if (!user?.userId) {
      uni.showToast({ title: '用户不存在', icon: 'none' })
      return
    }
    form.value = {
      userId: user.userId,
      userName: user.userName,
      nickName: user.nickName,
      phonenumber: user.phonenumber ?? '',
      email: user.email ?? '',
      sex: user.sex ?? '0',
      status: user.status ?? '0',
      remark: user.remark ?? '',
      roleIds: res.roles?.map((r: any) => r.roleId) ?? [],
      postIds: res.posts?.map((p: any) => p.postId) ?? [],
    }
    syncPickerValues()
  }
  catch {
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
  finally {
    loading.value = false
  }
}

function validateForm() {
  if (!form.value.userName?.trim()) {
    uni.showToast({ title: '请输入登录账号', icon: 'none' })
    return false
  }
  if (!isEdit.value && !form.value.password?.trim()) {
    uni.showToast({ title: '请输入登录密码', icon: 'none' })
    return false
  }
  return true
}

async function handleSubmit() {
  if (!canSubmit.value) {
    uni.showToast({ title: '无操作权限', icon: 'none' })
    return
  }
  if (!validateForm())
    return

  submitting.value = true
  try {
    const payload = { ...form.value }
    if (isEdit.value)
      delete payload.password
    if (isEdit.value)
      await updateUser(payload)
    else
      await addUser(payload)

    uni.showToast({ title: '保存成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 400)
  }
  catch {
    // http 已 toast
  }
  finally {
    submitting.value = false
  }
}

function handleDelete() {
  if (!canDelete.value || !form.value.userId)
    return
  uni.showModal({
    title: '确认删除',
    content: `确定删除用户「${form.value.nickName || form.value.userName}」吗？`,
    success: async (res) => {
      if (!res.confirm)
        return
      submitting.value = true
      try {
        await delUser(form.value.userId!)
        uni.showToast({ title: '已删除', icon: 'success' })
        setTimeout(() => uni.navigateBack(), 400)
      }
      catch {
        // http 已 toast
      }
      finally {
        submitting.value = false
      }
    },
  })
}

onLoad((options) => {
  if (options?.id)
    userId.value = options.id
  uni.setNavigationBarTitle({
    title: isEdit.value ? '编辑用户' : '新增用户',
  })
  syncPickerValues()
  loadDetail()
})
</script>

<template>
  <AppPage :safe-top="false">
    <view v-if="loading" class="bp-crud-page">
      <text class="bp-text-secondary">加载中...</text>
    </view>

    <view v-else class="bp-crud-page">
      <view class="bp-crud-section">
        <text class="bp-section-title">账号信息</text>
        <text class="bp-section-desc">登录账号创建后不可修改</text>
        <wd-form :model="form" border title-width="200rpx" custom-class="bp-crud-group">
          <wd-form-item title="登录账号">
            <wd-input
              v-model="form.userName"
              compact
              clearable
              placeholder="请输入登录账号"
              :disabled="!canSubmit || isEdit"
            />
          </wd-form-item>
          <wd-form-item v-if="!isEdit" title="登录密码">
            <wd-input
              v-model="form.password"
              compact
              show-password
              clearable
              placeholder="请输入密码"
              :disabled="!canSubmit"
            />
          </wd-form-item>
          <wd-form-item title="用户昵称">
            <wd-input
              v-model="form.nickName"
              compact
              clearable
              placeholder="请输入昵称"
              :disabled="!canSubmit"
            />
          </wd-form-item>
          <wd-form-item
            title="状态"
            :value="statusLabel"
            placeholder="请选择"
            is-link
            :clickable="canSubmit"
            @click="openStatusPicker"
          />
          <wd-form-item
            title="性别"
            :value="sexLabel"
            placeholder="请选择"
            is-link
            :clickable="canSubmit"
            @click="openSexPicker"
          />
        </wd-form>
      </view>

      <view class="bp-crud-section">
        <text class="bp-section-title">联系方式</text>
        <wd-form :model="form" border title-width="200rpx" custom-class="bp-crud-group">
          <wd-form-item title="手机号码">
            <wd-input
              v-model="form.phonenumber"
              compact
              clearable
              placeholder="选填"
              :disabled="!canSubmit"
            />
          </wd-form-item>
          <wd-form-item title="邮箱">
            <wd-input
              v-model="form.email"
              compact
              clearable
              placeholder="选填"
              :disabled="!canSubmit"
            />
          </wd-form-item>
          <wd-form-item title="备注">
            <wd-input
              v-model="form.remark"
              compact
              clearable
              placeholder="选填"
              :disabled="!canSubmit"
            />
          </wd-form-item>
        </wd-form>
      </view>

      <text class="bp-crud-hint">部门、角色、岗位请在 PC 端分配。</text>
    </view>

    <view v-if="!loading" class="bp-crud-footer">
      <PermButton
        v-if="canSubmit"
        type="primary"
        block
        :loading="submitting"
        :perm="isEdit ? 'system:user:edit' : 'system:user:add'"
        @click="handleSubmit"
      >
        {{ isEdit ? '保存' : '提交' }}
      </PermButton>
      <PermButton
        v-if="canDelete"
        type="error"
        plain
        block
        :loading="submitting"
        perm="system:user:remove"
        @click="handleDelete"
      >
        删除
      </PermButton>
    </view>

    <wd-picker
      v-model="statusPickerValue"
      v-model:visible="statusPickerVisible"
      :columns="statusColumns"
      title="状态"
      @confirm="onStatusConfirm"
    />
    <wd-picker
      v-model="sexPickerValue"
      v-model:visible="sexPickerVisible"
      :columns="sexColumns"
      title="性别"
      @confirm="onSexConfirm"
    />
  </AppPage>
</template>

<style lang="scss" scoped>
:deep(.wd-form-item__body .wd-input) {
  width: 100%;
}
</style>
