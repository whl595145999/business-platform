<script lang="ts" setup>
import type { IOrgVo } from '@/api/types/scm/org'
import { pageOrg } from '@/api/scm/org'
import { formatOrgStatusLabel, formatOrgTypeLabel, ORG_STATUS_OPTIONS, ORG_TYPE_OPTIONS } from '@/constants/scm-org'
import { useAuth } from '@/hooks/useAuth'
import { useListRefresh } from '@/hooks/useListRefresh'

definePage({
  style: {
    navigationBarTitleText: '组织档案',
  },
})

const { hasPermi } = useAuth()

const pagingRef = ref<{
  complete: (data: IOrgVo[] | false, total?: number) => void
  reload?: (showLoading?: boolean) => void
} | null>(null)
const dataList = ref<IOrgVo[]>([])
const keyword = ref('')
const filterOrgType = ref<number | undefined>()
const filterStatus = ref<number | undefined>()
const orgTypeFilterVisible = ref(false)
const statusFilterVisible = ref(false)
const orgTypeFilterValue = ref<number[]>([-1])
const statusFilterValue = ref<number[]>([-1])

const orgTypeFilterColumns = [
  { label: '全部类型', value: -1 },
  ...ORG_TYPE_OPTIONS.map(item => ({ label: item.label, value: item.value })),
]
const statusFilterColumns = [
  { label: '全部状态', value: -1 },
  ...ORG_STATUS_OPTIONS.map(item => ({ label: item.label, value: item.value })),
]

const filterOrgTypeLabel = computed(() => {
  if (filterOrgType.value === undefined)
    return '全部类型'
  return formatOrgTypeLabel(filterOrgType.value)
})

const filterStatusLabel = computed(() => {
  if (filterStatus.value === undefined)
    return '全部状态'
  return formatOrgStatusLabel(filterStatus.value)
})

useListRefresh(pagingRef)

function openOrgTypeFilter() {
  orgTypeFilterValue.value = [filterOrgType.value ?? -1]
  orgTypeFilterVisible.value = true
}

function openStatusFilter() {
  statusFilterValue.value = [filterStatus.value ?? -1]
  statusFilterVisible.value = true
}

function onOrgTypeFilterConfirm({ value }: { value: number[] }) {
  const picked = value[0]
  filterOrgType.value = picked === -1 ? undefined : picked
  pagingRef.value?.reload?.(true)
}

function onStatusFilterConfirm({ value }: { value: number[] }) {
  const picked = value[0]
  filterStatus.value = picked === -1 ? undefined : picked
  pagingRef.value?.reload?.(true)
}

async function queryList(pageNo: number, pageSize: number) {
  if (!hasPermi('scm:wms:org:list')) {
    pagingRef.value?.complete([], 0)
    return
  }
  try {
    const { rows, total } = await pageOrg({
      pageNum: pageNo,
      pageSize,
      orgName: keyword.value || undefined,
      orgType: filterOrgType.value,
      status: filterStatus.value,
    })
    pagingRef.value?.complete(rows || [], total ?? 0)
  }
  catch {
    pagingRef.value?.complete(false)
  }
}

function handleSearch() {
  pagingRef.value?.reload?.(true)
}

function avatarText(item: IOrgVo) {
  return (item.orgName || '?').slice(0, 1)
}

const canQuery = computed(() => hasPermi('scm:wms:org:query') || hasPermi('scm:wms:org:list'))

function handleAdd() {
  uni.navigateTo({ url: '/pages-sub/scm/org/form' })
}

function handleDetail(item: IOrgVo) {
  if (!canQuery.value) {
    uni.showToast({ title: '无查看权限', icon: 'none' })
    return
  }
  uni.navigateTo({ url: `/pages-sub/scm/org/detail?id=${item.id}` })
}

function handleEdit(item: IOrgVo) {
  if (!hasPermi('scm:wms:org:edit')) {
    uni.showToast({ title: '需要 scm:wms:org:edit 权限', icon: 'none' })
    return
  }
  uni.navigateTo({ url: `/pages-sub/scm/org/form?id=${item.id}` })
}
</script>

