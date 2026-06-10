# 电商供应链架构 — 文档资产索引

新设计须标明相对下表的 **增量 / 变更 / 废弃**。各 Step 产出细则见 [workflow.md](workflow.md)。

| 文档 | 适用 Step | 内容 |
| --- | --- | --- |
| [B1-org/PRODUCT.md](../../docs/domains/wms/prd/B1-org/PRODUCT.md) | 1–5, 9–10 | 组织主数据（已落地标杆） |
| [domains/README.md](../../docs/domains/README.md) | 1, 10 | 域文档索引、波次状态 |
| [ROADMAP.md](../../docs/ROADMAP.md) | 1, 8, 10 | 阶段、MVP 边界 |
| [ARCHITECTURE.md](../../docs/ARCHITECTURE.md) | 2, 8 | 模块与域 |
| [API_CONTRACT.md](../../docs/API_CONTRACT.md) | 7 | Facade、Event、幂等 |
| [DATABASE.md](../../docs/DATABASE.md) | 6 | 表前缀、租户、索引、**§3.5 列默认** |
| [CODE_CONVENTION.md](../../docs/CODE_CONVENTION.md) | 3, 5 | 数字码 |
| [B2-product/PRODUCT.md](../../docs/domains/product/prd/B2-product/PRODUCT.md) | 3, 4, 6 | 货品主数据（B2 已落地） |
| [GENERATOR_SCM.md](../../docs/GENERATOR_SCM.md) | 6–7 后主数据 | 单表 CRUD 生成边界 |
| [DISTRIBUTED_EVOLUTION.md](../../docs/DISTRIBUTED_EVOLUTION.md) | 8 远期 | 拆服务 |

> 仓型码表、单据码表、WMS 域总览等 **待 B3/B5+ PRD 定稿后** 写入对应 `docs/domains/{域}/prd/B{n}-*/PRODUCT.md` 与 `script/sql/{domain}/_gen/*_dict.sql`。

## 行业分层（与实现 Skill 一致）

```text
主数据 → 作业单据 → 库存账 → 渠道可售
```

- 业务波次与 MDM 顺序：[scm-product-manager/industry-baseline.md](../scm-product-manager/industry-baseline.md)（**只引用，不复制**）
- 实现对照：[scm-platform-dev/workflow.md](../scm-platform-dev/workflow.md)

## PRD 结构标杆

以 [B1-org/PRODUCT.md](../../docs/domains/wms/prd/B1-org/PRODUCT.md) 为模板：版本表、三类读者、FR、AC、追溯矩阵、关联 SQL。
