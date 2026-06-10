# 组织主数据 — 技术设计（B1 · `wms_org`）

| 项 | 内容 |
| --- | --- |
| 文档类型 | **技术设计**（架构师 Step 6–9） |
| 波次 | **B1** |
| 域 | `wms` |
| 产品 PRD | [PRODUCT.md](../../prd/B1-org/PRODUCT.md) |
| 关联 | [DATABASE.md](../../../../DATABASE.md) · [API_CONTRACT.md](../../../../API_CONTRACT.md) |

---

## 14. 研发技术章 — 数据库设计（架构师 Step 6）

### 14.1 模块归属

| 项 | 约定 |
| --- | --- |
| 域 | WMS 主数据 |
| Maven | `whl-scm-wms-api` · `whl-scm-wms-biz` · `whl-scm-wms-admin-adapter` |
| 聚合 | `Org`（组织档案） |
| 表 | `wms_org` |
| 菜单 id | **1708**（[MODULE_RULES.md](../../../../MODULE_RULES.md)） |

B1 **无** Event 发布、**无** PDA/OpenAPI 接口。

### 14.2 ER 关系

```mermaid
erDiagram
    wms_org ||--o{ wms_org : "parent_org_id"
    wms_org {
        bigint id PK
        varchar tenant_id
        varchar org_code UK
        varchar org_name
        smallint org_type
        bigint parent_org_id "0=顶级"
        bigint linked_dept_id FK_optional
        smallint status
        int sort_order
    }
```

**【研发】** B3 后扩展：`wms_warehouse.owner_org_id` → `wms_org.id`（本期不建外键，应用层校验）。

### 14.3 DDL（`script/sql/wms/wms_org_ddl.sql`）

列默认见 [DATABASE.md](../../../../DATABASE.md) §3.5。权威脚本：

```sql
-- 见 script/sql/wms/wms_org_ddl.sql（勿在此复制，以免与落盘脚本漂移）
```

### 14.4 索引与约束说明

| 键 | 用途 |
| --- | --- |
| `uk_wms_org_code (tenant_id, org_code)` | AC-ORG-04 编码唯一 |
| `idx_wms_org_parent` | 查子节点、树展开 |
| `idx_wms_org_status` | 启用组织下拉过滤 |

**应用层校验**（biz，非 DB FK）：`parent_org_id` 非 0 时须存在且同租户；禁止环；删除前无子节点。

### 14.5 码表

| 字段 | 文档 | 值 |
| --- | --- | --- |
| `org_type` | DDL COMMENT / 前端 `ORG_TYPE_OPTIONS` | 10/20/30/40 |
| `status` | `wms_enable_status` 或静态 OPTIONS | 10 启用 · 20 停用 |

可选 dict：`script/sql/wms/_gen/wms_org_dict.sql`（`wms_org_type`、`wms_enable_status`）。

### 14.6 容量

| 场景 | 行数 | 说明 |
| --- | --- | --- |
| 单租户 | &lt; 500 | 集团 + 法人 + 事业部 |
| 多租户合计 | &lt; 5 万 |  btree 索引足够 |

### 14.7 错误码（WMS 域，biz 抛出）

| 码 | 场景 | 对齐 AC |
| --- | --- | --- |
| `WMS_ORG_CODE_DUPLICATE` | 编码重复 | AC-ORG-04 |
| `WMS_ORG_HAS_CHILDREN` | 有子节点删除 | AC-ORG-10 |
| `WMS_ORG_PARENT_CYCLE` | 上级成环 | AC-ORG-08 |
| `WMS_ORG_DISABLED` | 停用不可选 | AC-ORG-12 |
| `WMS_ORG_NOT_FOUND` | id 不存在 | — |

**Handoff** → `@mysql-dba` 落盘 DDL + dict · `@scm-crud-generate` 生成器配置 `wms_org`。

---

## 15. 研发技术章 — 接口设计（架构师 Step 7）