<template>
  <AppPage :safe-top="false">
    <view class="org-list">
      <view class="org-list__search bp-card">
        <wd-search
          v-model="keyword"
          placeholder="搜索组织名称"
          hide-cancel
          @search="handleSearch"
          @clear="handleSearch"
        />
        <view class="org-list__filters">
          <view class="org-list__filter" @click="openOrgTypeFilter">
            <text class="org-list__filter-label">{{ filterOrgTypeLabel }}</text>
            <view class="i-carbon-chevron-down org-list__filter-icon" />
          </view>
          <view class="org-list__filter" @click="openStatusFilter">
            <text class="org-list__filter-label">{{ filterStatusLabel }}</text>
            <view class="i-carbon-chevron-down org-list__filter-icon" />
          </view>
        </view>
        <view class="bp-list-toolbar">
          <PermButton
            type="primary"
            size="small"
            perm="scm:wms:org:add"
            @click="handleAdd"
          >
            新增
          </PermButton>
        </view>
      </view>

      <wd-picker
        v-model="orgTypeFilterValue"
        v-model:visible="orgTypeFilterVisible"
        :columns="orgTypeFilterColumns"
        title="组织类型"
        @confirm="onOrgTypeFilterConfirm"
      />
      <wd-picker
        v-model="statusFilterValue"
        v-model:visible="statusFilterVisible"
        :columns="statusFilterColumns"
        title="状态"
        @confirm="onStatusFilterConfirm"
      />

      <z-paging
        ref="pagingRef"
        v-model="dataList"
        class="org-list__paging"
        :fixed="false"
        @query="queryList"
      >
        <view
          v-for="item in dataList"
          :key="item.id"
          class="org-list__card bp-card"
        >
          <view class="org-list__main" @click="handleDetail(item)">
            <view class="org-list__avatar">
              <text class="org-list__avatar-text">{{ avatarText(item) }}</text>
            </view>
            <view class="org-list__info">
              <view class="org-list__head">
                <text class="org-list__name">{{ item.orgName }}</text>
                <wd-tag :type="item.status === 10 ? 'success' : 'danger'" plain round>
                  {{ formatOrgStatusLabel(item.status) }}
                </wd-tag>
              </view>
              <text class="org-list__row">编码 {{ item.orgCode }}</text>
              <text class="org-list__row">类型 {{ formatOrgTypeLabel(item.orgType) }}</text>
              <text class="org-list__row">上级 {{ item.parentOrgName || '—' }}</text>
            </view>
          </view>
          <view class="bp-row-actions">
            <text
              v-if="canQuery"
              class="bp-row-actions__btn"
              @click.stop="handleDetail(item)"
            >
              详情
            </text>
            <text
              v-if="hasPermi('scm:wms:org:edit')"
              class="bp-row-actions__btn"
              @click.stop="handleEdit(item)"
            >
              编辑
            </text>
          </view>
        </view>

        <template v-if="!hasPermi('scm:wms:org:list')" #empty>
          <wd-empty tip="需要 scm:wms:org:list 权限">
            <template #image>
              <view class="i-carbon-locked org-list__empty-icon" />
            </template>
          </wd-empty>
        </template>
      </z-paging>
    </view>
  </AppPage>
</template>

<style lang="scss" scoped>
.org-list {
  display: flex;
  flex-direction: column;
  height: 100vh;
  box-sizing: border-box;

  &__search {
    margin: var(--bp-space-md) var(--bp-spacing-page);
    padding: var(--bp-space-xs) 0;
    overflow: hidden;
  }

  &__filters {
    display: flex;
    gap: var(--bp-space-sm);
    padding: 0 var(--bp-space-md) var(--bp-space-sm);
  }

  &__filter {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4rpx;
    padding: 12rpx 16rpx;
    border-radius: var(--bp-radius-sm);
    background: var(--bp-bg-page);
    font-size: var(--bp-font-caption);
    color: var(--bp-text-secondary);
  }

  &__filter-label {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__filter-icon {
    font-size: 24rpx;
    flex-shrink: 0;
  }

  &__paging {
    flex: 1;
    height: 0;
  }

  &__card {
    margin: 0 var(--bp-spacing-page) var(--bp-space-sm);
    padding: var(--bp-space-md);
  }

  &__main {
    display: flex;
    align-items: flex-start;
    gap: var(--bp-space-md);
  }

  &__avatar {
    width: 88rpx;
    height: 88rpx;
    border-radius: var(--bp-radius-round);
    background: var(--bp-color-primary-light);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  &__avatar-text {
    font-size: var(--bp-font-subhead);
    font-weight: var(--bp-font-weight-semibold);
    color: var(--bp-color-primary);
  }

  &__info {
    flex: 1;
    min-width: 0;
  }

  &__head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: var(--bp-space-sm);
    margin-bottom: var(--bp-space-xs);
  }

  &__name {
    font-size: var(--bp-font-subhead);
    font-weight: var(--bp-font-weight-medium);
    color: var(--bp-text-primary);
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__row {
    display: block;
    font-size: var(--bp-font-caption);
    color: var(--bp-text-secondary);
    line-height: 1.65;
  }

  &__empty-icon {
    font-size: 120rpx;
    color: var(--bp-text-disabled);
  }
}
</style>
