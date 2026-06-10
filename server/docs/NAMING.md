# 命名规范

> 产品：**供应链业务平台** | 作者：**whl** | groupId：**`com.whl`**

---

## 1. Maven 坐标

| 项 | 目标值 |
| --- | --- |
| groupId | `com.whl` |
| 根 artifactId | `whl-scm-platform` |
| 版本 | `1.0.0-SNAPSHOT` 起（与 RuoYi 基座版本号可并列记录） |

### 模块 artifactId

| 层级 | 模式 | 示例 |
| --- | --- | --- |
| 启动 | `whl-{scene}-server` | `whl-admin-server` |
| 扩展聚合 | `whl-extend` | `whl-extend` |
| 监控 / 调度 | `whl-{name}` | `whl-monitor-admin`、`whl-snailjob-server` |
| **Framework（原 ruoyi-common）** | **`whl-framework-{name}`** | `whl-framework-web` |
| Platform（原 whl-platform） | `whl-platform-{name}` | `whl-platform-system`、`whl-platform-auth` |
| Platform API | `whl-platform-api` | `UserFacade`、`WorkflowFacade` |
| Platform Auth | `whl-platform-auth` | 统一 `/auth/*`（admin / pda / openapi） |
| SCM 契约 | `whl-scm-{domain}-api` | `OrderFacade` 等 |
| SCM 实现 | `whl-scm-{domain}-biz` | `whl-scm-wms-biz` |
| SCM 适配器 | `whl-scm-{domain}-{channel}-adapter` | `whl-scm-wms-pda-adapter` |

### 目录与 Maven 对应

```text
whl-framework/          # 聚合 POM，对应现 ruoyi-common/
whl-platform/           # 聚合 POM，对应现 whl-platform/
whl-scm/                # 聚合 POM，供应链业务
whl-server/             # 聚合 POM：whl-admin-server + whl-extend
```

---

## 2. Java 包名

根包：**`com.whl`**

```text
com.whl
├── boot                         # 各 Server 启动类
├── framework                    # 技术层 ← 原 com.whl.framework.*
│   ├── core
│   ├── web
│   ├── security
│   ├── mybatis
│   ├── redis
│   ├── rpc
│   └── event
├── platform                     # 平台层
│   ├── api                      # UserFacade、WorkflowFacade（whl-platform-api）
│   ├── auth                     # 统一认证 /auth/*（whl-platform-auth）
│   ├── system                   # 实现 + sys_*
│   └── workflow
└── scm                          # 供应链
    ├── oms / wms / inventory / product / erp
    │   ├── api                  # OrderFacade 等（whl-scm-{domain}-api）
    │   ├── biz
    │   │   ├── application
    │   │   ├── domain
    │   │   └── infrastructure
    │   └── adapter
```

### 存量过渡

| 代码 | 包名 | 策略 |
| --- | --- | --- |
| 原 RuoYi Framework/Platform | `com.whl.framework.*` / `com.whl.platform.*` | ✅ 阶段 3 已迁移 |
| 启动类 | `org.dromara.*` | ✅ 阶段 3B 已迁至 `com.whl.boot.*` |
| 新 SCM 业务 | `com.whl.scm.*` | 立即生效 |
| 新 Framework 扩展 | `com.whl.framework.*` | 立即生效 |

---

## 3. 数据库（单库 + 表前缀）

### 3.1 当前策略

| 项 | 规定 |
| --- | --- |
| 库名 | **`business_platform` 不改** |
| 是否拆库 | **否**（单体单库） |
| 存量表 | **`sys_*`、`flow_*` 等结构不改** |
| 新增 SCM 表 | 同库，**必须域前缀** |

### 3.2 表前缀

| 域 | 前缀 | 示例 |
| --- | --- | --- |
| 平台（RuoYi） | `sys_` | `sys_user`（不动） |
| 工作流 | `flow_` | `flow_definition`（不动） |
| OMS | `oms_` | `oms_order`、`oms_order_line` |
| WMS | `wms_` | `wms_outbound_task`、`wms_location` |
| Inventory | `inv_` | `inv_stock`、`inv_reserve_record` |
| Product | `prd_` | `prd_category`、`prd_brand`、`prd_sku`（SPU 属销售/catalog，B6+） |
| ERP | `erp_` | `erp_purchase_order` |

### 3.3 命名格式

```text
{前缀}_{业务名}     # 小写，蛇形，见名知域

✓  oms_order
✓  wms_outbound_task
✓  wms_outbound_task_line
✗  order              # 无前缀
✗  t_wms_task         # 不用 t_ 风格
✗  scm_order          # 不用泛化前缀
```

### 3.4 新表必备字段

与 RuoYi `BaseEntity` 一致；**每张 SCM 表（含明细 `_line`）建表时必须带齐**：

```sql
create_dept     BIGINT       DEFAULT NULL COMMENT '创建部门',
create_by       BIGINT       DEFAULT NULL COMMENT '创建者',
create_time     DATETIME     DEFAULT NULL COMMENT '创建时间',
update_by       BIGINT       DEFAULT NULL COMMENT '更新者',
update_time     DATETIME     DEFAULT NULL COMMENT '更新时间',
del_flag        CHAR(1)      DEFAULT '0' COMMENT '删除标志',
```

完整清单：`id`, `tenant_id` + 上表 6 列。详见 [DATABASE.md](./DATABASE.md) §3.1。

---

## 4. 类与接口命名

| 类型 | 规则 | 示例 |
| --- | --- | --- |
| Facade | `{名词}Facade` | `OrderFacade`、`UserFacade` |
| Facade 实现（平台） | `{名词}FacadeImpl` | `UserFacadeImpl`（在 `whl-platform-system` 等） |
| Facade 实现（SCM） | `{用例}AppService` implements Facade | `OrderConfirmAppService` |
| 领域服务 | `{名词}DomainService` | `InventoryReserveDomainService` |
| 命令 | `{动词}{名词}Command` | `CreateOrderCommand` |
| 事件 | `{名词}{过去式}Event` | `OrderConfirmedEvent` |
| Controller | `{资源}Controller` | 仅在 `*-adapter` 模块 |
| Mapper | `{实体}Mapper` | 仅在 `*-biz` 的 infrastructure |
| 表实体 | 与表名对应 | 表 `wms_outbound_task` → `WmsOutboundTask` |

---

## 5. 前端

| 现目录 | 目标名 |
| --- | --- |
| `business-platform-admin-web` | `whl-admin-web` |
| `business-platform-admin-app` | `whl-admin-app` |

展示名：**供应链业务平台** | 版权：**Author: whl**

---

## 6. 禁止与允许

**禁止**

- 在 `whl-framework-*` 写 SCM 业务逻辑
- 新 SCM 表无前缀
- 修改 `sys_*` 表结构（无平台升级需求时）
- 跨域依赖 `*-biz`

**允许**

- 第三方库包名（如 `org.dromara.sms4j`）保持不变
- 单库 `business_platform` 内存放所有前缀表
- Maven 模块 rename `ruoyi-common` → `whl-framework` 已完成（阶段 3）
