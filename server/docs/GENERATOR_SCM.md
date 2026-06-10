# SCM 代码生成器（scm_crud）

> 配合 [MODULE_RULES.md](./MODULE_RULES.md) §7/§8 · Agent Skill：[scm-crud-generate](../.cursor/skills/scm-crud-generate/SKILL.md)（分步：[workflow.md](../.cursor/skills/scm-crud-generate/workflow.md) · 扩展见 `reference.md` / `extension.md`）

---

## 1. 适用边界

| 表前缀 | 模板 |
|--------|------|
| `sys_*` | 平台 `crud` / `tree` |
| `wms_*` / `oms_*` / `inv_*` | **`scm_crud`** |
| 单据 / 状态机 | 手写 Facade，不用生成器 |

---

## 2. 架构约定

### 2.1 分层产物（zip 内 `scm-gen/{domain}/{business}/`）

| 层 | 内容 |
|----|------|
| `whl-scm-{domain}-api` | Facade / Command / Query / Dto / `ScmPageResult` |
| `whl-scm-{domain}-biz` | Entity / Mapper / `{Business}AppService` |
| `whl-scm-{domain}-admin-adapter` | `Scm{Business}Controller` / `Save{Business}Request` / **`{Business}Vo`** |
| `admin-web` | `views/scm/{domain}/{business}/index.vue` + MERGE 片段 |
| `script/sql/{domain}/_gen/` | 菜单 SQL |

### 2.2 API 契约

| 层 | HTTP 入参 | HTTP 出参 | Facade 内部 |
|----|-----------|-----------|-------------|
| 管理端 Controller | `Save{Business}Request` | **`R<{Business}Vo>`** / `TableDataInfo<{Business}Vo>` | 调 Facade 得 `{Business}Dto`，**Adapter 内 `toVo` 转换** |
| Facade | Command / Query | `{Business}Dto` / `ScmPageResult<Dto>` | 不暴露给前端 JSON |

| 操作 | HTTP | Controller 返回 |
|------|------|-----------------|
| 分页 | `GET /api/scm/{domain}/{resources}?pageNum&pageSize` | `TableDataInfo<{Business}Vo>` |
| 详情 | `GET /{key}` | `R<{Business}Vo>` |
| 新增 | `POST` | `R<{Business}Vo>` |
| 修改 | `PUT /{key}` | `R<{Business}Vo>` |
| 删除 | `DELETE /{key}` | `R<Void>` |
| 下拉 | `GET /options`（可选） | `R<List<{Business}Vo>>` |

> 与 RuoYi 一致：**Dto 是 Facade 契约**；**Vo 才是 admin 管理端 JSON 出参**（对应 `admin-web` 的 `{Business}VO`）。Admin Controller 统一 `R<XxxVo>` + `toVo(Dto)`。OpenAPI 用 `OpenApi*Response`、PDA 用 `Pda*Vo`，**不**复用 admin Vo 或直出 Dto（见 [API_CONTRACT.md](./API_CONTRACT.md) §2.5、[MODULE_RULES.md](./MODULE_RULES.md) §9）。

### 2.3 主键（默认 AUTO_INCREMENT）

- DDL：`id BIGINT NOT NULL AUTO_INCREMENT`
- 导入后：主键 **自增=1**、**插入=0**
- Entity：`@TableId(type = IdType.AUTO)`，创建时 **不 setId**
- 勿使用已废弃的 `IdGeneratorUtil.nextLongId()`；主键用 `@TableId(type = IdType.AUTO)`

### 2.4 Java 模型

| 场景 | 注解 | 组装方式 |
|------|------|----------|
| **scm_crud 生成** | `@Data` + `@NoArgsConstructor` | Controller / AppService 用 **setter** |
| **java-backend-dev 手写**（`*-api`、adapter `request/`） | `@Data` + `@NoArgsConstructor` + `@AllArgsConstructor` | 可用全参构造或 setter；**禁止 `record`** |
| **adapter `controller/vo/`** | `@Data` + `@NoArgsConstructor` | Controller 内 `toVo(Dto)` 用 setter |
| **框架信封** | `EventMetadata` 仍为 record | 访问 `.tenantId()` / `.eventId()`，不用 getter |
| **Maven** | `whl-scm/pom.xml` 继承 `lombok`（`provided`） | `*-api` 仅依赖 `whl-framework-kernel` 时也需 Lombok |

- 字段注释：`/** 中文说明 */`（同平台 `CaptchaVo`），类级 JavaDoc 不写 `@param` 列表
- POJO 访问：`.getXxx()`；**`boolean` 原始类型**用 `.isXxx()`（如 `isIncludeInSum()`、`isShipmentConsume()`）
- 勿把 POJO 当 record 写 `.tenantId()`；**仅** `EventMetadata` 等 kernel record 保留 record 访问器
- **`ScmPageResult.java` 全项目只需 merge 一次**

