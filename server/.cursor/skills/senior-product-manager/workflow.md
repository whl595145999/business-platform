# 互联网产品 8 Step 工作流

## 入口分流

```text
需求类型？
  ├─ 管理端/UI/权限/平台功能/埋点 → 本 Skill Step 1 起
  ├─ 供应链单据/库存/仓储规则 → scm-product-manager
  ├─ 技术方案/API/库表 → ecommerce-scm-architect
  └─ 仅实现已定 PRD → scm-platform-dev
```

Agent 开场：列 **模糊点清单**，Step 1 未确认前不得进入 Step 2。

---

## Step 1 — Requirement Clarification

**必含**：目标、用户、痛点、商业价值、KPI、**待确认项列表**

---

## Step 2 — User Story

角色 × 动机 × 价值；区分 B 端（仓储管理员）与 C 端（若适用）。

---

## Step 3 — Business Flow

用户主路径 + 异常 + 边界；文字流程图即可。

---

## Step 4 — Feature List

**P0 / P1 / P2** + 依赖关系；对齐 [ROADMAP.md](../../docs/ROADMAP.md) 阶段。

---

## Step 5 — PRD

背景、目标、页面列表、功能、交互、数据规则、异常、**埋点**

---

## Step 6 — Prototype Description

布局、组件、按钮、弹窗、错误/空/加载 — **文字简述**即可；深度线框与组件 Props 交 [senior-ui-ux-designer](../senior-ui-ux-designer/SKILL.md) Step 3–8。

标杆：B1 落地后 `views/scm/wms/org/index.vue`（生成器 MERGE）；重建前参考 [frontend-dev/reference.md](../../../../business-platform-admin-web/.cursor/skills/frontend-dev/reference.md)

---

## Step 7 — Acceptance Criteria

Given / When / Then，可测、无歧义。

---

## Step 8 — Delivery Package

| 交付项 | 说明 |
| --- | --- |
| 页面清单 | `views/` 路径 |
| 接口清单 | 对照 admin-web `api/` |
| 实体建议 | 仅建议，DDL 交 `mysql-dba` |
| 测试场景 | 冒烟用例 |
| 风险清单 | 与 Step 1 KPI 呼应 |

**Handoff**：`@scm-platform-dev` · `@frontend-dev` · 供应链深度需求转 `@scm-product-manager`
