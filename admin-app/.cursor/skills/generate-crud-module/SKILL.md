---
name: generate-crud-module
description: >-
  移动端 CRUD 快路径：business-platform-admin-app 对齐 admin-web API。system 用
  /list+listXxx；scm 用 /api/scm/{domain}/{resources}+pageXxx。脚手架、列表表单、
  scaffold CRUD、移动模块生成。Use for mobile list/detail/form modules.
disable-model-invocation: true
---

# Generate CRUD Module (Mobile)

> **每次一个 Step，等用户「继续」**。Step 末输出：`Step N 完成，请确认后继续 Step N+1。`

## When to use

- 新 **list / detail / form** 模块（如 role、post、scm org）
- [scm-crud-generate/workflow.md](../../../../business-platform/.cursor/skills/scm-crud-generate/workflow.md) Step 7 移动端对齐
- **非常规页 / 首页 / PDA** → [uniapp-dev](../uniapp-dev/SKILL.md)（PDA **禁止**本 Skill）

## API 路径分流（必须先做）

```text
对照 admin-web 哪类 API？
  ├─ system（RuoYi 平台）→ /system/{module}/list，函数 listXxx
  └─ scm（供应链）        → /api/scm/{domain}/{resources}，函数 pageXxx
```

| 类型 | PC 标杆 | 移动 API 函数 | 分页路径 |
| --- | --- | --- | --- |
| **system** | `src/api/system/post/index.ts` | `listPost` | `GET /system/post/list` |
| **scm** | `src/api/scm/wms.ts` → `pageOrg` | `pageOrg` | `GET /api/scm/wms/orgs` |

**禁止**对 SCM 模块使用 `/list` 或 `listXxx`（与生成器契约不一致）。  
SCM 标杆见 [examples.md §2](examples.md#2-scm-组织档案org标杆)。

## Mandatory references（细则在 docs，本 Skill 不复制）

1. [docs/开发规范.md](../../../docs/开发规范.md) §2 技术栈 · §5 API
2. [docs/CRUD模块规范.md](../../../docs/CRUD模块规范.md)
3. [docs/admin-web接口对照.md](../../../docs/admin-web接口对照.md)
4. [docs/UI组件规范.md](../../../docs/UI组件规范.md) §5 表单（wot-ui 2.0.8）
5. 标杆：`pages-sub/admin/post/`（system 列表）· `pages-sub/scm/org/form.vue`（scm 表单）· PC `api/scm/wms.ts`

## Workflow（每次一个 Step）

| Step | 输出 |
| --- | --- |
| 1 | 读 PC `api`；确认 **system / scm** 路径与函数名 |
| 2 | types + API（`http.ts` only） |
| 3 | 列表页（AppPage + wd-search + z-paging + `useListRefresh`） |
| 4 | `ADMIN_PAGE_MAP` + 菜单 component 对齐 |
| 5 | 更新 `docs/admin-web接口对照.md` |
| 6 | PR 检查（[code-review-scm](../../../../business-platform/.cursor/skills/code-review-scm/SKILL.md) §E 或 `docs/PR检查清单.md`）+ 冒烟 |

### Step 1 — admin-web 对照

**system**（复制 `src/api/system/{module}/index.ts`）：

- `GET /system/{module}/list` → `listXxx(query)`
- `GET /system/{module}/:id` → `getXxx(id)`
- `POST /system/{module}` → `addXxx(data)`
- `PUT /system/{module}` → `updateXxx(data)`
- `DELETE /system/{module}/:id` → `delXxx(id)`

**scm**（复制 `src/api/scm/{domain}.ts` 中 `#region scm-gen:*`）：

- `GET /api/scm/{domain}/{resources}?pageNum&pageSize` → `pageXxx(query)`
- `GET /api/scm/{domain}/{resources}/{id}` → `getXxx(id)`（可选 `tenantId` param）
- `POST /api/scm/{domain}/{resources}` → `addXxx(data)`
- `PUT /api/scm/{domain}/{resources}/{id}` → `updateXxx(data)`
- `DELETE /api/scm/{domain}/{resources}/{id}` → `delXxx(id)`

列表返回：`IPageResult<T>`。权限：system 用 `system:{module}:*`；scm 用 `scm:{domain}:{business}:*`（如 `scm:wms:org:list`）。

### Step 2 — File layout

| File | Purpose |
|------|---------|
| `src/api/types/{domain}/{module}.ts` | `IXxxQuery`, `IXxxVo`, `IXxxForm` |
| `src/api/{domain}/{module}.ts` | page/list + get/add/update/del |
| `src/pages-sub/{pkg}/{module}/index.vue` | List |
| `src/pages-sub/{pkg}/{module}/form.vue` | Form（Phase 2） |

- `{pkg}`: `admin`（system）| `scm`（供应链）
- scm 的 `{domain}` 指 API 文件域：`wms` / `inventory` / `oms`（非 URL 第一段）

### Step 3 — List page rules

- Root: `<AppPage :safe-top="false">`
- `z-paging` + **Step 1 确认的函数名**（`listXxx` 或 `pageXxx`）+ `@query`
- `useListRefresh(pagingRef)` — form 返回后 reload
- `hasPermi` + `PermButton`；**不要**新建薄包装组件

### Step 4 — Menu mapping

```typescript
'system/role/index': '/pages-sub/admin/role/index',
'scm/wms/org/index': '/pages-sub/scm/org/index',  // 与后端 menu component 一致
```

### Step 5 — Naming

| Item | system 示例 | scm 示例 |
|------|-------------|----------|
| API fn（分页） | `listPost` | `pageOrg` |
| perm | `system:post:list` | `scm:wms:org:list` |
| Type | `IPostVo` | `IOrgVo` |

### Step 6 — Scaffold（system 可选）

```bash
pnpm scaffold:crud -- --module post --domain system --title 岗位管理 \
  --perm system:post --id-field postId --name-field postName
```

**scm 模块**：脚手架后须把 `listXxx` + `/list` **改为** `pageXxx` + `/api/scm/...`（对照 PC）。

## Phase 2 (detail/form)

- 参考 `pages-sub/admin/post/form.vue`
- `uni.navigateBack()` + 列表 `useListRefresh`
- `onShow` 从 `@dcloudio/uni-app` 导入，**不能**从 `vue` 导入

## Do NOT

- SCM 使用 `/list` 或 `listXxx`
- alova / vue-query 做业务 API
- 跳过 `admin-web接口对照.md`

## Additional templates

- [reference.md](reference.md) — system / scm 骨架
- [examples.md](examples.md) — post（system）+ org（scm）

## Related

- [scm-platform-dev](../../../../business-platform/.cursor/skills/scm-platform-dev/SKILL.md)
- [scm-crud-generate/workflow.md](../../../../business-platform/.cursor/skills/scm-crud-generate/workflow.md)
- [uniapp-dev](../uniapp-dev/SKILL.md)
- [frontend-dev](../../../../business-platform-admin-web/.cursor/skills/frontend-dev/SKILL.md)
