# 供应链产品 — 九域与 MVP

## 九域归属（本产品）

> 实施波次与 MDM 顺序见 [industry-baseline.md](industry-baseline.md)。

| 域 | 产品侧主责 Step | 本项目模块 | 波次 |
| --- | --- | --- | --- |
| 用户 | —（平台） | `whl-platform-*` | L0 已有 |
| 商品 | Step 2 | `whl-scm-product` `prd_*` | **B2 已落地** · [PRODUCT.md](../../docs/domains/product/prd/B2-product/PRODUCT.md) |
| 营销 | — | 未建 | — |
| 交易 | Step 3–5 | OMS `oms_*` | B6 |
| 库存 | Step 4–5 | Inventory `inv_*` | B4 |
| 采购 | Step 2–3 | ERP `erp_*` | 远期 |
| 仓储 | Step 2–3 | WMS `wms_*` | B1 组织 · B3 仓库 · B5 单据 |
| 仓储·PDA 现场 | Step 4–5 | `pda-adapter` | B8 |
| 物流 | Step 5 | TMS 未建 | 远期 |
| 售后 | Step 5 逆向 | 规则在 PRD | 远期 |

## 四流分析模板

| 流 | 供应链关注点 |
| --- | --- |
| 业务流 | 谁下单、谁发货、谁收货、谁结账 |
| 物流流 | 仓、运、签、退 |
| 资金流 | 货权、内部结算、对账（二期） |
| 信息流 | 单据状态、库存账、事件通知 |

## 与架构师 Skill 分工

| 供应链产品（本 Skill） | 架构师 |
| --- | --- |
| 单据字段、业务状态、库存口径 | DDD、ER、API、Event、部署 |
| 页面与权限（Step 6） | 三通道 Vo、模块边界 |
| Given-When-Then | 幂等、错误码、异常技术方案 |
