# Product SQL（B2 货品 `prd_*`）

| 文件 | 说明 |
| --- | --- |
| [prd_category_ddl.sql](./prd_category_ddl.sql) | 类目树 |
| [prd_brand_ddl.sql](./prd_brand_ddl.sql) | 品牌 |
| [prd_sku_ddl.sql](./prd_sku_ddl.sql) | SKU 主数据 |
| [prd_sku_barcode_ddl.sql](./prd_sku_barcode_ddl.sql) | 条码注册 |
| [_gen/prd_unit_code_dict.sql](./_gen/prd_unit_code_dict.sql) | 计量单位 dict |
| [_gen/prd_tax_rate_dict.sql](./_gen/prd_tax_rate_dict.sql) | 默认税率 dict |
| [_gen/prd_product_menu.sql](./_gen/prd_product_menu.sql) | 菜单 1711–1713（挂 1700） |

设计：[B2-product/PRODUCT.md](../../docs/domains/product/prd/B2-product/PRODUCT.md)  
列默认：[DATABASE.md](../../docs/DATABASE.md) §3.5（`BIGINT`/`INT`/`SMALLINT`/`TINYINT` → `0` · `VARCHAR` → `''`）

重建（含 B1 + 菜单）：`../../script/wms/scm-db-reinit.sh --with-menu`
