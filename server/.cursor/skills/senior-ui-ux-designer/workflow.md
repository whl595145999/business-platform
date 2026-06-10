# Senior UI/UX Designer 工作流

## 入口分流（三种模式）

```text
输入来源？
  ├─ 「好不好」「排版」「UX 点评」+ 现有页面 → 【Audit】Step 0（一轮交付）
  ├─ 仅有业务想法 / 无 PRD、无现有页 → 先产品 Skill，勿 Step 3 起
  ├─ PRD 已有页面列表 + 字段 → 【全量】Step 1 起
  ├─ 仅改单页交互/组件/查询区 → 【增量】Step 3–4 + Step 8
  ├─ 仅统一 token（无布局变更）→ Step 5 差异说明（引用 SSOT）
  └─ 仅移动适配 → Step 6（需 PC 线框或 PRD 字段已定）
```

Agent 开场模板：

```markdown
# UI/UX 分流
- **模式**：Audit / 增量 / 全量
- **端**：PC / 移动 / 双端
- **页面**：`views/scm/...` 或 `pages-sub/...` 或（Audit）现有文件路径
- **实现路径**：生成器 MERGE / 手写 / 移动 CRUD / uniapp-dev
- **必读**：PRD 路径 或 现有代码/规范
- **本轮回合**：Step N
```

---

## Step 0 — Design Audit（设计走查）

**适用**：现有页评价、改版前诊断、规范合规检查。**行业对应**：Design Critique / Heuristic Review。

**必读**（按端）：[reference.md](reference.md) · [UI组件规范](../../../../business-platform-admin-app/docs/UI组件规范.md)（移动）· [GENERATOR_SCM.md](../../docs/GENERATOR_SCM.md) §7（PC 查询区）

**产出**（建议一轮写完）

```markdown
# Step 0 — Design Audit

## 评审对象
- 页面 / 路由 / 端

## 总评（1–2 句）

## 维度评分（1–5，主观）
| 维度 | 分 | 说明 |
| 信息架构 | | |
| 排版与层级 | | |
| 组件与规范一致性 | | |
| 状态完整性（空/错/载/权限） | | |
| 业务表达（非技术向指标） | | |

## 问题清单
| 优先级 | 问题 | 建议 | 责任 |
| P0 | | | 产品 / UX / 实现 |
| P1 | | | |
| P2 | | | |

## 规范合规
- [ ] 移动：AppPage / token / wot-ui 2.0.8 表单（`wd-form-item`，禁 `label`）
- [ ] PC：查询区宽度、生成器骨架未破坏

## 下一步建议
- [ ] 仅实现修复 → `@uniapp-dev` / `@frontend-dev`（列文件）
- [ ] 需改版 → 【增量】Step 3–4 或 【全量】Step 1 起
- [ ] 需产品决策 → `@scm-product-manager`（列决策点）
```

**规则**

- Audit **不给 Vue 代码**；修复交给实现 Skill。
- 业务指标「展示什么」归产品；UX 只评「怎么排、是否好用」。
- 结尾必须明确：**是否进入增量/全量**，以及交给哪个实现 Skill。

**输出标题**：`# Step 0 — Design Audit`

---

## Step 1 — Information Architecture

**必读**：[reference.md §IA](reference.md) · 当期 PRD · RuoYi 菜单惯例

**产出**

- 页面树（模块 → 子页 → 弹窗/抽屉）
- 导航：RuoYi 侧栏 `component` / 移动 `ADMIN_PAGE_MAP`
- 模块：与 `whl-scm` 域、`views/scm/{domain}/` / `pages-sub/` 对齐

**检查清单**

- [ ] 与 [industry-baseline.md](../scm-product-manager/industry-baseline.md) 当期波次一致
- [ ] 一菜单页一 Controller（[MODULE_RULES.md](../../docs/MODULE_RULES.md) §8）
- [ ] 未写 API 实现细节（Step 8 交接表列路径）

**输出标题**：`# Step 1 — Information Architecture`

---

## Step 2 — User Flow

**产出**

- 主路径（列表 → 查询 → 新增/编辑 → 保存 → 刷新）
- 异常：校验失败、重复编码、停用、无权限
- 边界：空列表、加载中、未登录、租户切换

**检查清单**

- [ ] 与 PRD 业务状态一致（不发明状态机）
- [ ] 权限与 `scmPermPrefix` / `hasPermi` 一致

**输出标题**：`# Step 2 — User Flow`

---

## Step 3 — Wireframe

**产出**

- 线框（ASCII / 区块表）：查询区、工具栏、表格、分页、表单、底栏
- 布局说明：栅格、表单项列数、主按钮位置、TabBar/安全区

