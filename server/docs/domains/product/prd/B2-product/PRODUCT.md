# 货品主数据 — B2 产品需求（`prd_*`）

> TECH：[B2-product/TECH.md](../../design/B2-product/TECH.md)（架构师 Step 6–7）· [PRD规范.md](../../../../PRD规范.md)

| 项 | 内容 |
| --- | --- |
| 波次 | **B2** |
| 域 | product（`prd_*`） |
| 场景 | 集团多组织 · 供应链履约（零食 + 生鲜 + 可选物料/药品） |
| 状态 | **Step 2 主数据已定稿** · Step 6–7 已定稿（页面/AC） |
| 前置 | B1 `wms_org` 已验收 |

| 标记 | 读者 |
| --- | --- |
| **【业务】** | 集团货品管理员 / 实施 |
| **【产品】** | PM |
| **【研发】** | 开发 / 测试 |

| 名词 | 口径 |
| --- | --- |
| **货品 / SKU** | 供应链作业锚点 **`sku_code`** |
| **SPU** | 销售/catalog；B2 不做 → B6+ |
| **批次号** | B5 入库实例；不在 `prd_sku` 存 |

**脚本**：[prd_category_ddl.sql](../../../../script/sql/product/prd_category_ddl.sql) · [prd_brand_ddl.sql](../../../../script/sql/product/prd_brand_ddl.sql) · [prd_sku_ddl.sql](../../../../script/sql/product/prd_sku_ddl.sql) · [prd_sku_barcode_ddl.sql](../../../../script/sql/product/prd_sku_barcode_ddl.sql) · [prd_unit_code_dict.sql](../../../../script/sql/product/_gen/prd_unit_code_dict.sql) · [prd_tax_rate_dict.sql](../../../../script/sql/product/_gen/prd_tax_rate_dict.sql)

---

## 进度

| Step | 内容 | 状态 |
| --- | --- | --- |
| 1 | 业务建模 | ✅ |
| 2 | 主数据（4 表 + 2 dict） | ✅ |
| 3～5 | 单据 / 库存 / 流程 | ⏭ 跳过（B2 纯 MDM，无单据/库存） |
| 6 | 页面 / 权限 | ✅ 见 §3 |
| 7 | 验收标准 AC | ✅ 见 §4 |
| 8 | 风险 | —（实现前补） |

---

## Step 1 — 业务建模

功能级 · B2 供应链货品主数据（非 C 端卖场）。

### 1.1 参与角色

| 角色 | 职责 |
| --- | --- |
| 集团货品管理员 | 维护类目、品牌、SKU |
| 集团管理员 | 同上；超管切租户 |
| 组织仓储管理员 | 查询货品（B5 前只读） |
| 组织销售运营 | 查 `sku_code` 下单（B6） |

### 1.2 业务对象

```text
prd_category ──┐
prd_brand ─────┼──► prd_sku（sku_code）
               │         └─ prd_sku_barcode
               │
类目、品牌并列；SKU 上分别 FK
```

| 对象 | B2 |
| --- | --- |
| 类目 / 品牌 / SKU | ✅ |
| SPU | ❌ → B6+ |
| 批次 / 库存 | ❌ → B4 / B5 |

**【产品】** 入出库、库存、订单行 **只引用 `sku_code`**。

### 1.3 业务动作

维护类目 / 品牌 / SKU（CRUD、启停）；查询 SKU（列表、编码、下拉）。

### 1.4 上下游

```text
B1 wms_org → B2 prd_* → B3 仓 → B4 库存 → B5 入出库 → B6 订单
```

跨域 **`ProductFacade`**：`getSku` · `validateSku` · `resolveBarcode` · `searchSku`。

### 1.5 四流

| 流 | B2 |
| --- | --- |
| 业务流 | 集团统一货品编码库 |
| 物流流 | 实物走 WMS；虚拟不走仓 |
| 资金流 | 档案默认税率；**不计税、不开票** |
| 信息流 | 档案固化类型；SPU 展示另建 |

### 1.6 本期范围

