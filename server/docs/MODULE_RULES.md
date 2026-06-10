# 模块依赖红线与检查清单

> 配合 [ARCHITECTURE.md](./ARCHITECTURE.md) 使用。Code Review 与拆分服务前必查。

---

## 1. 依赖方向

```text
whl-server → whl-platform-auth → whl-platform-system → whl-framework
whl-server → whl-scm-adapter → whl-scm-biz → whl-platform → whl-framework
```

## 2. 禁止依赖

| # | 规则 |
| --- | --- |
| 1 | `whl-framework-*` 不得依赖 `whl-platform-*`、`whl-scm-*` |
| 2 | `whl-platform-*` 不得依赖 `whl-scm-*` |
| 2a | `whl-platform-system` 不得依赖 `whl-platform-auth`（避免循环） |
| 3 | `*-api` 不得依赖 `*-biz`、Spring、MyBatis |
| 4 | `*-adapter` 不得依赖其他域的 `*-biz` |
| 5 | 域 A 的 `*-biz` 不得依赖 域 B 的 `*-biz` |
| 6 | 任何模块不得跨域注入其他域的 Mapper |
| 7 | Framework 模块不得出现 `oms_` / `wms_` 等业务表 Entity |

## 3. 允许依赖

| # | 规则 |
| --- | --- |
| 1 | `whl-server` 只依赖 `*-adapter`（本域 `*-biz` 由 adapter 传递）；平台认证依赖 `whl-platform-auth` |
| 2 | `*-biz` 依赖本域 `*-api` |
| 3 | `*-biz` 依赖其他域 `*-api`（跨域 Facade） |
| 4 | `*-biz` 依赖 `whl-platform-api` / `whl-scm-*-api` 的 Facade（非 system / 他域 biz 实现类） |
| 5 | 所有模块可依赖 `whl-framework-*` |

## 4. 单一职责自检（每个 PR）

- [ ] 新增类是否放在正确层？（Controller 不在 biz 模块）
- [ ] 新增 admin 接口是否按菜单拆 Controller？（见 §8）
- [ ] admin HTTP 出参是否为 `{Noun}Vo` + `toVo(Dto)`，未直出 Facade Dto？（见 [API_CONTRACT.md](./API_CONTRACT.md) §2.5）
- [ ] OpenAPI / PDA 是否使用独立表现模型（`OpenApi*Response` / `Pda*Vo`），未复用 admin Vo？（见 §9）
- [ ] 是否只通过 Facade 跨域调用？
- [ ] 正式 SCM 是否写入对应域 `whl-scm-{domain}-api/biz`，而非继续堆在 `whl-scm/demo`？
- [ ] 新表是否带域前缀（`oms_` / `wms_` / `inv_` / `prd_`）？
- [ ] 新表是否含 `tenant_id`？
- [ ] 是否修改了 `sys_*` 表结构？（若无平台升级需求，应为否）
- [ ] `*-api` 是否无 Spring 依赖？

## 5. 远期拆服务检查

- [ ] 目标域是否仅有本域前缀的表？
- [ ] 跨域是否全部为 `*-api` 或 Event？
- [ ] Server 是否足够薄，可单独 boot 一个域？

---

## 6. Demo 与正式边界（必注意）

> `whl-scm/demo` **故意放宽** Maven 边界以跑通链路；**正式 SCM 业务必须按下列规则拆分**，不可沿用 Demo 的单 jar 多域结构。

### 6.1 一个 api jar 多域 — Demo 可，正式禁止

| 阶段 | 模块 | 说明 |
| --- | --- | --- |
| **Demo** | `whl-scm-demo-api` | 同时含 `com.whl.scm.oms/wms/inventory.api.*`，**包名已按域分开** |
| **正式** | `whl-scm-oms-api`、`whl-scm-wms-api`… | **一域一 Maven 模块**；包名可不变，只拆 jar |

**PR 自检（正式代码）**

- [ ] 新契约是否落在对应域的 `whl-scm-{domain}-api`，而非 demo-api？
- [ ] 调用方 pom 是否只依赖需要的 `*-api`，未引入无关域契约？

### 6.2 一个 biz jar 多域 — Demo 可，正式禁止

| 阶段 | 模块 | 说明 |
| --- | --- | --- |
| **Demo** | `whl-scm-demo-biz` | OMS/WMS/Inventory 实现在同一 jar，仅用于内存 Demo |
| **正式** | `whl-scm-oms-biz`、`whl-scm-wms-biz`… | **一域一 jar**；跨域只调 `*-api` Facade 或 Event |

**PR 自检（正式代码）**

- [ ] 是否出现 `import com.whl.scm.wms.biz.*` 写在 `oms-biz` 中？（禁止）
- [ ] 跨域是否只注入 `InventoryFacade` 等接口，而非他域 `*AppService` / Mapper？
- [ ] Event 监听器是否放在**订阅域**的 biz（如 WMS 听 `OrderConfirmedEvent`）？

