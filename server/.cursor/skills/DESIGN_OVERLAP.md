# 设计类 Skill Step 对照表

避免 `scm-product-manager`、`senior-ui-ux-designer` 与 `ecommerce-scm-architect` 重复输出同一内容。

| # | scm-product-manager | senior-ui-ux-designer | ecommerce-scm-architect | 唯一主责 |
| --- | --- | --- | --- | --- |
| 1 | Business Model（四流） | Information Architecture | 业务分析 | 供应链用 **产品**；IA 用 **UI/UX** |
| 2 | Master Data | User Flow | 主数据设计 | **产品** 字段 → **UI/UX** 交互 → **架构师** 表 |
| 3 | Document Design | Wireframe | — | **产品** 单据 · **UI/UX** 线框 |
| 4 | Inventory Model | Component Design | 库存流程 | **产品** 公式 · **UI/UX** 组件 |
| 5 | Business Flow | Design System | 业务流程设计 | **产品** 流程 · **UI/UX** token |
| — | — | Responsive / A11y | 领域模型 DDD | **UI/UX** 多端 · **架构师** DDD |
| — | — | Developer Handoff | 状态机 | **UI/UX** 交付包 · **架构师** 码表 |
| 6 | PRD（页面列表+字段） | （承接 PRD 细化） | 数据库设计 | **产品** 业务页 · **架构师** ER |
| 7 | AC（GWT） | （供 AC 交互补充） | 接口设计 | **产品** AC · **架构师** API |
| 8 | 风险（业务） | — | 系统架构 + 异常场景 | 各写各层 |
| — | — | — | PRD 技术章（Step 10） | **架构师** 合并入 `docs/` |

**规则**：同一需求只选一个「主 Skill」；UI/UX 在 **PRD Step 6 已定** 后从 Step 1 起；架构师从 Handoff 步起跳。
