# API 契约规范（Facade / Command / Event）

> 目标：`*-api` 模块 jar **零 Spring 依赖**，单体与 Dubbo/Feign **共用同一份契约**。

---

## 1. 模块职责

| 模块 | 放什么 | 不放什么 |
| --- | --- | --- |
| `whl-scm-{domain}-api` | Facade 接口、Command、Query、DTO、Event POJO | `@Service`、Mapper、Controller |
| `whl-platform-api` | 平台 Facade（`UserFacade`、`WorkflowFacade`）、DTO | `@Service`、Mapper、Controller |
| `whl-framework-kernel` | `EventMetadata`、`DomainEventPayload`、值对象 | 业务 Facade |

**统一约定**：跨模块契约模块一律 `*-api`，对外接口一律 `*Facade`；实现类在对应 `*-biz` 或 `whl-platform-system` / `whl-platform-workflow`。

---

## 2. Facade 设计规则

```java
// 示例：whl-scm-oms-api
public interface OrderFacade {

    OrderResult createOrder(CreateOrderCommand command);

    OrderResult confirmOrder(ConfirmOrderCommand command);

    OrderDto getOrder(GetOrderQuery query);
}
```

| 规则 | 说明 |
| --- | --- |
| 接口稳定 | 只增方法不删；破坏性变更用 `OrderFacadeV2` |
| 入参 | 用 Command / Query，不用散参 |
| Facade 出参 | 用 DTO，不暴露 Entity |
| **HTTP 出参** | **禁止**任何通道直接把 Facade Dto 作为 JSON；各 adapter 有独立表现模型，见 [§2.5](#25-三通道表现层admin--openapi--pda) |
| 租户 | Command 带 `tenantId`，或由 adapter 从 TenantContext 填充 |
| 幂等 | 写 Command 必须带 `idempotencyKey` |
| 实现 | 仅在 `*-biz` 的 `application` 包，`@Service` + 接口名 `*AppService` 实现 Facade |

### 2.5 三通道表现层（Admin / OpenAPI / PDA）

> 行业惯例：**Facade Dto = 域内契约**；**每种 HTTP 消费者 = 独立 JSON 模型**。三者不共用类，逻辑仍只写在 `*-biz`。

```text
                         ┌─────────────────────┐
                         │  *-api Facade       │
                         │  Command/Query/Dto  │
                         └──────────┬──────────┘
                                    │
          ┌─────────────────────────┼─────────────────────────┐
          ▼                         ▼                         ▼
   *-admin-adapter           *-openapi-adapter          *-pda-adapter
   /api/scm/...              /openapi/scm/...           /api/pda/wms/...
          │                         │                         │
     {Noun}Vo              OpenApi{Noun}Response          Pda{Noun}Vo
   controller/vo/         controller/response/            controller/vo/
```

| 通道 | Maven 模块 | 进程 | URL 前缀 | 出参类型 | 包路径 | 转换方法 |
| --- | --- | --- | --- | --- | --- | --- |
| **Admin** | `whl-scm-{domain}-admin-adapter` | `whl-admin-server` | `/api/scm/{domain}/...` | `{Noun}Vo` | `.../admin/controller/vo/` | `toVo(Dto)` |
| **OpenAPI** | `whl-scm-{domain}-openapi-adapter` | `whl-openapi-server` | `/openapi/scm/...`（`context-path=/openapi`） | `OpenApi{Noun}Response` | `.../openapi/controller/response/` | `toOpenApiResponse(Dto)` |
| **PDA** | `whl-scm-wms-pda-adapter`（WMS 现场） | `whl-pda-server` 或暂挂 admin | `/api/pda/wms/...` | `Pda{Noun}Vo` | `.../pda/controller/vo/` | `toPdaVo(Dto)` |

**统一转换链（每个 adapter 内）：**

```text
HTTP Request → Command/Query → Facade → Dto → toXxx(Dto) → R<表现模型>
```

**禁止：**

- Facade `{Noun}Dto` 直接作为任一通道的 JSON 返回类型
- Admin 的 `{Noun}Vo` 给 OpenAPI 或 PDA 复用（字段、稳定性、版本策略不同）
- 在 adapter 写业务规则（if/else 改库存、改状态）；只调 Facade

**命名示例（入库单）：**

| 层 | 类名 |
| --- | --- |
| Facade | `InboundDto` / `InboundLineDto` |
| Admin | `InboundVo` / `InboundLineVo` |
| OpenAPI | `OpenApiInboundResponse` / `OpenApiInboundLineResponse` |
| PDA | `PdaInboundVo` / `PdaInboundLineVo` |

Admin 前端 `types.ts` 中 `{Noun}VO` 与 `{Noun}Vo` 字段对齐；OpenAPI 在 Swagger / 对外文档中维护 `OpenApi*Response` schema。

#### 认证与权限（分通道）

| 通道 | 认证 | 权限 / 数据范围 |
| --- | --- | --- |
| Admin | Sa-Token 用户 | `@SaCheckPermission` + RuoYi 数据权限（dept/org） |
| OpenAPI | OpenAPI client + token | 按 client 绑定 tenant、渠道、授权仓范围 |
| PDA | 仓管员 / 设备登录 | 绑定 warehouse + operateOrg；写操作 **必传** `idempotencyKey` |

#### OpenAPI 对外契约（上生产须满足）

- 路径或 Header 做 **API 版本**（如 `/openapi/scm/v1/...`）
- 字段变更 **只增不破**；破坏性变更升 major 版本
- 错误响应含 **机器可读 `code`**（见 [§6](#6-错误码)），不只中文 `message`
- 详见 [OPENAPI_CALL_DEMO.md](./OPENAPI_CALL_DEMO.md)

#### PDA 约定

- PDA 不是独立业务域，归属 WMS `pda-adapter`（见 [ARCHITECTURE.md](./ARCHITECTURE.md) §6）
- 出参字段 **少于** Admin（扫码、确认类接口）；入参带条码、库位、实收数量等
- 弱网重试：写接口 Command **必须**带 `idempotencyKey`（见 Command 示例 §3）

#### 生成器边界

| 模板 | 生成范围 |
| --- | --- |
| `scm_crud` | 仅 **admin-adapter** 的 `{Business}Vo` + `Scm{Business}Controller` |
| OpenAPI / PDA | **手写** adapter + 专用 Response/Vo（接口形态与 admin CRUD 不同） |

---

## 3. Command / Query 命名

| 类型 | 格式 | 示例 |
| --- | --- | --- |
| 写命令 | `{Verb}{Noun}Command` | `CreateOrderCommand` |
| 读查询 | `Get{Noun}Query` / `{Noun}PageQuery` | `GetOrderQuery` |
| 结果 | `{Noun}Result` / `{Noun}Dto` | `OrderResult` |

### 写 Command 必备字段

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderCommand {

    /** 租户编号 */
    private String tenantId;

    /** 幂等键（PDA/重试场景必填） */
    private String idempotencyKey;

    /** 履约仓库编码 */
    private String warehouseCode;

    /** 订单明细行 */
    private List<OrderLineCommand> lines;
}
```

> **约定**：`whl-scm` 契约与 HTTP 请求体统一用 **`@Data` + `@NoArgsConstructor` + `@AllArgsConstructor` POJO**，字段 `/** 注释 */`，**不用 Java `record`**。访问字段用 getter（`command.getTenantId()`），不用 record 风格 `command.tenantId()`。  
> **`boolean` 原始类型**：Lombok 生成 `isXxx()`（如 `command.isShipmentConsume()`），不是 `getXxx()`。  
> **例外**：`whl-framework-kernel` 的 `EventMetadata` 仍为 record（信封值对象），访问用 **`metadata.tenantId()`** / **`metadata.eventId()`**，不是 getter。  
> **Maven**：`whl-scm/pom.xml` 父 POM 继承 `lombok`（`provided`），供各 `*-api` / `*-biz` / adapter 编译 POJO。

---

## 4. Event 设计规则

### 4.1 定义（在 `*-api`，Lombok POJO）

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderConfirmedEvent implements DomainEventPayload {

    /** 事件元数据 */
    private EventMetadata metadata;

    /** 订单号 */
    private String orderNo;

    /** 发货仓库编码 */
    private String warehouseCode;

    public static final String EVENT_TYPE = "oms.order.OrderConfirmed";
    public static final String VERSION = "1";

    public static OrderConfirmedEvent of(String tenantId, String orderNo, String warehouseCode) {
        return new OrderConfirmedEvent(
            EventMetadata.of(EVENT_TYPE, VERSION, tenantId),
            orderNo,
            warehouseCode
        );
    }

    @Override
    public String eventType() {
        return EVENT_TYPE;
    }

    @Override
    public String version() {
        return VERSION;
    }
}
```

### 4.2 EventMetadata（whl-framework-kernel）

| 字段 | 说明 |
| --- | --- |
| `eventId` | UUID，消费端幂等键 |
| `eventType` | 域.名词.动词，如 `oms.order.OrderConfirmed` |
| `version` | 字符串 `"1"`，不兼容时升版本 |
| `tenantId` | 租户 |
| `occurredAtEpochMilli` | 发生时间 |

**访问方式（record，与 POJO 不同）**：`event.getMetadata().tenantId()`、`event.getMetadata().eventId()`。监听方示例见 `OrderConfirmedEventListener`。

### 4.3 注册表（新增事件必须登记）

| eventType | 版本 | 发布域 | 订阅域 | 说明 |
| --- | --- | --- | --- | --- |
| `oms.order.OrderConfirmed` | 1 | OMS | WMS | 创建出库任务 |
| `inventory.stock.StockReserved` | 1 | Inventory | OMS | 预占回写 |
| `wms.shipping.ShipmentCompleted` | 1 | WMS | OMS | 发货完成 |

### 4.4 发布（在 `*-biz`）

```java
domainEventPublisher.publishPayload(
    OrderConfirmedEvent.of(tenantId, orderNo, warehouseCode)
);
```

单体：Spring Event；分布式：同 payload 投递 MQ，**不改 Event 类**。

---

## 5. 跨域调用矩阵

| 调用方 | 被调方 | 方式 | 接口 |
| --- | --- | --- | --- |
| OMS | Product | 同步 | `ProductFacade.validateSku` |
| OMS | Inventory | 同步 | `InventoryFacade.reserve` |
| OMS | WMS | **异步** | `OrderConfirmedEvent` |
| WMS | Inventory | 同步 | `InventoryFacade.confirmDeduct` |
| WMS | OMS | **异步** | `ShipmentCompletedEvent` |

**默认原则**：读/sync 用 Facade；状态流转/解耦用 Event。

---

## 6. 错误码

```text
格式：{域}{3位数字}

OMS001  订单不存在
OMS002  订单状态不允许确认
INV001  库存不足
WMS001  出库任务不存在
```

定义在 `whl-framework-kernel` 或各域 `*-api` 的 `*ErrorCode` 枚举，Biz 抛 `ServiceException(code, message)`。

---

## 7. RPC 抽象（whl-framework-rpc）

```java
public class OrderConfirmAppService implements OrderFacade {

    @RpcReference
    private InventoryFacade inventoryFacade;
}
```

| 模式 | 配置 | 行为 |
| --- | --- | --- |
| `local` | 默认 | 注入 Spring Bean |
| `dubbo` | 后期 | `@DubboReference` |
| `feign` | 后期 | Feign Client |

**Biz 代码不感知**具体 RPC 实现。

---

## 8. HTTP 通道与进程

| 通道 | 进程 | Controller 映射前缀 | 对外完整 URL 示例 |
| --- | --- | --- | --- |
| Admin | `whl-admin-server` | `/api/scm/{domain}/...` | `POST /api/scm/oms/orders` |
| OpenAPI | `whl-openapi-server`（`context-path=/openapi`） | `/scm/{domain}/...` | `GET /openapi/scm/inventory/stock` |
| PDA | `whl-pda-server`（远期）或 admin | `/api/pda/wms/...` | `POST /api/pda/wms/inbound/confirm-receive` |
| OpenAPI 认证 | `whl-openapi-server` | `/auth/*` | `POST /openapi/auth/login` |

- OpenAPI 进程内 `@RequestMapping` **不含** `/openapi`，由 `context-path` 统一加前缀
- 各 adapter 只调 Facade；跨域走 `*-api` 或 Event
- 表现模型分通道，见 [§2.5](#25-三通道表现层admin--openapi--pda)
- 后期 Gateway：`/openapi/**` → `whl-openapi-server`，`/api/**` → `whl-admin-server`

---

## 9. 版本与兼容

| 变更类型 | 做法 |
| --- | --- |
| Facade 新增方法 | 直接加，兼容 |
| Event 增字段 | 升 `version` → `2`，消费者双读过渡 |
| Facade 破坏性变更 | 新接口 `OrderFacadeV2`，并行运行后下线 V1 |
| OpenAPI Response 破坏性变更 | 升 URL 版本 `/v2/` 或新 schema 名，旧版并行过渡 |

---

## 10. 相关文档

- [MODULE_RULES.md](./MODULE_RULES.md) §8～§10（Admin / OpenAPI / PDA adapter）
- [OPENAPI_CALL_DEMO.md](./OPENAPI_CALL_DEMO.md)
- [DISTRIBUTED_EVOLUTION.md](./DISTRIBUTED_EVOLUTION.md)

---

## 11. Demo 合并 jar vs 正式分域（必注意）

当前 `whl-scm/demo` 为 **架构样例**，下列合并 **仅 Demo 允许**：

| 合并方式 | Demo | 正式 |
| --- | --- | --- |
| 多域契约同一 api jar | `whl-scm-demo-api` | 拆为 `whl-scm-{domain}-api` |
| 多域实现同一 biz jar | `whl-scm-demo-biz` | 拆为 `whl-scm-{domain}-biz`，**禁止 biz 互依赖** |
| Facade 实现命名 | `OrderDemoAppService` | `{用例}AppService`（非 `*FacadeImpl`） |

**包名**：Demo 已使用 `com.whl.scm.oms.api.*` 等正式包路径；拆 Maven 模块时 **无需改 import**。

**跨域**：Demo 与正式相同 — 同步 `*Facade`、异步 Event；禁止注入他域 biz 实现类。

完整检查清单见 [MODULE_RULES.md](./MODULE_RULES.md) §6。
