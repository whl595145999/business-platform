---
name: code-review-scm
description: >-
  SCM PR 自检与 Code Review：MODULE_RULES、三通道 Vo、MERGE、DDL、生成器产物、
  admin-web、admin-app。Use before PR, after codegen merge, or reviewing a diff.
disable-model-invocation: true
---

# SCM Code Review

> 轻量 PR 自检 Skill。不替代人工 Review，输出结构化 **`# Review Report`**。  
> 每次只审一类变更；Step 末：`Step N 完成，请确认后继续 Step N+1。`

## When to use

- 提交 PR / merge 生成器 zip 之后
- 用户说「帮我 review」「检查 PR」「自检」
- 产品/架构 PRD 定稿后对照 AC（读 `docs/*_PRD.md`，非代码 diff）；波次与依赖见 [industry-baseline.md](../scm-product-manager/industry-baseline.md)
- 子 Skill 最后一步（如 `java-backend-dev` Step 6、`frontend-dev` Step 8）可转本 Skill

## 分流（必须先做）

```text
本次 diff 主要涉及？
  ├─ DDL / SQL only        → checklist §A
  ├─ 生成器 MERGE + zip    → checklist §B
  ├─ Java 后端             → checklist §C
  ├─ admin-web             → checklist §D
  ├─ admin-app             → checklist §E
  └─ 跨仓全栈              → §A→§C→§D（可选 §E），每节一份报告
  └─ 仅 PRD/设计文档       → checklist §G
  └─ PDA / OpenAPI diff    → checklist §H
```

详细条目：[checklist.md](checklist.md)

## Workflow

| Step | 输出 |
| --- | --- |
| 1 | 识别变更类型 + 读取 `git diff` / 用户指定文件 |
| 2 | 按 checklist 逐项检查，标 **PASS / FAIL / N/A** |
| 3 | `# Review Report`（阻塞项 + 建议项 + 文档引用） |

### Step 3 报告模板

```markdown
# Review Report

- **范围**：（DDL / MERGE / Java / PC / 移动 / 全栈）
- **阻塞**：（必须修才能合）
- **建议**：（可跟进）
- **已核对文档**：（MODULE_RULES §x、API_CONTRACT §x …）

## 检查结果

| 项 | 结果 | 说明 |
| --- | --- | --- |
| … | PASS/FAIL | … |
```

## 硬红线（FAIL 即阻塞）

| 主题 | 规则 |
| --- | --- |
| 分层 | `*-api` 无 Spring；跨域仅 Facade/Event，无跨域 Mapper |
| HTTP | Admin 出参 **Vo** + `toVo(Dto)`；OpenAPI/PDA 独立模型 |
| 库表 | SCM 表带域前缀 + `tenant_id`；`del_flag`；码 **SMALLINT 步长 10** |
| 生成器 | MERGE 仅 append `#region`；不覆盖 org/warehouse |
| PC 查询区 | 无内联 width（日期 308px 除外） |
| 移动 | 仅 `http.ts`；SCM 用 `pageXxx` 非 `/list` |

## 按场景快捷入口

| 场景 | 重点 |
| --- | --- |
| 主数据 codegen | [workflow Step 4–5](../scm-crud-generate/workflow.md) + checklist §B |
| 仓库 category/subtype | `subtype/100 == category/10`（非 `subtype/100 != category`） |
| 单据幂等 | 写操作 Command 带 `idempotencyKey` |
| 移动 SCM | 对照 `admin-web/src/api/scm/*.ts`，见 [generate-crud-module](../../../../business-platform-admin-app/.cursor/skills/generate-crud-module/SKILL.md) |

## Do not

- 未看 diff 就标全部 PASS
- 把 COMMENT 自动生成枚举当缺失项（已决策不做）
- 要求拆库/微服务（当前单库策略）

## Related

- [checklist.md](checklist.md)
- [scm-platform-dev](../scm-platform-dev/SKILL.md)
- [MODULE_RULES.md](../../docs/MODULE_RULES.md) §4/§8/§9
- [API_CONTRACT.md](../../docs/API_CONTRACT.md) §2.5
- admin-app：[PR检查清单.md](../../../../business-platform-admin-app/docs/PR检查清单.md)
