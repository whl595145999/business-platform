<script lang="ts" setup>
import type { IUserVo } from '@/api/types/system/user'
import { delUser, getUser } from '@/api/system/user'
import { useAuth } from '@/hooks/useAuth'
import { useDict } from '@/hooks/useDict'

definePage({
  style: {
    navigationBarTitleText: '用户详情',
  },
})

const { hasPermi } = useAuth()
const { getDictLabel } = useDict(['sys_normal_disable', 'sys_user_sex'])

const userId = ref<string | number>()
const loading = ref(true)
const detail = ref<IUserVo | null>(null)

const canEdit = computed(() => hasPermi('system:user:edit'))
const canDelete = computed(() => hasPermi('system:user:remove'))

function statusLabel(value?: string) {
  return getDictLabel('sys_normal_disable', value) || value || '-'
}

function sexLabel(value?: string) {
  return getDictLabel('sys_user_sex', value) || value || '-'
}

async function loadDetail() {
  loading.value = true
  try {
    const res = await getUser(userId.value)
    detail.value = res.user || res.data || null
    if (!detail.value?.userId)
      uni.showToast({ title: '用户不存在', icon: 'none' })
  }
  catch {
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
  finally {
    loading.value = false
  }
}

function goEdit() {
  uni.navigateTo({ url: `/pages-sub/admin/user/form?id=${userId.value}` })
}

function handleDelete() {
  if (!detail.value?.userId)
    return
  uni.showModal({
    title: '确认删除',
    content: `确定删除用户「${detail.value.nickName || detail.value.userName}」吗？`,
    success: async (res) => {
      if (!res.confirm)
        return
      try {
        await delUser(detail.value!.userId)
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
  if (!hasPermi('system:user:query') && !hasPermi('system:user:list')) {
    uni.showToast({ title: '无查看权限', icon: 'none' })
    setTimeout(() => uni.navigateBack(), 500)
    return
  }
  if (options?.id)
    userId.value = options.id
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
        <text class="bp-section-title">账号信息</text>
        <wd-cell-group border custom-class="bp-crud-group">
          <wd-cell title="用户昵称" :value="detail.nickName || '-'" />
          <wd-cell title="登录账号" :value="detail.userName" />
          <wd-cell title="状态" :value="statusLabel(detail.status)" />
          <wd-cell title="性别" :value="sexLabel(detail.sex)" />
        </wd-cell-group>
      </view>

      <view class="bp-crud-section">
        <text class="bp-section-title">联系与组织</text>
        <wd-cell-group border custom-class="bp-crud-group">
          <wd-cell title="手机号码" :value="detail.phonenumber || '-'" />
          <wd-cell title="邮箱" :value="detail.email || '-'" />
          <wd-cell title="部门" :value="detail.deptName || '-'" />
        </wd-cell-group>
      </view>

      <view v-if="detail.remark || detail.createTime" class="bp-crud-section">
        <text class="bp-section-title">其他</text>
        <wd-cell-group border custom-class="bp-crud-group">
          <wd-cell v-if="detail.remark" title="备注" :value="detail.remark" />
          <wd-cell v-if="detail.createTime" title="创建时间" :value="detail.createTime" />
        </wd-cell-group>
      </view>

      <text class="bp-crud-hint">角色、岗位分配请在 PC 端用户管理中配置。</text>
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
