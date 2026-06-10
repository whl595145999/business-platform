<script lang="ts" setup>
import type { IOrgVo } from '@/api/types/scm/org'
import type { IDeptVo } from '@/api/types/system/dept'
import { delOrg, getOrg } from '@/api/scm/org'
import { listDept } from '@/api/system/dept'
import { formatOrgStatusLabel, formatOrgTypeLabel } from '@/constants/scm-org'
import { useAuth } from '@/hooks/useAuth'

definePage({
  style: {
    navigationBarTitleText: '组织详情',
  },
})

const { hasPermi } = useAuth()

const orgId = ref<string | number>()
const loading = ref(true)
const detail = ref<IOrgVo | null>(null)
const linkedDeptName = ref('')

function findDeptName(nodes: IDeptVo[], deptId: number): string {
  for (const node of nodes) {
    if (Number(node.deptId) === deptId)
      return node.deptName
    if (node.children?.length) {
      const found = findDeptName(node.children, deptId)
      if (found)
        return found
    }
  }
  return ''
}

const canQuery = computed(() => hasPermi('scm:wms:org:query') || hasPermi('scm:wms:org:list'))
const canEdit = computed(() => hasPermi('scm:wms:org:edit'))
const canDelete = computed(() => hasPermi('scm:wms:org:remove'))

async function loadDetail() {
  if (!orgId.value)
    return
  loading.value = true
  try {
    detail.value = await getOrg(orgId.value)
    if (!detail.value?.id) {
      uni.showToast({ title: '组织不存在', icon: 'none' })
      return
    }
    linkedDeptName.value = ''
    if (detail.value.linkedDeptId) {
      try {
        const tree = await listDept({ status: '0' }) || []
        linkedDeptName.value = findDeptName(tree, detail.value.linkedDeptId)
      }
      catch {
        linkedDeptName.value = ''
      }
    }
  }
  catch {
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
  finally {
    loading.value = false
  }
}

function goEdit() {
  uni.navigateTo({ url: `/pages-sub/scm/org/form?id=${orgId.value}` })
}

function handleDelete() {
  if (!detail.value?.id)
    return
  uni.showModal({
    title: '确认删除',
    content: `确定删除组织「${detail.value.orgName}」吗？`,
    success: async (res) => {
      if (!res.confirm)
        return
      try {
        await delOrg(detail.value!.id)
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
  if (!canQuery.value) {
    uni.showToast({ title: '无查看权限', icon: 'none' })
    setTimeout(() => uni.navigateBack(), 500)
    return
  }
  if (options?.id)
    orgId.value = options.id
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
          <wd-cell title="组织名称" :value="detail.orgName" />
          <wd-cell title="组织编码" :value="detail.orgCode" />
          <wd-cell title="组织类型" :value="formatOrgTypeLabel(detail.orgType)" />
          <wd-cell title="状态" :value="formatOrgStatusLabel(detail.status)" />
        </wd-cell-group>
      </view>

      <view class="bp-crud-section">
        <text class="bp-section-title">层级与排序</text>
        <wd-cell-group border custom-class="bp-crud-group">
          <wd-cell title="上级组织" :value="detail.parentOrgName || '顶级（无上级）'" />
          <wd-cell
            v-if="detail.linkedDeptId"
            title="关联部门"
            :value="linkedDeptName || `部门 #${detail.linkedDeptId}`"
          />
          <wd-cell title="排序" :value="String(detail.sortOrder ?? 0)" />
        </wd-cell-group>
      </view>

      <view v-if="detail.remark || detail.updateTime" class="bp-crud-section">
        <text class="bp-section-title">其他</text>
        <wd-cell-group border custom-class="bp-crud-group">
          <wd-cell v-if="detail.remark" title="备注" :value="detail.remark" />
          <wd-cell v-if="detail.updateTime" title="更新时间" :value="detail.updateTime" />
        </wd-cell-group>
      </view>
    </view>

    <view v-if="detail && (canEdit || canDelete)" class="bp-crud-footer">
      <PermButton v-if="canEdit" type="primary" block perm="scm:wms:org:edit" @click="goEdit">
        编辑
      </PermButton>
      <PermButton v-if="canDelete" type="error" plain block perm="scm:wms:org:remove" @click="handleDelete">
        删除
      </PermButton>
    </view>
  </AppPage>
</template>
