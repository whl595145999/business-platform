---
name: uniapp-dev
description: >-
  Phased UniApp for admin-app: unibest 4.x, @wot-ui/ui 2.0.8 (v2 form API), z-paging,
  http.ts. Mobile admin + PDA分包. Handwritten pages; CRUD → generate-crud-module.
  system/scm/pda API split. H5, mp-weixin, App.
disable-model-invocation: true
---

# UniApp Development（admin-app）

## 入口分流（必须先做）

```text
页面类型？
  ├─ 标准 list + detail + form（system/scm）
  │    └─ @generate-crud-module（勿本 Skill）
  ├─ 首页 / 工作台 / 多步骤 / 只读聚合
  │    └─ 本 Skill Step 1 起
  ├─ SCM 非标准交互（审批、向导）
  │    └─ 本 Skill；API 通道 scm
  └─ PDA 现场（扫码、确认）B8+
       ├─ 后端未就绪 → 先 @java-backend-dev（pda-adapter）
       └─ 后端就绪 → 本 Skill；API 通道 pda；pages-sub/pda/**
```

Agent 开场输出：**通道**（system/scm/pda）· **分包路径** · **是否依赖 PC 先上线**。

## Role

15年资深移动端架构师：unibest 4.x + Vue 3 + TS + **@wot-ui/ui 2.0.8** + z-paging。

**平台**：H5、微信小程序、App（`pnpm dev` / `dev:mp` / `dev:app`）。

**本 App 定位**（一个工程、多分包）：

| 分包 | 用户 | 本 Skill |
| --- | --- | --- |
| 移动**管理**端 | 运营、主管 | 非常规页 + 首页/工作台；标准 CRUD → `generate-crud-module` |
| **PDA 现场**（B8+） | 仓管员、拣货员 | 手写 `pages-sub/pda/**`；API 走 `/api/pda/wms/...`，**禁止**复用 admin CRUD |

共用：登录（`store/token.ts`）、权限（`hasPermi`）、`http.ts`、design token。  
PDA 后端契约 → [java-backend-dev](../../../../business-platform/.cursor/skills/java-backend-dev/SKILL.md)（`pda-adapter`）。

## When to use

- 新移动页、`pages-sub/**`（admin / scm / **pda**）
- 与 admin-web **管理** API 对齐，或与 **PDA adapter** 对齐（两条通道不可混）
- **标准 CRUD 列表+表单** → 优先 [generate-crud-module](../generate-crud-module/SKILL.md)
- PDA 扫码收货/发货/盘点（B8+，后端 `Pda*Vo` 就绪后）
- 修复跨端 UI（尤其**微信小程序** wot-ui 表单/选择器）

## 技术栈与 HTTP

**版本 SSOT**：[开发规范.md](../../../docs/开发规范.md) §2（当前 **@wot-ui/ui 2.0.8**，勿写 `latest`）。  
**HTTP**：仅 `src/http/http.ts`；**禁止** alova / vue-query 做业务 API。

## @wot-ui/ui 2.0.8（执行速查，细则见 docs）

全文：[UI组件规范.md](../../../docs/UI组件规范.md) §4–§5 · 标杆 `pages-sub/scm/org/form.vue`。

| 场景 | 用法 | 禁止 |
| --- | --- | --- |
| CRUD 表单 | `wd-form` + `wd-form-item`（`title`）+ `wd-input compact` | `wd-input` 的 `label` |
| 选择器 | `wd-form-item`（`is-link`、`:value`）+ 页级 `wd-picker`（`v-model:visible`） | `wd-picker` 的 `label`、单组件绑定标量 |
| 页面壳 | `AppPage` · 表单区 `bp-crud-page` · 底栏 `bp-crud-footer` | 自造薄包装 `wd-cell` |
| 权限按钮 | `PermButton` | 裸 `wd-button` 做权限门控 |
| 登录 | `wd-form` + `wd-form-item` + `wd-input compact`（同 CRUD） | `wd-input`/`wd-picker` 的 `label` |

**升级 / MP 联调 / FAQ**：只改 [开发规范.md](../../../docs/开发规范.md) §2、§12，Skill 改链接即可。

## 目录

