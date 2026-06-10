# Agent 指引（business-platform）

供应链后端 · Maven `whl-scm-platform` · 单库 `business_platform`。

## Cursor Skills（优先）

设计（不写代码）— [ROLES.md](.cursor/skills/ROLES.md)：

| 定位 | Skill |
| --- | --- |
| **卖** | `@senior-product-manager` |
| **履约** | `@scm-product-manager` |
| **界面** | `@senior-ui-ux-designer` |
| **技术方案** | `@ecommerce-scm-architect` |

实现：`@scm-platform-dev`

| 场景 | Skill |
| --- | --- |
| 建表 / DDL | `@mysql-dba` |
| 主数据代码生成（Java + PC 一页 zip） | `@scm-crud-generate` |
| 单据 / 状态机 / 库存逻辑 | `@java-backend-dev` |
| PR 自检 | `@code-review-scm` |

索引：[.cursor/skills/README.md](.cursor/skills/README.md) · 路由：[.cursor/skills/SKILL_ROUTING.md](.cursor/skills/SKILL_ROUTING.md)

## 硬约束（摘要）

- 表前缀 `wms_`/`oms_`/`inv_`/`prd_` + `tenant_id` + `del_flag`
- 分层：`*-api` Facade → `*-biz` AppService → `*-admin-adapter` Vo
- 跨域仅 `*-api` + Event；不做 COMMENT 自动生成 Java 枚举

文档：[docs/README.md](docs/README.md) · 业务域 PRD：[docs/domains/README.md](docs/domains/README.md) · 生成器：[docs/GENERATOR_SCM.md](docs/GENERATOR_SCM.md)

## 工作区

建议在 monorepo 根或三仓同开，跨仓库 Skill 链接才有效。