### 6.3 Facade 实现命名 — 平台与 SCM 并存

| 层 | 实现类命名 | 示例 |
| --- | --- | --- |
| `whl-platform-system` / workflow | `{名词}FacadeImpl` | `UserFacadeImpl`（RuoYi 存量） |
| `whl-scm-*-biz` | `{用例}AppService` implements Facade | `OrderAppService`（**新 SCM 统一此风格**） |

不必为统一而重命名平台存量类；**新增 SCM 实现一律 `*AppService`**。

### 6.4 Demo 模块何时退役 — **已完成**

`whl-scm/demo` 已删除。当前已落地 B1 `wms_org` · B2 `prd_*` Admin CRUD。

详见 [ROADMAP.md](./ROADMAP.md) 阶段 4～5、[API_CONTRACT.md](./API_CONTRACT.md) §11。

---

## 7. Java 注释规范（SCM 必遵）

> 作者统一 **`@author whl`**。与 PRD 三类读者对齐：契约写「做什么」，实现写「怎么做 / 红线」。

| 层级 | 要求 |
| --- | --- |
| **`*-api` Facade** | 类 + 每个 public 方法 JavaDoc；注明幂等、跨域调用方、对应 PRD 章节 |
| **`*-api` Command/Query/DTO/Event** | `@Data` POJO + 字段 `/** */`；类级 JavaDoc；字段含义与表/码表一致（**不用 `record`**） |
| **`*-api` 枚举** | 类说明 + 每个枚举值 `{码}-{中文}` |
| **`*-biz` Entity** | 类说明表名；每个字段 `/** … */` 对齐 DDL COMMENT |
| **`*-biz` AppService** | 类说明职责；public 方法 JavaDoc；复杂 private 方法一行说明 |
| **`*-biz` Mapper** | 接口 JavaDoc 说明对应表 |
| **`*-adapter` Controller** | 类 + 每个接口方法；`@Operation` 与 JavaDoc 语义一致 |
| **admin `*-adapter` Vo** | `controller/vo/{Noun}Vo.java`；HTTP JSON 出参，与 Facade Dto 分离 |
| **openapi `*-adapter` Response** | `controller/response/OpenApi{Noun}Response.java`；对外 JSON，与 admin Vo / Facade Dto 均分离 |
| **pda `*-adapter` Vo** | `controller/vo/Pda{Noun}Vo.java`；现场精简出参 |
| **package-info.java** | 包职责、所属域、依赖方向 |

**POJO 访问**：`whl-scm` 用 `.getXxx()`；`boolean` 用 `.isXxx()`；`EventMetadata`（record）用 `.tenantId()` / `.eventId()`。`whl-scm/pom.xml` 继承 Lombok（`provided`）。

**模板**

```java
/**
 * 库存 Facade。
 * <p>
 * 跨域：OMS/WMS 仅依赖本接口，禁止直查 {@code inv_*} 表。
 *
 * @author whl
 */
```

**禁止**：无意义的 `@author whl` 空类注释；注释与实现/PRD 不一致。

---

## 8. Admin Adapter Controller 拆分（SCM 必遵）

