---
name: scm-crud-generate
description: >-
  SCM 主数据代码生成 scm_crud：一次 zip = Java + admin-web index.vue + MERGE。
  代码生成、按表生成、主数据CRUD、wms_/oms_/inv_。RuoYi 生成器、DDL-to-CRUD、扩展域/HTML类型。
disable-model-invocation: true
---

# SCM CRUD Generate（通用 · 可扩展）

> **一次 zip = 后端 + PC 前台**，不要拆成两个 Skill。分步流程见 [workflow.md](workflow.md)。

## When to use

- 任意 SCM **主数据单表**：分页列表 + 增删改查 + 菜单 + admin-web 页
- 用户提到：`scm_crud`、代码生成、按表生成、生成后台/前台、扩展新域/新字段类型
- **不用**于：平台 `sys_*`（用 crud/tree）、单据状态机、主子表

## 执行方式

**每次一个 Step，等用户确认**。Step 末：`Step N 完成，请确认后继续 Step N+1。`  
Step 2–3 由用户在 RuoYi Admin 完成；Agent 从 Step 1 或 Step 4 接手。

| Step | 执行方 | 内容 |
| --- | --- | --- |
| 1 | Agent | DDL + dict SQL |
| 2 | **用户** | RuoYi 导入 → scm_crud → options |
| 3 | **用户** | 预览 + 下载 zip |
| 4 | Agent | 落盘 + MERGE |
| 5 | Agent | `mvn compile` + 冒烟 |
| 6 | Agent | dict / OPTIONS 微调 → [frontend-dev 生成器分支](../../../../business-platform-admin-web/.cursor/skills/frontend-dev/SKILL.md) |
| 7 | 可选 | [generate-crud-module](../../../../business-platform-admin-app/.cursor/skills/generate-crud-module/SKILL.md) |

完整说明：[workflow.md](workflow.md)

## Quick start（Agent 跟踪清单）

```text
- [ ] Read reference.md §1-3.1（命名 + API 契约 + 三通道边界）
- [ ] Step 1: DDL + dict（或委托 mysql-dba）
- [ ] Step 2-3: 输出 RuoYi 清单 → 等用户 zip 路径
- [ ] Step 4: MERGE snippets（ErrorCode / types / api / menu / Java / index.vue）
- [ ] Step 5: mvn compile + scm-crud-gen-verify.sh / 分页 CRUD 冒烟
- [ ] Step 6: frontend-dev 生成器分支（勿重写 index.vue）
```

## 输入 → 输出（通用映射）

```text
{prefix}_{business}  ──GenUtils──►  domain, Business, Entity, perm, apiPrefix
       │                                    │
       ▼                                    ▼
 gen_table.options              whl-scm-{domain}-api/biz/admin-adapter
       │                                    │
       ▼                                    ▼
 vm/scm/*.vm                    admin-web views + api/scm/{domain}.ts
       │                                    │
       ▼                                    ▼
     zip + MERGE snippets              可运行分页 CRUD
```

详细符号表：[reference.md](reference.md)

## 固定架构（所有表相同）

| 层 | 职责 |
|----|------|
| `*-api` | Facade, Command, Query, Dto, `ScmPageResult`（`@Data` POJO，无 Spring） |
| `*-biz` | `{Business}AppService`, Entity, Mapper, `buildQueryWrapper` |
| `*-admin-adapter` | `Scm{Business}Controller`, `Save{Business}Request`, **`{Business}Vo`**（仅管理端） |
| admin-web | `index.vue` + `<pagination>` + 对话框 CRUD |

