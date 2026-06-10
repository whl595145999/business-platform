<script lang="ts" setup>
import type { IDeptForm, IDeptVo } from '@/api/types/system/dept'
import { addDept, delDept, getDept, listDept, listDeptExcludeChild, updateDept } from '@/api/system/dept'
import { useAuth } from '@/hooks/useAuth'
import { useDict } from '@/hooks/useDict'

definePage({
  style: {
    navigationBarTitleText: '部门表单',
  },
})

const { hasPermi } = useAuth()
const { sys_normal_disable } = useDict(['sys_normal_disable'])

const deptId = ref<string | number>()
const loading = ref(false)
const submitting = ref(false)
const parentOptions = ref<Array<{ label: string, value: string | number }>>([])
const parentPickerVisible = ref(false)
const statusPickerVisible = ref(false)
const parentPickerValue = ref<Array<string | number>>([0])
const statusPickerValue = ref<string[]>(['0'])

const isEdit = computed(() => deptId.value !== undefined && deptId.value !== '')

const form = ref<IDeptForm>({
  parentId: 0,
  deptName: '',
  orderNum: 0,
  leader: '',
  phone: '',
  email: '',
  status: '0',
})

const statusColumns = computed(() =>
  (sys_normal_disable.value || []).map(item => ({
    label: item.dictLabel,
    value: item.dictValue,
  })),
)

const parentLabel = computed(() =>
  parentOptions.value.find(item => item.value === form.value.parentId)?.label ?? '',
)

const statusLabel = computed(() =>
  statusColumns.value.find(item => item.value === form.value.status)?.label ?? '',
)

const canSubmit = computed(() =>
  isEdit.value ? hasPermi('system:dept:edit') : hasPermi('system:dept:add'),
)

const canDelete = computed(() => isEdit.value && hasPermi('system:dept:remove'))

function flattenForPicker(list: IDeptVo[], prefix = ''): Array<{ label: string, value: string | number }> {
  const result: Array<{ label: string, value: string | number }> = []
  for (const item of list) {
    const label = prefix ? `${prefix} / ${item.deptName}` : item.deptName
    result.push({ label, value: item.deptId })
    if (item.children?.length)
      result.push(...flattenForPicker(item.children, label))
  }
  return result
}

function syncPickerValues() {
  parentPickerValue.value = [form.value.parentId ?? 0]
  statusPickerValue.value = [form.value.status ?? '0']
}

function openParentPicker() {
  if (!canSubmit.value)
    return
  syncPickerValues()
  parentPickerVisible.value = true
}

function openStatusPicker() {
  if (!canSubmit.value)
    return
  syncPickerValues()
  statusPickerVisible.value = true
}

function onParentConfirm({ value }: { value: Array<string | number> }) {
  form.value.parentId = value[0]
}

function onStatusConfirm({ value }: { value: string[] }) {
  form.value.status = value[0]
}

async function loadParentOptions() {
  try {
    const tree = isEdit.value && deptId.value
      ? await listDeptExcludeChild(deptId.value)
      : await listDept()
    parentOptions.value = [
      { label: '顶级部门', value: 0 },
      ...flattenForPicker(tree || []),
    ]
  }
  catch {
    parentOptions.value = [{ label: '顶级部门', value: 0 }]
  }
}

async function loadDetail() {
  loading.value = true
  try {
    await loadParentOptions()
    if (!isEdit.value) {
      syncPickerValues()
      return
    }

    const dept = await getDept(deptId.value)
    if (!dept?.deptId) {
      uni.showToast({ title: '部门不存在', icon: 'none' })
      return
    }
    form.value = {
      deptId: dept.deptId,
      parentId: dept.parentId ?? 0,
      deptName: dept.deptName,
      orderNum: dept.orderNum ?? 0,
      leader: dept.leader ?? '',
      phone: dept.phone ?? '',
      email: dept.email ?? '',
      status: dept.status ?? '0',
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
  if (!form.value.deptName?.trim()) {
    uni.showToast({ title: '请输入部门名称', icon: 'none' })
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
    const payload: IDeptForm = {
      ...form.value,
      orderNum: Number(form.value.orderNum) || 0,
      parentId: form.value.parentId ?? 0,
    }
    if (isEdit.value)
      await updateDept(payload)
    else
      await addDept(payload)

    uni.showToast({ title: '保存成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 400)
  }
  catch {
    // http 拦截器已 toast
  }
  finally {
    submitting.value = false
  }
}

function handleDelete() {
  if (!canDelete.value || !form.value.deptId)
    return

  uni.showModal({
    title: '确认删除',
    content: `确定删除部门「${form.value.deptName}」吗？`,
    success: async (res) => {
      if (!res.confirm)
        return
      submitting.value = true
      try {
        await delDept(form.value.deptId!)
        uni.showToast({ title: '已删除', icon: 'success' })
        setTimeout(() => uni.navigateBack(), 400)
      }
      catch {
        // http 拦截器已 toast
      }
      finally {
        submitting.value = false
      }
    },
  })
}

onLoad((options) => {
  if (options?.id)
    deptId.value = options.id

  uni.setNavigationBarTitle({
    title: isEdit.value ? '编辑部门' : '新增部门',
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
        <wd-form :model="form" border title-width="200rpx" custom-class="bp-crud-group">
          <wd-form-item
            title="上级部门"
            :value="parentLabel"
            placeholder="请选择"
            is-link
            :clickable="canSubmit"
            @click="openParentPicker"
          />
          <wd-form-item title="部门名称">
            <wd-input
              v-model="form.deptName"
              compact
              clearable
              placeholder="请输入部门名称"
              :disabled="!canSubmit"
            />
          </wd-form-item>
          <wd-form-item title="显示排序">
            <wd-input
              v-model="form.orderNum"
              compact
              type="number"
              placeholder="数字越小越靠前"
              :disabled="!canSubmit"
            />
          </wd-form-item>
          <wd-form-item title="负责人">
            <wd-input
              v-model="form.leader"
              compact
              clearable
              placeholder="选填"
              :disabled="!canSubmit"
            />
          </wd-form-item>
          <wd-form-item title="联系电话">
            <wd-input
              v-model="form.phone"
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
          <wd-form-item
            title="状态"
            :value="statusLabel"
            placeholder="请选择"
            is-link
            :clickable="canSubmit"
            @click="openStatusPicker"
          />
        </wd-form>
      </view>
    </view>

    <view v-if="!loading" class="bp-crud-footer">
      <PermButton
        type="primary"
        block
        :loading="submitting"
        :perm="isEdit ? 'system:dept:edit' : 'system:dept:add'"
        @click="handleSubmit"
      >
        {{ isEdit ? '保存修改' : '确认新增' }}
      </PermButton>

      <PermButton
        v-if="canDelete"
        type="error"
        plain
        block
        :loading="submitting"
        perm="system:dept:remove"
        @click="handleDelete"
      >
        删除部门
      </PermButton>
    </view>

    <wd-picker
      v-model="parentPickerValue"
      v-model:visible="parentPickerVisible"
      :columns="parentOptions"
      title="上级部门"
      @confirm="onParentConfirm"
    />
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