**PC 硬约束**（[GENERATOR_SCM.md](../../docs/GENERATOR_SCM.md) §7）

- 查询区 **勿**内联 `width`（日期范围 **308px** 除外）
- 列表：查询在上、按钮行、`el-table`、`<pagination>`

**移动硬约束**（[UI组件规范](../../../../business-platform-admin-app/docs/UI组件规范.md)）

- 首页：`PageHero` → 指标/区块 → `MenuGrid` / `wd-empty`
- CRUD 表单：`bp-crud-page` + `wd-form` + `bp-crud-footer`（标杆 `scm/org/form.vue`）

**输出标题**：`# Step 3 — Wireframe`

---

## Step 4 — Component Design

**产出**

- 组件清单：`el-*` / `wd-*` / 业务壳（`AppPage`、`PermButton`…）
- 字段绑定（PRD 字段名）
- 状态：empty / loading / error / disabled / 无权限
- 交互：删除确认、校验时机、选择器弹层（移动 `wd-picker` + `v-model:visible`）

**生成器路径**：标明 `scm_crud` 生成 vs MERGE 后 `frontend-dev` G2 补 dict/OPTIONS

**输出标题**：`# Step 4 — Component Design`

---

## Step 5 — Design System（差异 + 引用 SSOT）

**原则**：**不另起一套**；默认只写 **与本页相关的差异** 与 **须遵守的 SSOT 链接**。

| 端 | SSOT（全文不在此重复） |
| --- | --- |
| PC | Element Plus + RuoYi 查询区 · `element-ui.scss` |
| 移动 | `tokens.scss` · [UI组件规范](../../../../business-platform-admin-app/docs/UI组件规范.md) · **wot-ui 2.0.8** |

**产出**

- 本页沿用的 token / 组件（列表引用）
- **仅当改版时**：新增或变更的色、间距、图标规则（Delta）
- 禁止重复抄写完整版本表（移动见 `开发规范.md` §2）

**输出标题**：`# Step 5 — Design System Alignment`

---

## Step 6 — Responsive Design

**产出**

- H5 / 微信小程序 / App：导航、安全区、胶囊、TabBar 留白
- `AppPage` / `--bp-custom-nav-padding-top` / `layout-vars`
- PC 与移动：**字段与操作一致**，API 契约一致（`pageXxx` vs `listXxx`）

**输出标题**：`# Step 6 — Responsive Design`

---

## Step 7 — Accessibility（分级）

按端裁剪，**不必每次写完整 WCAG 报告**（行业内部后台惯例）。

| 端 | 必查项 |
| --- | --- |
| **移动管理端** | 触控区 ≥ 88rpx 推荐；对比度；字号三档；**表单标签可见**（`wd-form-item`） |
| **PDA 现场** | 大按钮、强光可读、单手/手套操作、扫码主路径一步可达 |
| **PC 后台** | 表格密度、焦点顺序、表单 label 关联、`el-form` 校验提示 |

**输出标题**：`# Step 7 — Accessibility`

---

## Step 8 — Developer Handoff

**产出** — 开发须能 **无歧义** 选实现 Skill 与标杆文件。

### 8.1 交接表（必填）

| 页面 | 端 | 路由/路径 | 实现 Skill | 标杆文件 | 权限前缀 |
| --- | --- | --- | --- | --- | --- |
| （示例）组织档案 | 移动 | `pages-sub/scm/org/form` | `generate-crud-module` | `scm/org/form.vue` | `scm:wms:org:*` |
| （示例）首页工作台 | 移动 | `pages/index/index` | `uniapp-dev` | `index/index.vue` | — |

### 8.2 说明块

| 块 | 内容 |
| --- | --- |
| 组件树 | 页面 → 业务组件 → wot-ui / Element |
| 交互说明 | 点击、校验、刷新、下拉、租户 |
| 状态说明 | 空/错/载、dict / OPTIONS |
| API 对照 | 列 admin-web 同名方法（路径即可） |

### 8.3 下游触发

```text
PC      → @frontend-dev（G1–G4 或手写）
移动 CRUD → @generate-crud-module（examples §2）
移动复杂 / 首页 / PDA → @uniapp-dev
提交前   → @code-review-scm §D / §E
```

**输出标题**：`# Step 8 — Developer Handoff`

---

## 与产品 Skill 的重叠

| 主题 | 产品 Skill | 本 Skill |
| --- | --- | --- |
| 页面清单 | PRD Step 5–6 | Step 1 IA 细化 |
| 业务指标含义 | 产品 | Audit 只评呈现方式 |
| 原型 | 文字简述 | Step 3–4 线框+组件 |
| 验收 | AC | Step 8 状态供 AC 补充 |
