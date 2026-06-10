# 货品主数据 — 技术设计（B2 · `prd_*`）

| 项 | 内容 |
| --- | --- |
| 文档类型 | **技术设计**（架构师 Step 6–7） |
| 波次 | **B2** |
| 域 | `product` |
| 产品 PRD | [PRODUCT.md](../../prd/B2-product/PRODUCT.md) |
| 关联 | [DATABASE.md](../../../../DATABASE.md) · [API_CONTRACT.md](../../../../API_CONTRACT.md) · [CODE_CONVENTION.md](../../../../CODE_CONVENTION.md) |

---

## 14. 研发技术章 — 数据库设计（架构师 Step 6）

### 14.1 模块归属

| 项 | 约定 |
| --- | --- |
| 域 | Product 主数据 |
| Maven | `whl-scm-product-api` · `whl-scm-product-biz` · `whl-scm-product-admin-adapter` |
| 聚合 | `Category` · `Brand` · `Sku`（含 `SkuBarcode` 实体） |
| 表 | `prd_category` · `prd_brand` · `prd_sku` · `prd_sku_barcode` |
| 菜单 | **1700** 供应链下同级：**1708** 组织档案 · **1711** 货品类目 · **1712** 品牌档案 · **1713** 货品 |

B2 **无** Event 发布、**无** PDA/OpenAPI（B8 再补现场扫码增强）。

### 14.2 ER 关系

```mermaid
erDiagram
    prd_category ||--o{ prd_category : "parent_id"
    prd_category ||--o{ prd_sku : "category_id"
    prd_brand ||--o{ prd_sku : "brand_id optional"
    prd_sku ||--o{ prd_sku_barcode : "sku_id"
    prd_category {
        bigint id PK
        varchar category_code UK
        bigint parent_id "0=顶级"
        smallint status "10/20"
        tinyint default_expiry_flag
    }
    prd_brand {
        bigint id PK
        varchar brand_code UK
        smallint status "10/20"
    }
    prd_sku {
        bigint id PK
        varchar sku_code UK
        bigint category_id
        bigint brand_id "0=未选"
        smallint product_type
        tinyint expiry_flag
        tinyint quota_flag
        smallint barcode_policy
        varchar primary_barcode
    }
    prd_sku_barcode {
        bigint id PK
        bigint sku_id
        varchar barcode UK_tenant
        tinyint default_flag
        tinyint status "1/0"
    }
```

**【研发】** 不建 DB 外键；`category_id` / `brand_id` / `sku_id` 由 AppService 校验（PRD V-02、V-13、V-BC*）。

### 14.3 DDL

列默认 [DATABASE.md](../../../../DATABASE.md) §3.5。权威脚本：

| 表 | 脚本 |
| --- | --- |
| `prd_category` | [prd_category_ddl.sql](../../../../script/sql/product/prd_category_ddl.sql) |
| `prd_brand` | [prd_brand_ddl.sql](../../../../script/sql/product/prd_brand_ddl.sql) |
| `prd_sku` | [prd_sku_ddl.sql](../../../../script/sql/product/prd_sku_ddl.sql) |
| `prd_sku_barcode` | [prd_sku_barcode_ddl.sql](../../../../script/sql/product/prd_sku_barcode_ddl.sql) |

dict：`prd_unit_code` · `prd_tax_rate`（`script/sql/product/_gen/`）。

### 14.4 索引说明

| 表 | 键 | 用途 |
| --- | --- | --- |
| `prd_category` | `uk(tenant_id, category_code)` | V-C01 |
| `prd_category` | `idx(tenant_id, parent_id)` | 树展开 |
| `prd_brand` | `uk(tenant_id, brand_code)` | V-B01 |
| `prd_sku` | `uk(tenant_id, sku_code)` | V-01 · 跨域锚点 |
| `prd_sku` | `idx(tenant_id, category_id)` | 列表筛选 |
| `prd_sku` | `idx(tenant_id, expiry_flag)` | B5 效期品筛选 |
| `prd_sku` | `idx(tenant_id, primary_barcode)` | 冗余码查询；大量 `''` 时 B5 以子表为准 |
| `prd_sku_barcode` | `uk(tenant_id, barcode)` | V-BC01 · `resolveBarcode` |
| `prd_sku_barcode` | `idx(tenant_id, sku_id, default_flag)` | 默认码 |

