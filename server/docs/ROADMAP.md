# 分阶段实施路线

> 原则：一步一步来 | 单库不拆 | 新表用域前缀 | Framework = 原 ruoyi-common

---

## 阶段 0：定规范 ✅

- [x] 产品名、groupId、分层、表前缀策略
- [x] `ruoyi-common` → **`whl-framework`** 命名确认
- [x] 文档：ARCHITECTURE / NAMING / MODULE_RULES / ROADMAP

---

## 阶段 1：元数据品牌化（小改，不动包名/库表）✅

| 任务 | 状态 |
| --- | --- |
| 根 POM `com.whl:whl-scm-platform` | ✅ |
| application.yml 应用名、Swagger 标题 | ✅ |
| README 作者 whl、供应链业务平台、RuoYi 致谢 | ✅ |
| 前端标题（admin-web / admin-app） | ✅ |
| **不改** Java 包名、`business_platform` 库、`sys_*` 表 | ✅ |

---

## 阶段 2：Framework Kernel ✅

| 任务 | 状态 |
| --- | --- |
| `whl-framework-kernel` | ✅ EventMetadata、DomainEventPayload、ScmErrorCode |

---

## 阶段 3：Framework / Platform 渐进 rename ✅

| 任务 | 状态 |
| --- | --- |
| `ruoyi-common/*` → `whl-framework/*` | ✅ |
| `ruoyi-modules/*` → `whl-platform/*` | ✅ |
| `org.dromara.common.*` → `com.whl.framework.*` | ✅ |
| `org.dromara.system.*` 等 → `com.whl.platform.*` | ✅ |
| `ruoyi-admin` → `whl-admin-server` | ✅ 阶段 3B |
| `ruoyi-extend` → `whl-extend` | ✅ 阶段 3B |
| `com.whl.boot.*` 启动类 | ✅ 阶段 3B |

---

## 阶段 3B：Server 层 rename ✅

| 任务 | 状态 |
| --- | --- |
| `whl-server/` 聚合 POM | ✅ |
| `ruoyi-admin` → `whl-server/whl-admin-server` | ✅ |
| `ruoyi-extend` → `whl-server/whl-extend` | ✅ |
| `ruoyi-monitor-admin` → `whl-monitor-admin` | ✅ |
| `ruoyi-snailjob-server` → `whl-snailjob-server` | ✅ |
| `DromaraApplication` → `com.whl.boot.AdminApplication` | ✅ |
| Docker / `ry.sh` / compose 同步 | ✅ |

---

## 阶段 3C：装配与 API 契约统一 ✅

| 任务 | 状态 |
| --- | --- |
| `whl-scm-demo-admin-adapter` 传递依赖 `whl-scm-demo-biz` | ✅ |
| `whl-admin-server` 只依赖 demo-adapter（不直接依赖 biz） | ✅ |
| `whl-platform-api`（`UserFacade` / `WorkflowFacade`） | ✅ |
| `UserFacadeImpl` 在 `whl-platform-system` | ✅ |
| 登录代码迁出 server → `whl-platform-auth` | ✅ 阶段 3D |

---

## 阶段 3D：平台统一认证 ✅

| 任务 | 状态 |
| --- | --- |
| 新建 `whl-platform-auth`（`/auth/*`、验证码、策略、Listener） | ✅ |
| `whl-admin-server` 依赖 auth，移除 server 内登录代码 | ✅ |
| admin / openapi / pda 共用 auth jar（远期多进程 + 共享 Redis） | ✅ `whl-openapi-server` 已接入 |
| 独立 `whl-auth-server` 认证中心 | 远期 |

详见 [AUTH_MIGRATION.md](./AUTH_MIGRATION.md)。

---

## 阶段 5：业务落地（进行中）

设计参考：B1 [PRODUCT.md](./domains/wms/prd/B1-org/PRODUCT.md) · [TECH.md](./domains/wms/design/B1-org/TECH.md) · B2 [PRODUCT.md](./domains/product/prd/B2-product/PRODUCT.md) · [TECH.md](./domains/product/design/B2-product/TECH.md)

| 域 | 状态 |
| --- | --- |
| B1 组织 `wms_org` | ✅ 已验收 |
| B2 货品 `prd_*` | ✅ Admin CRUD 已实现 |
| B3+ 仓库 / 库存 / OMS | ⏸ 待 PRD + DDL 后按波次推进 |

---

## 阶段 6：前端业务页面（进行中）

| 项 | 状态 |
| --- | --- |
| admin-web SCM 页面 | ✅ B1 组织 + B2 货品（`src/views/scm/*`） |
| 菜单 SQL | `script/sql/wms/_gen/wms_org_menu.sql` · `script/sql/product/_gen/prd_product_menu.sql` |

- admin-app SCM 页面 — 远期
- 可选 `whl-pda-server` 拆分 — 远期

---

## 阶段 7：分布式（远期，可选拆库）

- MQ + Outbox + Dubbo/Feign
- 按域拆进程；表前缀映射到独立库

---

## 下一步

1. B2 货品 AC 验收（效期/条码/`validateSku`）
2. B3 仓库 PRD 定稿后再写代码
3. 本地库重建：`script/wms/scm-db-reinit.sh --with-menu`
