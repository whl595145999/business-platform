<script lang="ts" setup>
import type { IRoleVo } from '@/api/types/system/role'
import { delRole, getRole } from '@/api/system/role'
import { useAuth } from '@/hooks/useAuth'
import { useDict } from '@/hooks/useDict'

definePage({
  style: {
    navigationBarTitleText: '角色详情',
  },
})

const { hasPermi } = useAuth()
const { getDictLabel } = useDict(['sys_normal_disable'])

const roleId = ref<string | number>()
const loading = ref(true)
const detail = ref<IRoleVo | null>(null)

const canEdit = computed(() => hasPermi('system:role:edit'))
const canDelete = computed(() => hasPermi('system:role:remove'))

function statusLabel(value?: string) {
  return getDictLabel('sys_normal_disable', value) || value || '-'
}

async function loadDetail() {
  loading.value = true
  try {
    const res = await getRole(roleId.value)
    detail.value = res.role || res.data || null
    if (!detail.value?.roleId)
      uni.showToast({ title: '角色不存在', icon: 'none' })
  }
  catch {
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
  finally {
    loading.value = false
  }
}

function goEdit() {
  uni.navigateTo({ url: `/pages-sub/admin/role/form?id=${roleId.value}` })
}

function handleDelete() {
  if (!detail.value?.roleId)
    return
  uni.showModal({
    title: '确认删除',
    content: `确定删除角色「${detail.value.roleName}」吗？`,
    success: async (res) => {
      if (!res.confirm)
        return
      try {
        await delRole(detail.value!.roleId)
        uni.showToast({ title: '已删除', icon: 'success' })
        setTimeout(() => uni.navigateBack(), 400)
      }
      catch {
        // http 已 toast
      }
    },
  })
}

onLoad((options) => {
  if (!hasPermi('system:role:query') && !hasPermi('system:role:list')) {
    uni.showToast({ title: '无查看权限', icon: 'none' })
    setTimeout(() => uni.navigateBack(), 500)
    return
  }
  if (options?.id)
    roleId.value = options.id
  loadDetail()
})
</script>

<template>
  <AppPage :safe-top="false">
    <view v-if="loading" class="bp-crud-page">
      <text class="bp-text-secondary">加载中...</text>
    </view>

    <view v-else-if="detail" class="bp-crud-page">
      <view class="bp-crud-section">
        <text class="bp-section-title">基本信息</text>
        <wd-cell-group border custom-class="bp-crud-group">
          <wd-cell title="角色名称" :value="detail.roleName" />
          <wd-cell title="权限字符" :value="detail.roleKey" />
          <wd-cell title="显示顺序" :value="String(detail.roleSort ?? '-')" />
          <wd-cell title="状态" :value="statusLabel(detail.status)" />
          <wd-cell v-if="detail.remark" title="备注" :value="detail.remark" />
          <wd-cell v-if="detail.createTime" title="创建时间" :value="detail.createTime" />
        </wd-cell-group>
      </view>

      <text class="bp-crud-hint">菜单权限、数据权限请在 PC 端配置。</text>
    </view>

    <view v-if="detail && (canEdit || canDelete)" class="bp-crud-footer">
      <wd-button v-if="canEdit" type="primary" block @click="goEdit">
        编辑
      </wd-button>
      <wd-button v-if="canDelete" type="error" plain block @click="handleDelete">
        删除
      </wd-button>
    </view>
  </AppPage>
</template>