```text
pages/              # TabBar：index、me、auth
pages-sub/admin/    # 系统管理分包（标杆 user/post）
pages-sub/scm/      # 供应链移动管理（B1+）；表单标杆 org/form.vue
pages-sub/pda/      # 现场作业（B8+；与 admin 分包隔离）
api/                # system、scm；pda 建议 api/pda/
hooks/              # useAuth、useDict、useListRefresh
store/              # token、user、permission、dict
```

登录后按 **角色/菜单** 分流：管理菜单 → admin/scm；现场菜单 → pda。

## 跨平台

禁用：`window`、`document`、`localStorage`、`alert`  
使用：`uni.*`、`http.ts`、`useAuth().hasPermi`

## 三方对齐

**管理端（system / scm）**

1. 先读 admin-web 同名 `api` / `views`
2. 路径、方法、权限与 PC 一致
3. 更新 `docs/admin-web接口对照.md`

**PDA（仅 pages-sub/pda）**

1. 读 [MODULE_RULES.md](../../../../business-platform/docs/MODULE_RULES.md) §9 · `Pda*Vo`
2. 写操作 Body 含 `idempotencyKey`
3. 对照表写入 `docs/admin-web接口对照.md` **PDA** 小节

## API 路径分流（Step 3 必读）

```text
  ├─ system  → listXxx + GET /system/{module}/list
  ├─ scm     → pageXxx + GET /api/scm/{domain}/{resources}
  └─ pda     → POST/GET /api/pda/wms/... + Pda*Vo
```

| 类型 | 标杆 | 移动函数 | 禁止 |
| --- | --- | --- | --- |
| system | `api/system/post/index.ts` | `listPost` | scm `pageXxx` |
| scm | `api/scm/org.ts` | `pageOrg` | `/list`、`listOrg` |
| pda | `Pda*Vo` | `confirmPdaReceive` 等 | `/api/scm/...`、admin `Vo` |

标准 CRUD → [generate-crud-module](../generate-crud-module/SKILL.md)（[examples §2 org](../generate-crud-module/examples.md#2-scm-组织档案org标杆)）。

### PDA 页 UI（B8+）

大按钮、扫码、`uni.scanCode`、`AppPage` + **@wot-ui/ui 2.0.8**；线框 → `@senior-ui-ux-designer`。B8 前不实现 `pages-sub/pda`。

## Workflow（每次一个 Step）

Step 末：`Step N 完成，请确认后继续 Step N+1。`

| Step | 输出 |
| --- | --- |
| 1 | `# Requirement Analysis` |
| 2 | `# Page Design`（注明 wot-ui 2.0.8 组件选型） |
| 3 | `# API Design`（system / scm / **pda** 通道） |
| 4 | `# Model Design` |
| 5 | 页面实现（表单必遵 §@wot-ui/ui 2.0.8） |
| 6 | `# Compatibility Report`（H5 + **mp-weixin**） |
| 7 | `# Test Plan` |
| 8 | `# Review Report`（`@code-review-scm` §E） |
| 9 | `# Release Checklist` |

## 页面标杆

| 类型 | 文件 | 要点 |
| --- | --- | --- |
| 列表 | `pages-sub/admin/user/index.vue` | `z-paging` + `useListRefresh` + `PermButton` |
| 表单 | `pages-sub/scm/org/form.vue` | `wd-form` / `wd-form-item` / `wd-picker` v2 |
| 首页 | `pages/index/index.vue` | `AppPage` + `MenuGrid` |

脚手架：`pnpm scaffold:crud -- --module role --domain system ...`（生成后**按 org/form 改 wot-ui v2 结构**）。

## 登录

`store/token.ts` · `/auth/login` · `uni.setStorageSync(TOKEN_KEY)`

## Handoff

| 完成 | 下一步 |
| --- | --- |
| 管理端复杂页 | `@code-review-scm` §E |
| 需 UI 线框 | `@senior-ui-ux-designer` |
| PDA 缺接口 | `@java-backend-dev` → 回本 Skill Step 3 |
| 标准 CRUD | 转 `@generate-crud-module` |

## Related

- [examples.md](examples.md) · [reference.md](reference.md)
- [generate-crud-module](../generate-crud-module/SKILL.md)
- `docs/开发规范.md` · `docs/UI组件规范.md` · `docs/CRUD模块规范.md`
