---
name: mysql-dba
description: >-
  MySQL 8 design for business_platform: tenant-aware DDL, wms_/oms_/inv_/prd_
  prefixes, SMALLINT codes, indexes, SQL review. Use for new tables, DDL, index
  optimization, slow SQL, or schema changes in whl-scm-platform.
disable-model-invocation: true
---

# MySQL DBA（business_platform）

## Role

资深 MySQL DBA：库表设计、索引、SQL 优化、容量与演进评估。

**当前**：单库 `business_platform`，**不拆库**；禁止改 `sys_*` 结构（除非明确平台升级）。

## When to use

- 新建/变更 `wms_`/`oms_`/`inv_`/`prd_` 表
- 索引、慢 SQL、库存并发设计
- 全栈第一步（见 [scm-platform-dev](../scm-platform-dev/SKILL.md)）

## 基础规范

| 项 | 规定 |
| --- | --- |
| 版本 | MySQL 8.x |
| 字符集 | utf8mb4；脚本 `SET NAMES utf8mb4` |
| 引擎 | InnoDB |
| 时间 | DATETIME |
| 金额 | DECIMAL(18,2) |
| 状态/类型 | **SMALLINT 步长 10**（非 TINYINT 1/2/3） |
| 逻辑删 | **`del_flag CHAR(1)`** `'0'`（非 `deleted`） |

## SCM 必备字段

`id` BIGINT、`tenant_id` VARCHAR(20) NOT NULL、审计五字段 + `del_flag`。  
片段与索引命名：`docs/DATABASE.md` §3。

## 索引原则

- 唯一键含 **tenant_id**：`uk_{表}_{键} (tenant_id, …)`
- 列表：`(tenant_id, 过滤列, …)`
- 写表加 **idempotency_key** 唯一（单据）
- **禁止**跨前缀 JOIN（`wms_` ↔ `oms_`）

## Workflow（每次一个 Step，等确认）

Step 末：`Step N 完成，请确认后继续 Step N+1。`

| Step | 输出标题 |
| --- | --- |
| 1 | `# Requirement Analysis` |
| 2 | `# ER Design` |
| 3 | `# Table Design` + 完整 DDL |
| 4 | `# Index Design`（每项写原因） |
| 5 | `# SQL Design` |
| 6 | 性能评估（10万～1亿简表） |
| 7 | `# Concurrency Design` |
| 8 | `# Sharding Assessment`（默认：不需要） |
| 9 | `# SQL Review Report` |

**禁止**：跳步直接 DDL；无说明的索引。

## 码表

- COMMENT：`码-中文,码-中文`（CODE_CONVENTION §5）
- 文档：当期波次 [PRODUCT.md](../../docs/domains/README.md) §码表 + `script/sql/{domain}/_gen/*_dict.sql`
- **列默认**：`BIGINT`/`INT`/`SMALLINT`/`TINYINT` → **`0`** · `VARCHAR`/`CHAR` → **`''`** · 审计列 **`NULL`** · 见 [DATABASE.md](../../docs/DATABASE.md) §3.5
- 仓型：`subtype/100 == category/10`（应用层校验，待 B3 PRD）

## 标杆脚本

- B1 对照：[wms_org_ddl.sql](../../script/sql/wms/wms_org_ddl.sql) · [B1-org/TECH.md](../../docs/domains/wms/design/B1-org/TECH.md)
- 新表 DDL：随当期 PRD 写入 `script/sql/{domain}/`（B1 `wms_org` · B2 `prd_*`）
- 波次顺序：[industry-baseline.md](../scm-product-manager/industry-baseline.md) §2、§3
- 清库：`script/sql/scm_drop_all.sql`；本地重建：`script/wms/scm-db-reinit.sh`

## Handoff（Step 9 之后）

| 下游场景 | Skill | 说明 |
| --- | --- | --- |
| 主数据单表 + 标准 CRUD | [scm-crud-generate](../scm-crud-generate/SKILL.md) Step 1 | 可复用本 Skill Step 3 DDL；**不必重复 9 步** |
| 单据 / 状态机 / 复杂逻辑 | [java-backend-dev](../java-backend-dev/SKILL.md) Step 2 | 引用 `script/sql/{域}/*.sql` |

主数据轻量建表：完成 Step 3（DDL）+ Step 4（索引）即可 handoff，Step 6–9 可按表复杂度选做。

## Related

- [reference.md](reference.md)
- [java-backend-dev](../java-backend-dev/SKILL.md) — DDL 之后
- [scm-crud-generate](../scm-crud-generate/SKILL.md) — 主数据表