> **本节仅约束 admin-adapter**（`/api/scm/...`）。OpenAPI / PDA 见 [§9](#9-openapi--pda-adapter)。三通道总览见 [API_CONTRACT.md](./API_CONTRACT.md) §2.5。

> **禁止**把整域 HTTP 接口堆在一个 `ScmXxxController` 里。按 **admin 菜单页** 一一对应拆分，提升可读性与 Code Review 效率。新菜单随各波次 PRD 的 `script/sql/{domain}/_gen/*_menu.sql` 产出。

### 8.1 拆分原则

| # | 规则 |
| --- | --- |
| 1 | **一菜单页一 Controller**（`menu_type=C` 的页面） |
| 2 | 类名：`Scm{业务名}Controller`，放在 `whl-scm-{domain}-admin-adapter/.../controller/` |
| 3 | `@RequestMapping` 用 **资源路径**（如 `/api/scm/wms/warehouses`），与前端 `src/api/scm/*.ts` 保持一致 |
| 4 | HTTP **入参** POJO 放 `controller/request/`；**出参 Vo** 放 `controller/vo/`，与 `*-api` Dto/Command 分离；Adapter 内 `toVo(Dto)` |
| 5 | 包级 `package-info.java` 列出本包 Controller 与菜单对照 |
| 6 | **无菜单**但需 HTTP 暴露（AC 脚本、OpenAPI 联调）→ 单独 Controller，JavaDoc 注明「无 admin 菜单」 |

### 8.2 现行菜单 ↔ Controller（B1 + B2）

| menu_id | 菜单 | Controller | 路径前缀 |
| --- | --- | --- | --- |
| 1708 | 组织档案 | `ScmOrgController` | `/api/scm/wms/orgs` |
| 1711 | 货品类目 | `ScmCategoryController` | `/api/scm/product/categories` |
| 1712 | 品牌档案 | `ScmBrandController` | `/api/scm/product/brands` |
| 1713 | 货品 | `ScmSkuController` | `/api/scm/product/skus` |

后续波次（B3+）在本表追加，以各波次 PRD / `*_menu.sql` 为准。

### 8.3 新增页面时的 PR 自检

- [ ] 是否新建独立 Controller（而非往旧类追加方法）？
- [ ] 类/方法 JavaDoc 是否写明 **menu_id、前端路由、PRD 章节**？
- [ ] `@Tag` / `@Operation` 是否与 JavaDoc 一致？
- [ ] 是否在 `controller/request/` 新增请求体并写字段说明？
- [ ] 是否更新本表（§8.2）或 `package-info.java` 索引？

### 8.4 Controller 模板

```java
/**
 * 入库单管理端接口。
 * <p>
 * 对应 admin 菜单 <b>1704 入库单</b>（{@code /scm/wms/inbound}），PRD §8.2。
 *
 * @author whl
 */
@Tag(name = "SCM WMS - 入库单")
@RestController
@RequestMapping("/api/scm/wms/inbound")
public class ScmInboundController extends BaseController {
    // 薄委托 Facade；每个 public 方法写 JavaDoc + @Operation
}
```

---

## 9. OpenAPI & PDA Adapter（SCM 必遵）

> 与 §8 admin 并列：**同一 Facade，三种 HTTP 表现模型，三种 Maven adapter**。禁止跨通道复用 Vo/Dto 作为 JSON 类型。

### 9.1 模块与路径

| 通道 | Maven 模块 | 包根 | `@RequestMapping` | 完整 URL 示例 |
| --- | --- | --- | --- | --- |
| OpenAPI | `whl-scm-{domain}-openapi-adapter` | `com.whl.scm.{domain}.adapter.openapi` | `/scm/{domain}/...` | `GET /openapi/scm/inventory/stock` |
| PDA | `whl-scm-wms-pda-adapter` | `com.whl.scm.wms.adapter.pda` | `/api/pda/wms/...` | `POST /api/pda/wms/inbound/confirm-receive` |

- OpenAPI 进程 `context-path=/openapi`；Controller **不写** `/openapi` 前缀
- PDA 归属 WMS，不单独建 `whl-scm-pda` 域模块
- `whl-openapi-server` / `whl-pda-server` 只依赖对应 `*-adapter`，**不**直接依赖 `*-biz`

### 9.2 表现模型命名

| 通道 | 出参 | 入参（写操作） | 转换 |
| --- | --- | --- | --- |
| OpenAPI | `OpenApi{Noun}Response` | `OpenApiCreate{Noun}Request`（按需） | `toOpenApiResponse(Dto)` |
| PDA | `Pda{Noun}Vo` | `PdaConfirm{Noun}Request` 等 | `toPdaVo(Dto)` |

**禁止**：`R<{Noun}Dto>` 作为 OpenAPI 长期契约；禁止把 `InboundVo` 给 OpenAPI/PDA 复用。

### 9.3 OpenAPI Controller 模板

```java
/**
 * 库存 OpenAPI（PRD §13.2）。
 * <p>
 * 对外 JSON 为 {@link OpenApiStockResponse}，不暴露 Facade Dto。
 *
 * @author whl
 */
@Tag(name = "OpenAPI SCM - 库存")
@RestController
@RequestMapping("/scm/inventory")
public class ScmOpenApiStockController {

    @GetMapping("/stock")
    public R<OpenApiStockResponse> getStock(@Valid OpenApiGetStockRequest request) {
        StockDto dto = inventoryFacade.getStock(toQuery(request));
        return R.ok(toOpenApiResponse(dto));
    }
}
```

### 9.4 PDA Controller 模板

```java
/**
 * PDA 入库收货确认（无 admin 菜单）。
 *
 * @author whl
 */
@Tag(name = "PDA WMS - 入库")
@RestController
@RequestMapping("/api/pda/wms/inbound")
public class ScmPdaInboundController {

    @PostMapping("/confirm-receive")
    public R<PdaInboundVo> confirmReceive(@Valid @RequestBody PdaConfirmReceiveRequest request) {
        InboundDto dto = inboundFacade.confirmReceive(toCommand(request));
        return R.ok(toPdaVo(dto));
    }
}
```

### 9.5 PR 自检（OpenAPI / PDA）

- [ ] 是否新建/修改 `*-openapi-adapter` 或 `pda-adapter`，而非在 admin Controller 加 `/openapi` 路由？
- [ ] HTTP 返回类型是否为 `OpenApi*Response` / `Pda*Vo`？
- [ ] 写 Command 是否含 `idempotencyKey`（PDA 必填）？
- [ ] OpenAPI 是否在 Swagger / 对外文档登记 schema 与版本路径？
- [ ] Controller 内是否只有参数转换 + Facade 调用（无业务分支）？
