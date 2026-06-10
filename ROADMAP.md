# Roadmap

## v1：AI Governance Bootstrap

- 建立 `.ai` 统一治理中心。
- 建立根级 AI 协作入口。
- 建立 docs / adr / knowledge / specs / templates / checklists 骨架。
- 保持旧 `.cursor` / `.trae` 兼容。

## v2：AI Governance Executable Standard

- Skill Routing 可执行化。
- 新增模块 Workflow 可执行化。
- Generator Registry 建立。
- 采购与库存核心知识补齐。
- 模块规格模板、SQL 模板、前后端页面契约补齐。
- Review Checklist 强化。

## v3：Source Code Governance

目标：开始真实治理源码，但仍避免大规模破坏性重构。

优先级：

1. 选择一个低风险模块做端到端示例。
2. 统一 admin-web API 类型和页面模板。
3. 统一 admin-app API、分页、表单模板。
4. 统一 server DTO / VO / Convert 生成契约。
5. 建立菜单、权限、字典生成清单。

## v4：SCM Module Generator

- 输入 Module Spec。
- 输出 SQL / Backend / Admin Web / Admin App / Docs。
- 支持主子表。
- 支持导入导出。
- 支持状态机。
- 支持库存流水。

## v5：Domain Knowledge Deepening

- purchase：采购订单、采购入库、采购退货。
- inventory：库存余额、库存流水、盘点、调拨。
- warehouse：仓库、库区、库位。
- sales：销售订单、销售出库、销售退货。
- finance：应收、应付、结算。
