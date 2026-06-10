# UI/UX — 项目设计参考

## PC 管理端（admin-web）

| 项 | 约定 |
| --- | --- |
| 框架 | Vue 3 + TS + Element Plus |
| 布局 | RuoYi：侧栏 + 顶栏 + `app-container` |
| SCM 视图 | `src/views/scm/{domain}/{feature}/index.vue` |
| API | `src/api/scm/{domain}.ts` · `types.ts` `#region scm-gen:*` |
| 权限 | `v-hasPermi="['scm:wms:org:add']"` |
| 租户 | 超管 `useScmTenant()` |
| 码表 | `useDict('wms_org_type')` 或静态 `ORG_TYPE_OPTIONS` + `formatOrgTypeLabel` |
| 查询区 | label 68px、控件 240px；日期范围表单项 `width:308px` |
| 列表 | `v-loading` + `el-table` + `<pagination>` |
| 表单 | `el-dialog` + `el-form` rules |

标杆路径（重建后）：[frontend-dev/reference.md](../../../../business-platform-admin-web/.cursor/skills/frontend-dev/reference.md)

## 移动管理端（admin-app）

| 项 | 约定 |
| --- | --- |
| 框架 | unibest · Vue3 · wot-ui · z-paging |
| 页面壳 | `AppPage` + `PageHero`（custom 导航） |
| Token | `src/styles/tokens.scss` — 主色 `#1677ff` |
| 字号 | 标题 34rpx / 正文 28rpx / 辅助 24rpx |
| 列表 | `wd-search` + z-paging + `useListRefresh` |
| SCM API | **`pageXxx`** + `/api/scm/{domain}/{resources}`（禁止 `/list`） |
| 权限 | `PermButton` + `hasPermi` |

详规：[UI组件规范.md](../../../../business-platform-admin-app/docs/UI组件规范.md)

## SCM 主数据页（生成器路径）

```text
PRD 字段定稿 → mysql-dba DDL → scm-crud-generate zip → MERGE index.vue
  → 本 Skill 只规定：查询项、列、表单项、dict/OPTIONS、按钮权限
  → frontend-dev G1–G4 微调（不重写列表骨架）
```

## 信息架构模板（RuoYi 菜单）

| 字段 | 示例（B1 组织） |
| --- | --- |
| 菜单名 | 组织档案 |
| component | `scm/wms/org/index` |
| permPrefix | `scm:wms:org` |
| 路由 views | `scm/wms/org/index.vue` |

菜单 SQL 随 PRD 写入 `script/sql/{domain}/_gen/*_menu.sql`。

## 三端勿混

| 通道 | UI Skill 范围 |
| --- | --- |
| admin-web | ✅ 本 Skill 主战场 |
| admin-app 管理 CRUD | ✅ Step 6 → `generate-crud-module` |
| admin-app 复杂 / PDA | ✅ 现场线框 → `uniapp-dev`（`pages-sub/pda`） |
| PDA API | 无 admin 菜单；类型 `Pda*Vo`，非 `Vo` |
| OpenAPI | ❌ 无页面；不写 Vo 结构 |

## 相关文档

- [GENERATOR_SCM.md](../../docs/GENERATOR_SCM.md) — 查询区、MERGE
- [MODULE_RULES.md](../../docs/MODULE_RULES.md) §8 — Controller 拆分
- [B1-org/PRODUCT.md](../../docs/domains/wms/prd/B1-org/PRODUCT.md) — 业务字段来源（已落地）
