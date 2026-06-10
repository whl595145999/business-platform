<script lang="ts" setup>
import type { IUserVo } from '@/api/types/system/user'
import { listUser } from '@/api/system/user'
import { useAuth } from '@/hooks/useAuth'
import { useDict } from '@/hooks/useDict'
import { useListRefresh } from '@/hooks/useListRefresh'

definePage({
  style: {
    navigationBarTitleText: '用户管理',
  },
})

const { hasPermi } = useAuth()
const { getDictLabel } = useDict(['sys_normal_disable'])

const pagingRef = ref<{
  complete: (data: IUserVo[] | false, total?: number) => void
  reload?: (showLoading?: boolean) => void
} | null>(null)
const dataList = ref<IUserVo[]>([])
const keyword = ref('')

useListRefresh(pagingRef)

const canQuery = computed(() => hasPermi('system:user:query') || hasPermi('system:user:list'))

async function queryList(pageNo: number, pageSize: number) {
  if (!hasPermi('system:user:list')) {
    pagingRef.value?.complete([], 0)
    return
  }
  try {
    const { rows, total } = await listUser({
      pageNum: pageNo,
      pageSize,
      userName: keyword.value || undefined,
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

function avatarText(item: IUserVo) {
  const name = item.nickName || item.userName || '?'
  return name.slice(0, 1).toUpperCase()
}

function handleAdd() {
  uni.navigateTo({ url: '/pages-sub/admin/user/form' })
}

function handleDetail(item: IUserVo) {
  if (!canQuery.value) {
    uni.showToast({ title: '无查看权限', icon: 'none' })
    return
  }
  uni.navigateTo({ url: `/pages-sub/admin/user/detail?id=${item.userId}` })
}

function handleEdit(item: IUserVo) {
  if (!hasPermi('system:user:edit')) {
    uni.showToast({ title: '需要 system:user:edit 权限', icon: 'none' })
    return
  }
  uni.navigateTo({ url: `/pages-sub/admin/user/form?id=${item.userId}` })
}
</script>

<template>
  <AppPage :safe-top="false">
    <view class="user-list">
      <view class="user-list__search bp-card">
        <wd-search
          v-model="keyword"
          placeholder="搜索用户名"
          hide-cancel
          @search="handleSearch"
          @clear="handleSearch"
        />
        <view class="bp-list-toolbar">
          <PermButton
            type="primary"
            size="small"
            perm="system:user:add"
            @click="handleAdd"
          >
            新增
          </PermButton>
        </view>
      </view>

      <z-paging
        ref="pagingRef"
        v-model="dataList"
        class="user-list__paging"
        :fixed="false"
        @query="queryList"
      >
        <view
          v-for="item in dataList"
          :key="item.userId"
          class="user-list__card bp-card"
        >
          <view class="user-list__main" @click="handleDetail(item)">
            <view class="user-list__avatar">
              <image
                v-if="item.avatar"
                :src="item.avatar"
                class="user-list__avatar-img"
                mode="aspectFill"
              />
              <text v-else class="user-list__avatar-text">{{ avatarText(item) }}</text>
            </view>
            <view class="user-list__info">
              <view class="user-list__head">
                <text class="user-list__name">{{ item.nickName || item.userName }}</text>
                <wd-tag :type="item.status === '0' ? 'success' : 'danger'" plain round>
                  {{ statusLabel(item.status) }}
                </wd-tag>
              </view>
              <text class="user-list__row">账号 {{ item.userName }}</text>
              <text v-if="item.deptName" class="user-list__row">部门 {{ item.deptName }}</text>
              <text v-if="item.phonenumber" class="user-list__row">手机 {{ item.phonenumber }}</text>
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
              v-if="hasPermi('system:user:edit')"
              class="bp-row-actions__btn"
              @click.stop="handleEdit(item)"
            >
              编辑
            </text>
          </view>
        </view>

        <template v-if="!hasPermi('system:user:list')" #empty>
          <wd-empty tip="需要 system:user:list 权限">
            <template #image>
              <view class="i-carbon-locked user-list__empty-icon" />
            </template>
          </wd-empty>
        </template>
      </z-paging>
    </view>
  </AppPage>
</template>

<style lang="scss" scoped>
.user-list {
  display: flex;
  flex-direction: column;
  height: 100vh;
  box-sizing: border-box;

  &__search {
    margin: var(--bp-space-md) var(--bp-spacing-page);
    padding: var(--bp-space-xs) 0;
    overflow: hidden;
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
    overflow: hidden;
  }

  &__avatar-img {
    width: 100%;
    height: 100%;
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
