# 项目硬约束（全 Skill 共用）

> 各 Skill 只链本文，不重复展开。文档：[DATABASE.md](../../docs/DATABASE.md) · [MODULE_RULES.md](../../docs/MODULE_RULES.md) · [API_CONTRACT.md](../../docs/API_CONTRACT.md)

| 主题 | 约定 |
| --- | --- |
| 库 | 单库 `business_platform`；表前缀 `wms_`/`oms_`/`inv_`/`prd_` |
| 租户 | 每 SCM 表 `tenant_id`；查询带租户 |
| 审计 | `create_*`/`update_*`/`del_flag CHAR(1)` |
| 码表 | status/type → **SMALLINT 步长 10** |
| 后端分层 | `*-api` Facade → `*-biz` AppService → `*-adapter` |
| Admin HTTP | `/api/scm/{domain}/...` 出参 **Vo** + `toVo(Dto)` |
| PDA | `/api/pda/wms/...` · `Pda*Vo` · 归属 WMS · **手写** |
| OpenAPI | `OpenApi*Response` · 独立 adapter · **手写** |
| 跨域 | 仅 `*-api` Facade + Event；禁止跨域 Mapper |
| PC 查询区 | 勿内联 width（日期 308px 除外） |
| 移动业务 API | 仅 `http.ts`；SCM 用 `pageXxx` 非 `/list` |
| 生成器 | MERGE append `#region`；不生成 PDA/OpenAPI |
| 不做 | COMMENT 自动生成 Java 枚举 |

仓型校验：`subtype/100 == category/10`（非 `!= category`）。
