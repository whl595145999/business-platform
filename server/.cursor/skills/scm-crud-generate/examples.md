# SCM CRUD Generate — Examples

## 例 1：B1 组织 `wms_org`（验收标杆）

表名 `wms_org` → 自动推断 `domain=wms`、`Business=Org`、`resources=orgs`。

脚本：`script/sql/wms/wms_org_ddl.sql`、`_gen/wms_org_dict.sql`  
文档：[GENERATOR_SCM.md §6](../../../docs/GENERATOR_SCM.md#6-生成器验收b1-wms_org) · [PRODUCT.md](../../docs/domains/wms/prd/B1-org/PRODUCT.md)

### 生成器 options

```json
{
  "scmDomain": "wms",
  "scmResource": "orgs",
  "scmBusinessKey": "id",
  "scmMenuComponent": "scm/wms/org/index",
  "scmPermPrefix": "scm:wms:org"
}
```

### 预期 zip 关键文件

```text
scm-gen/wms/org/
  whl-scm-wms-api/.../facade/OrgFacade.java
  whl-scm-wms-admin-adapter/.../ScmOrgController.java
  admin-web/src/views/scm/wms/org/index.vue
  MERGE/types.snippet · MERGE/wms-api.snippet
  script/sql/wms/_gen/wms_org_menu.sql
```

| 项 | 值 |
|----|-----|
| perm | `scm:wms:org` |
| API | `GET/POST /api/scm/wms/orgs` |

前置：`whl-scm/wms` 模块已建 · PRD + DDL 已定 · 见 [industry-baseline.md](../scm-product-manager/industry-baseline.md) B1。

---

## 例 2：oms 域新表（扩展性验证）

表名 `oms_carrier` → 自动推断：

| 项 | 值 |
|----|-----|
| domain | `oms` |
| Business | `Carrier` |
| resources | `carriers` |
| perm | `scm:oms:carrier` |

模块路径：`whl-scm/oms/whl-scm-oms-*`（需 Maven 模块已存在）。

---

## 例 3：业务键用 code（可选）

表 `prd_sku` 对外用 `sku_code`：`scmBusinessKey` = `sku_code`。

---

## 反例：不要用 scm_crud 的场景

| 场景 | 应用方案 |
|------|----------|
| `sys_user` 平台用户 | 平台 `crud` |
| 树形 `sys_dept` | 平台 `tree` |
| 单据流 `wms_inbound_order` | 手写 Facade + 状态机 |
| 主子表 order + lines | 首期不做 |