### 15.1 Facade（`whl-scm-wms-api`）

```java
public interface OrgFacade {
    ScmPageResult<OrgDto> pageOrg(PageOrgQuery query);
    OrgDto getOrg(GetOrgQuery query);
    OrgDto createOrg(CreateOrgCommand command);
    OrgDto updateOrg(UpdateOrgCommand command);
    void deleteOrg(DeleteOrgCommand command);
    List<OrgDto> listOrgOptions(ListOrgOptionsQuery query);
}
```

| 类型 | 类名 | 关键字段 |
| --- | --- | --- |
| Query | `PageOrgQuery` | `tenantId`, `pageNum`, `pageSize`, `orgName`, `orgType`, `status` |
| Query | `GetOrgQuery` | `tenantId`, `orgId`（= `id`） |
| Query | `ListOrgOptionsQuery` | `tenantId`, `status`（默认 10 仅启用） |
| Command | `CreateOrgCommand` | `tenantId`, `orgCode`, `orgName`, `orgType`, `parentOrgId`, `status`, `linkedDeptId`, `sortOrder`, `remark` |
| Command | `UpdateOrgCommand` | 同上 + `orgId` |
| Command | `DeleteOrgCommand` | `tenantId`, `orgId` |
| Dto | `OrgDto` | `orgId`, `orgCode`, `orgName`, `orgType`, `parentOrgId`, `parentOrgName`, `status`, `linkedDeptId`, `sortOrder`, `remark` |

**【研发】** `OrgAppService` 实现 Facade；`parentOrgId` 校验：0 或有效 id、同租户、不成环；`createOrg` 校验 `org_code` 唯一。

### 15.2 Admin HTTP（`ScmOrgController`）

| menu_id | 路径 | 方法 | 权限 | 说明 |
| --- | --- | --- | --- | --- |
| 1708 | `GET /api/scm/wms/orgs` | `pageOrg` | `scm:wms:org:list` | 分页；query: `orgName`,`orgType`,`status` |
| 1708 | `GET /api/scm/wms/orgs/{orgId}` | `getOrg` | `scm:wms:org:query` | 详情 |
| 1708 | `POST /api/scm/wms/orgs` | `createOrg` | `scm:wms:org:add` | Body: `OrgSaveRequest` |
| 1708 | `PUT /api/scm/wms/orgs/{orgId}` | `updateOrg` | `scm:wms:org:edit` | Body: `OrgSaveRequest` |
| 1708 | `DELETE /api/scm/wms/orgs/{orgId}` | `deleteOrg` | `scm:wms:org:remove` | 逻辑删除 |
| 1708 | `GET /api/scm/wms/orgs/options` | `listOrgOptions` | `scm:wms:org:list` | 下拉/树；默认 `status=10` |

**表现层**

| 类 | 包 | 说明 |
| --- | --- | --- |
| `OrgVo` | `controller/vo/` | HTTP 出参；`orgId` ← `Dto.orgId` |
| `OrgSaveRequest` | `controller/request/` | 入参；`parentOrgId` 顶级传 **0** |

```text
Controller → Command/Query → OrgFacade → OrgDto → toVo(Dto) → R<OrgVo> / TableDataInfo<OrgVo>
```

### 15.3 前端契约（admin-web）

| 函数 | HTTP | 文件 |
| --- | --- | --- |
| `pageOrg` | `GET /api/scm/wms/orgs` | `api/scm/wms.ts` |
| `getOrg` | `GET /api/scm/wms/orgs/{orgId}` | 同上 |
| `addOrg` | `POST /api/scm/wms/orgs` | 同上 |
| `updateOrg` | `PUT /api/scm/wms/orgs/{orgId}` | 同上 |
| `delOrg` | `DELETE /api/scm/wms/orgs/{orgId}` | 同上 |
| `listOrgOptions` | `GET /api/scm/wms/orgs/options` | 同上 |

Types（`api/scm/types.ts`）：`OrgVO`, `OrgForm` — 主键字段 **`orgId`**。

