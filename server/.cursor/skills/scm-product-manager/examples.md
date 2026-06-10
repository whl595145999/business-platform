# 供应链产品 — 示例

## 例 1 — 采购收货（功能级，全 8 Step）

@scm-product-manager```text
 设计采购订单+收货入库，从 Step 1
```

Step 3：采购订单、收货单与 `wms_inbound_order` 关联  
Step 4：在途 vs 可用库存口径  
Step 7：Given 采购单已审核 When 收货确认 Then 生成入库单且库存增加

---

## 例 2 — 增量（已有 B1 组织 PRD）

```text
@scm-product-manager 只补退货逆向流程 Step 5、7
```

---

## 例 3 — PRD 后接架构与开发

```text
Step 8 完成 → @ecommerce-scm-architect Step 6–7（库表+API）
            → @scm-platform-dev
```

---

## 例 4 — 从 0 重建（程序级 Step 1+2）

```text
@scm-product-manager 供应链从 0 重建，先程序级 Step 1
```

Step 1：九域 + L0～L5 + 四场景选型（集团多组织）+ V1 不做跨 org  
Step 2：MDM 依赖顺序 + B1 组织 … B8 高级；**不写代码**

确认后：

```text
@scm-product-manager 组织主数据，功能级 Step 1 起
```

Step 1：仅组织 Business Model  
Step 2：仅 `wms_org` 字段与治理（引用 baseline §2 ①）  
Step 3～4：跳过  
Step 5～8：组织 CRUD 流程 + `domains/wms/prd/B1-org/PRODUCT.md` + AC

---

## 例 5 — 主数据轻量路径

```text
Step 2 完成（组织）→ @ecommerce-scm-architect Step 6
                   → @scm-platform-dev → mysql-dba → scm-crud-generate → frontend-dev
```

---

## 例 6 — B2 货品主数据（Admin CRUD 已落地）

```text
@scm-platform-dev B2 货品 AC 验收：效期/条码/validateSku；补财税 Tab
```

**引用**：[B2-product/PRODUCT.md](../../docs/domains/product/prd/B2-product/PRODUCT.md) · [TECH.md](../../docs/domains/product/design/B2-product/TECH.md) · DDL `script/sql/product/`
