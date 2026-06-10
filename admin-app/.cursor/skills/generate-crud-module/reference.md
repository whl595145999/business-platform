# CRUD Module — Code Skeletons

先确认 **system** 还是 **scm**（见 [SKILL.md](SKILL.md) API 分流）。  
Replace `{Domain}`, `{Module}`, `{module}`, `{domain}`, `{title}`, `{perm}`, `{idField}`, `{nameField}`.

---

## system — `src/api/system/{module}.ts`

## types — `src/api/types/{domain}/{module}.ts`

```typescript
import type { IPageQuery } from '@/api/types/backend'

export interface I{Module}Query extends IPageQuery {
  {nameField}?: string
  status?: string
}

export interface I{Module}Vo {
  {idField}: string | number
  {nameField}: string
  status?: string
  createTime?: string
  [key: string]: any
}

export interface I{Module}Form {
  {idField}?: string | number
  {nameField}: string
  status?: string
  remark?: string
}
```

## system api — `src/api/system/{module}.ts`

```typescript
import type { IPageResult } from '@/api/types/backend'
import type { I{Module}Form, I{Module}Query, I{Module}Vo } from '@/api/types/{domain}/{module}'
import { http } from '@/http/http'

export function list{Module}(query: I{Module}Query) {
  return http.get<IPageResult<I{Module}Vo>>('/{domain}/{module}/list', query)
}

export function get{Module}(id?: string | number) {
  const safeId = id === undefined || id === null || id === '' ? '' : String(id)
  return http.get<I{Module}Vo>(`/{domain}/{module}/${safeId}`)
}

export function add{Module}(data: I{Module}Form) {
  return http.post<void>('/{domain}/{module}', data)
}

export function update{Module}(data: I{Module}Form) {
  return http.put<void>('/{domain}/{module}', data)
}

export function del{Module}(id: string | number | Array<string | number>) {
  const safeId = Array.isArray(id) ? id.join(',') : id
  return http.delete<void>(`/{domain}/{module}/${safeId}`)
}
```

---

## scm — `src/api/scm/wms.ts` 风格（勿用 /list）

对照 PC：`business-platform-admin-web/src/api/scm/wms.ts` 中 `pageOrg`。

```typescript
import type { IPageResult } from '@/api/types/backend'
import type { IOrgForm, IOrgQuery, IOrgVo } from '@/api/types/scm/org'
import { http } from '@/http/http'

export function pageOrg(query?: IOrgQuery) {
  return http.get<IPageResult<IOrgVo>>('/api/scm/wms/orgs', query)
}

export function getOrg(id: string | number, tenantId?: string) {
  return http.get<IOrgVo>(`/api/scm/wms/orgs/${id}`, { tenantId })
}

export function addOrg(data: IOrgForm) {
  return http.post<IOrgVo>('/api/scm/wms/orgs', data)
}

export function updateOrg(data: IOrgForm) {
  const id = data.id
  return http.put<IOrgVo>(`/api/scm/wms/orgs/${id}`, data)
}

export function delOrg(id: string | number) {
  return http.delete<void>(`/api/scm/wms/orgs/${id}`)
}
```

列表页调用 **`pageOrg`**，权限 `scm:wms:org:list`。

---

## list page — `src/pages-sub/admin/{module}/index.vue`

Copy from `src/pages-sub/admin/user/index.vue` and replace:

| user | target |
|------|--------|
| `IUserVo` | `I{Module}Vo` |
| `listUser` | `list{Module}`（system）或 `page{Module}`（scm） |
| `system:user:list` | `{perm}:list` |
| search field `userName` | `{nameField}` |
| title 用户管理 | {title} |
| avatar / dept / phone rows | module-specific columns |

**有 form 页时必加：**

```typescript
import { useListRefresh } from '@/hooks/useListRefresh'

useListRefresh(pagingRef)
```

> `useListRefresh` 内部使用 `@dcloudio/uni-app` 的 `onShow`，勿从 `vue` 导入。

## form page — wot-ui 2.0.8 + 保存后返回

**结构**（禁止 `wd-input`/`wd-picker` 的 `label`）：

- `AppPage` → `bp-crud-page` → `wd-form` + `wd-form-item`（`title`）
- 输入：`wd-input compact`
- 选择：`wd-form-item`（`is-link` + `:value`）+ 页底 `wd-picker`（`v-model:visible`）
- 按钮：`bp-crud-footer` + `PermButton`

标杆：`src/pages-sub/scm/org/form.vue` · [UI组件规范.md](../../../docs/UI组件规范.md) §5。

```typescript
uni.showToast({ title: '保存成功', icon: 'success' })
setTimeout(() => uni.navigateBack(), 400)
```

列表页 `useListRefresh` 会在 `onShow` 时 `pagingRef.reload()`。

## menu.ts entry

```typescript
'system/{module}/index': '/pages-sub/admin/{module}/index',
```

## 接口对照.md row

```markdown
| `system/{module}/index.ts` → `list{Module}` | `system/{module}.ts` → `list{Module}` | GET `/{domain}/{module}/list` |
```
