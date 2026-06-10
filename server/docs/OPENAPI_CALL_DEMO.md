# OpenAPI 调用与编写规范 Demo

> **注意**：`whl-scm/demo` 已退役。OpenAPI 分层规范仍适用；SCM 模块从 0 重建中，`*-openapi-adapter` 在 B8 等波次按需恢复。  
> SCM 验收：`script/wms/b1-org-ac-acceptance.sh`（B1 组织）

> 可复制本 Demo 分层思路，按同样分层新增正式业务。HTTP 脚本：`script/demo/openapi-call-demo.sh` / `.http`（OpenAPI 健康检查样例，与 SCM 内存 Demo 无关）

---

## 1. 分层一览

```text
Client
  → whl-openapi-server          （进程，context-path=/openapi）
  → *-openapi-adapter           （HTTP：/scm/{domain}/*，不写业务规则）
  → *-api Facade                （契约：Command / Query / DTO）
  → *-biz *AppService           （业务规则、事务、Mapper、Event）
```

| 层 | 模块示例 | 职责 |
| --- | --- | --- |
| api | `whl-scm-demo-api` | `ScmDemoFacade`、`GetDemoHealthQuery`、`DemoHealthDto` |
| biz | `whl-scm-demo-biz` | `ScmDemoSnapshotAppService implements ScmDemoFacade` |
| openapi-adapter | `whl-scm-demo-openapi-adapter` | `ScmOpenApiDemoController` |
| server | `whl-openapi-server` | 启动、`application.yml`，不放业务 |

### admin / openapi / pda adapter 的 HTTP 出参

| 入口 | 路径前缀 | JSON 出参 | 说明 |
| --- | --- | --- | --- |
| **admin adapter** | `/api/scm/...` | **`{Noun}Vo`** | 调 Facade 得 Dto，Controller 内 `toVo(Dto)` |
| **openapi adapter** | `/openapi/scm/...` | **`OpenApi{Noun}Response`** | Controller 内 `toOpenApiResponse(Dto)`，**禁止**长期 `R<{Noun}Dto>` |
| **pda adapter** | `/api/pda/wms/...` | **`Pda{Noun}Vo`** | 现场精简字段，`toPdaVo(Dto)` |

三通道细则：[API_CONTRACT.md](./API_CONTRACT.md) §2.5、[MODULE_RULES.md](./MODULE_RULES.md) §9。

---

## 2. 规范样例代码（只读 · 最小模板）

新增一个 OpenAPI 接口，按下面 4 步复制即可。

### 步骤 1 · api：Query + DTO + Facade 方法

```java
// 需 import lombok.Data / NoArgsConstructor / AllArgsConstructor
// whl-scm-{domain}-api/.../query/GetXxxQuery.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetDemoHealthQuery {
    /** 租户编号 */
    private String tenantId;
}

// whl-scm-{domain}-api/.../dto/DemoHealthDto.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemoHealthDto {
    private String tenantId;
    private String domain;
    private String message;
}

// whl-scm-{domain}-api/.../facade/ScmDemoFacade.java
DemoHealthDto health(GetDemoHealthQuery query);
```

### 步骤 2 · biz：AppService 实现 Facade

```java
// whl-scm-{domain}-biz/.../XxxAppService.java
@Service
@RequiredArgsConstructor
public class ScmDemoSnapshotAppService implements ScmDemoFacade {

    @Override
    public DemoHealthDto health(GetDemoHealthQuery query) {
        // ★ 业务写在这里：校验、查库、领域规则
        return new DemoHealthDto(query.getTenantId(), "demo", "...");
    }
}
```

### 步骤 3 · openapi-adapter：Controller + OpenApi Response

```java
// whl-scm-{domain}-openapi-adapter/.../response/OpenApiDemoHealthResponse.java
@Data
@NoArgsConstructor
public class OpenApiDemoHealthResponse {
    private String tenantId;
    private String domain;
    private String message;
}

// whl-scm-{domain}-openapi-adapter/.../controller/ScmOpenApiDemoController.java
@GetMapping("/health")
public R<OpenApiDemoHealthResponse> health() {
    GetDemoHealthQuery query = new GetDemoHealthQuery(TenantHelper.getTenantId());
    DemoHealthDto dto = scmDemoFacade.health(query);
    return R.ok(toOpenApiResponse(dto));
}

private OpenApiDemoHealthResponse toOpenApiResponse(DemoHealthDto dto) {
    OpenApiDemoHealthResponse r = new OpenApiDemoHealthResponse();
    r.setTenantId(dto.getTenantId());
    r.setDomain(dto.getDomain());
    r.setMessage(dto.getMessage());
    return r;
}
```