移动（可选）：`pageOrg` 等同路径，禁止 `/list`（[generate-crud-module](../../../../../business-platform-admin-app/.cursor/skills/generate-crud-module/SKILL.md)）。

### 15.4 生成器 `gen_table.options`（`wms_org`）

| Key | 值 |
| --- | --- |
| `scmDomain` | `wms` |
| `scmResource` | `orgs` |
| `scmBusinessKey` | `id` |
| `scmMenuId` | `1708` |
| `scmMenuComponent` | `scm/wms/org/index` |
| `scmPermPrefix` | `scm:wms:org` |
| `scmParentMenuId` | `1700` |
| `scmGenerateOptions` | `true` |

### 15.5 Event / 三通道

| 通道 | B1 |
| --- | --- |
| Admin | ✅ 上文 |
| OpenAPI | ❌ |
| PDA | ❌ |
| Domain Event | ❌（主数据变更暂不广播；B3 后可补 `OrgChangedEvent`） |

### 15.6 错误码 → HTTP

| 错误码 | 建议 HTTP | 文案 |
| --- | --- | --- |
| `WMS_ORG_CODE_DUPLICATE` | 409 | 组织编码已存在，请更换 |
| `WMS_ORG_HAS_CHILDREN` | 409 | 存在下级组织，无法删除 |
| `WMS_ORG_PARENT_CYCLE` | 400 | 上级组织不能为自己或下级组织 |
| `WMS_ORG_DISABLED` | 409 | 该组织已停用，不可选用 |
| `WMS_ORG_NOT_FOUND` | 404 | 组织不存在 |

**Handoff** → `@mysql-dba` · `@scm-crud-generate` · `@frontend-dev` G1–G4。

---

## 17. 研发技术章 — 系统架构（架构师 Step 8）

### 17.1 当前态（B1）

| 项 | 结论 |
| --- | --- |
| 部署形态 | **模块化单体** `whl-admin-server`（:8080） |
| 数据库 | 单库 `business_platform`，表 `wms_org` |
| 缓存 / MQ / 搜索 | **本期不需要**；组织主数据直读 DB |
| 分布式 | 不拆进程、不拆库（见 [DISTRIBUTED_EVOLUTION.md](../../../../DISTRIBUTED_EVOLUTION.md) 远期） |

### 17.2 B1 模块与调用链

```text
┌─────────────────────────────────────────────────────────────────┐
│ 客户端                                                           │
│  admin-web (Vue3)          admin-app (UniApp)                    │
│  views/scm/wms/org         pages-sub/scm/org/{index,detail,form} │
└───────────────┬─────────────────────────┬───────────────────────┘
                │ GET/POST /api/scm/wms/orgs*                      │
                ▼                          ▼
┌───────────────────────────────────────────────────────────────────┐
│ whl-admin-server                                                  │
│  whl-scm-wms-admin-adapter                                        │
│    ScmOrgController  →  Command/Query  →  OrgFacade               │
│  whl-scm-wms-biz                                                  │
│    OrgAppService  →  WmsOrgMapper  →  wms_org                     │
│  whl-platform-system（租户 / 权限 / Sa-Token）                     │
└───────────────────────────────────────────────────────────────────┘
```

| 层 | artifact | B1 职责 |
| --- | --- | --- |
| 表现（HTTP） | `whl-scm-wms-admin-adapter` | `OrgVo` / `OrgSaveRequest`，薄转换 |
| 应用 | `whl-scm-wms-biz` | `OrgAppService`：唯一性、成环、子节点校验 |
| 契约 | `whl-scm-wms-api` | `OrgFacade` + Command/Query/Dto |
| 平台 | `whl-platform-auth` | `/auth/login`；租户 `ScmTenantResolver` |

**B1 不启用**：`pda-adapter`、`openapi-adapter`、Domain Event、独立 `whl-pda-server`。

### 17.3 与九域 / 波次关系

