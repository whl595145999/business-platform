<script lang="ts" setup>
import type { IPostVo } from '@/api/types/system/post'
import { listPost } from '@/api/system/post'
import { useAuth } from '@/hooks/useAuth'
import { useDict } from '@/hooks/useDict'
import { useListRefresh } from '@/hooks/useListRefresh'

definePage({
  style: {
    navigationBarTitleText: '岗位管理',
  },
})

const { hasPermi } = useAuth()
const { getDictLabel } = useDict(['sys_normal_disable'])

const pagingRef = ref<{
  complete: (data: IPostVo[] | false, total?: number) => void
  reload?: (showLoading?: boolean) => void
} | null>(null)
const dataList = ref<IPostVo[]>([])
const keyword = ref('')

useListRefresh(pagingRef)

async function queryList(pageNo: number, pageSize: number) {
  if (!hasPermi('system:post:list')) {
    pagingRef.value?.complete([], 0)
    return
  }
  try {
    const { rows, total } = await listPost({
      pageNum: pageNo,
      pageSize,
      postName: keyword.value || undefined,
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

function statusLabel(value?: string) {
  return getDictLabel('sys_normal_disable', value) || value || '-'
}

function avatarText(item: IPostVo) {
  return (item.postName || '?').slice(0, 1)
}

function handleAdd() {
  uni.navigateTo({ url: '/pages-sub/admin/post/form' })
}

function handleEdit(item: IPostVo) {
  if (!hasPermi('system:post:edit')) {
    uni.showToast({ title: '需要 system:post:edit 权限', icon: 'none' })
    return
  }
  uni.navigateTo({ url: `/pages-sub/admin/post/form?id=${item.postId}` })
}
</script>

<template>
  <AppPage :safe-top="false">
    <view class="post-list">
      <view class="post-list__search bp-card">
        <wd-search
          v-model="keyword"
          placeholder="搜索岗位名称"
          hide-cancel
          @search="handleSearch"
          @clear="handleSearch"
        />
        <view class="post-list__toolbar">
          <PermButton
            type="primary"
            size="small"
            perm="system:post:add"
            @click="handleAdd"
          >
            新增岗位
          </PermButton>
        </view>
      </view>

      <z-paging
        ref="pagingRef"
        v-model="dataList"
        class="post-list__paging"
        :fixed="false"
        @query="queryList"
      >
        <view
          v-for="item in dataList"
          :key="item.postId"
          class="post-list__card bp-card"
          @click="handleEdit(item)"
        >
          <view class="post-list__main">
            <view class="post-list__avatar">
              <text class="post-list__avatar-text">{{ avatarText(item) }}</text>
            </view>
            <view class="post-list__info">
              <view class="post-list__head">
                <text class="post-list__name">{{ item.postName }}</text>
                <wd-tag :type="item.status === '0' ? 'success' : 'danger'" plain round>
                  {{ statusLabel(item.status) }}
                </wd-tag>
              </view>
              <text class="post-list__row">岗位编码 {{ item.postCode }}</text>
              <text v-if="item.postSort !== undefined" class="post-list__row">排序 {{ item.postSort }}</text>
              <text v-if="item.createTime" class="post-list__row">创建时间 {{ item.createTime }}</text>
            </view>
          </view>
        </view>

        <template v-if="!hasPermi('system:post:list')" #empty>
          <wd-empty tip="需要 system:post:list 权限">
            <template #image>
              <view class="i-carbon-locked post-list__empty-icon" />
            </template>
          </wd-empty>
        </template>
      </z-paging>
    </view>
  </AppPage>
</template>

<style lang="scss" scoped>
.post-list {
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