| 做 | 不做 |
| --- | --- |
| 类目、品牌、SKU、默认财税 | SPU、BOM、实时计税、产地白名单 |
| 实物 / 虚拟 | 服务类 SKU、PDA |
| 编码唯一、启停 | 库存、批次实例 |

### 1.7 履约规则（实物）

| 规则 | 说明 |
| --- | --- |
| **必有批次** | 实物入库均须 `batch_no`（B5） |
| **`expiry_flag=1`** | **效期品**；B2 必填保质期；B5 强制到期 + FEFO |
| **`expiry_flag=0`** | **非效期品**；B2 效期四列为 **`0`**；仅 batch |

有保质期（生鲜、零食、药品）→ **`expiry_flag=1`**；玩具、包材 → **`0`**。

---

## Step 2 — 主数据

码表：[CODE_CONVENTION.md](../../../../CODE_CONVENTION.md)

### 2.1 MDM 清单

| 表 | 业务列 | 依赖 | DDL |
| --- | --- | --- | --- |
| `prd_category` | 14 | B1 | [prd_category_ddl.sql](../../../../script/sql/product/prd_category_ddl.sql) |
| `prd_brand` | 5 | — | [prd_brand_ddl.sql](../../../../script/sql/product/prd_brand_ddl.sql) |
| **`prd_sku`** | **27** | 类目 | [prd_sku_ddl.sql](../../../../script/sql/product/prd_sku_ddl.sql) |
| `prd_sku_barcode` | 5 | SKU | [prd_sku_barcode_ddl.sql](../../../../script/sql/product/prd_sku_barcode_ddl.sql) |

每张表 +8 审计列。dict：`prd_unit_code`、`prd_tax_rate`。列默认见 [DATABASE.md](../../../../DATABASE.md) §3.5（`BIGINT`/`INT`/`SMALLINT`/`TINYINT` → **`0`** · `VARCHAR` → **`''`**）。

实施顺序：`prd_category` ∥ `prd_brand` → `prd_sku` → `prd_sku_barcode`

### 2.2 `prd_sku` — 设计要点

#### 三根轴

| 轴 | 字段 |
| --- | --- |
| 分栏 | `category_id` |
| 企业角色 | `item_class` + 可采/可售/可领 |
| 仓配 | `product_type` + **`expiry_flag`** / **`quota_flag`** |

#### 履约字段

| 字段 | 类型 | 适用 | 含义 |
| --- | --- | --- | --- |
| **`expiry_flag`** | TINYINT 1/0 | **实物** | **是否效期品**；`1` → V-05 效期数值 >0 |
| **`quota_flag`** | TINYINT 1/0 | **虚拟** | **是否配额管理**；`0` 不限量 · `1` 配额 |

| product_type | expiry_flag | quota_flag | B4/B5 |
| --- | --- | --- | --- |
| **10 实物** | **0 或 1** | **必须为 0** | 均有 batch；`1` 时 +到期/FEFO |
| **20 虚拟** | **必须为 0** | **0 或 1** | 不走 WMS 实物账 |

**【产品】** 界面实物只展示 **「是否效期」开关** → 写 `expiry_flag`；选「是」则效期 Tab 必填。

#### 数据来源

| 类型 | 字段 |
| --- | --- |
| dict | `unit_code` → `prd_unit_code`；`input_tax_rate` / `output_tax_rate` → **`prd_tax_rate`** |
| TINYINT 1/0 | `expiry_flag`、`quota_flag`、三能力、`status` |
| Java 枚举 | `item_class`、`product_type`、`barcode_policy` |
| 主数据 FK | `category_id`、`brand_id` |

### 2.3 `prd_sku` — 表职责

| 项 | 值 |
| --- | --- |
| 表 | `prd_sku` |
| 模块 | `whl-scm-product-biz` |
| 锚点 | `sku_code` |
| 范围 | 集团货品库，不挂 `wms_org` |

| 存 | 不存 |
| --- | --- |
| 是否效期、履约类型、默认保质期 | 库存、批次实例、定价 |
| 条码策略 + 冗余默认码 | 条码明细、BOM |

**【研发】** 他域只调 `ProductFacade`。

