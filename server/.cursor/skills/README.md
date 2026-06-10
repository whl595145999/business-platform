# Cursor Skills — 供应链业务平台

> 角色：**卖** · **履约** · **界面** · **技术方案** → [ROLES.md](ROLES.md)  
> 路由：[SKILL_ROUTING.md](SKILL_ROUTING.md) · 硬约束：[CONSTRAINTS.md](CONSTRAINTS.md)  
> Step 末：`Step N 完成，请确认后继续 Step N+1。`

## 设计（不写代码）

| Skill | 定位 |
| --- | --- |
| [senior-product-manager](senior-product-manager/SKILL.md) | **卖** — 体验、故事、埋点 |
| [scm-product-manager](scm-product-manager/SKILL.md) | **履约** — 单据、库存、四流 |
| [senior-ui-ux-designer](senior-ui-ux-designer/SKILL.md) | **界面** — Audit 走查 / 增量 / 全量、Handoff |
| [ecommerce-scm-architect](ecommerce-scm-architect/SKILL.md) | **技术方案** — ER、API、三通道 |

## 实现入口

| Skill | 定位 |
| --- | --- |
| **[scm-platform-dev](scm-platform-dev/SKILL.md)** | 分流 → 子 Skill |

## 后端 `business-platform`

| Skill | 何时用 |
| --- | --- |
| [mysql-dba](mysql-dba/SKILL.md) | DDL、索引 |
| [scm-crud-generate](scm-crud-generate/SKILL.md) | 主数据 zip（Java+PC） |
| [java-backend-dev](java-backend-dev/SKILL.md) | 单据/库存/**PDA/OpenAPI** |
| [code-review-scm](code-review-scm/SKILL.md) | PR / PRD Review |

## 前端

| Skill | 仓库 |
| --- | --- |
| [frontend-dev](../../../business-platform-admin-web/.cursor/skills/frontend-dev/SKILL.md) | PC 管理端 |
| [generate-crud-module](../../../business-platform-admin-app/.cursor/skills/generate-crud-module/SKILL.md) | 移动**管理**端 |
| [uniapp-dev](../../../business-platform-admin-app/.cursor/skills/uniapp-dev/SKILL.md) | 移动非常规页 |

## 标准链路

```text
从 0 重建 → scm-product-manager（读 industry-baseline.md）→ 各波次 `docs/domains/{域}/prd/` → senior-ui-ux-designer → `design/` → scm-platform-dev
履约需求 → scm-product-manager → senior-ui-ux-designer（按场景）→ architect → scm-platform-dev
卖货/体验 → senior-product-manager → senior-ui-ux-designer → scm-platform-dev（多为 frontend-dev）
主数据   → mysql-dba → scm-crud-generate → frontend-dev
PDA现场  → scm-product-manager（流程）→ java-backend-dev（pda-adapter）
```

重建波次与 MDM 顺序：[scm-product-manager/industry-baseline.md](scm-product-manager/industry-baseline.md)

用法：`@scm-platform-dev …` · [AGENTS.md](../AGENTS.md)

> **跨仓链接**：本文件在 `business-platform/.cursor/skills/`；指向 admin-web / admin-app 用 `../../../{仓库名}/`；子目录 Skill 再多一层 `../`。