> **与 admin 区别**：同一 Facade 能力若也要给 RuoYi 管理端，在 `*-admin-adapter` 用 `{Noun}Vo` + `toVo(Dto)`。OpenAPI **不要**复用 admin Vo，也**不要**把 Facade Dto 当对外 JSON 类型。

对外 URL：`GET /openapi/scm/demo/health`（`/openapi` 来自 server `context-path`）

### 步骤 4 · server：装配 adapter

`whl-openapi-server/pom.xml` 依赖对应 `*-openapi-adapter`，无需改 Java。

---

## 3. 写操作样例（订单 · 含跨域 Facade + Event）

| 步骤 | 文件 | 要点 |
| --- | --- | --- |
| Command | `CreateOrderCommand` | 必含 `tenantId`、`idempotencyKey` |
| Facade | `OrderFacade.createOrder` | 接口在 `oms-api` |
| 业务 | `OrderDemoAppService` | 幂等、调 `InventoryFacade`、发 `OrderConfirmedEvent` |
| HTTP | `ScmOpenApiDemoController.createOrder` | Request → Command → `orderFacade.createOrder` → `toOpenApiResponse` |

跨域：**同步**调其他域 `*-api` Facade；**异步**发 Event（见 `OrderDemoAppService.confirmOrder`）。

---

## 4. HTTP 调用

### 前置

1. 启动 MySQL / Redis
2. 执行 `script/sql/update/update_openapi_client.sql`（已有库）
3. 启动 `OpenApiApplication`（8081）

### 一键脚本

```bash
chmod +x script/demo/openapi-call-demo.sh
./script/demo/openapi-call-demo.sh
```

### 手动 curl

```bash
# 登录
curl -s http://localhost:8081/openapi/auth/login \
  -H 'Content-Type: application/json' \
  -d '{
    "clientId":"a8f3c2e1b9d0476581920abcdef12345",
    "grantType":"password",
    "tenantId":"000000",
    "username":"admin",
    "password":"admin123"
  }'

# 业务（替换 TOKEN、CLIENT_ID）
curl -s http://localhost:8081/openapi/scm/demo/health \
  -H "Authorization: Bearer TOKEN" \
  -H "clientid: CLIENT_ID"
```

### 请求头约定

| Header | 说明 |
| --- | --- |
| `Authorization` | `Bearer {access_token}` |
| `clientid` | 与登录 `clientId` 一致，Sa-Token 校验 |

---

## 5. 新增正式域时的目录模板

```text
whl-scm/whl-scm-oms/
├── whl-scm-oms-api/
│   └── com/whl/scm/oms/api/
│       ├── facade/OrderFacade.java
│       ├── command/CreateOrderCommand.java
│       ├── query/GetOrderQuery.java
│       └── dto/OrderDto.java
├── whl-scm-oms-biz/
│   └── com/whl/scm/oms/biz/
│       ├── application/OrderAppService.java    ← implements OrderFacade
│       ├── domain/
│       └── infrastructure/
├── whl-scm-oms-admin-adapter/                ← /api/scm/oms/*  → {Noun}Vo
└── whl-scm-oms-openapi-adapter/              ← /scm/oms/*       → OpenApi*Response
```

WMS 另增 `whl-scm-wms-pda-adapter` → `/api/pda/wms/*` → `Pda*Vo`。

`whl-openapi-server` 只加依赖 `whl-scm-oms-openapi-adapter`，**不要**依赖 `oms-biz`（由 adapter 传递）。

---

## 6. 自检清单（每个 PR）

- [ ] Controller 无业务规则（无库存计算、状态机分支）
- [ ] 写 Command 含 `idempotencyKey`
- [ ] 跨域只调 `*-api` Facade，不注入他域 `*-biz`
- [ ] `*-api` 无 Spring / MyBatis 依赖
- [ ] OpenAPI 路径在 `*-openapi-adapter`，不在 admin-adapter 混写
- [ ] OpenAPI 返回 `OpenApi*Response`，未直出 Facade Dto

---

## 7. 相关文档

- [API_CONTRACT.md](./API_CONTRACT.md) — Facade / Command / Event / **三通道表现层 §2.5**
- [MODULE_RULES.md](./MODULE_RULES.md) — 依赖红线、**OpenAPI/PDA adapter §9**
- [ROADMAP.md](./ROADMAP.md) 阶段 4～5 — Demo 退役与重建说明
- [AUTH_MIGRATION.md](./AUTH_MIGRATION.md) — OpenAPI clientId