### 2.4 `prd_sku` — 表结构（27 业务 + 8 审计 = 35 列）

| 分组 | 列 | 字段 |
| --- | --- | --- |
| 标识 | 7 | `sku_code` … `remark` |
| 分类 | 3 | `category_id`, `brand_id`, `unit_code` |
| 能力 | 4 | `item_class`, 三能力 |
| 履约 | 3 | `product_type`, **`expiry_flag`**, **`quota_flag`** |
| 效期 | 4 | `shelf_life_*`, `near_expiry_*` |
| 产地 | 1 | **`default_origin_code`** |
| 财税 | 3 | `tax_*` |
| 条码 | 2 | `barcode_policy`, `primary_barcode` |
| 审计 | 8 | 标准字段 |

**扩展（+9，按需）**：物流 6 + 药品 3。

### 2.5 `prd_sku` — 字段说明

审计字段同 [DATABASE.md](../../../../DATABASE.md) §3.1。

#### 标识 · 分类 · 能力

| 字段 | 类型 | 默认 | 说明 |
| --- | --- | --- | --- |
| `sku_code` | VARCHAR(64) | `''` | 必填；租户内唯一 |
| `sku_name` | VARCHAR(256) | `''` | 必填 |
| `sku_short_name` | VARCHAR(64) | `''` | PDA/标签短名 |
| `spec` | VARCHAR(256) | `''` | 规格 |
| `status` | TINYINT | 1 | 1 启用 · 0 停用 |
| `sort_order` | INT | 0 | 排序 |
| `remark` | VARCHAR(500) | `''` | 备注 |
| `category_id` | BIGINT | 0 | 必填；关联 `prd_category` |
| `brand_id` | BIGINT | 0 | 可选；**0** = 未选 |
| `unit_code` | VARCHAR(32) | `''` | 必填；dict `prd_unit_code` |
| `item_class` | SMALLINT | 0 | 10 贸易 · 20 物料 · 30 包材 · 40 药品 · 50 虚拟 |
| `purchase_allowed` | TINYINT | 1 | 可采购 |
| `sale_allowed` | TINYINT | 1 | 可销售 |
| `issue_allowed` | TINYINT | 0 | 可领料 |

#### 履约

| 字段 | 类型 | 默认 | 说明 |
| --- | --- | --- | --- |
| `product_type` | SMALLINT | 0 | 10 实物 · 20 虚拟 |
| **`expiry_flag`** | TINYINT | 0 | **是否效期品**：**1** 是 · **0** 否；**仅实物** |
| **`quota_flag`** | TINYINT | 0 | **是否配额**：**1** 是 · **0** 否；**仅虚拟** |

#### 效期（仅 `expiry_flag=1`）

| 字段 | 类型 | 默认 | 说明 |
| --- | --- | --- | --- |
| `shelf_life_value` | INT | 0 | 保质期数值；**`expiry_flag=1` 时须 >0** |
| `shelf_life_unit` | SMALLINT | 0 | 10 小时 · 20 天；**`expiry_flag=1` 时须非 0** |
| `near_expiry_value` | INT | 0 | 临期预警；**0**=未设 |
| `near_expiry_unit` | SMALLINT | 0 | 同效期单位；**0**=未设 |

#### 产地 · 财税 · 条码

| 字段 | 类型 | 默认 | 说明 |
| --- | --- | --- | --- |
| **`default_origin_code`** | VARCHAR(32) | `''` | 默认产地；B5 入库预填 |
| `tax_category_code` | VARCHAR(32) | `''` | 税收分类编码 |
| `input_tax_rate` | VARCHAR(32) | `''` | 进项税率 dict；**`''`**=未设 |
| `output_tax_rate` | VARCHAR(32) | `''` | 销项税率 dict；**`''`**=未设 |
| `barcode_policy` | SMALLINT | 20 | 10 必须有码 · 20 可无 |
| `primary_barcode` | VARCHAR(32) | `''` | 默认扫码条码 |

**`barcode_policy` 作用**（B2 档案规则，不存条码明细）：

