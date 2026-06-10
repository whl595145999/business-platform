# SCM PR Checklist

Agent 按变更类型勾选；每项标 **PASS / FAIL / N/A** 并附一行说明。

---

## §A DDL / SQL

- [ ] 表名域前缀 `wms_`/`oms_`/`inv_`/`prd_`
- [ ] `tenant_id VARCHAR(20) NOT NULL`
- [ ] 审计字段 + `del_flag CHAR(1)` 默认 `'0'`
- [ ] 主键 `BIGINT AUTO_INCREMENT`（主数据默认）
- [ ] status/type 等码字段 **SMALLINT 步长 10**
- [ ] 唯一键含 `tenant_id`：`uk_{表}_{键} (tenant_id, …)`
- [ ] 列表索引 `(tenant_id, 过滤列, …)`
- [ ] 脚本 `SET NAMES utf8mb4`
- [ ] 未改 `sys_*` 结构（除非平台升级）
- [ ] 无跨前缀 JOIN（`wms_` ↔ `oms_`）
- [ ] COMMENT 仅文档用途，不要求生成 Java 枚举

参考：`docs/DATABASE.md` · `mysql-dba` Skill

---

## §B 生成器 MERGE

> 唯一清单：[scm-crud-generate/workflow Step 4](../scm-crud-generate/workflow.md#step-4--落盘与-mergeagent)

- [ ] ErrorCode snippet **append**，枚举项前有逗号
- [ ] types/api snippet 在 `#region scm-gen:{domain}-{business}` 内
- [ ] 未 blind 覆盖 `types.ts` / `wms.ts` / ErrorCode 全文件
- [ ] Java 落盘路径在 `whl-scm/{domain}/` 非 demo（正式代码）
- [ ] `ScmPageResult.java` 未重复复制
- [ ] 未覆盖 `ScmTenantResolver` / `useScmTenant.ts`
- [ ] MERGE 未 blind 覆盖已有 `#region scm-gen:*`（重建后同波次再生成时）
- [ ] 菜单 SQL 已登记；MODULE_RULES §8 Controller 拆分与 PRD 菜单一致
- [ ] `mvn compile` 通过
- [ ] 页面引用 `@/api/scm/{domain}.ts` 非 zip `_gen/*.ts`

参考：`docs/GENERATOR_SCM.md` §4

---

## §C Java 后端

### 依赖与分层（MODULE_RULES §2–4）

- [ ] 新类在正确层（Controller/Vo 在 adapter，业务在 AppService）
- [ ] `*-api` 无 Spring / MyBatis 依赖
- [ ] 跨域调用经 `*-api` Facade 或 Event，无跨域 Mapper
- [ ] 正式契约在 `whl-scm-{domain}-api`，非 demo-api

### 契约（API_CONTRACT §2.5）

- [ ] Facade 入参 Command/Query，出参 Dto
- [ ] Admin Controller 出参 `{Noun}Vo` + `toVo(Dto)`
- [ ] 未把 Dto 直出 JSON
- [ ] OpenAPI/PDA 未复用 admin Vo（若涉及）

### 数据与业务

- [ ] 查询带 `tenant_id`
- [ ] 写操作有 `@Transactional`（单域内）
- [ ] 单据写操作 Command 含 `idempotencyKey`（若适用）
- [ ] 仓库 subtype 校验：`subtype/100 == category/10`（若涉及仓型）
- [ ] POJO 用 `@Data`，非 record（`EventMetadata` 除外）

### 菜单 Controller（MODULE_RULES §8）

- [ ] 按菜单页拆分 Controller，非整域堆单类
- [ ] JavaDoc 含 menu_id / 前端路由（新页）

参考：`java-backend-dev` · `docs/MODULE_RULES.md` §4/§8/§9

---

## §H PDA / OpenAPI adapter

- [ ] PDA 在 `whl-scm-wms-pda-adapter`，路径 `/api/pda/wms/...`
- [ ] 出参 `Pda*Vo` + `toPdaVo(Dto)`，未复用 admin `*Vo`
- [ ] 写操作 Command 含 `idempotencyKey`
- [ ] 未在 `Scm*Controller`（admin）上挂 PDA/OpenAPI 路由
- [ ] OpenAPI 用 `OpenApi*Response`，独立 `*-openapi-adapter`
- [ ] 业务逻辑在 AppService，adapter 无 SQL/库存规则

标杆：`ScmPdaInboundController` · MODULE_RULES §9

---

## §D admin-web

- [ ] API 路径与后端一致（scm：`/api/scm/{domain}/{resources}`）
- [ ] types 与 `{Noun}VO` 字段与后端 Vo 对齐
- [ ] `v-hasPermi` 与 `scmPermPrefix` 一致
- [ ] 查询区无内联 width（日期范围 308px 除外）
- [ ] 租户：`useScmTenant` 传参（超管场景）
- [ ] dict 或静态 OPTIONS 与码表一致
- [ ] 生成器页未从零重写（若来自 zip）

参考：`frontend-dev` · `docs/GENERATOR_SCM.md` §7

---

## §E admin-app

- [ ] 仅 `src/http/http.ts` 业务请求
- [ ] **system**：`listXxx` + `/system/{module}/list`
- [ ] **scm**：`pageXxx` + `/api/scm/...`（**禁止** `/list`）
- [ ] 分页 `pageNum`/`pageSize`，`IPageResult<T>`
- [ ] `hasPermi` 与后台 `perms` 一致
- [ ] 列表+form 有 `useListRefresh`；`onShow` 从 `@dcloudio/uni-app` 导入
- [ ] `ADMIN_PAGE_MAP` 与 menu `component` 对齐
- [ ] 已更新 `docs/admin-web接口对照.md`（新模块）

参考：`generate-crud-module` · `docs/PR检查清单.md`

---

## §G PRD / 设计文档（无代码 diff 时）

- [ ] FR 与 AC（Given-When-Then）可追溯
- [ ] 波次范围与 [industry-baseline.md](../scm-product-manager/industry-baseline.md) §3、ROADMAP 一致
- [ ] MDM 硬依赖未违反（如未定义 SKU 却设计入出库，见 baseline §2）
- [ ] 状态码 SMALLINT 步长 10
- [ ] 域边界与 [domains/README.md](../../docs/domains/README.md) / MODULE_RULES 一致
- [ ] 主数据归属表前缀正确（wms_/oms_/inv_/prd_）
- [ ] 未要求 COMMENT 自动生成枚举

来源 Skill：`scm-product-manager` Step 7 · `ecommerce-scm-architect` Step 10

---

## §F 全栈串联（可选）

主数据 PR 建议顺序审：**§A → §B → §C → §D → §E**，合并为一份 Report，阻塞项去重。

```text
mysql-dba DDL
  → scm-crud-generate MERGE
  → java 编译
  → PC 联调
  → 移动（若有）
```
