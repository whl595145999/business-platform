# SCM CRUD Generate — Reference

## 1. 命名矩阵（任意表 `{prefix}_{business}`）

| 符号 | 规则 | `wms_org` | `oms_supplier` |
|------|------|-----------|----------------|
| `{table}` | DDL 表名 | `wms_org` | `oms_supplier` |
| `{domain}` | 表前缀 → 域 | `wms` | `oms` |
| `{DomainCap}` | 首字母大写 | `Wms` | `Oms` |
| `{business}` | 去前缀 snake → camel | `org` | `supplier` |
| `{Business}` | PascalCase | `Org` | `Supplier` |
| `{resources}` | REST 复数（options 可覆盖） | `orgs` | `suppliers` |
| `{Entity}` | `{DomainCap}{Business}` | `WmsOrg` | `OmsSupplier` |
| `{Facade}` | `{Business}Facade` | `OrgFacade` | `SupplierFacade` |
| `{AppService}` | `{Business}AppService` | `OrgAppService` | `SupplierAppService` |
| `{Controller}` | `Scm{Business}Controller` | `ScmOrgController` | `ScmSupplierController` |
| `{Vo}` | `{Business}Vo`（admin HTTP 出参） | `OrgVo` | `SupplierVo` |
| OpenAPI Response | `OpenApi{Business}Response` | `OpenApiOrgResponse` | — |
| PDA Vo | `Pda{Business}Vo`（WMS 现场） | — | — |
| `{perm}` | `scm:{domain}:{business}` | `scm:wms:org` | `scm:oms:supplier` |
| `{apiPrefix}` | `/api/scm/{domain}/{resources}` | `/api/scm/wms/orgs` | `/api/scm/oms/suppliers` |

## 2. Maven / 前端路径矩阵

| 产出 | 路径模板 |
|------|----------|
| Facade / Command / Query / Dto | `whl-scm/{domain}/whl-scm-{domain}-api/src/main/java/com/whl/scm/{domain}/api/...` |
| Entity / Mapper / AppService | `whl-scm/{domain}/whl-scm-{domain}-biz/src/main/java/com/whl/scm/{domain}/biz/...` |
| Controller / SaveRequest / **Vo** | `whl-scm/{domain}/whl-scm-{domain}-admin-adapter/.../controller/`、`.../request/`、`.../vo/` |
| Vue 页 | `admin-web/src/views/scm/{domain}/{business}/index.vue` |
| API 函数 | merge 进 `admin-web/src/api/scm/{domain}.ts` |
| Types | merge 进 `admin-web/src/api/scm/types.ts` |
| 菜单 SQL | `script/sql/{domain}/_gen/{table}_menu.sql` |
| ErrorCode | snippet → `.../api/error/{DomainCap}ErrorCode.java` |

## 3. API 契约（固定，不随表变化）

| Op | HTTP | Facade | Controller |
|----|------|--------|------------|
| Page | `GET {apiPrefix}?pageNum&pageSize&filters` | `ScmPageResult<{Business}Dto> page{Business}(Page{Business}Query)` | `TableDataInfo<{Business}Vo>` |
| Get | `GET {apiPrefix}/{key}` | `get{Business}` → `{Business}Dto` | `R<{Business}Vo>` |
| Create | `POST {apiPrefix}` | `create{Business}` → `{Business}Dto` | `R<{Business}Vo>` |
| Update | `PUT {apiPrefix}/{key}` | `update{Business}` → `{Business}Dto` | `R<{Business}Vo>` |
| Delete | `DELETE {apiPrefix}/{key}` | `delete{Business}` | `R<Void>` |
| Options | `GET {apiPrefix}/options` | `list{Business}Options` → `List<{Business}Dto>` | `R<List<{Business}Vo>>` |

Adapter 分页：`new TableDataInfo<>(page.getRows().stream().map(this::toVo).toList(), page.getTotal())`

## 3.1 三通道表现层（scm_crud 仅覆盖 Admin）

| 通道 | 模块 | HTTP 出参 | 生成器 |
|------|------|-----------|--------|
| Admin | `*-admin-adapter` | `{Business}Vo` + `toVo(Dto)` | **`scm_crud` 生成** |
| OpenAPI | `*-openapi-adapter` | `OpenApi{Business}Response` + `toOpenApiResponse(Dto)` | 手写 |
| PDA | `whl-scm-wms-pda-adapter` | `Pda{Business}Vo` + `toPdaVo(Dto)` | 手写 |

细则：[API_CONTRACT.md](../../../docs/API_CONTRACT.md) §2.5、[MODULE_RULES.md](../../../docs/MODULE_RULES.md) §9。