| 值 | 含义 | 校验 / 作业 |
| --- | --- | --- |
| **10** | **必须有国标码** | 保存时 V-06：子表至少 1 条启用条码；标品零食、药品 |
| **20** | **可无条码** | 子表允许 0 条；生鲜等走搜选 / 本单选行；收货后可打批次签（B5） |

类目 `default_barcode_policy` 新建 SKU 时带入；`primary_barcode` 与子表默认码同步。

#### 扩展（按需 ALTER · +9）

| 字段 | 说明 |
| --- | --- |
| `gross_weight_g` / `net_weight_g` | 毛重/净重（克） |
| `length_mm` / `width_mm` / `height_mm` / `volume_mm3` | 尺寸体积 |
| `drug_license_no` / `drug_approval_no` / `drug_storage_condition` | 药品专档 |

### 2.6 `prd_sku` — 码表与 dict

**`item_class`**：10 贸易 · 20 物料 · 30 包材 · 40 药品 · 50 虚拟（B2 可用）；60 服务 · 70 半成品 → 拒绝（V-08）

**`product_type`**：10 实物 · 20 虚拟

**二值 1/0**：`expiry_flag`、`quota_flag`、`status`、三能力

**枚举**：`barcode_policy` 10/20 · `shelf_life_unit` 10/20 时/天

**dict**：`unit_code` → `prd_unit_code` · `input_tax_rate` / `output_tax_rate` → `prd_tax_rate`（`dict_value` 如 `0.1300`）

**启停分工**：`prd_sku.status` 用 **1/0**；`prd_category` / `prd_brand.status` 用 **10/20**（见 [CODE_CONVENTION.md](../../../../CODE_CONVENTION.md) §1.1）

### 2.7 `prd_sku` — 校验

**B2 保存**

| ID | 规则 |
| --- | --- |
| V-01 | `sku_code` 租户唯一 |
| V-02 | `category_id` ≠ **0** 且类目 `status=10` |
| V-03 | `product_type=10` → `expiry_flag` ∈ {0,1}；**`quota_flag=0`** |
| V-04 | `product_type=20` → **`expiry_flag=0`**；`quota_flag` ∈ {0,1} |
| V-05 | **`expiry_flag=1`** → `shelf_life_value` **>0** 且 `shelf_life_unit` ∈ {10,20} |
| V-15 | **`expiry_flag=0`** → 效期四列均为 **`0`** |
| V-06 | `barcode_policy=10` → 子表 ≥1 条 `status=1`（同 V-BC03） |
| V-07 | 药品 → **`expiry_flag=1`** |
| V-08 | `item_class` ∈ {60, 70} → 拒绝 |
| V-09 | 条码与子表一致 |
| V-10 | `status=0` → 不可引用 |
| V-11 | 虚拟 item_class=50 → `product_type=20` |
| V-12 | 贸易/物料/包材/药品 → `product_type=10` |
| V-13 | `brand_id` ≠ **0** → 品牌 `status=10` |
| V-14 | 临期 < 保质期；临期 **0** 跳过；`near_expiry_value` >0 → `near_expiry_unit` ∈ {10,20} |
| V-16 | 税率非 **`''`** → 须在 dict **`prd_tax_rate`** 中 |
| V-17 | 保存必填：`sku_code`/`sku_name`/`unit_code` 非 **`''`**；`category_id`/`item_class`/`product_type` 非 **0** |
| V-18 | `unit_code` 在 dict `prd_unit_code` 中 |

**B4/B5**

| ID | 规则 |
| --- | --- |
| V-19 | 实物入库 → `batch_no` 必填 |
| V-20 | **`expiry_flag=1`** → 产日/到期、FEFO、临期禁收；**批次产地必填** |

### 2.8 `prd_sku` — 索引与 Facade

`UNIQUE(tenant_id, sku_code)` · `idx(category)` · `idx(status)` · `idx(item_class)` · **`idx(expiry_flag)`** · `idx(primary_barcode)`

