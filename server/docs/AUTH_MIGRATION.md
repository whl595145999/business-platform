# 平台统一认证（whl-platform-auth）

> admin / openapi / pda 共用登录能力；远期可演进为独立认证中心。

---

## 1. 模块职责

| 模块 | 职责 |
| --- | --- |
| `whl-platform-auth` | HTTP `/auth/*`、验证码、注册、授权策略、Sa-Token Listener |
| `whl-platform-system` | 用户/租户/客户端/RBAC 数据与 `/system/*` |
| `whl-platform-api` | 跨域 Facade（`UserFacade`），非 HTTP 登录 |
| `whl-server/*` | 进程入口，依赖 `whl-platform-auth` |

包名：`com.whl.platform.auth.*`

---

## 2. 依赖方向

```text
whl-server → whl-platform-auth → whl-platform-system → whl-framework-*
```

禁止：`whl-platform-system` → `whl-platform-auth`（避免循环）。

---

## 3. 多客户端（sys_client）

同一套 `/auth/login`，用 **`clientId`** 区分 admin / pda / openapi：

| client_key | clientId（登录参数） | device_type | 说明 |
| --- | --- | --- | --- |
| pc | `e5cd7e4891bf95d1d19206ce24a7b32e` | pc | 管理端 |
| app | `428a8310cd442757ae699df5d894f051` | android | PDA / 移动端 |
| openapi | `a8f3c2e1b9d0476581920abcdef12345` | api | OpenAPI 进程（8081，`/openapi/auth/*`） |

| 场景 | 配置位置 |
| --- | --- |
| 授权类型、超时 | `sys_client` 表 |
| Token 存储 | 共享 Redis（Sa-Token） |

新增 PDA / OpenAPI 客户端：在 `sys_client` 增加记录，无需改 auth 模块代码。

---

## 4. 演进路线

| 阶段 | 形态 |
| --- | --- |
| **P1（当前）** | `whl-platform-auth` jar，随 `whl-admin-server` 启动 |
| **P2（当前）** | `whl-openapi-server`（8081）+ `whl-admin-server` 共用 auth jar + 共享 Redis |
| **P3（远期）** | 独立 `whl-auth-server` + Gateway 转发 `/auth/**` |

---

## 5. 验收清单

- [ ] `GET /auth/code`、`GET /auth/tenant/list`
- [ ] `POST /auth/login`、`POST /auth/logout`
- [ ] 登录后 `GET /system/user/getInfo`
- [ ] `whl-admin-server` 内无 `AuthController` / `SysLoginService`

---

## 6. 刻意不做

- 现阶段不拆独立 auth 进程
- 不合并 `whl-platform-api` 与 `whl-platform-auth`
- 不改 `/auth/*` URL 与 Token 格式
