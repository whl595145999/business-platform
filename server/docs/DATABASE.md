# 数据库规范

> 当前策略：**单库 `business_platform`，不拆库，不改 `sys_*` 结构**  
> 远期策略：**表前缀 → Schema 隔离 → 独立库**，三步演进

---

## 1. 当前（阶段 0～4）

| 项 | 规定 |
| --- | --- |
| 库名 | `business_platform`（不改名） |
| 连接 | 沿用 RuoYi 动态数据源 `master` |
| 平台表 | `sys_*`、`flow_*`、`sj_*` 等 **结构不动** |
| SCM 新表 | **同库**，必须带域前缀 |

---

## 2. 表前缀注册表

| 域 | 前缀 | 示例表 | 负责模块 |
| --- | --- | --- | --- |
| Platform | `sys_` | `sys_user` | whl-platform-system |
| Workflow | `flow_` | `flow_definition` | whl-platform-workflow |
| OMS | `oms_` | `oms_order`、`oms_order_line` | whl-scm-oms-biz |
| WMS | `wms_` | `wms_outbound_task`、`wms_location` | whl-scm-wms-biz |
| Inventory | `inv_` | `inv_stock`、`inv_reserve_record` | whl-scm-inventory-biz |
| Product | `prd_` | `prd_category`、`prd_brand`、`prd_sku`、`prd_sku_barcode` | whl-scm-product-biz |
| ERP | `erp_` | `erp_purchase_order` | whl-scm-erp-biz |

**禁止**：无前缀表、跨域共用一张「大宽表」、OMS Mapper 访问 `wms_*` 表。

**DDL 策略（从 0 重建）**：新表 DDL 随各波次 PRD 写入 `script/sql/{domain}/`（B1 `wms_org` · B2 `prd_*`）。波次见 [industry-baseline.md](../.cursor/skills/scm-product-manager/industry-baseline.md)。**状态/类型数字码**见 [CODE_CONVENTION.md](./CODE_CONVENTION.md)（步长 10）。

---

## 3. 新表模板

### 3.1 必备字段（与 RuoYi BaseEntity 对齐）

**每张 SCM 新表必须包含以下字段**（缺一不可，含 `_line` 明细表）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | `BIGINT` | 是 | 主键 |
| `tenant_id` | `VARCHAR(20)` | 是 | 租户编号 |
| `create_dept` | `BIGINT` | 是（列必有） | 创建部门；无则 `NULL` |
| `create_by` | `BIGINT` | 是（列必有） | 创建者 |
| `create_time` | `DATETIME` | 是（列必有） | 创建时间 |
| `update_by` | `BIGINT` | 是（列必有） | 更新者 |
| `update_time` | `DATETIME` | 是（列必有） | 更新时间 |
| `del_flag` | `CHAR(1)` | 是 | 逻辑删除，`0` 正常 |

**标准审计字段 SQL 片段**（建表时放在业务字段之后、索引之前）：

```sql
    create_dept     BIGINT       DEFAULT NULL COMMENT '创建部门',
    create_by       BIGINT       DEFAULT NULL COMMENT '创建者',
    create_time     DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_by       BIGINT       DEFAULT NULL COMMENT '更新者',
    update_time     DATETIME     DEFAULT NULL COMMENT '更新时间',
    del_flag        CHAR(1)      DEFAULT '0' COMMENT '删除标志',
```

实体类继承 RuoYi `BaseEntity`（或项目内等价基类），由框架自动填充 `create_*` / `update_*`。

### 3.2 建表示例

```sql
CREATE TABLE wms_outbound_task (
    id              BIGINT       NOT NULL COMMENT '主键',
    tenant_id       VARCHAR(20)  NOT NULL DEFAULT '' COMMENT '租户编号',
    task_no         VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '任务单号',
    order_no        VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '来源订单号',
    warehouse_code  VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '仓库编码',
    status          VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '任务状态',
    idempotency_key VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '幂等键',
    create_dept     BIGINT       DEFAULT NULL COMMENT '创建部门',
    create_by       BIGINT       DEFAULT NULL COMMENT '创建者',
    create_time     DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_by       BIGINT       DEFAULT NULL COMMENT '更新者',
    update_time     DATETIME     DEFAULT NULL COMMENT '更新时间',
    del_flag        CHAR(1)      DEFAULT '0' COMMENT '删除标志',
    PRIMARY KEY (id),
    UNIQUE KEY uk_wms_outbound_task_no (tenant_id, task_no),
    UNIQUE KEY uk_wms_outbound_idem (tenant_id, idempotency_key),
    KEY idx_wms_outbound_order (tenant_id, order_no)
) ENGINE=InnoDB COMMENT='WMS出库任务';
```

