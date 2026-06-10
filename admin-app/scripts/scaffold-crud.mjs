#!/usr/bin/env node
/**
 * CRUD 模块脚手架 — 生成 types / api / list 页面骨架
 *
 * Usage:
 *   pnpm scaffold:crud -- --module role --domain system --title 角色管理 \
 *     --perm system:role --id-field roleId --name-field roleName
 */
import fs from 'node:fs'
import path from 'node:path'
import process from 'node:process'

function parseArgs(argv) {
  const args = {}
  for (let i = 0; i < argv.length; i++) {
    const key = argv[i]
    if (key.startsWith('--')) {
      const name = key.slice(2)
      args[name] = argv[i + 1]
      i++
    }
  }
  return args
}

function toPascal(str) {
  return str.replace(/(^|-)([a-z])/g, (_, __, c) => c.toUpperCase())
}

function ensureDir(dir) {
  fs.mkdirSync(dir, { recursive: true })
}

function writeIfMissing(filePath, content) {
  if (fs.existsSync(filePath)) {
    console.log(`skip (exists): ${filePath}`)
    return false
  }
  ensureDir(path.dirname(filePath))
  fs.writeFileSync(filePath, content, 'utf8')
  console.log(`created: ${filePath}`)
  return true
}

const raw = parseArgs(process.argv.slice(2))
const moduleKebab = raw.module
const domain = raw.domain || 'system'
const title = raw.title || `${moduleKebab}管理`
const perm = raw.perm || `${domain}:${moduleKebab}`
const idField = raw['id-field'] || `${moduleKebab}Id`
const nameField = raw['name-field'] || `${moduleKebab}Name`
const pkg = raw.pkg || (domain === 'system' ? 'admin' : domain)

if (!moduleKebab) {
  console.error(`Usage: pnpm scaffold:crud -- --module role --domain system --title 角色管理 --perm system:role --id-field roleId --name-field roleName`)
  process.exit(1)
}

const Module = toPascal(moduleKebab)
const root = process.cwd()

const typesContent = `import type { IPageQuery } from '@/api/types/backend'

/** ${title} — 列表查询（请对照 admin-web 补全字段） */
export interface I${Module}Query extends IPageQuery {
  ${nameField}?: string
  status?: string
}

/** ${title} — 列表项 */
export interface I${Module}Vo {
  ${idField}: string | number
  ${nameField}: string
  status?: string
  createTime?: string
  [key: string]: any
}

/** ${title} — 表单 */
export interface I${Module}Form {
  ${idField}?: string | number
  ${nameField}: string
  status?: string
  remark?: string
}
`

const apiContent = `import type { IPageResult } from '@/api/types/backend'
import type { I${Module}Form, I${Module}Query, I${Module}Vo } from '@/api/types/${domain}/${moduleKebab}'
import { http } from '@/http/http'

/** 查询列表（admin-web: GET /${domain}/${moduleKebab}/list） */
export function list${Module}(query: I${Module}Query) {
  return http.get<IPageResult<I${Module}Vo>>('/${domain}/${moduleKebab}/list', query)
}

export function get${Module}(id?: string | number) {
  const safeId = id === undefined || id === null || id === '' ? '' : String(id)
  return http.get<I${Module}Vo>(\`/${domain}/${moduleKebab}/\${safeId}\`)
}

export function add${Module}(data: I${Module}Form) {
  return http.post<void>('/${domain}/${moduleKebab}', data)
}

export function update${Module}(data: I${Module}Form) {
  return http.put<void>('/${domain}/${moduleKebab}', data)
}

export function del${Module}(id: string | number | Array<string | number>) {
  const safeId = Array.isArray(id) ? id.join(',') : id
  return http.delete<void>(\`/${domain}/${moduleKebab}/\${safeId}\`)
}
`