| 方法 | 说明 |
| --- | --- |
| `getSku` | 按 `skuCode` 或 `skuId`；Dto + `barcodes[]` |
| `validateSku` | 引用前校验：`status=1`、V-03～04、能力位、品牌启停（V-13）、类目启停（V-02） |
| `resolveBarcode` | 租户内条码 → **`skuCode`**（V-BC05 停用码不参与） |
| `searchSku` | 品名 / `sku_code` / 条码模糊；仅 `status=1` |

### 2.9 `prd_sku` — 类目默认与示例

**类目 → SKU**：`default_item_class`、`default_product_type`、**`default_expiry_flag`**、`default_barcode_policy`、`default_shelf_life_*` …

**类目示例**

| 类目 | class | **效期** | 条码策略 |
| --- | --- | --- | --- |
| 生鲜 | 10 | **1** | 20 可无 |
| 零食 | 10 | **1** | 10 必须有 |
| 玩具 | 10 | **0** | 10 必须有 |
| 包材 | 30 | **0** | 20 可无 |
| 原料 | 20 | 0 或 1 | 20 可无 |
| 药品 | 40 | **1** | 10 必须有 |

### 2.10 `prd_sku` — DDL

见 [prd_sku_ddl.sql](../../../../script/sql/product/prd_sku_ddl.sql)。

---

### 2.11 `prd_category` — 货品类目树

#### 2.11.1 表职责

| 项 | 值 |
| --- | --- |
| 表 | `prd_category` |
| 锚点 | `category_code` |
| 范围 | 集团类目树；**不挂** `wms_org` |

| 存 | 不存 |
| --- | --- |
| 树形分栏、启停 | SKU 实例、库存 |
| **新建 SKU 默认策略**（`default_*`） | 条码明细 |

**【产品】** 类目 = 导航 + 报表 + **默认规则模板**；SKU 保存后可改，不必与类目默认一致。

#### 2.11.2 表结构（14 业务 + 8 审计）

| 分组 | 字段 |
| --- | --- |
| 标识 | `category_code`, `category_name`, `parent_id`, `status`, `sort_order`, `remark` |
| 默认策略 | `default_item_class`, `default_product_type`, **`default_expiry_flag`**, `default_barcode_policy` |
| 默认效期 | `default_shelf_life_value/unit`, `default_near_expiry_value/unit` |
| 审计 | 标准 8 列 |

#### 2.11.3 字段说明

审计字段同 [DATABASE.md](../../../../DATABASE.md) §3.1。

| 字段 | 类型 | 默认 | 说明 |
| --- | --- | --- | --- |
| `category_code` | VARCHAR(64) | `''` | 必填；租户内唯一 |
| `category_name` | VARCHAR(128) | `''` | 必填 |
| `parent_id` | BIGINT | 0 | 上级类目 `id`；**0 = 顶级**（对齐 B1 `parent_org_id`） |
| `status` | SMALLINT | 10 | **10** 启用 · **20** 停用 |
| `sort_order` | INT | 0 | 排序 |
| `remark` | VARCHAR(500) | `''` | 备注 |
| `default_item_class` | SMALLINT | 0 | 新建 SKU 继承；**0**=不继承 |
| `default_product_type` | SMALLINT | 0 | 10 实物 · 20 虚拟；**0**=不继承 |
| **`default_expiry_flag`** | TINYINT | 0 | **1** 效期 · **0** 非效期 |
| `default_barcode_policy` | SMALLINT | 0 | 10/20；**0**=不继承 |
| `default_shelf_life_value` | INT | 0 | `default_expiry_flag=1` 时建议 >0 |
| `default_shelf_life_unit` | SMALLINT | 0 | 10 小时 · 20 天 |
| `default_near_expiry_value` | INT | 0 | 临期预警 |
| `default_near_expiry_unit` | SMALLINT | 0 | 同效期单位 |

#### 2.11.4 树示例

```text
食品 (FOOD)
├── 生鲜 (FRESH)     default_expiry_flag=1
├── 零食 (SNACK)     default_expiry_flag=1 · default_barcode_policy=10
└── 玩具 (TOY)       default_expiry_flag=0
包材 (PACK)          default_item_class=30 · default_expiry_flag=0
原料 (MAT)           default_item_class=20
药品 (DRUG)          default_item_class=40 · default_expiry_flag=1
```