```text
L1 主数据：wms_org (B1) ──► prd_* (B2) ──► wms_warehouse (B3) ──► inv_* (B4) …
```

| 下游 | 依赖 `wms_org` 方式 |
| --- | --- |
| B3 仓库 | `owner_org_id` 应用层校验（本期无 FK） |
| B4 库存 | 货权组织维度 |
| admin `sys_dept` | 可选 `linked_dept_id`，**不等于**货权组织 |

### 17.4 非功能（B1 基线）

| 项 | 目标 |
| --- | --- |
| 租户隔离 | 所有 Query/Command 带 `tenantId`（`ScmTenantResolver`） |
| 权限 | `@SaCheckPermission("scm:wms:org:*")` |
| 审计 | RuoYi `create_by` / `update_by` / `@Log`（导出） |
| 性能 | 单租户 &lt;500 行，分页 + 索引足够；无 Redis 缓存 |

### 17.5 远期标注（不在 B1 实现）

| 项 | 触发波次 |
| --- | --- |
| `OrgChangedEvent` 广播 | B3+ 仓/库存需感知组织变更时 |
| 组织数据 Redis 缓存 | 下拉 QPS 显著上升时 |
| `whl-pda-server` 独立进程 | B8 |
| 跨 org 调拨 / 结算 | B7+ / 财务域 |

**Handoff** → B1 验收（§11.3）→ `@scm-platform-dev` 仅修 AC 缺口。

---

## 18. 研发技术章 — 异常场景（架构师 Step 9）

> B1 主数据无库存/支付类异常；以下为组织域必覆盖场景（与 §14.7、§15.6 对齐）。

| 场景 | 检测 | 处理 | 恢复 | 审计 |
| --- | --- | --- | --- | --- |
| 编码重复 | `uk_wms_org_code` / biz 查重 | 拒绝创建；`WMS_ORG_CODE_DUPLICATE` → 409 | 用户更换 `org_code` | 无落库 |
| 上级成环 | `OrgAppService` 祖先链遍历 | 拒绝保存；`WMS_ORG_PARENT_CYCLE` → 400 | 用户改 `parentOrgId` | — |
| 删除有子节点 | 查 `parent_org_id = id` | 拒绝删除；`WMS_ORG_HAS_CHILDREN` → 409 | 先删/移子节点 | — |
| 停用组织被选 | `listOrgOptions` 默认 `status=10`；保存校验 | `WMS_ORG_DISABLED` → 409 | 启用或换组织 | — |
| 租户越权 | `tenantId` 与登录租户不一致 | Sa-Token / Resolver 隔离 | — | 安全日志（平台） |
| 无权限写操作 | `@SaCheckPermission` | 403 无权限 | 管理员分配 perm | AC-ORG-14 |
| 并发双增同编码 | DB 唯一索引兜底 | 第二笔 409 | 重试换码 | — |

**不写操作幂等**：B1 组织 CRUD 为管理端操作，**不要求** `idempotencyKey`（PDA/B8 另论）。

**Handoff** → 验收用例 AC-ORG-04、08、10、12、14 必须覆盖上表。

---

## 16. 修订记录

| 版本 | 日期 | 说明 |
| --- | --- | --- |
| v1.6 | 2026-06 | 架构师 Step 8–9：§17 系统架构、§18 异常场景；Handoff 转 B1 验收 |
| v1.5 | 2026-06 | 架构师 Step 7：§15 接口设计 |
| v1.4 | 2026-06 | 架构师 Step 6：§14 数据库设计 |
| v1.3 | 2026-06 | Step 7：AC-ORG-01～15 + FR↔AC 矩阵 |
| v1.2 | 2026-06 | `parent_org_id` 默认 **0** 表示顶级（对齐 RuoYi） |
| v1.1 | 2026-06 | 主键统一为 `id`；API `orgId`；`tenant_id` 对齐 DATABASE |
| v1.0 | 2026-06 | B1 组织主数据 Step 6 初稿 |
