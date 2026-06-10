---
name: scm-platform-dev
description: >-
  SCM 全栈快捷开发分流：主数据CRUD、代码生成、scm_crud、单据手写、warehouse/oms/inventory。
  Routes mysql-dba, scm-crud-generate, java-backend-dev, frontend-dev, uniapp-dev.
  Use when starting a feature, full-stack, or which skill to follow.
disable-model-invocation: true
---

# SCM 全栈快捷开发（总入口）

> 供应链业务平台 · 模块化单体 · 单库 `business_platform` · 行业：集团多组织 + WMS/OMS/库存

## 何时用本 Skill

- 不确定用哪个子 Skill
- **PRD/设计已定**，新功能落地（库表 → 后端 → PC → 移动）
- 用户说「快捷开发」「按规范做」「全栈」「写代码」

**从 0 设计（未定稿、不写代码）**：

- 供应链业务/单据/库存 PRD → [scm-product-manager](../scm-product-manager/SKILL.md)
- 互联网/B 端体验/埋点 → [senior-product-manager](../senior-product-manager/SKILL.md)
- 线框/组件/设计系统/开发交付包 → [senior-ui-ux-designer](../senior-ui-ux-designer/SKILL.md)（PRD 已定后）
- 技术架构/API/ER/DDD → [ecommerce-scm-architect](../ecommerce-scm-architect/SKILL.md)

定稿后再回本 Skill。

**执行方式**：先 **分流**（见 [SKILL_ROUTING.md](../SKILL_ROUTING.md)）→ **只加载一个子 Skill** → **每次一个 Step，等用户确认**。  
Step 末统一：`Step N 完成，请确认后继续 Step N+1。`

> **工作区**：建议在 monorepo 根目录或同时打开 `business-platform` + `admin-web` + `admin-app`，否则跨仓 Skill 链接可能失效。

---

## 仓库与 Skill 地图

| 仓库 | Skill | 职责 |
| --- | --- | --- |
| `business-platform` | [mysql-dba](../mysql-dba/SKILL.md) | DDL、索引、SQL Review |
| `business-platform` | [java-backend-dev](../java-backend-dev/SKILL.md) | Facade/AppService/Adapter（手写业务） |
| `business-platform` | [scm-crud-generate](../scm-crud-generate/SKILL.md) | 主数据：**一次 zip = Java + PC 页** |
| `business-platform` | [code-review-scm](../code-review-scm/SKILL.md) | PR 自检 / Code Review |
| `business-platform` | [scm-product-manager](../scm-product-manager/SKILL.md) | 供应链产品（单据/库存/四流） |
| `business-platform` | [senior-product-manager](../senior-product-manager/SKILL.md) | 互联网产品（故事/P0/埋点） |
| `business-platform` | [senior-ui-ux-designer](../senior-ui-ux-designer/SKILL.md) | UI/UX（IA、线框、交付包） |
| `business-platform` | [ecommerce-scm-architect](../ecommerce-scm-architect/SKILL.md) | 解决方案架构（API/ER/DDD） |
| `business-platform-admin-web` | [frontend-dev](../../../../business-platform-admin-web/.cursor/skills/frontend-dev/SKILL.md) | Vue3 管理端 |
| `business-platform-admin-app` | [generate-crud-module](../../../../business-platform-admin-app/.cursor/skills/generate-crud-module/SKILL.md) | 移动管理 CRUD 快路径 |
| `business-platform-admin-app` | [uniapp-dev](../../../../business-platform-admin-app/.cursor/skills/uniapp-dev/SKILL.md) | 手写移动页 + **PDA 前端**（`pages-sub/pda`） |

详细行业流程：[workflow.md](workflow.md)

---

## 需求分流（必须先做）

```text
尚无定稿、不写代码？
  ├─ 仓储/库存/单据/采购/物流/售后 → scm-product-manager
  ├─ 管理端体验/平台功能/埋点 → senior-product-manager
  ├─ 线框/组件/多端适配/交付包 → senior-ui-ux-designer
  └─ 技术方案/API/库表/DDD → ecommerce-scm-architect
  → 定稿后回到本 Skill

新表/改表？
  ├─ 是 → mysql-dba（Step 1 起）
  └─ 否 ↓

用户说「生成后台 / 生成前台 / 代码生成」？
  └─ 主数据单表 → scm-crud-generate（**同一 zip 含后端+PC**，勿拆成两个 Skill）

SCM 主数据单表 + 标准分页 CRUD？
  ├─ 是 → scm-crud-generate（[workflow.md](../scm-crud-generate/workflow.md) Step 1 起）
  └─ 否 ↓

单据/状态机/跨域/库存预占？
  └─ java-backend-dev（Step 1 起）— **不用** scm_crud

要管理端页面？
  ├─ 生成器路径 → scm-crud-generate Step 4–6；收尾用 frontend-dev **生成器分支**
  └─ 手写页 → frontend-dev 完整 9 Step

要移动端（admin-app）？
  ├─ 标准列表+表单 → generate-crud-module
  └─ 非常规页 / 首页 / PDA 页 → uniapp-dev

PDA 现场 / OpenAPI？
  ├─ PDA 后端 → java-backend-dev（`pda-adapter`，`/api/pda/wms/...`）
  ├─ PDA 前端（B8+）→ uniapp-dev（`pages-sub/pda`，**非** generate-crud-module）
  └─ OpenAPI → java-backend-dev（`*-openapi-adapter`）— 非 frontend / 非 admin-app CRUD

提交 PR / PRD 审？
  └─ code-review-scm（§A–G，含 PRD §G）
```