### 14.5 容量

| 场景 | 行数 | 说明 |
| --- | --- | --- |
| 单租户 SKU | &lt; 5 万 | 零食+生鲜+物料 |
| 条码/SKU | ≈ 1～3 | 国标码 + 备用码 |
| 合计 | &lt; 20 万条码 | btree 足够 |

### 14.6 错误码（Product 域）

| 码 | 场景 | PRD |
| --- | --- | --- |
| `PRD_SKU_CODE_DUPLICATE` | 编码重复 | V-01 · AC-PRD-S* |
| `PRD_SKU_VALIDATION` | 保存校验失败（效期/类型/条码等） | V-03～V-18 |
| `PRD_SKU_DISABLED` | `status=0` 或能力不允许 | V-10 · AC-PRD-S05 |
| `PRD_SKU_NOT_FOUND` | id/code 不存在 | — |
| `PRD_CATEGORY_CODE_DUPLICATE` | 类目编码重复 | V-C01 |
| `PRD_CATEGORY_CYCLE` | 上级成环 | V-C02 · AC-PRD-C02 |
| `PRD_CATEGORY_HAS_CHILDREN` | 有子节点删除 | V-C04 |
| `PRD_BRAND_CODE_DUPLICATE` | 品牌编码重复 | V-B01 |
| `PRD_BARCODE_DUPLICATE` | 条码租户内重复 | V-BC01 |
| `PRD_BARCODE_NOT_FOUND` | 扫码无匹配 | AC-PRD-S04 反例 |

**Handoff** → `@mysql-dba`（DDL 已落盘）· `@scm-crud-generate` ×3 + SKU 子表 MERGE。

---

## 15. 研发技术章 — 接口设计（架构师 Step 7）

### 15.1 Facade 分层

| Facade | 模块 | 调用方 |
| --- | --- | --- |
| **`ProductFacade`** | `whl-scm-product-api` | OMS / WMS / Inventory（跨域只读+校验） |
| `CategoryFacade` | 同上 | admin-web 类目页 |
| `BrandFacade` | 同上 | admin-web 品牌页 |
| `SkuFacade` | 同上 | admin-web 货品页（含条码子表） |

### 15.2 `ProductFacade`（跨域 · 同步）

```java
public interface ProductFacade {
    SkuDto getSku(GetSkuQuery query);
    void validateSku(ValidateSkuQuery query);
    ResolveBarcodeResult resolveBarcode(ResolveBarcodeQuery query);
    ScmPageResult<SkuDto> searchSku(SearchSkuQuery query);
}
```

| 类型 | 类名 | 关键字段 |
| --- | --- | --- |
| Query | `GetSkuQuery` | `tenantId`, `skuId` 或 `skuCode`（二选一） |
| Query | `ValidateSkuQuery` | `tenantId`, `skuCode`, `requiredCapability`（`PURCHASE`/`SALE`/`ISSUE` 可选） |
| Query | `ResolveBarcodeQuery` | `tenantId`, `barcode` |
| Query | `SearchSkuQuery` | `tenantId`, `keyword`, `pageNum`, `pageSize` |
| Dto | `SkuDto` | `skuId`, `skuCode`, `skuName`, `productType`, `expiryFlag`, `quotaFlag`, `barcodePolicy`, `primaryBarcode`, `status`, `barcodes[]` |
| Dto | `SkuBarcodeDto` | `barcode`, `defaultFlag`, `status` |
| Result | `ResolveBarcodeResult` | `skuCode`（仅此字段；数量默认 1 在作业层） |

**【研发】** `validateSku` 抛 `PRD_SKU_*`；不返回 Dto。B5 入库前调 `validateSku` + `getSku` 取效期规则。

### 15.3 Admin Facade（CRUD 摘要）

**CategoryFacade**

```java
ScmPageResult<CategoryDto> pageCategory(PageCategoryQuery query);
List<CategoryDto> listCategoryTree(ListCategoryTreeQuery query);
CategoryDto createCategory(CreateCategoryCommand command);
CategoryDto updateCategory(UpdateCategoryCommand command);
void deleteCategory(DeleteCategoryCommand command);
```

