---
name: frontend-dev
description: >-
  Phased Vue3 admin development for business-platform-admin-web. Two paths: generator
  merge/tuning after scm_crud zip, or hand-written scm pages. Element Plus, useDict,
  static OPTIONS, v-hasPermi. Use for admin pages or post-codegen PC联调.
disable-model-invocation: true
---

# Frontend Development（admin-web）

## Role

资深前端架构师：Vue 3 + TS + Element Plus 中后台。

**不是 React**；不用 TanStack Query。

## 路径分流（必须先做）

```text
页面来自 scm_crud 生成器 zip（已 MERGE index.vue）？
  ├─ 是 → 【生成器分支】Step G1 起（勿从零写 index.vue）
  └─ 否 → 【手写分支】Step 1 起（完整 9 Step）
```

生成器主流程在 [scm-crud-generate](../../../../business-platform/.cursor/skills/scm-crud-generate/workflow.md) Step 4–6；本 Skill 只负责 **PC 收尾或手写**。  
标杆页面见 [reference.md](reference.md)（B1 组织 · B2 货品）。业务 PRD 见 [domains/](../../../business-platform/docs/domains/)。  
Step 末：`Step N 完成，请确认后继续 Step N+1。`

---

## 生成器分支（MERGE 后联调）

**何时用**：`scm-crud-generate` 已落盘 `views/scm/**` + `api/scm/*`，需 dict/OPTIONS/冒烟。

| Step | 输出 |
| --- | --- |
| G1 | 核对 MERGE：`#region scm-gen:{domain}-{business}` 在 types.ts、`{domain}.ts` |
| G2 | dict：补 `sys_dict` 或改静态 `XXX_OPTIONS` + `formatXxxLabel`（标杆：`wms/org` 的 `org_type`） |
| G3 | 查询区/权限/租户：`useScmTenant`、`v-hasPermi` 与 `scmPermPrefix` 一致 |
| G4 | 冒烟：分页、增删改、重新登录后菜单可见 |

**MERGE**：由 [workflow Step 4](../../../../business-platform/.cursor/skills/scm-crud-generate/workflow.md#step-4--落盘与-mergeagent) 完成；本分支只做 G1 核对 + dict/OPTIONS/冒烟。

**不要**：重写生成器已出的列表/表单结构；除非业务明确要求改布局。

---

## 手写分支（完整 Workflow）

**何时用**：单据页、库存页、非标准 CRUD、生成器不适用的场景。

| Step | 输出 |
| --- | --- |
| 1 | `# Requirement Analysis` |
| 2 | `# Page Design` |
| 3 | `# API Design` |
| 4 | `# Component Design` |
| 5 | `# State Design` |
| 6 | 实现（views + api + types） |
| 7 | `# Tests` / 冒烟 |
| 8 | `# Review Report`（或转 [code-review-scm](../../../../business-platform/.cursor/skills/code-review-scm/SKILL.md) §D） |
| 9 | `# PR Summary` |

每次只执行一个 Step，等用户「继续」。

---

## 技术栈

Vue 3.5 · Vite · Element Plus · Pinia · axios（`@/utils/request`）  
类型：`@/api/scm/types.ts` · 接口：`wms.ts` · `product.ts`

## 目录

```text
src/views/scm/{domain}/{feature}/index.vue
src/api/scm/{domain}.ts
src/composables/useScmTenant.ts
```

## SCM 列表页约定

- 外层 `div.p-2`；查询区 `showSearch` + `el-card`
- **查询区勿**给 input/select 写内联 width（日期范围 308px 除外）
- 表格 `v-loading`；`<pagination>`；弹窗 `el-dialog`
- 权限 `v-hasPermi="['scm:wms:org:add']"`
- 租户：`useScmTenant()`（超管）
- 码表：`useDict` 或静态 `XXX_OPTIONS` + `formatXxxLabel`（如 org_type）

## 标杆页面

B1 `views/scm/wms/org` · B2 `views/scm/product/*` — 详见 [reference.md](reference.md)。

## Related

- [reference.md](reference.md)
- [senior-ui-ux-designer](../../../../business-platform/.cursor/skills/senior-ui-ux-designer/SKILL.md) — UI 交付包（Step 8）输入
- [scm-crud-generate/workflow.md](../../../../business-platform/.cursor/skills/scm-crud-generate/workflow.md)
- [scm-platform-dev](../../../../business-platform/.cursor/skills/scm-platform-dev/SKILL.md)
- [java-backend-dev](../../../../business-platform/.cursor/skills/java-backend-dev/SKILL.md)
