# SCM 数字码约定

> 全平台 **状态、类型** 字段存库用 **SMALLINT**，禁止 VARCHAR 枚举名；禁止业务代码写裸数字，必须走 Java 枚举。

## 1. 步长 10（可扩展）

| 类别 | 规则 | 示例 |
| --- | --- | --- |
| **单据/订单 status** | 从 **10** 起，步长 **10** | 10 草稿 · 20 盘点中 · 30 已审核 |
| **业务 type** | 从 **10** 起，步长 **10** | 10 采购入库 · 20 其他入库 |
| **主数据启用 status** | 步长 **10** | 10 启用 · 20 停用 · 30 筹建 |
| **中间插入** | 占用空档 | 15=部分收货（未来） |

**禁止** 1、2、3 连续编号（不利于扩展）。

### 1.1 二值字段（是否 / 允许否）

纯 **是/否**、**允许/不允许**（无第三态、无扩展档）用 **`TINYINT`**：

| 值 | 含义 |
| --- | --- |
| **1** | 是 / 允许 |
| **0** | 否 / 不允许 |

示例：`purchase_allowed`、`sale_allowed`、`issue_allowed`、`bom_parent_flag`、`default_flag`（子表）。

**与步长 10 的分工**：`status`、`item_class`、`product_type` 等多档枚举仍用 §1 步长 10；**禁止** 把二值字段写成 `10/20`。

DDL COMMENT 示例：`可采购：1-是,0-否`。

## 2. 仓型两段码（百位归属）

| 字段 | 规则 |
| --- | --- |
| `warehouse_category` | 10 物理 · 20 逻辑 · 30 虚拟 |
| `warehouse_subtype` | **百位 = category/10**；个位步长 10 |

示例：category=10 → subtype **110** DC、**150** RETURN；category=30 → **310** EC。

## 3. 码表索引

| 文档 | 内容 |
| --- | --- |
| B1 组织码表 | [PRODUCT.md](./domains/wms/prd/B1-org/PRODUCT.md) §3（`org_type`、`status`） |
| B2 货品码表 | [PRODUCT.md](./domains/product/prd/B2-product/PRODUCT.md) Step 2 §2.6 |
| B2 计量单位 dict | [prd_unit_code_dict.sql](./script/sql/product/_gen/prd_unit_code_dict.sql) |
| B2 默认税率 dict | [prd_tax_rate_dict.sql](./script/sql/product/_gen/prd_tax_rate_dict.sql) |
| DDL 列默认 | [DATABASE.md](./DATABASE.md) §3.5（`BIGINT`/`INT`/`SMALLINT`/`TINYINT` → `0` · `VARCHAR` → `''`） |

## 4. 仍为字符串（例外）

| 字段 | 原因 |
| --- | --- |
| `warehouse_code`、`org_code`、`sku_code` | 业务编码 |
| `channel_code` | 外部渠道标识 |

## 5. DDL 字段 COMMENT 写法

MySQL `COMMENT` 只给人看表结构用，**权威定义仍在 Java 枚举 + 码表文档**。行业常见三种写法：

| 写法 | 示例 | 常见于 |
| --- | --- | --- |
| **码-含义，逗号分隔** | `10-草稿,20-盘点中,30-待审核` | 推荐（本仓库 SCM 表） |
| 码=含义 | `10=草稿,20=盘点中` | 部分代码生成器、运维脚本 |
| 码+含义（空格） | `0正常 1停用` | 若依/RuoYi 原生 `sys_*` 表 |

**本仓库约定**：枚举字段 COMMENT 用 **`码-中文,码-中文`**；含义较长时加字段前缀，如 `仓库大类：10-物理仓,20-逻辑仓`；项过多（如 subtype）写常用项 +「完整见当期 PRD 码表章或 `_gen/*_dict.sql`」。

**不建议** 把 COMMENT 当唯一文档：界面展示、API 文档以 Java 枚举 `code` + `label` 为准。

## 6. Java 实现

```java
public enum InboundStatus {
    DRAFT(10, "草稿"),
    CONFIRMED(20, "已确认");
    private final int code;
}
```

- DB 读写用 `code`  
- 界面用 `label`  
- **不用** `sys_dict`（除非运营要强配）
