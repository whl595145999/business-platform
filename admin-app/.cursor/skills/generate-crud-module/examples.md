# Skill 实例

## 1. system — 岗位管理（post）

> 本实例验证 `generate-crud-module` Skill 完整流程，可作为后续模块模板。

### 1.1 输入

| 参数 | 值 |
|------|-----|
| module | `post` |
| domain | `system` |
| title | 岗位管理 |
| perm | `system:post` |
| idField | `postId` |
| nameField | `postName` |
| 标杆 | `role`（分页列表 + form） |

### 1.2 生成物

```
src/api/types/system/post.ts
src/api/system/post.ts
src/pages-sub/admin/post/index.vue   # 列表 + useListRefresh
src/pages-sub/admin/post/form.vue    # 新增/编辑/删除
docs/admin-web接口对照.md            # 岗位模块行
```

`ADMIN_PAGE_MAP` 已有 `system/post/index`，无需新增。

### 1.3 命令（可选脚手架起点）

```bash
pnpm scaffold:crud -- \
  --module post \
  --domain system \
  --title 岗位管理 \
  --perm system:post \
  --id-field postId \
  --name-field postName
```

脚手架生成列表骨架后，对照 `role` 补 form.vue 与完整字段。

### 1.4 权限标识

| 操作 | perm |
|------|------|
| 列表 | `system:post:list` |
| 新增 | `system:post:add` |
| 编辑 | `system:post:edit` |
| 删除 | `system:post:remove` |

### 1.5 验证清单

- [x] 列表页加载（`listPost` → `IPageResult`）
- [x] 搜索岗位名称
- [x] 新增 → form → 保存 → 返回列表刷新（`useListRefresh`）
- [x] 点击列表项 → 编辑 → 保存后列表更新
- [x] 删除岗位
- [x] 无权限时按钮隐藏 / 空态提示
- [x] form 路由 H5 + 小程序均可访问

### 1.6 与 dept 的差异

| 模块 | 列表 API | 特殊处理 |
|------|----------|----------|
| post | 分页 `rows/total` | 同 role，直接 z-paging |
| dept | 树形数组 | 展平 + 客户端分页 |
| role | 分页 | 编辑保留 `menuIds` |

### 1.7 对话触发示例

```
用 generate-crud-module skill，参考 post 实例，生成 notice 通知公告模块
```

---

## 2. scm — 组织档案（org）标杆

> **B1 重建后**对齐 PC：`admin-web/src/api/scm/wms.ts`（`pageOrg`）· `views/scm/wms/org/index.vue`。  
> 移动表单 UI 标杆：`pages-sub/scm/org/form.vue`（**@wot-ui/ui 2.0.8**，`wd-form` + `wd-form-item`）。  
> API 契约以 [PRODUCT.md](../../../business-platform/docs/domains/wms/prd/B1-org/PRODUCT.md) · [TECH.md](../../../business-platform/docs/domains/wms/design/B1-org/TECH.md) 为准；移动须对齐 **scm 契约**，不用 `/list`。

| 参数 | 值 |
|------|-----|
| module | `org` |
| api 文件域 | `wms`（`src/api/scm/wms.ts` 合并风格，或 `src/api/scm/org.ts`） |
| resources | `orgs` |
| perm | `scm:wms:org` |
| idField | `id` |
| nameField | `orgName` |

### 2.1 PC 对照（Step 1 必读）

| 操作 | PC | 移动函数 |
|------|-----|----------|
| 分页 | `GET /api/scm/wms/orgs` | `pageOrg(query)` |
| 详情 | `GET /api/scm/wms/orgs/{id}` | `getOrg(id, tenantId?)` |
| 新增 | `POST /api/scm/wms/orgs` | `addOrg(data)` |
| 修改 | `PUT /api/scm/wms/orgs/{id}` | `updateOrg(data)` |
| 删除 | `DELETE /api/scm/wms/orgs/{id}` | `delOrg(id)` |

### 2.2 权限

| 操作 | perm |
|------|------|
| 列表 | `scm:wms:org:list` |
| 新增 | `scm:wms:org:add` |
| 编辑 | `scm:wms:org:edit` |
| 删除 | `scm:wms:org:remove` |

### 2.3 注意

- `org_type` 在 PC 用静态 OPTIONS，移动侧同样用常量或 `useDict`，勿假设 `wms_org_type` 必有 dict
- 超管场景传 `tenantId`（对照 PC `useScmTenant`）
- 菜单 component：`scm/wms/org/index` → `ADMIN_PAGE_MAP` 映射 `pages-sub/scm/org/index`

### 2.4 对话触发示例

```
用 generate-crud-module，按 examples §2 org 标杆，对齐 admin-web pageOrg API
```