const pageContent = `<script lang="ts" setup>
import type { I${Module}Vo } from '@/api/types/${domain}/${moduleKebab}'
import { list${Module} } from '@/api/${domain}/${moduleKebab}'
import { useAuth } from '@/hooks/useAuth'
import { useListRefresh } from '@/hooks/useListRefresh'

definePage({
  style: {
    navigationBarTitleText: '${title}',
  },
})

const { hasPermi } = useAuth()

const pagingRef = ref<{
  complete: (data: I${Module}Vo[] | false, total?: number) => void
  reload?: (showLoading?: boolean) => void
} | null>(null)
const dataList = ref<I${Module}Vo[]>([])
const keyword = ref('')

useListRefresh(pagingRef)

async function queryList(pageNo: number, pageSize: number) {
  if (!hasPermi('${perm}:list')) {
    pagingRef.value?.complete([], 0)
    return
  }
  try {
    const { rows, total } = await list${Module}({
      pageNum: pageNo,
      pageSize,
      ${nameField}: keyword.value || undefined,
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

function displayName(item: I${Module}Vo) {
  return item.${nameField} || '-'
}
</script>

<template>
  <AppPage :safe-top="false">
    <view class="crud-list">
      <view class="crud-list__toolbar bp-card">
        <wd-search
          v-model="keyword"
          placeholder="搜索"
          hide-cancel
          @search="handleSearch"
          @clear="handleSearch"
        />
      </view>

      <z-paging
        ref="pagingRef"
        v-model="dataList"
        class="crud-list__paging"
        :fixed="false"
        @query="queryList"
      >
        <view
          v-for="item in dataList"
          :key="item.${idField}"
          class="crud-list__card bp-card"
        >
          <view class="crud-list__main">
            <view class="crud-list__avatar">
              <text class="crud-list__avatar-text">{{ displayName(item).slice(0, 1) }}</text>
            </view>
            <view class="crud-list__info">
              <text class="crud-list__name">{{ displayName(item) }}</text>
              <text v-if="item.createTime" class="crud-list__row">{{ item.createTime }}</text>
            </view>
          </view>
        </view>

        <template v-if="!hasPermi('${perm}:list')" #empty>
          <wd-empty :tip="'需要 ${perm}:list 权限'">
            <template #image>
              <view class="i-carbon-locked crud-list__empty-icon" />
            </template>
          </wd-empty>
        </template>
      </z-paging>
    </view>
  </AppPage>
</template>

<style lang="scss" scoped>
.crud-list {
  display: flex;
  flex-direction: column;
  height: 100vh;
  box-sizing: border-box;

  &__toolbar {
    margin: var(--bp-space-md) var(--bp-spacing-page);
    padding: var(--bp-space-xs) 0;
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
    align-items: center;
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

  &__name {
    display: block;
    font-size: var(--bp-font-subhead);
    font-weight: var(--bp-font-weight-medium);
    color: var(--bp-text-primary);
  }

  &__row {
    display: block;
    margin-top: var(--bp-space-xs);
    font-size: var(--bp-font-caption);
    color: var(--bp-text-secondary);
  }

  &__empty-icon {
    font-size: 120rpx;
    color: var(--bp-text-disabled);
  }
}
</style>
`

const typesPath = path.join(root, `src/api/types/${domain}/${moduleKebab}.ts`)
const apiPath = path.join(root, `src/api/${domain}/${moduleKebab}.ts`)
const pagePath = path.join(root, `src/pages-sub/${pkg}/${moduleKebab}/index.vue`)

writeIfMissing(typesPath, typesContent)
writeIfMissing(apiPath, apiContent)
writeIfMissing(pagePath, pageContent)

console.log('')
console.log('Next steps:')
console.log(`  1. 对照 admin-web src/api/${domain}/${moduleKebab}/ 补全 types 与 API`)
console.log(`  2. 在 src/utils/menu.ts ADMIN_PAGE_MAP 添加:`)
console.log(`     '${domain}/${moduleKebab}/index': '/pages-sub/${pkg}/${moduleKebab}/index'`)
console.log(`  3. 更新 docs/admin-web接口对照.md`)
console.log(`  4. 重启 pnpm dev（新页面需重新扫描路由）`)
