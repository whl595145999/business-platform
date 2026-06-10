# Agent 指引（admin-app）

> **默认行为**：本仓库相关问题，Agent 须先读本文件 + 加载下表 Skill 再回答/写代码（见 `.cursor/rules/skill-first.mdc`）。  
> 你可直接 `@uniapp-dev`、`@generate-crud-module` 显式指定；不写 `@` 也会自动分流。

UniApp · unibest · **@wot-ui/ui 2.0.8**（v2 表单 API）· 业务请求仅 `src/http/http.ts`。

**定位**：一个 App 工程 — **移动管理端**（主）+ **PDA 现场分包**（B8+，`pages-sub/pda`，独立 API）。标准 CRUD 不用 `uniapp-dev` 手写。

**PRD**：移动独有 → [docs/prd/](docs/prd/)；供应链业务 → [domains/](../business-platform/docs/domains/)（`prd/` 产品 · `design/` 技术）。

## Cursor Skills（优先）

| 场景 | Skill |
| --- | --- |
| 全栈分流 | `@scm-platform-dev`（[business-platform/.cursor/skills](../business-platform/.cursor/skills/README.md)） |
| **UI 线框 / 走查 / 信息架构**（**不写代码**） | `@senior-ui-ux-designer`（Audit / 增量 / 全量）→ 实现 Skill |
| 标准列表 + 表单 CRUD | `@generate-crud-module` |
| 非常规移动页 / 首页 / PDA / MP 修 UI | `@uniapp-dev` |
| PR 自检 | `@code-review-scm`（§E） |

**推荐链**：现有页点评 → `senior-ui-ux-designer` **Audit（Step 0）**；新页/改版 → 全量或增量 → Handoff 表 → `generate-crud-module` / `uniapp-dev`。

## API 路径（必分流）

| 类型 | 分页/调用 | 示例 |
| --- | --- | --- |
| **system** | `listXxx` + `/system/{module}/list` | `listPost` |
| **scm** | `pageXxx` + `/api/scm/{domain}/{resources}` | `pageOrg` → `/api/scm/wms/orgs` |
| **pda** | 现场接口 + `Pda*Vo` | `POST /api/pda/wms/inbound/confirm-receive` |

**禁止**：SCM 用 `/list`；PDA 用 `/api/scm/...` 或 admin `Vo`。  
标杆：SCM → [generate-crud-module/examples.md §2](.cursor/skills/generate-crud-module/examples.md#2-scm-组织档案org标杆) · PDA → [MODULE_RULES.md §9](../business-platform/docs/MODULE_RULES.md#9-openapi--pda-adapter)

## 硬约束（摘要）

- 禁止 alova / vue-query 业务 API
- 列表 + form 必须 `useListRefresh`；`onShow` 从 `@dcloudio/uni-app` 导入
- 新模块更新 `docs/admin-web接口对照.md`

常驻 rules：`.cursor/rules/`（红线）与 Skills（流程）并用。

## 工作区

建议在 monorepo 根或三仓同开。
