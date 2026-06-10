# 供应链业务平台 — 文档中心

> 作者：**whl** | 产品：**供应链业务平台** | groupId：**`com.whl`** | 根工程：**`whl-scm-platform`**

---

## Cursor Agent Skills

> 自动化开发 / 代码生成 / PR 自检：优先 `@scm-platform-dev` 分流。  
> 索引：[.cursor/skills/README.md](../.cursor/skills/README.md) · 仓指引：[AGENTS.md](../AGENTS.md)

| 场景 | Skill |
| --- | --- |
| 供应链产品（单据/库存） | `scm-product-manager` |
| 互联网产品（故事/原型） | `senior-product-manager` |
| 技术架构（API/ER） | `ecommerce-scm-architect` |
| 全栈实现 | `scm-platform-dev` |
| DDL | `mysql-dba` |
| 主数据生成器（Java + PC） | `scm-crud-generate` |
| 单据后端 | `java-backend-dev` |
| PR Review | `code-review-scm` |

PC / 移动 Skills 分别在 `business-platform-admin-web`、`business-platform-admin-app` 的 `.cursor/skills/`。

---

## 阅读顺序

| 顺序 | 文档 | 说明 |
| --- | --- | --- |
| 0 | [GENERATOR_SCM.md](./GENERATOR_SCM.md) | **代码生成器**（主数据 CRUD）；配合 `scm-crud-generate` Skill |
| 0a | [industry-baseline.md](../.cursor/skills/scm-product-manager/industry-baseline.md) | **重建波次** B1～B8、MDM 顺序（Skill 单点真理） |
| 1 | [ARCHITECTURE.md](./ARCHITECTURE.md) | **总览**：分层、模块、域边界 |
| 2 | [NAMING.md](./NAMING.md) | Maven、包名、类名 |
| 3 | [MODULE_RULES.md](./MODULE_RULES.md) | 依赖红线、PR 检查清单 |
| 4 | [DATABASE.md](./DATABASE.md) | 单库、表前缀、拆库三步走 |
| 5 | [API_CONTRACT.md](./API_CONTRACT.md) | Facade / Event / 幂等 / 错误码 |
| 5a | [PRD规范.md](./PRD规范.md) | **PRD 规范** · 域索引 [domains/README.md](./domains/README.md) |
| 6 | [README.md](./domains/README.md) | **业务域文档**（B1 已落地 · B2 Step 2 已定稿） |
| 6a | [B1-org/](./domains/wms/prd/B1-org/) | **B1 组织** 产品 + [TECH.md](./domains/wms/design/B1-org/TECH.md) |
| 7 | [ROADMAP.md](./ROADMAP.md) | 分阶段实施 |
| 8 | [AUTH_MIGRATION.md](./AUTH_MIGRATION.md) | 平台统一认证（whl-platform-auth） |
| 9 | [DISTRIBUTED_EVOLUTION.md](./DISTRIBUTED_EVOLUTION.md) | 单体 → Dubbo/Cloud 操作手册 |

---

## 核心决策速查

| 项 | 决定 |
| --- | --- |
| Framework | `ruoyi-common` → **`whl-framework-*`** |
| 数据库 | **单库 `business_platform`，不拆**；新表 **`oms_` / `wms_` / `inv_` / `prd_`** |
| 跨域 | 只依赖 **`*-api`** + **Event** |
| RPC | `whl-framework-rpc`：`local` → `dubbo` / `feign` |
| 是否最优 | **当前阶段合理可演进**；分布式细则见 DISTRIBUTED_EVOLUTION |

---

## 文档变更规则

新增业务域、Facade、Event、数据表时，必须同步更新：

1. [API_CONTRACT.md](./API_CONTRACT.md) — 事件注册表  
2. [DATABASE.md](./DATABASE.md) — 表前缀注册表  
3. [DISTRIBUTED_EVOLUTION.md](./DISTRIBUTED_EVOLUTION.md) — 服务名注册表  

PR 勾选 [MODULE_RULES.md](./MODULE_RULES.md) 检查项。

---

## 当前阶段

**阶段 5–6 — 供应链业务落地**

- **B1 组织 `wms_org`**：已验收
- **B2 货品 `prd_*`**：Admin CRUD 已实现
- **B3+** 仓库 / 库存 / OMS：待 PRD 定稿
- 波次：[industry-baseline.md](../.cursor/skills/scm-product-manager/industry-baseline.md) · [ROADMAP.md](./ROADMAP.md) · [domains/README.md](./domains/README.md)
