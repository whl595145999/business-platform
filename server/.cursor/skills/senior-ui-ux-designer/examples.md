# UI/UX Designer — 示例

## 例 1 — B1 组织档案（全量，PRD 已定）

```text
@senior-ui-ux-designer 组织档案 PC+移动 UI，全量，从 Step 1
```

| Step | 要点 |
| --- | --- |
| 1 | IA：`供应链 → 仓储 → 组织档案`；component `scm/wms/org/index` |
| 2 | 流：分页查询 → 新增 → 校验 org_code 唯一 → 停用不可用于新单 |
| 3 | 线框：查询（orgName、orgType、status）+ 表格 + 分页 |
| 4 | `org_type` OPTIONS；`status` dict；移动 `wd-form` + 页级 `wd-picker` |
| 5 | 引用 Element Plus / `UI组件规范.md`，无新 token |
| 6 | 移动 `pages-sub/scm/org/*` · `pageOrg` · `ADMIN_PAGE_MAP` |
| 7 | 移动：表单标签 wot-ui v2；PC：查询区 label 宽度 |
| 8 | 交接表 → `frontend-dev` G2；移动 → `generate-crud-module` §2 |

---

## 例 2 — 查询区对齐（增量）

```text
@senior-ui-ux-designer 组织列表查询区对齐规范，增量 Step 3–4 + 8
```

对照 [GENERATOR_SCM.md §7](../../docs/GENERATOR_SCM.md) 去掉内联 width；Step 8 简表只列 PC + `frontend-dev`。

---

## 例 3 — 首页 UX 走查（Audit）

```text
@senior-ui-ux-designer admin-app 首页设计与排版走查，Audit
```

**输入**：`pages/index/index.vue` + `UI组件规范.md` §6

**Step 0 产出要点**

- 维度评分：IA / 排版 / 规范 / 状态 / 业务表达
- P1：指标偏「权限数」技术向 → 产品定是否换业务指标
- P2：与「我的」Hero 信息重复 → 增量 Step 3 若改版
- 下一步：若改版 → 全量 Step 1；若只修文案 → `uniapp-dev`

---

## 例 4 — 错误分流

| 用户说 | 应转 |
| --- | --- |
| 「组织要不要支持跨 org 调拨」 | `scm-product-manager` |
| 「帮我写 org 列表 Vue」 | `frontend-dev` |
| 「小程序表单没标签」 | `uniapp-dev`（实现修 bug，非 UI 全量） |
| 「org 表加字段」 | `mysql-dba` |

---

## 例 5 — 全链路位置

```text
scm-product-manager Step 6–7（ORG PRD + AC）
  → senior-ui-ux-designer 全量 Step 1–8
  → ecommerce-scm-architect（若缺 API 技术章）
  → scm-platform-dev → scm-crud-generate → frontend-dev
```

---

## 例 6 — Step 8 交接表示例（移动首页改版后）

| 页面 | 端 | 路径 | 实现 Skill | 标杆 | 权限 |
| --- | --- | --- | --- | --- | --- |
| 工作台首页 | 移动 | `pages/index/index` | `uniapp-dev` | 改版后自身 | — |
| 组织档案 | 移动 | `pages-sub/scm/org/form` | `generate-crud-module` | `scm/org/form.vue` | `scm:wms:org:*` |