#### 2.11.5 校验

| ID | 规则 |
| --- | --- |
| V-C01 | `category_code` 租户唯一 |
| V-C02 | `parent_id` 无环；顶级为 **0** |
| V-C03 | `status=20` → 不可新建 SKU（V-02） |
| V-C04 | 有子节点或被 SKU 引用 → 禁止物理删除 |
| V-C05 | **`default_expiry_flag=1`** → `default_shelf_life_value` **>0** 且 `default_shelf_life_unit` ∈ {10,20} |

#### 2.11.6 索引与 DDL

`UNIQUE(tenant_id, category_code)` · `idx(tenant_id, parent_id)` · `idx(tenant_id, status)`

见 [prd_category_ddl.sql](../../../../script/sql/product/prd_category_ddl.sql)。

---

### 2.12 `prd_brand` — 品牌

#### 2.12.1 表职责

| 项 | 值 |
| --- | --- |
| 表 | `prd_brand` |
| 锚点 | `brand_code` |
| 关联 | `prd_sku.brand_id` 可选 FK |

| 存 | 不存 |
| --- | --- |
| 品牌编码、名称、启停 | 类目归属（SKU 上选 brand） |

#### 2.12.2 表结构（5 业务 + 8 审计）

| 字段 | 类型 | 默认 | 说明 |
| --- | --- | --- | --- |
| `brand_code` | VARCHAR(64) | `''` | 必填；租户内唯一 |
| `brand_name` | VARCHAR(128) | `''` | 必填 |
| `status` | SMALLINT | 10 | **10** 启用 · **20** 停用 |
| `sort_order` | INT | 0 | 排序 |
| `remark` | VARCHAR(500) | `''` | 备注 |

#### 2.12.3 校验

| ID | 规则 |
| --- | --- |
| V-B01 | `brand_code` 租户唯一 |
| V-B02 | `status=20` → 新建 SKU 不可选 |
| V-B03 | 被 SKU 引用 → 禁止物理删除 |

#### 2.12.4 索引与 DDL

`UNIQUE(tenant_id, brand_code)` · `idx(tenant_id, status)`

见 [prd_brand_ddl.sql](../../../../script/sql/product/prd_brand_ddl.sql)。

---

### 2.13 `prd_sku_barcode` — 条码注册

#### 2.13.1 表职责

| 项 | 值 |
| --- | --- |
| 表 | `prd_sku_barcode` |
| 关系 | **1 SKU : N 条码** |
| 权威 | 条码占用、启停、解析 |

| 存 | 不存 |
| --- | --- |
| 扫码串 → `sku_id`、默认码、单码启停 | 箱规换算、批次签（B5） |

**【产品】** 扫条码认 SKU；数量默认 1、手改；整箱收货走搜选 / 本单选行 + 录数。

**【研发】** `resolveBarcode(barcode)` → **`skuCode`**；`getSku` 附带 `barcodes[]`；`default_flag=1` 同步 `prd_sku.primary_barcode`。

#### 2.13.2 表结构（5 业务 + 8 审计）

| 字段 | 类型 | 默认 | 说明 |
| --- | --- | --- | --- |
| `sku_id` | BIGINT | 0 | 必填；→ `prd_sku.id` |
| `barcode` | VARCHAR(32) | `''` | 必填；**租户内全局唯一**；保留前导 0 |
| `default_flag` | TINYINT | 0 | **1** 默认扫码 · **0** 否；每 SKU 最多 **1** 条为 1 |
| `status` | TINYINT | 1 | **1** 启用 · **0** 停用 |
| `remark` | VARCHAR(500) | `''` | 备注 |

#### 2.13.3 校验

| ID | 规则 |
| --- | --- |
| V-BC01 | `barcode` 租户内唯一（跨 SKU 不可重复） |
| V-BC02 | 同一 `sku_id` 最多一条 `default_flag=1` |
| V-BC03 | `barcode_policy=10` → 至少 1 条 `status=1` |
| V-BC04 | `default_flag=1` → 写回 `prd_sku.primary_barcode` |
| V-BC05 | `status=0` → 不参与 `resolveBarcode` |
| V-09 | 主表 `primary_barcode` 须与子表默认码一致 |

