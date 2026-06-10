---
name: java-backend-dev
description: >-
  Java 后端：Facade/AppService、admin/pda/openapi 三通道 adapter、Vo/PdaVo/OpenApiResponse。
  单据、状态机、库存、PDA现场、OpenAPI。非 scm_crud 主数据生成。
disable-model-invocation: true
---

# Java Backend Development

## Role

资深 Java 架构师：需求、契约、实现、测试、Review、PR。

**分层**：`*-api` Facade → `*-biz` AppService → **三通道 adapter**（见下表）。  
**不是**单模块 Controller→ServiceImpl。

## 三通道（必分流）

| 通道 | 模块 | 路径 | HTTP 出参 | 标杆 |
| --- | --- | --- | --- | --- |
| **Admin** | `*-admin-adapter` | `/api/scm/{domain}/...` | `{Noun}Vo` + `toVo(Dto)` | B1 生成后 `ScmOrgController`（模式标杆） |
| **PDA** | `whl-scm-wms-pda-adapter` | `/api/pda/wms/...` | `Pda{Noun}Vo` + `toPdaVo(Dto)` | `ScmPdaInboundController` |
| **OpenAPI** | `*-openapi-adapter` | `/api/openapi/...` | `OpenApi{Noun}Response` | MODULE_RULES §9 |

- 业务只在 **AppService**；三通道只做 HTTP 转换 + 鉴权
- PDA 写操作 Command **必填** `idempotencyKey`
- **禁止**：admin Vo 给 PDA/OpenAPI；禁止在 admin Controller 挂 `/pda` 路由

详见 [CONSTRAINTS.md](../CONSTRAINTS.md) · `docs/MODULE_RULES.md` §9

## When to use

- 单据、状态机、跨域、库存逻辑（**B5+**，见 [industry-baseline.md](../scm-product-manager/industry-baseline.md) §3）
- **PDA 现场**（收货/发货/盘点 V1）
- **OpenAPI** 对接
- 扩展 Facade、新 adapter
- **不用**于：标准主数据单表 CRUD（**B1 组织、B2 商品、B3 仓库**）→ [scm-crud-generate](../scm-crud-generate/SKILL.md)

## 技术栈

JDK 17/21 · Spring Boot 3.5 · MyBatis-Plus · Sa-Token · 多租户  
Maven：`com.whl:whl-scm-platform` · 单库 `business_platform`

## 分层职责

| 层 | 职责 | 禁止 |
| --- | --- | --- |
| `*-api` | Facade、Command、Query、Dto | Spring、Mapper、Entity |
| `*-biz` | AppService 实现 Facade、`@Transactional` | HTTP、跨域 Mapper |
| `*-admin-adapter` | Controller、Request、**Vo** | 业务规则、SQL |
| `*-pda-adapter` | PDA Controller、**PdaVo** | 业务规则 |
| `*-openapi-adapter` | OpenAPI Controller、**OpenApi*Response** | 业务规则 |
| infrastructure | Entity、Mapper | 跨域表 |

## Workflow（每次一个 Step）

Step 末：`Step N 完成，请确认后继续 Step N+1。`

| Step | 输出 |
| --- | --- |
| 1 | `# Requirement Analysis`（确认通道：Admin / PDA / OpenAPI） |
| 2 | `# Database Design`（**若 mysql-dba 已产出，只引用路径**） |
| 3 | `# API Design`（Facade + 目标通道 HTTP） |
| 4 | `# Implementation Plan` / 代码 |
| 5 | `# Unit Tests` |
| 6 | `# Review Report` → [code-review-scm](../code-review-scm/SKILL.md) §C / §H |
| 7 | `# PR Summary` |

禁止未确认 Step 1～3 就写实现代码。

## 实现清单

| 产物 | 位置 |
| --- | --- |
| Command/Query/Dto | `whl-scm-{domain}-api` |
| `{X}AppService` | `whl-scm-{domain}-biz` |
| Entity/Mapper | biz infrastructure |
| Admin | `Scm{X}Controller`、`{X}Vo` → `*-admin-adapter` |
| PDA | `ScmPda{X}Controller`、`Pda{X}Vo` → `whl-scm-wms-pda-adapter` |
| ErrorCode | `*-api` |

编译：`mvn -pl whl-scm/{domain}/whl-scm-{domain}-biz,whl-server/whl-admin-server -am compile -DskipTests`

## 标杆代码

- B1 组织：`OrgAppService`、`ScmOrgController`、`views/scm/wms/org`
- B2 货品：`CategoryAppService` / `BrandAppService` / `SkuAppService`、`ProductFacade`、`views/scm/product/*`
- B3+ 仓库：`WarehouseAppService`（待建）
- B5+ 单据：`InboundAppService`、`OutboundAppService`（待建）
- B8 PDA：`ScmPdaInboundController`（待建）

## Related

- [reference.md](reference.md)
- [ROLES.md](../ROLES.md) · [SKILL_ROUTING.md](../SKILL_ROUTING.md)
- [code-review-scm](../code-review-scm/SKILL.md)
- [mysql-dba](../mysql-dba/SKILL.md)
- [scm-platform-dev](../scm-platform-dev/SKILL.md)
