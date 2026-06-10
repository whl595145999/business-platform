<script lang="ts" setup>
import type { IPostForm } from '@/api/types/system/post'
import { addPost, delPost, getPost, updatePost } from '@/api/system/post'
import { useAuth } from '@/hooks/useAuth'
import { useDict } from '@/hooks/useDict'

definePage({
  style: {
    navigationBarTitleText: '岗位表单',
  },
})

const { hasPermi } = useAuth()
const { sys_normal_disable } = useDict(['sys_normal_disable'])

const postId = ref<string | number>()
const loading = ref(false)
const submitting = ref(false)
const statusPickerVisible = ref(false)
const statusPickerValue = ref<string[]>(['0'])

const isEdit = computed(() => postId.value !== undefined && postId.value !== '')

const form = ref<IPostForm>({
  postCode: '',
  postName: '',
  postSort: 0,
  status: '0',
  remark: '',
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
  isEdit.value ? hasPermi('system:post:edit') : hasPermi('system:post:add'),
)

const canDelete = computed(() => isEdit.value && hasPermi('system:post:remove'))

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
    const post = await getPost(postId.value)
    if (!post?.postId) {
      uni.showToast({ title: '岗位不存在', icon: 'none' })
      return
    }
    form.value = {
      postId: post.postId,
      postCode: post.postCode,
      postName: post.postName,
      postSort: post.postSort ?? 0,
      status: post.status ?? '0',
      remark: post.remark ?? '',
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
  if (!form.value.postName?.trim()) {
    uni.showToast({ title: '请输入岗位名称', icon: 'none' })
    return false
  }
  if (!form.value.postCode?.trim()) {
    uni.showToast({ title: '请输入岗位编码', icon: 'none' })
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
    const payload: IPostForm = {
      ...form.value,
      postSort: Number(form.value.postSort) || 0,
    }
    if (isEdit.value)
      await updatePost(payload)
    else
      await addPost(payload)

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
  if (!canDelete.value || !form.value.postId)
    return

  uni.showModal({
    title: '确认删除',
    content: `确定删除岗位「${form.value.postName}」吗？`,
    success: async (res) => {
      if (!res.confirm)
        return
      submitting.value = true
      try {
        await delPost(form.value.postId!)
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
    postId.value = options.id

  uni.setNavigationBarTitle({
    title: isEdit.value ? '编辑岗位' : '新增岗位',
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
          <wd-form-item title="岗位名称">
            <wd-input
              v-model="form.postName"
              compact
              clearable
              placeholder="请输入岗位名称"
              :disabled="!canSubmit"
            />
          </wd-form-item>
          <wd-form-item title="岗位编码">
            <wd-input
              v-model="form.postCode"
              compact
              clearable
              placeholder="请输入岗位编码"
              :disabled="!canSubmit"
            />
          </wd-form-item>
          <wd-form-item title="显示排序">
            <wd-input
              v-model="form.postSort"
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
    </view>

    <view v-if="!loading" class="bp-crud-footer">
      <PermButton
        type="primary"
        block
        :loading="submitting"
        :perm="isEdit ? 'system:post:edit' : 'system:post:add'"
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
        perm="system:post:remove"
        @click="handleDelete"
      >
        删除岗位
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