## 4. gen_table.options（扩展键）

| Key | 必填 | 默认推断 | 说明 |
|-----|------|----------|------|
| `scmDomain` | 是 | 表前缀 | `wms` / `oms` / `inventory` |
| `scmResource` | 是 | `{business}s` | REST 路径段 |
| `scmBusinessKey` | 否 | `id` | 路径参数列名 |
| `scmMenuId` | 是 | 1721+ 递增 | 菜单主键 |
| `scmMenuOrder` | 否 | `0` | 同级排序 |
| `scmMenuComponent` | 否 | `scm/{domain}/{business}/index` | 前端路由 |
| `scmPermPrefix` | 否 | `scm:{domain}:{business}` | 权限前缀 |
| `scmParentMenuId` | 否 | `1700` | 供应链父目录 |
| `scmGenerateOptions` | 否 | `true` | 是否生成 `/options` 接口 |

## 5. 字段 HTML 类型（可扩展）

| htmlType | 列名模式 | 前端控件 | 后端 |
|----------|----------|----------|------|
| `input` | 默认 | `el-input` | — |
| `select` / `radio` | `*_type`, `status` | `el-select` / 固定码值 | COMMENT 解析 |
| `deptTree` | `dept_id`, `linked_dept_id` | `el-tree-select` | 调 platform dept API |
| `orgSelect` | `*_org_id` | `el-select` + options | `GET …/orgs/options` |
| `tenantSelect` | `tenant_id` | `useScmTenant` | `ScmTenantResolver` |
| `datetime` | 时间列 | `el-date-picker` | — |

**扩展新 HTML 类型**：`GenConstants` 加常量 → `editTable.vue` 加 option → `vm/scm/macros/formField.vue.vm` 加分支。

## 5.5 Java POJO 约定

| 范围 | 注解 | 说明 |
|------|------|------|
| scm_crud 生成 | `@Data` + `@NoArgsConstructor` | Controller 用 setter 组装 |
| adapter `controller/vo/` | `@Data` + `@NoArgsConstructor` | `toVo(Dto)` 映射 HTTP 出参 |
| **java-backend-dev 手写** `*-api` / adapter `request/` | 上列 + `@AllArgsConstructor` | 禁止 Java `record`；用 `.getXxx()` |
| `boolean` 原始类型 | — | Lombok 用 `.isXxx()`，不是 `getXxx()` |
| 框架 | `EventMetadata` 仍为 record | `.tenantId()` / `.eventId()`，不用 getter |
| Maven | `whl-scm/pom.xml` | 父 POM 继承 `lombok`（`provided`） |

## 6. 主键策略（默认）

| 项 | 规则 |
|----|------|
| DDL | `id BIGINT NOT NULL AUTO_INCREMENT` |
| 导入 | pk `isIncrement=1`, `isInsert=0` |
| Entity | `@TableId(type = IdType.AUTO, value = "id")` |
| Create | 不 setId；insert 后 `entity.getId()` |
| 非自增 | 用户取消自增 → 模板生成 `IdGeneratorUtil` 分支 + `// GEN-TODO` |

## 7. MERGE 区域约定

```typescript
// admin-web/src/api/scm/types.ts
/* #region scm-gen:wms-org */
export interface OrgVO { ... }
/* #endregion scm-gen:wms-org */
```

```typescript
// admin-web/src/api/scm/wms.ts
/* #region scm-gen:wms-org */
export const pageOrg = ...
/* #endregion scm-gen:wms-org */
```

```java
// WmsErrorCode.java
/* scm-gen:wms-org */
WMS_ORG_CODE_EXISTS("...", "..."),
/* end scm-gen:wms-org */
```

## 8. 域前缀注册表（GenUtils 可扩展）

| 表前缀 | domain | api 文件 | ErrorCode 类 |
|--------|--------|----------|--------------|
| `wms_` | `wms` | `wms.ts` | `WmsErrorCode` |
| `oms_` | `oms` | `oms.ts` | `OmsErrorCode` |
| `inv_` | `inventory` | `inventory.ts` | `InventoryErrorCode` |

新增域：上表加一行 + 新建 `whl-scm-{domain}-*` 模块（若不存在）+ `GenUtils.resolveScmDomain` 加前缀。

## 9. 与平台 crud 选用边界

| 场景 | 模板 |
|------|------|
| `sys_*` 平台表 | `crud` / `tree` |
| `wms_*` / `oms_*` / `inv_*` SCM 主数据 | **`scm_crud`** |
| 树形主数据（组织树等） | 首期手写；后续 `scm_tree`（未实现） |