### 3.3 索引命名

```text
PRIMARY KEY
uk_{表名}_{业务键}          # 唯一
idx_{表名}_{查询字段}       # 普通索引
```

### 3.4 实体类

表 `wms_outbound_task` → 实体 `WmsOutboundTask`（仅存在于对应 `*-biz` 的 infrastructure 包）。

### 3.5 列默认值（SCM 新表 · 全局）

**未在 PRD/DDL 中单独指定业务默认时，按类型写死：**

| MySQL 类型 | `DEFAULT` | 说明 |
| --- | --- | --- |
| **BIGINT** | **`0`** | 含可选 FK：`brand_id=0`、`category_id=0` 表示未选 |
| **INT** | **`0`** | 含 `sort_order`、效期数值 |
| **SMALLINT** | **`0`** | 含枚举列；**0 = 未设/不继承**（保存时由校验拒绝或必填） |
| **TINYINT** | **`0`** | 二值/小枚举；见下方「特殊情况」 |
| **VARCHAR / CHAR（业务列）** | **`''`** | 含 `tenant_id`、编码、名称；**不含** `del_flag` |
| **DATETIME / 审计 BIGINT** | **`NULL`** | `create_*` / `update_*`；由框架写入 |

**写法**：`NOT NULL DEFAULT 0` / `NOT NULL DEFAULT ''`；禁止业务列 `DEFAULT NULL`。

**特殊情况（PRD 显式指定，覆盖上表）**：

| 场景 | 默认 | 示例 |
| --- | --- | --- |
| 主键 | 无 DEFAULT | `id` 由雪花/AUTO_INCREMENT 写入 |
| 二值启停默认启用 | `1` | `prd_sku.status`、`prd_sku_barcode.status` |
| 能力默认允许 | `1` | `purchase_allowed`、`sale_allowed` |
| 主数据步长 10 启停 | `10` | `wms_org.status`、`prd_category.status` |
| 树顶级父节点 | `0` | `parent_id`、`parent_org_id`（与类型默认一致） |
| 逻辑删除 | `'0'` | `del_flag`（RuoYi） |
| 条码策略 | `20` | `prd_sku.barcode_policy` 默认可无码 |

**校验层**：用 **`0` / `''` 表示空**；保存时 PRD 必填项仍须校验非空/非 0（如 `sku_code`、`category_id`、`product_type`）。`expiry_flag=0` 时效期四列须为 **`0`**（V-15）。

---

## 4. 分布式演进：三步走

```text
Step 1  单库 + 表前缀（现在）
        business_platform.oms_order
        business_platform.wms_outbound_task

Step 2  单实例多 Schema（可选中间态）
        business_platform_oms.oms_order
        business_platform_wms.wms_outbound_task
        # JDBC URL 仍同一实例，逻辑隔离

Step 3  独立库（拆服务时）
        whl_oms.oms_order
        whl_wms.wms_outbound_task
        # 每个服务只连自己的库，表名可保持不变
```

**原则**：从 Step 1 开始就 **禁止跨前缀 JOIN**，Step 2/3 只是物理搬迁，不改业务代码。

---

## 5. 多租户

- 所有 SCM 新表必须有 `tenant_id`
- 查询必须带租户条件（复用 RuoYi 多租户插件）
- 跨租户数据禁止在同一业务链路混用

---

## 6. 事务边界

| 范围 | 规则 |
| --- | --- |
| 单域写操作 | 同一 `*-biz` 的 AppService 方法内 `@Transactional` |
| 跨域 | **禁止**同一数据库事务；用 Facade 同步 + Event 异步 |
| 事件投递 | 后期 Outbox 表：`oms_outbox` / `wms_outbox`（同前缀域内）

---

## 7. 相关文档

- [API_CONTRACT.md](./API_CONTRACT.md) — Command 幂等键
- [DISTRIBUTED_EVOLUTION.md](./DISTRIBUTED_EVOLUTION.md) — 拆库时机