#### 2.13.4 索引与 DDL

`UNIQUE(tenant_id, barcode)` · `idx(tenant_id, sku_id)` · `idx(tenant_id, sku_id, default_flag)`

见 [prd_sku_barcode_ddl.sql](../../../../script/sql/product/prd_sku_barcode_ddl.sql)。

#### 2.13.5 示例

```text
sku_code = SKU-CHIP-40G
  6901234567890   default_flag=1  status=1
  6901234567891   default_flag=0  status=1
```

无条码生鲜：`barcode_policy=20`，子表 0 条；入库搜选 / 本单选行（B5 可打批次签）。

---

## Step 6 — 页面与权限

> 技术路径见 [B2-product/TECH.md](../../design/B2-product/TECH.md) · UI 细节可 `@senior-ui-ux-designer` 增量 Step 3–4。

### 3.1 页面列表

> 菜单均挂 **1700 供应链**，与 **1708 组织档案** 同级（无中间目录）。

| menu_id | 菜单名 | path | component | 端 | 实现 |
| --- | --- | --- | --- | --- | --- |
| 1711 | 货品类目 | `product/categories` | `scm/product/category/index` | PC | 树选 + 分页表 |
| 1712 | 品牌档案 | `product/brands` | `scm/product/brand/index` | PC | CRUD |
| 1713 | 货品 | `product/skus` | `scm/product/sku/index` | PC | CRUD + 条码子表 |
| — | 货品（移动） | — | `pages-sub/scm/product/sku/index` | 移动 | `generate-crud-module`（可选） |

**【研发】** 条码 **`prd_sku_barcode`** 在 **货品** 页子表维护，不单独菜单。API 前缀 `scm:product:*`。

### 3.2 PC — 货品（核心）

| 区域 | 内容 |
| --- | --- |
| 查询区 | `skuCode`、`skuName`、`categoryId`（树）、`itemClass`、`productType`、`status` |
| 表格 | skuCode、skuName、categoryName、itemClass、productType、expiryFlag、status |
| 表单 Tab | **基础**（编码/名称/类目/品牌/单位/类型/能力）· **履约**（productType、expiry/quota、效期四列）· **财税** · **条码**（policy + 子表 grid） |

**【产品】** `product_type=10` 显示效期开关；`=20` 显示配额开关。类目选中后 **带入** `default_*`，可改。

### 3.3 权限点（示例）

| 权限标识 | 说明 |
| --- | --- |
| `scm:product:category:*` | 类目 CRUD |
| `scm:product:brand:*` | 品牌 CRUD |
| `scm:product:sku:list/query/add/edit/remove` | 货品 |

**【业务】** 集团货品管理员 full；组织仓储管理员 B2 只读（B5 前）。

---

## Step 7 — 验收标准

| 编号 | Given | When | Then |
| --- | --- | --- | --- |
| AC-PRD-C01 | 类目编码唯一 | 新增重复 `category_code` | 拒绝；提示编码已存在 |
| AC-PRD-C02 | 类目树 | 改 `parent_id` 成环 | 拒绝保存 |
| AC-PRD-S01 | 效期品 | `expiry_flag=1` 且保质期为空/0 | 拒绝；提示填写保质期 |
| AC-PRD-S02 | 非效期品 | `expiry_flag=0` | 效期四列保存为 **0** |
| AC-PRD-S03 | 条码策略 10 | 子表 0 条启用码 | 拒绝保存 |
| AC-PRD-S04 | 条码解析 | 扫启用码 | `resolveBarcode` → 正确 `skuCode` |
| AC-PRD-S05 | 停用 SKU | `status=0` | `validateSku` 失败；下拉不可选 |
| AC-PRD-S06 | 跨域引用 | OMS 下单前 | `validateSku` 校验能力与启停 |
| AC-PRD-S07 | 租户隔离 | A 租户 SKU | B 租户不可见/不可引用 |

**追溯**：V-01～V-18 · V-C* · V-B* · V-BC* → 上表 + TECH 错误码。

---