---

## 推荐全栈顺序（行业主数据）

与电商/供应链「主数据 → 作业单据 → 库存」一致，**按序执行、每步确认**：

| 序 | 环节 | Skill | 产出 |
| --- | --- | --- | --- |
| 1 | 库表 | mysql-dba | `script/sql/{域}/*.sql` |
| 2 | 主数据全栈 | scm-crud-generate | **Java + `index.vue` + MERGE**（RuoYi zip） |
| 2b | 单据后端 | java-backend-dev | Facade + AppService（不走生成器） |
| 3 | PC 收尾 | frontend-dev **生成器分支** | dict / OPTIONS / 冒烟（勿重写页） |
| 4 | 移动（可选） | generate-crud-module | `pages-sub/**` |

主数据固定话术：`mysql-dba → scm-crud-generate → frontend-dev（生成器分支）→ generate-crud-module（可选）`

**不做**：COMMENT 自动生 Java 枚举（已决策）；跨域同事务；改 `sys_*` 表结构（除非平台需求）。

---

## 分流反例（常见误路由）

| 用户说法 | 错误 Skill | 正确 Skill |
| --- | --- | --- |
| 生成入库单 / 出库单后台 | scm-crud-generate | java-backend-dev |
| 只要前台、没有 zip | frontend-dev 从零写 | 先 scm-crud-generate 或确认后端 API 已存在 |
| 移动 SCM 列表 | 照搬 system 的 `/list` | generate-crud-module（**pageXxx** + `/api/scm/...`） |
| 改 Velocity / GenUtils | 只改文档 | 改模板 + `scm-crud-template-smoke.sh` + 重启 admin |
| PDA 收货/扫码 | generate-crud-module / frontend-dev | java-backend-dev + uniapp-dev（`pages-sub/pda`） |

角色心智：[ROLES.md](../ROLES.md)（卖 / 履约 / 技术方案）

---

## 项目硬约束

见 [CONSTRAINTS.md](../CONSTRAINTS.md)（全 Skill 唯一摘要）

---

## 子 Skill 阶段数

| Skill | Steps |
| --- | --- |
| mysql-dba | 9（需求→分库评估） |
| java-backend-dev | 7 |
| frontend-dev | **G1–G4**（生成器）或 **9**（手写） |
| uniapp-dev | 9 |
| scm-crud-generate | 7（[workflow.md](../scm-crud-generate/workflow.md)，Step 2–3 用户操作 RuoYi） |
| generate-crud-module | 6（每次一步确认） |
| code-review-scm | 3（分流 → 检查 → Report） |
| scm-product-manager | 8（四流·单据·库存 PRD） |
| senior-product-manager | 8（澄清·故事·埋点·交付包） |
| senior-ui-ux-designer | 8（IA·线框·组件·交付包） |
| ecommerce-scm-architect | 10（技术架构；不写代码） |

---

## Agent 开场模板

用户描述需求后，输出：

```markdown
# 分流结果

- **阶段**：（设计 / 实现 / Review）
- **类型**：（供应链产品 / 互联网产品 / UI/UX / 技术架构 / 主数据 CRUD / 单据 / 仅前端 / 全栈）
- **主 Skill**：`@xxx`（设计类勿并行跑多个）
- **建议第一步**：Step N …
- **Handoff**：（完成后接哪个 Skill）

确认后我将只执行该 Skill 的 Step N，完成后等待你回复「继续」。
```

设计分流细则：[SKILL_ROUTING.md](../SKILL_ROUTING.md) · [DESIGN_OVERLAP.md](../DESIGN_OVERLAP.md)

---

## 文档索引

- [docs/README.md](../../docs/README.md) — 文档阅读顺序
- [B1-org/PRODUCT.md](../../docs/domains/wms/prd/B1-org/PRODUCT.md) — 组织业务（已落地）
- [GENERATOR_SCM.md](../../docs/GENERATOR_SCM.md) — 生成器操作
- [ROADMAP.md](../../docs/ROADMAP.md) — 阶段与重建波次
