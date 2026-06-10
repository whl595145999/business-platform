# Changelog

## v2.0.0-ai-governance

本版本在 v1 的基础上继续治理 AI Native 研发体系，仍然不修改业务源码。

### Added

- 新增 `.ai/skills/SKILL_ROUTING.md`：统一任务路由与 Skill 编排规则。
- 新增 `.ai/workflows/create-scm-module.md`：供应链模块从需求到交付的标准流程。
- 新增 `.ai/workflows/refactor-existing-module.md`：既有模块重构流程。
- 新增 `.ai/generator/registry.md`：模块生成器产物清单、命名规范、输入参数。
- 新增 `.ai/templates/module/module-spec.md`：模块规格说明模板。
- 新增 `.ai/templates/sql/table-template.sql`：业务表 DDL 模板。
- 新增 `.ai/templates/backend/crud-contract.md`：后端 CRUD 生成契约。
- 新增 `.ai/templates/frontend/page-contract.md`：Admin Web 页面生成契约。
- 新增 `.ai/templates/uniapp/page-contract.md`：移动端页面生成契约。
- 新增 `.ai/knowledge/purchase/order-lifecycle.md`：采购订单生命周期知识。
- 新增 `.ai/knowledge/inventory/stock-ledger.md`：库存流水与可用库存知识。
- 新增 `.ai/memory/decision/0001-ai-native-governance.md`：AI Native 治理决策记录。
- 新增 `docs/adr/0002-ai-governance-v2.md`：v2 架构决策。
- 新增 `docs/development/module-development-guide.md`：模块开发指南。

### Changed

- 强化 `.ai/README.md`：明确 v2 的使用路径与执行顺序。
- 强化 `.ai/skills/README.md`：从“索引”升级为“路由入口”。
- 强化 `.ai/checklists/review-checklist.md`：增加供应链一致性、权限、库存、幂等检查项。
- 强化 `ROADMAP.md`：明确 v3 开始进入真实源码治理。

### Compatibility

- 保留现有 `server/.cursor/skills`、`admin-web/.cursor/skills`、`admin-app/.cursor/skills`。
- 保留现有 `.cursor` / `.trae` 入口。
- 不移动、不删除、不重命名业务源码。
