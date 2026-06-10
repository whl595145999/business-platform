# Agent 指引（admin-web）

Vue 3 + Element Plus 管理端 · 对接 `/api/scm/{domain}/...`。

## Cursor Skills（优先）

| 场景 | Skill |
| --- | --- |
| 全栈分流 | `@scm-platform-dev`（仓库：[business-platform/.cursor/skills](../business-platform/.cursor/skills/README.md)） |
| UI 线框 / 交付包 | `@senior-ui-ux-designer`（[business-platform/.cursor/skills/senior-ui-ux-designer](../business-platform/.cursor/skills/senior-ui-ux-designer/SKILL.md)） |
| 页面开发 / 联调 | `@frontend-dev`（[.cursor/skills/frontend-dev/SKILL.md](.cursor/skills/frontend-dev/SKILL.md)） |
| 生成器 zip 已 merge | `frontend-dev` **生成器分支** G1–G4 |
| PR 自检 | `@code-review-scm`（§D） |

生成器 MERGE 清单：[business-platform/.cursor/skills/scm-crud-generate/workflow.md](../business-platform/.cursor/skills/scm-crud-generate/workflow.md#step-4--落盘与-mergeagent)

## 文档

- 业务 PRD / 技术设计（主仓）：[domains/](../business-platform/docs/domains/) — `prd/` 产品 · `design/` 技术
- B1 组织：[PRODUCT.md](../business-platform/docs/domains/wms/prd/B1-org/PRODUCT.md) · [TECH.md](../business-platform/docs/domains/wms/design/B1-org/TECH.md)
- PC 无独立 `docs/prd/`；标准 CRUD 复制 `views/scm/wms/org` 标杆

## 硬约束（摘要）

- 查询区勿内联 width（日期范围 308px 除外）
- `v-hasPermi` 与后端 `scmPermPrefix` 一致
- 码表：`useDict` 或静态 `XXX_OPTIONS`（B1 组织见 [.cursor/skills/frontend-dev/reference.md](.cursor/skills/frontend-dev/reference.md)）

## 工作区

建议在 monorepo 根或三仓同开。
