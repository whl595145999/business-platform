# 供应链业务平台 — 架构设计

> 作者：whl | 产品：供应链业务平台 | 基座：RuoYi-Vue-Plus 5.6.1  
> 设计目标：**模块化单体 → 可拆分分布式**，单一职责、边界清晰、契约先行

**文档体系**

| 文档 | 用途 |
| --- | --- |
| 本文 | 总览与分层 |
| [NAMING.md](./NAMING.md) | 命名 |
| [MODULE_RULES.md](./MODULE_RULES.md) | 依赖红线 |
| [DATABASE.md](./DATABASE.md) | 单库、表前缀、拆库路径 |
| [API_CONTRACT.md](./API_CONTRACT.md) | Facade / Event / 幂等 |
| [DISTRIBUTED_EVOLUTION.md](./DISTRIBUTED_EVOLUTION.md) | **分布式演进操作手册** |
| [ROADMAP.md](./ROADMAP.md) | 实施阶段 |

---

## 1. 设计结论

| 决策 | 结论 |
| --- | --- |
| `ruoyi-common` → **Framework** | **合适**。Maven：`whl-framework-*`，包：`com.whl.framework.*` |
| 数据库 | **单库 `business_platform`，不拆库**；新表用 `oms_` / `wms_` / `inv_` / `prd_` 前缀 |
| 架构风格 | **模块化单体 + 域内六边形 + API 契约 + Event 异步** |
| 是否最优 | **当前阶段下合理且可演进**；配套规范见各专题文档，拆分前按清单验收 |

---

## 2. 物理模块全景

```text
whl-scm-platform/                    groupId: com.whl
│
├── whl-framework/                     ← 原 ruoyi-common（纯技术，无业务）
│   ├── whl-framework-kernel           # 纯 Java：EventMetadata、ErrorCode
│   ├── whl-framework-core / web / mybatis / redis / security
│   ├── whl-framework-rpc            # local | dubbo | feign
│   └── whl-framework-event           # local | mq + Outbox 支持
│
├── whl-platform/                      ← 原 whl-platform
│   ├── whl-platform-system            # 用户/租户/权限/客户端
│   ├── whl-platform-auth              # 统一认证 /auth/* ✅
│   ├── whl-platform-workflow
│   └── whl-platform-api               # UserFacade、WorkflowFacade ✅
│
├── whl-scm/
│   ├── whl-scm-oms/     (api | biz | admin-adapter)
│   ├── whl-scm-wms/     (api | biz | admin-adapter | pda-adapter)
│   ├── whl-scm-inventory/
│   ├── whl-scm-product/
│   └── whl-scm-erp/
│
├── whl-server/
│   ├── whl-admin-server          # 管理端进程 [8080, 28080]
│   └── whl-extend/
│       ├── whl-monitor-admin     # 监控 [9090]
│       └── whl-snailjob-server   # 任务调度 [8800, 17888]
│   └── whl-pda-server            # 远期 PDA 独立进程
│
└── deploy/
```

---

## 3. 逻辑分层

```text
Client → Server → Adapter → Biz → Platform → Framework
              ↘ *-api 契约 ↗        （跨域）
              ↘ Event 异步 ↗
```

**依赖红线**：见 [MODULE_RULES.md](./MODULE_RULES.md)

---

## 4. 域内结构（单一职责）

```text
com.whl.scm.wms.biz
├── application      # 用例编排、@Transactional、调 Facade、发 Event
├── domain           # 模型、领域服务、Repository 接口
└── infrastructure   # Mapper、Repository 实现、Outbox
```

HTTP 只在 `whl-scm-wms-*-adapter`，不在 biz。

**三通道表现层**：同一 Facade，admin 用 `{Noun}Vo`、OpenAPI 用 `OpenApi*Response`、PDA 用 `Pda*Vo`，各 adapter 独立转换，禁止直出 Facade Dto。详见 [API_CONTRACT.md](./API_CONTRACT.md) §2.5。

---

## 5. Framework 层（原 ruoyi-common）

| 放 | 不放 |
| --- | --- |
| Web/Security/Redis/MyBatis 封装 | 订单、库存规则 |
| RPC/Event 抽象 | SCM Entity/Mapper |
| 全局异常、TraceId 过滤器 | 用户 CRUD |

---

## 6. 业务域

| 域 | 表前缀 | 远期服务名 |
| --- | --- | --- |
| OMS | `oms_` | whl-oms-service |
| WMS | `wms_` | whl-wms-service |
| Inventory | `inv_` | whl-inventory-service |
| Product | `prd_` | whl-product-service |
| ERP | `erp_` | whl-erp-service |
| Platform | `sys_` / `flow_` | whl-system-service |

PDA = WMS 的 `pda-adapter`，不是独立域。

---

## 7. 跨域协作

| 场景 | 方式 |
| --- | --- |
| 校验 SKU、预占库存 | 同步 `*-api` Facade |
| 确认订单 → 创建出库任务 | 异步 Event |
| 发货 → 回写订单 | 异步 Event |

详见 [API_CONTRACT.md](./API_CONTRACT.md)

---

## 8. 数据库（当前）

- 库：`business_platform` 不改、不拆  
- 存量：`sys_*` 不动  
- 新增：同库 + 域前缀，见 [DATABASE.md](./DATABASE.md)

---

## 9. 分布式演进摘要

```text
L0  单体：rpc=local, event=local, 单库
L1  逻辑分布式：Nacos + MQ + Outbox + rpc=dubbo 试点
L2  物理微服务：Gateway + 按域独立进程 + 拆库
```

**完整步骤、检查清单、Gateway 路由**：见 [DISTRIBUTED_EVOLUTION.md](./DISTRIBUTED_EVOLUTION.md)

---

## 10. 与 RuoYi 映射

| 现在 | 目标 | 状态 |
| --- | --- | --- |
| `ruoyi-common` | `whl-framework` | ✅ 阶段 3 |
| `ruoyi-modules` | `whl-platform` | ✅ 阶段 3 |
| `ruoyi-admin` | `whl-admin-server` | ✅ 阶段 3B |
| `ruoyi-extend` | `whl-extend` | ✅ 阶段 3B |
| `org.dromara.*`（启动类） | `com.whl.boot.*` | ✅ 阶段 3B |
| 无 | `whl-scm/*` | ✅ 阶段 2 |

---

## 11. 实施路线

见 [ROADMAP.md](./ROADMAP.md)。
