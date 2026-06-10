# 行业全栈开发流程（供应链中台）

> 从 0 设计：`scm-product-manager` / `senior-product-manager` → `senior-ui-ux-designer`（可选）→ `ecommerce-scm-architect`（技术章）→ 定稿后再走下文。

## 1. 业务分层（与 PRD 一致）

```text
主数据（组织、仓库、SKU）
    → 作业单据（入库、出库、订单、盘点）
        → 库存数量（inv_stock、预占、扣减）
            → 渠道可售（虚拟仓映射、Catalog）
```

## 2. 技术分层（本项目）

```text
DDL (mysql-dba)
    → *-api Facade 契约
        → *-biz AppService
            → *-admin-adapter → admin-web
            → *-openapi-adapter / *-pda-adapter（按需手写）
                → admin-app（对齐 PC API）
```

## 3. 何时生成器 vs 手写

| 场景 | 方式 | 行业对照 |
| --- | --- | --- |
| 单表主数据 CRUD | scm-crud-generate（一次 zip = Java + PC） | 主数据 MDM 配置页 |
| 入库/出库/订单状态机 | java-backend-dev 手写 | WMS/OMS 核心单据 |
| 库存预占扣减 | java-backend-dev + InventoryFacade | 库存引擎 |
| 虚拟仓可售 | 主数据 + mapping 表 + Catalog | 电商铺货库存 |

## 4. 码表与字典

| 用途 | 本项目做法 |
| --- | --- |
| 编译期契约 | Java 枚举 + `WMS_*_CODES.md`（手写/文档） |
| 运营可配下拉 | `sys_dict` + `useDict`（可选） |
| 无 dict 下拉 | 前端静态 OPTIONS（手写，如 org_type） |
| DDL COMMENT | 给人看；**不**驱动枚举自动生成 |

## 5. 波次推进（当前）

业务顺序见 [scm-product-manager/industry-baseline.md](../scm-product-manager/industry-baseline.md) §2、§3。实现按 **PRD 定稿 → 架构师 → 本 Skill 分流** 推进：

```text
B1 组织 wms_org
  → B2 商品 prd_*
    → B3 仓库 wms_warehouse + mapping
      → B4 库存 inv_*
        → B5 入出库 → B6 订单 → B7 盘点/调拨 → B8 高级
```

- B1 `wms_org` · B2 `prd_*` Admin CRUD 已落地
- 验收：以当期 `docs/domains/{域}/prd/` 的 AC 为准

## 6. 三端一致

| 层 | PC | 移动 |
| --- | --- | --- |
| API 路径 | `src/api/scm/wms.ts` | `src/api/system/*.ts` 或远期 `api/scm` |
| 权限 | `v-hasPermi` | `hasPermi` + `PermButton` |
| 类型命名 | `XxxVO` / `XxxForm` | `IXxxVo` / `IXxxForm` |
