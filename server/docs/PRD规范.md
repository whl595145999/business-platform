# PRD 文档规范（全仓 SSOT）

> 作者：**whl** · 适用：供应链业务平台 monorepo 四仓

## 1. 什么需要 PRD

| 类型 | 需要 PRD | 文档位置 |
| --- | --- | --- |
| 新供应链业务能力（B1～B8） | **是** | `docs/domains/{域}/prd/` + `design/` |
| 跨端功能（后端 + PC + 移动） | **是** | 主仓 `business-platform`，各端链引用 |
| 仅移动壳（首页、PDA 工作台） | **轻量 PRD** | `admin-app/docs/prd/` |
| 标准 CRUD（复制 org 标杆） | **否** | `admin-web接口对照.md` + 标杆代码 |
| Bug / 样式 / API 对齐 | **否** | 提交说明 |
| UX 走查 | **否** | `@senior-ui-ux-designer` Audit |

## 2. 仓库与主责

| 仓库 | PRD 职责 |
| --- | --- |
| **business-platform** | 业务 PRD SSOT（按域分包，产品/技术分开） |
| **business-platform-admin-web** | 链主 PRD；PC 独有交互可增 `docs/prd/` |
| **business-platform-admin-app** | 移动 / PDA **独有**壳 PRD |
| **business-platform** Skills | `scm-product-manager` 产出产品章 → 架构师写技术章 |

## 3. 目录结构（按业务域 · 产品/技术分开）

```text
business-platform/docs/
├── PRD规范.md
├── domains/                    # ★ 业务域文档（好找入口）
│   ├── README.md               # 总索引
│   ├── wms/                    # 表前缀 wms_（B1 组织）
│   │   ├── README.md
│   │   ├── prd/B1-org/PRODUCT.md
│   │   └── design/B1-org/TECH.md
│   └── product/                # 表前缀 prd_（B2 货品 Step 2 已定稿）
│       ├── README.md
│       └── prd/B2-product/PRODUCT.md
├── ORG_PRD.md                  # 跳转桩 → B1-org
└── prd/                        # 旧索引（跳转桩）

business-platform-admin-app/docs/
├── PRD规范.md
└── prd/                        # 仅移动独有壳
```

**原则**：

- 按 **业务域**（wms / product / inv / oms / pda）分目录，不按 Vue 路径分包。
- 每个波次 **产品**（`prd/`）与 **技术**（`design/`）**分文件**，方便产品/研发各看各的。

## 4. 文首元数据（必填）

每个 PRD 开头表格须含：

| 字段 | 说明 | 示例 |
| --- | --- | --- |
| 文档版本 | semver 或 v1.0 | v1.0 |
| 文档类型 | 产品 / 技术 | 产品 PRD · 技术设计 |
| 波次 | industry-baseline B1～B8 | B1 / B8 / — |
| 域 | 业务域 | wms / product / pda |
| 端 | 影响范围 | 后端+PC+移动 / 仅移动 / 仅 PDA |
| 主仓 | SSOT 仓库 | business-platform / admin-app |
| 状态 | 草稿/已定稿/已实现 | MVP 已落地 |
| 关联 | 上级 PRD、规范 | [domains/README.md](./domains/README.md)、MODULE_RULES §9 |

## 5. 三通道与 PRD 边界

| 通道 | API 前缀 | 产品 PRD 写什么 | 技术 design 写什么 |
| --- | --- | --- | --- |
| admin | `/api/scm/...` | 页面、字段、权限、AC | Facade、HTTP Vo、DDL |
| **pda** | `/api/pda/wms/...` | 现场作业流 | `Pda*Vo`、`idempotencyKey` |
| openapi | `/openapi/scm/...` | 对外业务契约 | OpenAPI schema |

PDA **禁止**写进 admin 菜单 PRD；见 [MODULE_RULES.md](./MODULE_RULES.md) §9。

## 6. 与 Skill 链

```text
scm-product-manager     → domains/{域}/prd/
ecommerce-scm-architect → domains/{域}/design/
senior-ui-ux-designer   → 线框 / Handoff（引用产品 PRD）
scm-platform-dev        → 实现
admin-app uniapp-dev    → 移动独有 PRD 落地
```

## 7. 命名

| 模式 | 用途 |
| --- | --- |
| `README.md`（域根） | 域文档索引；大域总览已拆为波次 `B{n}-*/PRODUCT.md` |
| `PRODUCT.md` | 波次产品 PRD |
| `TECH.md` | 波次技术设计 |
| `B{n}-{slug}/` | 波次目录 |
| `{SCOPE}_WORKBENCH_PRD.md` | 端壳（admin-app） |

## 8. 变更规则

- 产品 PRD 变更 → 同步 `ROADMAP.md`、`design/` 对应章、`admin-web接口对照.md`
- 技术设计变更 → 同步 `DATABASE.md`、`API_CONTRACT.md`
- 移动独有 PRD → 同步 `UI组件规范.md`
- 旧路径保留 **跳转桩**，避免断链

---

*索引：[domains/README.md](./domains/README.md)*