### 2.5 gen_table.options

| 键 | 说明 |
|----|------|
| `scmDomain` | `wms` / `oms` / `inventory` |
| `scmResource` | REST 复数名，如 `orgs` |
| `scmBusinessKey` | 路径参数列，默认 `id` |
| `scmMenuId` | 菜单 ID（1721+，占 4 个连续 ID） |
| `scmMenuComponent` | 如 `scm/wms/org/index` |
| `scmPermPrefix` | 如 `scm:wms:org` |
| `scmGenerateOptions` | 是否生成 `/options` |
| `parentMenuId` | 上级菜单，默认 `1700` |

`生成包路径` / `生成模块名` 对 scm **几乎无影响**，Java 固定进 `whl-scm/{domain}/...`。

**WMS 导入默认值**（可 UI 覆盖）：`org_type`→字典 `wms_org_type`、`status`→`wms_enable_status`；smallint/int→`Integer`。

---

## 3. 标准工作流

1. 编写 DDL（`tenant_id` + `AUTO_INCREMENT id` + 域前缀表名）
2. Admin → **系统工具 → 代码生成 → 导入**
3. 编辑 → 模板 **SCM 单表（Facade CRUD）** → 字段 + SCM 配置 → **提交**
4. **预览** `/tool/gen/preview/{tableId}`（**25** 个 `vm/scm/*`，含 `Vo.java.vm`）
5. **下载 zip** → 按 [§4 MERGE](#4-merge-清单) 手工合并（推荐：先 diff 再落盘，避免覆盖手写代码）
6. 导入菜单 SQL（utf8mb4）→ **重新登录**
7. `mvn compile` → 分页 CRUD 冒烟

**改 Velocity 模板后须重启 admin**。  
**「同步」**：仅 DB 表结构变更后使用；会刷新字段并尽量保留已配字典/查询方式。

---

## 4. MERGE 清单

> **Agent 执行**以 [workflow.md Step 4](../.cursor/skills/scm-crud-generate/workflow.md#step-4--落盘与-mergeagent) 为唯一清单；本节供人读对照。

- [ ] `MERGE/ErrorCode-*.snippet` → `{Domain}ErrorCode.java`（枚举项前加逗号）
- [ ] `MERGE/types-*.snippet` → `admin-web/src/api/scm/types.ts`（`#region scm-gen:{domain}-{business}`）
- [ ] `MERGE/api-*.snippet` → `admin-web/src/api/scm/{domain}.ts`，并在文件顶部 `import` 补全 `XxxVO/Form/Query`
- [ ] 复制 Java / Vue 到对应模块（含 `adapter/.../controller/vo/{Business}Vo.java`；**`ScmPageResult.java` 首次复制一份即可**）
- [ ] 菜单 SQL → `sys_menu` / `sys_role_menu`
- [ ] MODULE_RULES §8.2 登记 Controller（一行）
- [ ] **不要**覆盖 `ScmTenantResolver` / `useScmTenant.ts`

页面引用 **`@/api/scm/{domain}.ts`**，不是 zip 内 `_gen/*.ts`。

---

## 5. 辅助脚本

```bash
cd business-platform

# 模板 / 文档静态自检
./script/wms/scm-crud-template-smoke.sh

# 可选：导入 B1 wms_org 并打印 UI 清单
./script/wms/scm-crud-gen-verify.sh -h127.0.0.1 -uroot -p your_db
```

编译（merge 后，`whl-scm/wms` 模块须已按 B1 架构创建）：

```bash
"/path/to/mvn" -pl whl-scm/wms/whl-scm-wms-api,whl-scm/wms/whl-scm-wms-biz,whl-scm/wms/whl-scm-wms-admin-adapter,whl-platform/whl-platform-generator -am compile
```

---

## 6. 生成器验收（B1 `wms_org`）

| 用途 | 表 / 脚本 | 说明 |
|------|-----------|------|
| **B1 组织（正式）** | `wms_org` | [PRODUCT.md](./domains/wms/prd/B1-org/PRODUCT.md) · [TECH.md](./domains/wms/design/B1-org/TECH.md) |

### 6.1 数据库

```bash
mysql --default-character-set=utf8mb4 -h127.0.0.1 -uroot -p your_db \
  < script/sql/wms/wms_org_ddl.sql
mysql --default-character-set=utf8mb4 -h127.0.0.1 -uroot -p your_db \
  < script/sql/wms/_gen/wms_org_dict.sql
```

| 脚本 | 说明 |
|------|------|
| [wms_org_ddl.sql](../script/sql/wms/wms_org_ddl.sql) | B1 组织表 |
| [_gen/wms_org_dict.sql](../script/sql/wms/_gen/wms_org_dict.sql) | `wms_org_type` / `wms_enable_status` |
| [PRODUCT.md](./domains/wms/prd/B1-org/PRODUCT.md) §3 | `org_type` / `status` 码值 |

### 6.2 字段配置（推荐）

| 字段 | 主键 | 自增 | 增 | 改 | 列 | 查 | 查询 | 显示 | 字典 |
|------|:--:|:--:|:--:|:--:|:--:|:--:|------|------|------|
| `id` | ✓ | ✓ | — | — | ✓ | — | — | — | — |
| `tenant_id` | — | — | — | — | — | — | — | 默认 | — |
| `org_code` | — | — | ✓ | ✓ | ✓ | ✓ | `=` | 文本 | — |
| `org_name` | — | — | ✓ | ✓ | ✓ | ✓ | `LIKE` | 文本 | — |
| `org_type` | — | — | ✓ | ✓ | ✓ | ✓ | `=` | 下拉 | `wms_org_type` |
| `status` | — | — | ✓ | ✓ | ✓ | ✓ | `=` | 单选 | `wms_enable_status` |

### 6.3 生成信息（SCM 配置）

| 项 | 值 |
|----|-----|
| 业务名 / 类名 | `org` / `Org` |
| SCM 域 | `wms` |
| REST 资源名 | `orgs` → `/api/scm/wms/orgs` |
| 业务键 | `id` |
| 菜单 ID | `1721`（1721～1724） |
| component | `scm/wms/org/index` |
| 权限前缀 | `scm:wms:org` |
| options | 开 |

### 6.4 合并路径（zip → 工程）

| zip | 目标 |
|-----|------|
| `MERGE/*` | 见 [§4](#4-merge-清单) |
| `.../api/**` | `whl-scm-wms-api/.../api/` |
| `.../biz/**` | `whl-scm-wms-biz/.../biz/` |
| `.../adapter/**` | `whl-scm-wms-admin-adapter/.../`（`controller/`、`controller/request/`、**`controller/vo/`**） |
| `admin-web/.../org/index.vue` | `business-platform-admin-web/src/views/scm/wms/org/index.vue` |
| `script/sql/wms/_gen/wms_org_menu.sql` | 导入 DB |

### 6.5 冒烟

| # | 操作 | 预期 |
|---|------|------|
| 1 | `GET /api/scm/wms/orgs?pageNum=1&pageSize=10` | `rows` + `total` |
| 2 | `org_name` 搜 `验收` | LIKE 生效 |
| 3 | `POST` 重复 `org_code` | 业务错误码 |
| 4 | `PUT` / `DELETE /{id}` | 成功 |
| 5 | `GET /options` | 列表（若开启） |
| 6 | Admin 菜单 | 权限 `scm:wms:org:*` 生效 |

---

## 7. 常见问题

| 现象 | 处理 |
|------|------|
| 预览无 `vm/scm/*` | 未选 scm_crud，或 admin 未重启 |
| 下拉/单选变文本框 | 未绑 `dictType` 或未导入字典 SQL；可点 **同步** 补 WMS 默认字典 |
| `pageOrg is not defined` | 未 merge `api-*.snippet` 进 `wms.ts` |
| 编译缺 `ScmPageResult` | 从 zip 复制 `api/page/ScmPageResult.java`（一次） |
| `command.tenantId()` 编译失败 | 契约已改为 POJO，改用 `command.getTenantId()`（**例外**：`EventMetadata.tenantId()` 仍为 record） |
| `getIncludeInSum()` / `getShipmentConsume()` 找不到 | `boolean` 字段用 `isIncludeInSum()` / `isShipmentConsume()` |
| `StocktakeLineCommand::lineNo` 无效 | POJO 方法引用改为 `::getLineNo` |
| `程序包lombok不存在`（`*-api`） | 确认 `whl-scm/pom.xml` 已声明 Lombok；或模块显式依赖 |
| 菜单不显示 | 菜单 SQL 未导入或未重新登录 |
| 查询区字段对不齐 | 勿在查询区 `el-input`/`el-select` 写 `style="width:..."`；依赖 `element-ui.scss`（inline：label 68px、控件 240px）；日期范围项保留 `el-form-item style="width:308px"` |

---

## 8. 扩展与源码

| 需求 | 文档 |
|------|------|
| 命名矩阵 / API / MERGE 细节 | Skill [reference.md](../.cursor/skills/scm-crud-generate/reference.md) |
| 新域 / HTML 类型 / 业务键 | Skill [extension.md](../.cursor/skills/scm-crud-generate/extension.md) |
| 跨表示例 | Skill [examples.md](../.cursor/skills/scm-crud-generate/examples.md) |

| 组件 | 路径 |
|------|------|
| 模板常量 | `GenConstants.TPL_SCM_CRUD` |
| 域推断 / 字段默认 | `GenUtils.java` |
| 模板与 zip 路径 | `VelocityUtils.java`, `vm/scm/` |
| 生成服务 | `GenTableServiceImpl.java` |
| Admin UI | `genInfoForm.vue`, `editTable.vue` |