API 五件套 + 可选 `/options`：见 [reference.md §3](reference.md#3-api-契约固定不随表变化)

## 主键（默认 AUTO_INCREMENT）

- DDL / Entity / AppService 规则：[reference.md §6](reference.md#6-主键策略默认)
- Java 契约：**POJO**（`@Data`，生成器仅 `@NoArgsConstructor`；MVP 手写可加 `@AllArgsConstructor`），见 [reference.md §5.5](reference.md#55-java-pojo-约定)
- **不要**使用已废弃的 `IdGeneratorUtil.nextLongId()`；主键用 `@TableId(type = IdType.AUTO)`

## gen_table.options（扩展配置）

| Key | 说明 |
|-----|------|
| scmDomain / scmResource | REST 域与资源名 |
| scmBusinessKey | 路径参数，默认 `id` |
| scmMenuId / scmMenuComponent / scmPermPrefix | 菜单与权限 |
| scmGenerateOptions | 是否生成下拉接口 |

完整键表：[reference.md §4](reference.md#4-gen_tableoptions扩展键)

## MERGE（禁止覆盖）

**唯一执行清单**：[workflow.md Step 4](workflow.md#step-4--落盘与-mergeagent)。  
符号约定：[reference.md §7](reference.md#7-merge-区域约定) · 人读手册：[GENERATOR_SCM.md](../../docs/GENERATOR_SCM.md) §4

复用已有：`ScmTenantResolver`、`useScmTenant.ts` — **不重新生成**。

## 辅助脚本

```bash
cd business-platform

# 标杆 DDL + 字典 + UI 操作清单
./script/wms/scm-crud-gen-verify.sh -h127.0.0.1 -uroot -p business_platform

# 改 Velocity 模板后
./script/wms/scm-crud-template-smoke.sh
```

## 扩展指南

| 需求 | 文档 |
|------|------|
| 新域 `prd_*` | [extension.md §A](extension.md#a-新增-scm-域如-prd_) |
| 新 HTML 字段类型 | [extension.md §B](extension.md#b-新增-html-字段类型) |
| 业务键用 code | [extension.md §C](extension.md#c-自定义业务键非-id) |
| 自定义校验 | [extension.md §D](extension.md#d-自定义校验--关联逻辑) |
| 新 Velocity 宏 | [extension.md §E](extension.md#e-新增-velocity-模板片段) |
| 移动端对接 | [extension.md §G](extension.md#g-与移动端-skill-联动) |

## 完整示例

- 验收标杆：[examples.md §1 wms_org](examples.md#例-1b1-组织-wms_org验收标杆)
- 跨域扩展：[examples.md §2 oms_carrier](examples.md#例-2oms-域新表扩展性验证)

## 生成器源码索引

| 组件 | 路径 |
|------|------|
| 模板类型 | `GenConstants.TPL_SCM_CRUD` |
| 域推断 / pk 默认 | `GenUtils.java` |
| 模板与路径 | `VelocityUtils.java`, `vm/scm/` |
| zip 落盘 | `GenTableServiceImpl.java` |
| UI | `genInfoForm.vue`, `editTable.vue` |

## Do not

- 查询区手写 `style="width: 200px"` 等内联宽度（破坏 `element-ui.scss` inline 对齐；日期范围除外用 308px）
- SCM 表用平台 `crud` 模板
- 生成 `mapper.xml`
- blind 覆盖 ErrorCode / types.ts / wms.ts
- zip 未就绪就重写 Java 或从零写 `index.vue`
- 未验收就替换手写 org/warehouse
- COMMENT 自动生 Java 枚举（已决策不做）
- 生成 OpenAPI / PDA adapter（`OpenApi*Response` / `Pda*Vo` 须手写，见 reference.md §3.1）

## Related

- PR 自检：[code-review-scm](../code-review-scm/SKILL.md) checklist §B
- 分步流程：[workflow.md](workflow.md)
- 总入口：[scm-platform-dev](../scm-platform-dev/SKILL.md) · DDL 前置：[mysql-dba](../mysql-dba/SKILL.md)
- PC 联调：[frontend-dev](../../../../business-platform-admin-web/.cursor/skills/frontend-dev/SKILL.md)
- 规范：[MODULE_RULES.md](../../docs/MODULE_RULES.md) §7/§8/§9
- 三通道：[API_CONTRACT.md](../../docs/API_CONTRACT.md) §2.5
- 操作手册：[GENERATOR_SCM.md](../../docs/GENERATOR_SCM.md)
- 移动端：[generate-crud-module](../../../../business-platform-admin-app/.cursor/skills/generate-crud-module/SKILL.md)