**BrandFacade**：标准分页 CRUD + `listBrandOptions`（`status=10`）。

**SkuFacade**

```java
ScmPageResult<SkuDto> pageSku(PageSkuQuery query);
SkuDto getSkuDetail(GetSkuQuery query);
SkuDto createSku(CreateSkuCommand command);
SkuDto updateSku(UpdateSkuCommand command);
void deleteSku(DeleteSkuCommand command);
```

`CreateSkuCommand` / `UpdateSkuCommand` 含 **`barcodes[]`**；保存时事务内写 `prd_sku` + `prd_sku_barcode`，执行 PRD V-06～V-09、V-BC*，同步 `primary_barcode`。

### 15.4 Admin HTTP

| menu_id | Controller | 路径前缀 | 权限前缀 |
| --- | --- | --- | --- |
| 1711 | `ScmCategoryController` | `/api/scm/product/categories` | `scm:product:category` |
| 1712 | `ScmBrandController` | `/api/scm/product/brands` | `scm:product:brand` |
| 1713 | `ScmSkuController` | `/api/scm/product/skus` | `scm:product:sku` |

货品详情 `GET /api/scm/product/skus/{skuId}` 返回 `SkuVo` + `barcodes[]`。  
新建 SKU 可选 `GET /api/scm/product/categories/{categoryId}/defaults` 返回类目 `default_*`（前端预填）。

**表现层**：`CategoryVo` · `BrandVo` · `SkuVo` · `SkuBarcodeVo` · `SkuSaveRequest`（见 [API_CONTRACT.md](../../../../API_CONTRACT.md) §2.5）。

### 15.5 生成器 `gen_table.options`（标杆）

| 表 | `scmDomain` | `scmResource` | `scmMenuId` | `scmMenuComponent` |
| --- | --- | --- | --- | --- |
| `prd_category` | `product` | `categories` | 1711 | `scm/product/category/index` |
| `prd_brand` | `product` | `brands` | 1712 | `scm/product/brand/index` |
| `prd_sku` | `product` | `skus` | 1713 | `scm/product/sku/index` |

`prd_sku_barcode`：**不单独生成菜单**；SKU zip MERGE 子表 Mapper + 表单 grid。

`scmParentMenuId`：**1700**（供应链，与组织档案同级）。

### 15.6 Event / 三通道

| 通道 | B2 |
| --- | --- |
| Admin | ✅ §15.4 |
| OpenAPI | ❌ |
| PDA | ❌（`resolveBarcode` 供 B8 PDA 经 biz 调 `ProductFacade`） |
| Domain Event | ❌（B6 后可补 `SkuChangedEvent`） |

### 15.7 错误码 → HTTP

| 错误码 | HTTP | 文案示例 |
| --- | --- | --- |
| `PRD_SKU_CODE_DUPLICATE` | 409 | 货品编码已存在 |
| `PRD_SKU_VALIDATION` | 400 | 效期品请填写保质期 / 必须有条码 |
| `PRD_SKU_DISABLED` | 409 | 货品已停用或不可用于当前操作 |
| `PRD_BARCODE_DUPLICATE` | 409 | 条码已被其他货品占用 |
| `PRD_CATEGORY_CYCLE` | 400 | 上级类目不能为自己或下级 |

**Handoff** → `@scm-platform-dev` → `mysql-dba`（验收 DDL）→ `scm-crud-generate` → `frontend-dev` G1–G4。

---

## 17. 系统架构（Step 8 摘要）

| 项 | B2 结论 |
| --- | --- |
| 部署 | 模块化单体 `whl-admin-server` |
| 数据库 | 单库 `business_platform`，表 `prd_*` |
| 缓存/MQ | **不需要** |
| 跨域 | 仅 `ProductFacade`；禁止 OMS Mapper 读 `prd_*` |

```text
admin-web → ScmSkuController → SkuFacade → SkuAppService → prd_sku / prd_sku_barcode
oms-biz   → OrderAppService  → ProductFacade.validateSku / getSku
wms-biz   → InboundAppService → ProductFacade.resolveBarcode / validateSku  (B5+)
```
