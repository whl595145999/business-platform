# ADR 0002: AI Governance v2

## 状态

Accepted

## 背景

v1 已建立 `.ai` 基础目录，但部分内容仍偏骨架。v2 需要让 `.ai` 从“目录中心”升级为“可执行开发标准”。

## 决策

v2 增加以下能力：

- Skill Routing：统一任务分发。
- Create SCM Module Workflow：统一新增模块流程。
- Generator Registry：统一代码生成输入和输出。
- Purchase / Inventory 核心业务知识：优先沉淀高风险领域。
- Module Spec Template：生成代码前必须先定义模块规格。

## 不做事项

- 不重构业务源码。
- 不迁移旧 Skill。
- 不修改现有接口。
- 不修改数据库结构。

## 后续

v3 开始进入源码治理，优先处理：

- 重复 API / request / 类型定义。
- 后端 DTO / VO / Convert 规范落地。
- admin-web 与 admin-app 页面模板落地。
- 选一个低风险模块做端到端示例。
