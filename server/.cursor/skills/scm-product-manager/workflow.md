# 供应链产品 8 Step 工作流

## 入口分流

```text
诉求类型？
  ├─ 从 0 重建 / 全链路排期 → 读 industry-baseline.md → Step 1（程序级）→ Step 2（含波次表）
  ├─ 单一功能（如组织、入库）→ Step 1（功能级）起
  ├─ 仓储/库存/订单/采购/调拨/盘点/退货 → 本 Skill Step 1 起
  ├─ 仅补单据+状态机 → Step 3 → 5 → 7
  ├─ 仅库存口径（ATP/预占） → Step 4 → 5
  ├─ 已有波次 PRD（如 B1 组织）→ 读 reference + baseline，跑增量 Step
  └─ C 端商城/营销/埋点为主 → senior-product-manager
```

**程序级 vs 功能级**

| 类型 | Step 1 范围 | Step 2 额外产出 |
| --- | --- | --- |
| **程序级** | 九域 + L0～L5 + 场景选型（§4） | 全文 MDM 清单 + [industry-baseline.md](industry-baseline.md) §2 依赖 + §3 波次 |
| **功能级** | 当前功能的对象/动作/上下游 | 本功能相关 MDM 字段 + 与波次关系 |

---

## Step 1 — Business Model

**先分析四流**：业务流 · 物流 · 资金流 · 信息流（禁止跳步画页面）

**产出**：角色、业务对象、动作、上下游、业务域模型（可对标九域）

**必读**：[domains/README.md](../../docs/domains/README.md) · [ROADMAP.md](../../docs/ROADMAP.md) · 重建时 [industry-baseline.md](industry-baseline.md) §1、§4

**检查清单**

- [ ] 已声明：程序级 or 功能级
- [ ] 四流各至少 1 条与本需求相关
- [ ] 业务对象与九域归属清晰
- [ ] V1 边界（做 / 不做）已列出

---

## Step 2 — Master Data

**产出**：组织、仓库、商品、供应商、客户、单位、批次、库位 — **归属域**、**硬依赖顺序**、**本期波次**、**本期不做**

**必读**：[industry-baseline.md](industry-baseline.md) §2、§3、§5 · [DATABASE.md](../../docs/DATABASE.md) 表前缀

**检查清单**

- [ ] MDM 清单表（对象 · 域 · 表前缀草案 · 本期做否）
- [ ] 硬依赖顺序（谁先谁后，参照 baseline §2）
- [ ] 若程序级：波次 B1～Bn 与解锁能力（baseline §3）
- [ ] 编码 / 启停 / 删除治理（baseline §5）
- [ ] 未写 API、未写页面字段级 UI

---

## Step 3 — Document Design

**产出**：单据名、编码规则、状态（**SMALLINT 步长 10**）、字段、关联单

示例单据：采购订单、收货、销售订单、调拨、盘点、退货 — **仅列本期范围**。

参考：[CODE_CONVENTION.md](../../docs/CODE_CONVENTION.md) · [industry-baseline.md](industry-baseline.md) §7（单据码待 B5+ PRD）

**主数据 CRUD 可跳过本章**（无单据状态机）。

**检查清单**

- [ ] 单据族类型（计划/执行/交易）已标注
- [ ] 状态列表与转移方向
- [ ] 关联单据（头-行）

---

## Step 4 — Inventory Model

**产出**：现有/可用/锁定/在途/安全/预警 + **计算公式**

对齐 Inventory 域：`inv_stock`、预占、渠道可售；禁止与 WMS 作业混淆。

参考：[industry-baseline.md](industry-baseline.md) §6

**无库存需求可跳过**。

**检查清单**

- [ ] 各口径定义无歧义
- [ ] 公式可写成伪代码
- [ ] WMS 作业 vs 库存账边界清楚

---

## Step 5 — Business Flow

**产出**：正常 / 异常 / 逆向 / 取消 / 退货 流程（文字 + 可选流程图）

主数据 CRUD：**简版**（创建→编辑→启停→查询；删除/树调整异常）。

链路参考：[scm-platform-dev/workflow.md](../scm-platform-dev/workflow.md) §5

**检查清单**

- [ ] 正常路径逐步可执行
- [ ] 异常与逆向已列（本期范围内的）

---

## Step 6 — PRD

**此时才写页面**：背景、流程图、**页面列表**、字段、状态图、权限、通知、异常

结构对标 [B1-org/PRODUCT.md](../../docs/domains/wms/prd/B1-org/PRODUCT.md)（版本表、三类读者 `[业务]`/`[产品]`/`[研发]`）

建议路径：`docs/domains/{域}/prd/B{n}-{slug}/PRODUCT.md`（技术章 → `design/B{n}-{slug}/TECH.md`，见 [PRD规范.md](../../docs/PRD规范.md)）

**检查清单**

- [ ] 页面路由与菜单草案
- [ ] 字段表（含必填/校验业务规则）
- [ ] 权限与数据 scope
- [ ] 异常提示（业务文案，非错误码）

---

## Step 7 — Acceptance Criteria

**格式**：Given / When / Then；与 FR 编号可追溯（FR↔AC 矩阵）

**检查清单**

- [ ] 每条 AC 可测试
- [ ] 覆盖主路径 + 关键异常（停用、删除约束等）

---

## Step 8 — 风险分析

库存 / 财务 / 供应商 / 一致性 / 性能 — 每条含 **缓解措施**。

**检查清单**

- [ ] 至少 3 条风险
- [ ] 与本期波次范围相关

---

## Handoff

| 定稿 | 下一步 |
| --- | --- |
| Step 6–7 | `@senior-ui-ux-designer` Step 1–8（双端/复杂交互时）→ `@ecommerce-scm-architect`（ER、API） |
| 主数据 CRUD 页 | `@senior-ui-ux-designer`（可选）→ `@scm-platform-dev` → `scm-crud-generate` |
| 单据/库存引擎 | `@scm-platform-dev` → `java-backend-dev` |
| PRD 评审 | `@code-review-scm`（对照 AC） |
