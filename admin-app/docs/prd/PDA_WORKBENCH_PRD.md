# PDA 现场工作台 PRD（移动壳 · B8+）

| 项 | 内容 |
| --- | --- |
| 文档版本 | v0.1（骨架） |
| 波次 | **B8** |
| 域 | `pda` · 现场 UI 壳 |
| 端 | **仅** `business-platform-admin-app` |
| 主仓 | 本文件（UI）；业务 API PRD **待 B8 重建**（见 [domains/README.md](../../../business-platform/docs/domains/README.md)） |
| 状态 | **骨架** — 占位页已建，B8 前不实现作业流 |
| 关联 | [MODULE_RULES.md](../../../business-platform/docs/MODULE_RULES.md) §9 · [UI组件规范.md](../UI组件规范.md) |

---

## 1. 背景与目标

### 1.1 与管理端首页区别

| 维度 | 管理工作台（HOME） | PDA 工作台（本 PRD） |
| --- | --- | --- |
| 用户 | 办公室管理员 | 仓库现场作业员 |
| 入口 | TabBar 首页 | 现场菜单 / 扫码直达（待定） |
| 布局 | 宫格 + 指标 | **大按钮** + 扫码主路径 |
| API | `/api/scm/...`、`/system/...` | **`/api/pda/wms/...`** |
| 分包 | `pages/`、`pages-sub/admin|scm` | **`pages-sub/pda/`** |

### 1.2 目标

- 单手、手套场景可点（触控区 ≥ 96rpx 主按钮）
- 扫码 `uni.scanCode` 为第一动作
- 少字段、强反馈（成功/失败震动+toast）
- **不进** `ADMIN_PAGE_MAP`（无 admin 后台菜单 component）

### 1.3 非目标（v0.1）

- 完整收货/出库/盘点页面（随 B5～B7 单据 PRD）
- 与管理端共用 CRUD 表单模板

---

## 2. 页面结构（规划）

```text
pages-sub/pda/index.vue          # 现场工作台（功能入口）
pages-sub/pda/inbound/receive.vue   # B8+ 收货扫码
pages-sub/pda/outbound/ship.vue     # B8+ 出库复核
pages-sub/pda/stocktake/index.vue   # B8+ 盘点
```

### 2.1 工作台线框（index）

```text
┌─────────────────────────────────────┐
│ PageHero：现场作业 · {仓库/组织}      │
├─────────────────────────────────────┤
│ [  扫码作业  ]  主按钮全宽           │
├─────────────────────────────────────┤
│ 大宫格 2 列：收货 | 出库 | 盘点 ...   │
├─────────────────────────────────────┤
│ 今日完成 / 待办（v2 API 占位）        │
└─────────────────────────────────────┘
```

---

## 3. UI 硬约束

| 项 | 约定 |
| --- | --- |
| 组件库 | `@wot-ui/ui` 2.0.8 |
| 页面壳 | `AppPage` |
| 主按钮 | `wd-button` `block` `size="large"` |
| 表单 | 字段极少；扫码结果填充，非长表单 |
| 颜色 | design token；现场高对比 |
| Skill | `@uniapp-dev` · 线框 `@senior-ui-ux-designer` |

---

## 4. API 对接（移动侧）

| 项 | 约定 |
| --- | --- |
| 请求 | `src/http/http.ts` |
| 目录 | `src/api/pda/`（建议，B8 创建） |
| 类型 | 对齐后端 `Pda*Vo`，**禁止**复用 scm admin types |
| 写操作 | Body 含 `idempotencyKey` |

---

## 5. 路由与菜单

| 项 | v0.1 | B8 定稿 |
| --- | --- | --- |
| 分包 | `pages-sub/pda/` | 同左 |
| 动态菜单 | 无 | 现场角色独立菜单 component（非 admin map） |
| 开发调试 | 开发者工具直达 `/pages-sub/pda/index` | 菜单 SQL |

---

## 6. 验收（AC · 骨架）

- [x] `pages-sub/pda/index.vue` 占位页可打开
- [ ] 扫码按钮调用 `uni.scanCode`（B8）
- [ ] 收货页对接 `POST /api/pda/wms/inbound/confirm-receive`（B8）
- [ ] 写操作带 `idempotencyKey`
- [ ] `admin-web接口对照.md` PDA 小节已登记

---

## 7. 实现状态

| 文件 | 状态 |
| --- | --- |
| `pages-sub/pda/index.vue` | v0.1 占位（说明 B8+） |
| `api/pda/*` | 未创建 |
| 作业子页 | 未创建 |

---

*业务规则与 `Pda*Vo` 字段待 B8 业务 PRD 定稿后补充（见 [domains/README.md](../../../business-platform/docs/domains/README.md)）。*
