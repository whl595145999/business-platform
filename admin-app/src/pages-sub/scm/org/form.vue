<script lang="ts" setup>
import type { IOrgForm, IOrgVo } from '@/api/types/scm/org'
import type { IDeptVo } from '@/api/types/system/dept'
import { addOrg, delOrg, getOrg, listOrgOptions, updateOrg } from '@/api/scm/org'
import { listDept } from '@/api/system/dept'
import {
  formatOrgStatusLabel,
  formatOrgTypeLabel,
  ORG_STATUS_OPTIONS,
  ORG_TYPE_OPTIONS,
} from '@/constants/scm-org'
import { useAuth } from '@/hooks/useAuth'

definePage({
  style: {
    navigationBarTitleText: '组织表单',
  },
})

const { hasPermi } = useAuth()

const orgId = ref<string | number>()
const loading = ref(false)
const submitting = ref(false)
const orgOptions = ref<IOrgVo[]>([])
const deptOptions = ref<{ label: string, value: number }[]>([])

const isEdit = computed(() => orgId.value !== undefined && orgId.value !== '')

const form = ref<IOrgForm>({
  orgCode: '',
  orgName: '',
  orgType: 20,
  parentOrgId: 0,
  status: 10,
  sortOrder: 0,
  remark: '',
})

const orgTypeColumns = ORG_TYPE_OPTIONS.map(item => ({
  label: item.label,
  value: item.value,
}))

const statusColumns = ORG_STATUS_OPTIONS.map(item => ({
  label: item.label,
  value: item.value,
}))

const parentColumns = computed(() => {
  const excludeId = isEdit.value ? Number(orgId.value) : undefined
  const nodes = orgOptions.value
    .filter(item => item.id !== excludeId)
    .map(item => ({
      label: `${item.orgName} (${item.orgCode})`,
      value: item.id,
    }))
  return [{ label: '顶级（无上级）', value: 0 }, ...nodes]
})

const parentOrgLabel = computed(() => {
  const id = form.value.parentOrgId ?? 0
  if (id === 0)
    return '顶级（无上级）'
  return parentColumns.value.find(item => item.value === id)?.label ?? ''
})

const canSubmit = computed(() =>
  isEdit.value ? hasPermi('scm:wms:org:edit') : hasPermi('scm:wms:org:add'),
)

const canDelete = computed(() => isEdit.value && hasPermi('scm:wms:org:remove'))

const orgTypePickerVisible = ref(false)
const parentPickerVisible = ref(false)
const statusPickerVisible = ref(false)
const deptPickerVisible = ref(false)
const orgTypePickerValue = ref<number[]>([20])
const parentPickerValue = ref<number[]>([0])
const statusPickerValue = ref<number[]>([10])
const deptPickerValue = ref<number[]>([-1])

const deptColumns = computed(() => [
  { label: '不关联', value: -1 },
  ...deptOptions.value,
])

const linkedDeptLabel = computed(() => {
  const id = form.value.linkedDeptId
  if (id === undefined || id === null)
    return '不关联'
  return deptOptions.value.find(item => item.value === id)?.label ?? `部门 #${id}`
})

function syncPickerValues() {
  orgTypePickerValue.value = [form.value.orgType]
  parentPickerValue.value = [form.value.parentOrgId ?? 0]
  statusPickerValue.value = [form.value.status ?? 10]
}

