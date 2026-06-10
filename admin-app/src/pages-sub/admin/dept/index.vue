<script lang="ts" setup>
import type { IDeptVo } from '@/api/types/system/dept'
import { listDept } from '@/api/system/dept'
import { useAuth } from '@/hooks/useAuth'
import { useDict } from '@/hooks/useDict'
import { useListRefresh } from '@/hooks/useListRefresh'

definePage({
  style: {
    navigationBarTitleText: '部门管理',
  },
})

type IDeptFlat = IDeptVo & { depth: number }

const { hasPermi } = useAuth()
const { getDictLabel } = useDict(['sys_normal_disable'])

const pagingRef = ref<{
  complete: (data: IDeptFlat[] | false, total?: number) => void
  reload?: (showLoading?: boolean) => void
} | null>(null)
const dataList = ref<IDeptFlat[]>([])
const keyword = ref('')
const allFlatList = ref<IDeptFlat[]>([])

useListRefresh(pagingRef)

function flattenDeptTree(list: IDeptVo[], depth = 0): IDeptFlat[] {
  const result: IDeptFlat[] = []
  for (const item of list) {
    const { children, ...rest } = item
    result.push({ ...rest, children, depth })
    if (children?.length)
      result.push(...flattenDeptTree(children, depth + 1))
  }
  return result
}

function filterList(list: IDeptFlat[]) {
  const key = keyword.value.trim()
  if (!key)
    return list
  return list.filter(item => item.deptName?.includes(key))
}

async function queryList(pageNo: number, pageSize: number) {
  if (!hasPermi('system:dept:list')) {
    pagingRef.value?.complete([], 0)
    return
  }
  try {
    const tree = await listDept({
      deptName: keyword.value || undefined,
    })
    allFlatList.value = flattenDeptTree(tree || [])
    const filtered = filterList(allFlatList.value)
    const start = (pageNo - 1) * pageSize
    const page = filtered.slice(start, start + pageSize)
    pagingRef.value?.complete(page, filtered.length)
  }
  catch {
    pagingRef.value?.complete(false)
  }
}

function handleSearch() {
  pagingRef.value?.reload?.(true)
}

function statusLabel(value?: string) {
  return getDictLabel('sys_normal_disable', value) || value || '-'
}

function avatarText(item: IDeptVo) {
  return (item.deptName || '?').slice(0, 1)
}

function handleAdd() {
  uni.navigateTo({ url: '/pages-sub/admin/dept/form' })
}

function handleEdit(item: IDeptVo) {
  if (!hasPermi('system:dept:edit')) {
    uni.showToast({ title: '需要 system:dept:edit 权限', icon: 'none' })
    return
  }
  uni.navigateTo({ url: `/pages-sub/admin/dept/form?id=${item.deptId}` })
}
</script>

<template>
  <AppPage :safe-top="false">
    <view class="dept-list">
      <view class="dept-list__search bp-card">
        <wd-search
          v-model="keyword"
          placeholder="搜索部门名称"
          hide-cancel
          @search="handleSearch"
          @clear="handleSearch"
        />
        <view class="dept-list__toolbar">
          <PermButton
            type="primary"
            size="small"
            perm="system:dept:add"
            @click="handleAdd"
          >
            新增部门
          </PermButton>
        </view>
      </view>

      <z-paging
        ref="pagingRef"
        v-model="dataList"
        class="dept-list__paging"
        :fixed="false"
        @query="queryList"
      >
        <view
          v-for="item in dataList"
          :key="item.deptId"
          class="dept-list__card bp-card"
          :style="{ marginLeft: `calc(var(--bp-spacing-page) + ${item.depth * 24}rpx)` }"
          @click="handleEdit(item)"
        >
          <view class="dept-list__main">
            <view class="dept-list__avatar">
              <text class="dept-list__avatar-text">{{ avatarText(item) }}</text>
            </view>
            <view class="dept-list__info">
              <view class="dept-list__head">
                <text class="dept-list__name">{{ item.deptName }}</text>
                <wd-tag :type="item.status === '0' ? 'success' : 'danger'" plain round>
                  {{ statusLabel(item.status) }}
                </wd-tag>
              </view>
              <text v-if="item.leader" class="dept-list__row">负责人 {{ item.leader }}</text>
              <text v-if="item.phone" class="dept-list__row">电话 {{ item.phone }}</text>
              <text v-if="item.orderNum !== undefined" class="dept-list__row">排序 {{ item.orderNum }}</text>
            </view>
          </view>
        </view>

        <template v-if="!hasPermi('system:dept:list')" #empty>
          <wd-empty tip="需要 system:dept:list 权限">
            <template #image>
              <view class="i-carbon-locked dept-list__empty-icon" />
            </template>
          </wd-empty>
        </template>
      </z-paging>
    </view>
  </AppPage>
</template>

<style lang="scss" scoped>
.dept-list {
  display: flex;
  flex-direction: column;
  height: 100vh;
  box-sizing: border-box;

  &__search {
    margin: var(--bp-space-md) var(--bp-spacing-page);
    padding: var(--bp-space-xs) 0;
    overflow: hidden;
  }

  &__toolbar {
    display: flex;
    justify-content: flex-end;
    padding: var(--bp-space-xs) var(--bp-space-md) var(--bp-space-sm);
  }

  &__paging {
    flex: 1;
    height: 0;
  }

  &__card {
    margin: 0 var(--bp-spacing-page) var(--bp-space-sm);
    margin-right: var(--bp-spacing-page);
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
