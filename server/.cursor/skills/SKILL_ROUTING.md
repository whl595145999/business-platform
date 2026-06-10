# Skill 路由（Agent 必读）

> 用户 `@scm-platform-dev` 或描述模糊时，先读本页再选 **一个** Skill。  
> 原则：**设计不写代码 · 实现不跳过定稿 · 每次一个 Step**。  
> 角色心智：**卖** → `senior-product-manager` · **履约** → `scm-product-manager` · **界面** → `senior-ui-ux-designer` · **技术方案** → `ecommerce-scm-architect`（见 [ROLES.md](ROLES.md)）

---

## 30 秒决策树

```text
要写/改代码？
  └─ 否 → 设计阶段 ↓
         ├─ 仓储/库存/单据/采购/调拨/盘点/退货/四流？
         │    └─ @scm-product-manager（8 Step）
         ├─ 管理端体验/用户故事/P0/埋点/平台功能？
         │    └─ @senior-product-manager（8 Step）
         ├─ PRD 已定，要线框/组件/设计系统/多端/开发交付包？
         │    └─ @senior-ui-ux-designer（8 Step）
         └─ ER/API/DDD/部署/异常技术方案/技术 PRD？
              └─ @ecommerce-scm-architect（10 Step，可 Step 6 起）
  └─ 是 → @scm-platform-dev
         ├─ 建表 → mysql-dba
         ├─ 主数据单表 CRUD → scm-crud-generate
         ├─ 单据/状态机/库存引擎 → java-backend-dev
         ├─ PDA 现场 /api/pda/wms → java-backend-dev（pda-adapter，非 admin-app）
         ├─ OpenAPI 对接 → java-backend-dev（openapi-adapter）
         ├─ PC 管理端 → frontend-dev
         ├─ 移动管理端 CRUD → generate-crud-module（非 PDA）
         └─ PR / PRD 审 → code-review-scm（§A–G）
```

---

## 设计四件套：谁做什么

| 维度 | scm-product-manager | senior-product-manager | senior-ui-ux-designer | ecommerce-scm-architect |
| --- | --- | --- | --- | --- |
| 视角 | 供应链产品总监 | 互联网产品经理 | 资深 UI/UX 设计师 | 解决方案架构师 |
| 强项 | 四流、单据、库存口径、AC | 澄清、故事、P0/P1、埋点 | IA、线框、组件、设计系统、交付包 | DDD、ER、API、Event、部署 |
| 弱项 | 微服务拆分、MQ 选型 | WMS 单据状态机细节 | 业务单据规则、写代码 | 页面交互、运营文案 |
| Step 数 | 8 | 8 | 8 | 10 |
| 禁止 | 先画页面（Step 1–5） | 跳过澄清 | 写 Java/Vue、发明业务规则 | 写 Java/Vue |

**不要四个设计 Skill 从 Step 1 各跑一遍。** 选主 Skill，其余只在 Handoff 点接入。

---

## Step 重叠与推荐路径

| 主题 | 产品（scm-pm） | 架构师（architect） | 推荐 |
| --- | --- | --- | --- |
| 业务分析 | Step 1 四流 | Step 1 | 供应链 → **产品** |
| 主数据 | Step 2 | Step 3 | **产品** 定业务归属 → 架构师补表前缀/域 |
| 流程 | Step 5 | Step 4 | **产品** 业务流程 → 架构师对齐 Facade 边界 |
| 状态机 | Step 3 单据 | Step 5 | **产品** 业务状态 → 架构师 SMALLINT/错误码 |
| 库存模型 | Step 4 | Step 4 流程 | **产品** 口径公式 → 架构师 `inv_*` |
| 库表 | 不写 | Step 6 | **仅架构师** → mysql-dba |
| API/Event | 交付包建议 | Step 7 | **仅架构师** |
| PRD 成稿 | Step 6–7 | Step 10 | 产品写业务章 → 架构师合并技术章 |

### 标准全链路（新供应链功能）

```text
scm-product-manager Step 1–7
  → senior-ui-ux-designer（全量 Step 1–8 / 增量 3–4+8 / 现有页 Audit Step 0）
  → ecommerce-scm-architect Step 6–10（可跳过已覆盖的 Step）
  → scm-platform-dev
```

### 从 0 重建（程序级）

```text
scm-product-manager 读 industry-baseline.md
  → Step 1–2（程序级：L0–L5 + 波次 B1–Bn）
  → 各波次功能级 Step 1–7（如 B1 组织）
  → senior-ui-ux-designer（全量或 Audit，见 SKILL.md 三种模式）
  → ecommerce-scm-architect → scm-platform-dev
```

### 轻量路径（仅主数据 CRUD 页）

```text
scm-product-manager Step 2（简版）
  → mysql-dba → scm-crud-generate → frontend-dev
```

### 轻量路径（仅管理端改版）

```text
senior-product-manager Step 1–7
  → senior-ui-ux-designer（全量或增量 + Handoff 交接表）
  → frontend-dev / java-backend-dev（按交付包）
```

---

## 九域 → Skill 速查

| 域 | 设计 Skill | 实现 Skill |
| --- | --- | --- |
| 用户 | senior-product-manager | 平台模块（非 scm-crud） |
| 商品 | scm-product-manager → architect | B2 已落地 · [PRODUCT.md](../../docs/domains/product/prd/B2-product/PRODUCT.md) |
| 营销 | senior-product-manager | 未建 |
| 交易 | scm-product-manager → architect | java-backend-dev (OMS) |
| 库存 | scm-product-manager → architect | java-backend-dev (inv) |
| 采购 | scm-product-manager → architect | java-backend-dev (erp 远期) |
| 仓储 | scm-product-manager → architect | scm-crud + java-backend |
| 物流 | scm-product-manager | 远期 |
| 售后 | scm-product-manager Step 5/9 | java-backend-dev |

---

## 反例（必须纠正）

| 用户说 | 错误 | 正确 |
| --- | --- | --- |
| 设计入库单 | senior-product-manager | scm-product-manager |
| 画原型/线框 | ecommerce-scm-architect | senior-ui-ux-designer（PRD 已定） |
| 写 org 列表 Vue | senior-ui-ux-designer | frontend-dev |
| 生成 org CRUD | scm-product-manager Step 1–10 | scm-crud-generate |
| 四个设计 Skill 各跑 Step 1 | 重复劳动 | 选一个主 Skill |
| 架构师 Step 1 写页面交互 | 越界 | senior-ui-ux-designer Step 3–4 |
| PDA 收货/发货 | generate-crud-module / frontend-dev | 后端 java-backend-dev；前端 B8+ uniapp-dev |
| OpenAPI 给 ERP | 在 admin Controller 加路由 | openapi-adapter + `OpenApi*Response` |

---

## 全 Skill 索引

见 [README.md](README.md)
