<script lang="ts" setup>
import type { IRoleForm } from '@/api/types/system/role'
import { addRole, delRole, getRole, updateRole } from '@/api/system/role'
import { useAuth } from '@/hooks/useAuth'
import { useDict } from '@/hooks/useDict'

definePage({
  style: {
    navigationBarTitleText: '角色表单',
  },
})

const { hasPermi } = useAuth()
const { sys_normal_disable } = useDict(['sys_normal_disable'])

const roleId = ref<string | number>()
const loading = ref(false)
const submitting = ref(false)
const statusPickerVisible = ref(false)
const statusPickerValue = ref<string[]>(['0'])

const isEdit = computed(() => roleId.value !== undefined && roleId.value !== '')

const form = ref<IRoleForm>({
  roleName: '',
  roleKey: '',
  roleSort: 0,
  status: '0',
  remark: '',
  menuIds: [],
  deptIds: [],
  menuCheckStrictly: true,
  deptCheckStrictly: true,
  dataScope: '1',
})

const statusColumns = computed(() =>
  (sys_normal_disable.value || []).map(item => ({
    label: item.dictLabel,
    value: item.dictValue,
  })),
)

const statusLabel = computed(() =>
  statusColumns.value.find(item => item.value === form.value.status)?.label ?? '',
)

const canSubmit = computed(() =>
  isEdit.value ? hasPermi('system:role:edit') : hasPermi('system:role:add'),
)

const canDelete = computed(() => isEdit.value && hasPermi('system:role:remove'))

function syncPickerValues() {
  statusPickerValue.value = [form.value.status ?? '0']
}

function openStatusPicker() {
  if (!canSubmit.value)
    return
  syncPickerValues()
  statusPickerVisible.value = true
}

function onStatusConfirm({ value }: { value: string[] }) {
  form.value.status = value[0]
}

async function loadDetail() {
  if (!isEdit.value)
    return
  loading.value = true
  try {
    const res = await getRole(roleId.value)
    const role = res.role || res.data
    if (!role) {
      uni.showToast({ title: '角色不存在', icon: 'none' })
      return
    }
    form.value = {
      roleId: role.roleId,
      roleName: role.roleName,
      roleKey: role.roleKey,
      roleSort: role.roleSort ?? 0,
      status: role.status ?? '0',
      remark: role.remark ?? '',
      menuIds: res.menuIds ?? [],
      deptIds: [],
      menuCheckStrictly: role.menuCheckStrictly ?? true,
      deptCheckStrictly: role.deptCheckStrictly ?? true,
      dataScope: role.dataScope ?? '1',
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
  if (!form.value.roleName?.trim()) {
    uni.showToast({ title: '请输入角色名称', icon: 'none' })
    return false
  }
  if (!form.value.roleKey?.trim()) {
    uni.showToast({ title: '请输入权限字符', icon: 'none' })
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
    const payload: IRoleForm = {
      ...form.value,
      roleSort: Number(form.value.roleSort) || 0,
    }
    if (isEdit.value)
      await updateRole(payload)
    else
      await addRole(payload)

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
  if (!canDelete.value || !form.value.roleId)
    return
  uni.showModal({
    title: '确认删除',
    content: `确定删除角色「${form.value.roleName}」吗？`,
    success: async (res) => {
      if (!res.confirm)
        return
      submitting.value = true
      try {
        await delRole(form.value.roleId!)
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
    roleId.value = options.id
  uni.setNavigationBarTitle({
    title: isEdit.value ? '编辑角色' : '新增角色',
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
        <text class="bp-section-title">基本信息</text>
        <text class="bp-section-desc">权限字符创建后不可修改</text>
        <wd-form :model="form" border title-width="200rpx" custom-class="bp-crud-group">
          <wd-form-item title="角色名称">
            <wd-input
              v-model="form.roleName"
              compact
              clearable
              placeholder="请输入角色名称"
              :disabled="!canSubmit"
            />
          </wd-form-item>
          <wd-form-item title="权限字符">
            <wd-input
              v-model="form.roleKey"
              compact
              clearable
              placeholder="如 admin、common"
              :disabled="!canSubmit || isEdit"
            />
          </wd-form-item>
          <wd-form-item title="显示顺序">
            <wd-input
              v-model="form.roleSort"
              compact
              type="number"
              placeholder="数字越小越靠前"
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

      <text class="bp-crud-hint">菜单权限请在 PC 端配置；编辑将保留原有 menuIds。</text>
    </view>

    <view v-if="!loading" class="bp-crud-footer">
      <PermButton
        v-if="canSubmit"
        type="primary"
        block
        :loading="submitting"
        :perm="isEdit ? 'system:role:edit' : 'system:role:add'"
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
        perm="system:role:remove"
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
  </AppPage>
</template>

<style lang="scss" scoped>
:deep(.wd-form-item__body .wd-input) {
  width: 100%;
}
</style>
