# MySQL DBA Reference

## 文档

| 文档 | 路径 |
| --- | --- |
| 数据库规范 | `docs/DATABASE.md` |
| 数字码 | `docs/CODE_CONVENTION.md` |
| 组织 PRD 表 | [B1-org/PRODUCT.md](../../docs/domains/wms/prd/B1-org/PRODUCT.md) §6 |
| 货品 PRD 表 | [B2-product/PRODUCT.md](../../docs/domains/product/prd/B2-product/PRODUCT.md) §2 |
| API 幂等 | `docs/API_CONTRACT.md` |

## 表前缀

| 域 | 前缀 | 模块 |
| --- | --- | --- |
| WMS | `wms_` | whl-scm-wms-biz |
| OMS | `oms_` | whl-scm-oms-biz |
| Inventory | `inv_` | whl-scm-inventory-biz |
| Product | `prd_` | whl-scm-product-biz |

## 审计字段 SQL 片段

```sql
    create_dept     BIGINT       DEFAULT NULL COMMENT '创建部门',
    create_by       BIGINT       DEFAULT NULL COMMENT '创建者',
    create_time     DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_by       BIGINT       DEFAULT NULL COMMENT '更新者',
    update_time     DATETIME     DEFAULT NULL COMMENT '更新时间',
    del_flag        CHAR(1)      DEFAULT '0' COMMENT '删除标志',
```

## 标杆表示例

| 表 | 要点 |
| --- | --- |
| `prd_category` / `prd_brand` / `prd_sku` / `prd_sku_barcode` | 见 [script/sql/product/](../../script/sql/product/) · 列默认 [DATABASE.md](../../docs/DATABASE.md) §3.5 |
| `wms_org` | 见 [wms_org_ddl.sql](../../script/sql/wms/wms_org_ddl.sql) · 列默认 §3.5 |
| `wms_warehouse` | uk(tenant_id, warehouse_code)；category+subtype |
| `wms_inbound_order` | uk 单号 + idempotency_key |
| `inv_stock` | 库存键 + 并发更新 |

## Step 9 检查清单

- 全表扫描 / 缺 tenant_id
- 隐式转换、函数导致索引失效
- 深分页 OFFSET
- 唯一键未含 tenant_id
- status 用 VARCHAR 或 1/2/3 编号
- 跨域 JOIN