function openOrgTypePicker() {
  if (!canSubmit.value)
    return
  syncPickerValues()
  orgTypePickerVisible.value = true
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

function openDeptPicker() {
  if (!canSubmit.value)
    return
  deptPickerValue.value = [form.value.linkedDeptId ?? -1]
  deptPickerVisible.value = true
}

function flattenDept(nodes: IDeptVo[], prefix = ''): { label: string, value: number }[] {
  const rows: { label: string, value: number }[] = []
  for (const node of nodes) {
    const label = prefix ? `${prefix} / ${node.deptName}` : node.deptName
    rows.push({ label, value: Number(node.deptId) })
    if (node.children?.length)
      rows.push(...flattenDept(node.children, label))
  }
  return rows
}

function onDeptConfirm({ value }: { value: number[] }) {
  const picked = value[0]
  form.value.linkedDeptId = picked === -1 ? undefined : picked
}

function onOrgTypeConfirm({ value }: { value: number[] }) {
  form.value.orgType = value[0]
}

function onParentConfirm({ value }: { value: number[] }) {
  form.value.parentOrgId = value[0]
}

function onStatusConfirm({ value }: { value: number[] }) {
  form.value.status = value[0]
}

async function loadOrgOptions() {
  try {
    orgOptions.value = await listOrgOptions(undefined, 10) || []
  }
  catch {
    orgOptions.value = []
  }
}

async function loadDeptOptions() {
  try {
    const tree = await listDept({ status: '0' }) || []
    deptOptions.value = flattenDept(tree)
  }
  catch {
    deptOptions.value = []
  }
}

async function loadDetail() {
  if (!isEdit.value)
    return
  loading.value = true
  try {
    const org = await getOrg(orgId.value!)
    if (!org?.id) {
      uni.showToast({ title: '组织不存在', icon: 'none' })
      return
    }
    form.value = {
      orgCode: org.orgCode,
      orgName: org.orgName,
      orgType: org.orgType,
      parentOrgId: org.parentOrgId ?? 0,
      linkedDeptId: org.linkedDeptId,
      status: org.status ?? 10,
      sortOrder: org.sortOrder ?? 0,
      remark: org.remark ?? '',
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
  if (!form.value.orgName?.trim()) {
    uni.showToast({ title: '请输入组织名称', icon: 'none' })
    return false
  }
  if (!form.value.orgCode?.trim()) {
    uni.showToast({ title: '请输入组织编码', icon: 'none' })
    return false
  }
  if (form.value.orgType === undefined || form.value.orgType === null) {
    uni.showToast({ title: '请选择组织类型', icon: 'none' })
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
    const payload: IOrgForm = {
      ...form.value,
      parentOrgId: form.value.parentOrgId ?? 0,
      sortOrder: Number(form.value.sortOrder) || 0,
    }
    if (isEdit.value)
      await updateOrg(orgId.value!, payload)
    else
      await addOrg(payload)

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
  if (!canDelete.value || !orgId.value)
    return

  uni.showModal({
    title: '确认删除',
    content: `确定删除组织「${form.value.orgName}」吗？`,
    success: async (res) => {
      if (!res.confirm)
        return
      submitting.value = true
      try {
        await delOrg(orgId.value!)
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

onLoad(async (options) => {
  if (options?.id)
    orgId.value = options.id

  uni.setNavigationBarTitle({
    title: isEdit.value ? '编辑组织' : '新增组织',
  })
  syncPickerValues()
  await Promise.all([loadOrgOptions(), loadDeptOptions()])
  await loadDetail()
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
          <wd-form-item title="组织名称">
            <wd-input
              v-model="form.orgName"
              compact
              clearable
              placeholder="请输入组织名称"
              :disabled="!canSubmit"
            />
          </wd-form-item>
          <wd-form-item title="组织编码">
            <wd-input
              v-model="form.orgCode"
              compact
              clearable
              placeholder="如 ORG-B"
              :disabled="!canSubmit || isEdit"
            />
          </wd-form-item>
          <wd-form-item
            title="组织类型"
            :value="formatOrgTypeLabel(form.orgType)"
            placeholder="请选择"
            is-link
            :clickable="canSubmit"
            @click="openOrgTypePicker"
          />
          <wd-form-item
            title="上级组织"
            :value="parentOrgLabel"
            placeholder="请选择"
            is-link
            :clickable="canSubmit"
            @click="openParentPicker"
          />
          <wd-form-item
            title="关联部门"
            :value="linkedDeptLabel"
            placeholder="选填"
            is-link
            :clickable="canSubmit"
            @click="openDeptPicker"
          />
          <wd-form-item
            title="状态"
            :value="formatOrgStatusLabel(form.status)"
            placeholder="请选择"
            is-link
            :clickable="canSubmit"
            @click="openStatusPicker"
          />
          <wd-form-item title="排序">
            <wd-input
              v-model="form.sortOrder"
              compact
              type="number"
              placeholder="数字越小越靠前"
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
    </view>

    <view v-if="!loading" class="bp-crud-footer">
      <PermButton
        type="primary"
        block
        :loading="submitting"
        :perm="isEdit ? 'scm:wms:org:edit' : 'scm:wms:org:add'"
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
        perm="scm:wms:org:remove"
        @click="handleDelete"
      >
        删除组织
      </PermButton>
    </view>

    <wd-picker
      v-model="orgTypePickerValue"
      v-model:visible="orgTypePickerVisible"
      :columns="orgTypeColumns"
      title="组织类型"
      @confirm="onOrgTypeConfirm"
    />
    <wd-picker
      v-model="parentPickerValue"
      v-model:visible="parentPickerVisible"
      :columns="parentColumns"
      title="上级组织"
      @confirm="onParentConfirm"
    />
    <wd-picker
      v-model="statusPickerValue"
      v-model:visible="statusPickerVisible"
      :columns="statusColumns"
      title="状态"
      @confirm="onStatusConfirm"
    />
    <wd-picker
      v-model="deptPickerValue"
      v-model:visible="deptPickerVisible"
      :columns="deptColumns"
      title="关联部门"
      @confirm="onDeptConfirm"
    />
  </AppPage>
</template>

<style lang="scss" scoped>
:deep(.wd-form-item__body .wd-input) {
  width: 100%;
}
</style>
