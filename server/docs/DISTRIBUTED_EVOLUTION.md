# 分布式演进手册

> 从 **模块化单体** 到 **Dubbo / Spring Cloud 微服务** 的操作指南。  
> 前提：按 [ARCHITECTURE.md](./ARCHITECTURE.md) 开发，**拆的时候只换部署与通信，不换业务边界**。

---

## 1. 是否「最优」？

当前方案在 **whl 项目当前阶段** 下属于 **合理且可演进** 的设计，不是理论上的「唯一最优」，但具备：

| 能力 | 说明 |
| --- | --- |
| 边界清晰 | Framework / Platform / SCM / Server 四层 |
| 单一职责 | 每 Maven 模块一件事 |
| 契约先行 | `*-api` 零 Spring，可独立发 client jar |
| 单库前缀 | 现在简单，将来拆库成本低 |
| 通信双轨 | Facade 同步 + Event 异步 |

**仍可加强的点**（本文档 + 配套规范已覆盖）：

- 幂等键、Outbox、链路追踪 ID 写入规范  
- 服务名注册表、Gateway 路由约定  
- ArchUnit 依赖检查进 CI  
- 事件注册表强制评审  

---

## 2. 服务名注册表（现在就要对齐）

| Maven 模块 | 未来服务名 | 未来端口（示例） | 数据库（远期） |
| --- | --- | --- | --- |
| `whl-admin-server` | `whl-admin-service` | 8080 | 连接多域或经 Gateway |
| `whl-scm-oms-biz` | `whl-oms-service` | 8081 | `business_platform` → `whl_oms` |
| `whl-scm-wms-biz` | `whl-wms-service` | 8082 | → `whl_wms` |
| `whl-scm-inventory-biz` | `whl-inventory-service` | 8083 | → `whl_inventory` |
| `whl-platform-system` | `whl-system-service` | 8084 | 平台库 |
| `whl-pda-server` | `whl-pda-service` | 8085 | 仅 WMS |

**命名规则**：`whl-{domain}-service`，与 Nacos `spring.application.name` 一致。

---

## 3. 演进阶段

```text
L0  模块化单体（现在）
    whl-admin-server 一个进程
    rpc.mode=local
    event.mode=local
    单库 business_platform

L1  逻辑分布式
    引入 Nacos（配置 + 注册）
    引入 RocketMQ / Kafka
    Outbox 表（oms_outbox、wms_outbox）
    rpc.mode=dubbo 试点 inventory-service
    仍可不拆进程，只验证 RPC

L2  物理微服务
    按域独立 boot
    Spring Cloud Gateway 或 APISIX
    链路追踪 SkyWalking / OTel
    独立库（表前缀 → 整库迁移）

L3  治理增强
    熔断 Sentinel、限流、灰度
    读写分离 / ES 报表库
    独立 PDA 集群
```

---

## 4. 拆分前置检查清单（必须全部满足）

### 4.1 代码边界

- [ ] 目标域 **无** 对其他域 `*-biz` 的 Maven 依赖  
- [ ] 目标域 **无** 跨前缀表的 Mapper SQL  
- [ ] Controller 全在 `*-adapter`，不在 `*-biz`  
- [ ] Facade 接口在 `*-api` 且已稳定 1 个迭代  

### 4.2 数据

- [ ] 目标域表均为同一前缀（如仅 `wms_*`）  
- [ ] 无跨域外键  
- [ ] Outbox 表已启用（异步事件可靠投递）  

### 4.3 运维

- [ ] `spring.application.name` 已按注册表命名  
- [ ] 健康检查 `/actuator/health` 可用  
- [ ] 日志带 `traceId`、`tenantId`  

---

## 5. 通信切换（不改 Biz 代码）

### 5.1 单体 local

```yaml
whl:
  rpc:
    mode: local
  event:
    mode: local
```

### 5.2 Dubbo

```yaml
whl:
  rpc:
    mode: dubbo
  event:
    mode: mq

dubbo:
  application:
    name: whl-oms-service
  registry:
    address: nacos://127.0.0.1:8848
```

Provider：`@DubboService` 实现 `OrderFacade`  
Consumer：`@RpcReference` 不变  

### 5.3 Gateway 路由（示例）

```text
/api/scm/oms/**      → whl-oms-service
/api/scm/wms/**      → whl-wms-service
/api/scm/inventory/**→ whl-inventory-service
/system/**           → whl-system-service
/pda/**              → whl-pda-service
```

---

## 6. Outbox 模式（L1 必做）

**表**（域内，同前缀）：

```text
oms_outbox
wms_outbox
```

| 字段 | 说明 |
| --- | --- |
| id | 主键 |
| event_id | 与 EventMetadata 一致 |
| event_type | 事件类型 |
| payload | JSON |
| status | PENDING / SENT / FAILED |
| created_at | 创建时间 |

**流程**：业务事务内写业务表 + outbox → 定时任务/MQ 投递 → 标记 SENT。

---

## 7. 幂等与 tracing

| 场景 | 机制 |
| --- | --- |
| HTTP 写接口 | Header `Idempotency-Key` 或 Command 字段 |
| Facade 调用 | `idempotencyKey` 必填 |
| Event 消费 | `eventId` 去重表 `*_event_consume_log` |
| 全链路 | `X-Trace-Id` 经 Gateway → Adapter → Facade 传递 |

---

## 8. 推荐拆分顺序

```text
1. whl-inventory-service   # 被依赖多、接口清晰
2. whl-oms-service         # 订单核心
3. whl-wms-service + pda   # 仓储与 PDA
4. whl-system-service      # 平台最后拆或保留共享
```

---

## 9. 反模式（禁止）

| 反模式 | 后果 |
| --- | --- |
| 拆服务后再补 `*-api` | 契约混乱 |
| 分布式事务（2PC）跨 OMS/WMS | 复杂且难运维 |
| 各服务共用一个「大库」长期不拆 | 假微服务 |
| Event 无 Outbox 直接发 MQ | 丢消息 |
| PDA 直连 OMS 不经过 WMS | 边界破坏 |

---

## 10. 文档维护

新增域 / 新 Event / 新 Facade 时：

1. 更新 [API_CONTRACT.md](./API_CONTRACT.md) 事件注册表  
2. 更新本文 **服务名注册表**  
3. 更新 [DATABASE.md](./DATABASE.md) 前缀表  
4. PR 勾选 [MODULE_RULES.md](./MODULE_RULES.md) 检查项  

---

## 11. 相关文档

- [ARCHITECTURE.md](./ARCHITECTURE.md)
- [API_CONTRACT.md](./API_CONTRACT.md)
- [DATABASE.md](./DATABASE.md)
- [ROADMAP.md](./ROADMAP.md)
