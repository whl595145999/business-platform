# 组织主数据 — 产品 PRD（B1 · `wms_org`）

> 技术设计见 [TECH.md](../../design/B1-org/TECH.md)

| 项 | 内容 |
| --- | --- |
| 文档版本 | v1.5 |
| 波次 | **B1**（[industry-baseline.md](../../../../.cursor/skills/scm-product-manager/industry-baseline.md) §3） |
| 产品 | 供应链业务平台 |
| 状态 | **B1 已验收**（`b1-org-ac-acceptance.sh` 全绿；AC-ORG-14 无写权限用户需手测） |
| 关联 | [ROADMAP.md](../../../../ROADMAP.md) · [DATABASE.md](../../../../DATABASE.md) |

| 标记 | 读者 |
| --- | --- |
| **【业务】** | 运营 / 集团 IT |
| **【产品】** | PM / 实施 |
| **【研发】** | 开发 / 测试（DDL/API 细节由架构师 Step 10 合并） |

---

## 1. 背景与目标

### 1.1 背景

**【业务】** 集团租户内存在 **A 集团总部** 与 **B、C 等多家法人子公司**，各子公司组织层级不同。后续仓库、库存、单据均需挂 **货权组织**，须在 B1 先建立统一的 `wms_org` 主数据。

**【产品】** B1 组织档案是后续仓库、库存、单据的主数据前置依赖。

### 1.2 目标

| 目标 | 说明 |
| --- | --- |
| 组织树可维护 | 多层级增删改查、上级选择、启停 |
| 货权锚点清晰 | 法人/事业部/区域类型可辨，供 B3 `owner_org_id` 选用 |
| 租户隔离 | 数据按 `tenant_id` 隔离；超管可切换租户 |
| 与部门解耦 | `sys_dept` 仅可选关联，不改造平台表 |

### 1.3 本期不做

- 跨组织售卖授权（`wms_org_sales_auth`）
- 组织间调拨、货权转移
- 仓库、库存、单据
- Excel 批量导入、组织合并
- PDA 现场组织切换

---

## 2. 业务对象与示例配置

### 2.1 组织树示例（A / B / C）

**【业务】** 同一租户下的推荐配法：

```text
A公司（集团 ORG-A, type=10）
├── A总部职能（ORG-A-HQ, type=30）          ← 可选
├── B公司（法人 ORG-B, type=20）            ★ B 货权根
│     ├── B上海事业部（ORG-B-SH, type=30）
│     └── B杭州事业部（ORG-B-HZ, type=30）
└── C公司（法人 ORG-C, type=20）            ★ C 货权根
      ├── C华南大区（ORG-C-SOUTH, type=40）
      └── C成都分公司（ORG-C-CD, type=30）
```

**【产品】** B 与 C 子树结构可完全不同；`parent_org_id` 不得交叉（B 节点不能挂到 C 下）。顶级节点 **`parent_org_id = 0`**（与 RuoYi `sys_dept.parent_id` 一致，不用 `NULL`）。

### 2.2 组织 vs 部门

| 概念 | 表 | 用途 |
| --- | --- | --- |
| 货权组织 | `wms_org` | 库存归属、仓库 `owner_org_id` |
| 部门 | `sys_dept` | 登录用户、数据权限、审批 |

`linked_dept_id` 可选；B1 不强制与 `sys_dept` 同构。

---

## 3. 主数据字段（`wms_org`）

> **【研发】** 表主键列名 **`id`**（与 [DATABASE.md](../../../../DATABASE.md) §3.1、RuoYi、生成器一致）。  
> 列默认见 [DATABASE.md](../../../../DATABASE.md) §3.5（`BIGINT`/`INT`/`SMALLINT` → `0` · `VARCHAR` → `''`）。  
> 对外 API / 前端字段名 **`orgId`**（`id` → `orgId`）；他表引用本组织时用 **`owner_org_id`**、**`parent_org_id`** 等，值为 `wms_org.id`。

| 字段 | 类型 | 必填 | 说明 | 校验规则 |
| --- | --- | --- | --- | --- |
| `id` | BIGINT | 系统 | 主键 | 自增或雪花；编辑时 API 为 `orgId` |
| `tenant_id` | VARCHAR(20) | 是 | 租户编号 | 与登录租户一致 |
| `org_code` | VARCHAR(64) | 是 | 编码 | 租户内唯一；建议前缀 `ORG-B-*` |
| `org_name` | VARCHAR(128) | 是 | 名称 | 非空 |
| `org_type` | SMALLINT | 是 | 组织类型 | 10 集团 · 20 法人 · 30 事业部 · 40 区域 |
| `parent_org_id` | BIGINT | 是 | 上级组织 `id` | **默认 `0` 表示顶级**（无上级）；非 0 时须为有效 `wms_org.id`；不可成环；不可选自身或子孙 |
| `status` | SMALLINT | 是 | 状态 | **10** 启用 · **20** 停用 |
| `linked_dept_id` | BIGINT | 否 | 关联部门 | 默认 **0**=未关联；非 0 须为有效 `sys_dept` |
| `sort_order` | INT | 否 | 排序 | 默认 **0** |
| `remark` | VARCHAR(500) | 否 | 备注 | 默认 **`''`** |
| `create_by` / `create_time` | 审计 | 系统 | 创建 | 平台统一 |
| `update_by` / `update_time` | 审计 | 系统 | 更新 | 平台统一 |
| `del_flag` | CHAR(1) | 系统 | 删除 | `0` 正常 · `2` 删除 |

### 3.1 码表 `org_type`

| 值 | 名称 |
| --- | --- |
| 10 | 集团 |
| 20 | 法人 |
| 30 | 事业部 |
| 40 | 区域 |

**【研发】** 前端可用静态 `ORG_TYPE_OPTIONS` 或 `sys_dict`（`wms_org_type`）；与 [CODE_CONVENTION.md](../../../../CODE_CONVENTION.md) 步长 10 一致。

### 3.2 状态（无单据状态机）

```text
启用(10) ←→ 停用(20)
```

**【业务】** 停用后：新仓库、新单据、下拉选择 **不可再选** 该组织及其下级（B3+ 生效；B1 仅校验下拉与编辑提示）。

---

## 4. 业务流程

### 4.1 主路径

```text
列表查询（orgName/orgType/status）
  → 新增/编辑（弹窗表单）
  → 校验 org_code 唯一、parent 无环
  → 保存
  → 列表刷新
```

### 4.2 治理规则

| 规则 | 行为 |
| --- | --- |
| 编码唯一 | 重复时提示「组织编码已存在」 |
| 停用 | `status=20`；可选组织列表过滤掉停用节点 |
| 删除 | 存在子节点 → 禁止删除；B3+ 被仓库引用 → 禁止删除 |
| 改上级 | 校验不形成环；顶级保存为 `parent_org_id = 0` |
| 默认组织 | 首期 seed 至少 1 条启用组织（如 `ORG-DEFAULT`） |

---

## 5. 页面列表

| 页面 | 菜单名 | 路由 component | 端 | 实现路径 |
| --- | --- | --- | --- | --- |
| 组织档案 | 组织档案 | `scm/wms/org/index` | PC | `scm-crud-generate` + MERGE |
| 组织档案（移动） | 组织档案 | `pages-sub/scm/org/index` | 移动管理 | `generate-crud-module`（可选） |

**【研发】** 视图路径：`admin-web/src/views/scm/wms/org/index.vue` · API 前缀 `scm:wms:org`。

### 5.1 PC 页面结构

| 区域 | 内容 |
| --- | --- |
| 查询区 | `orgName`（模糊）、`orgType`、`status` |
| 工具栏 | 新增、修改、删除（权限控制） |
| 表格列 | orgCode、orgName、orgType、parentOrgName、status、updateTime |
| 表单弹窗 | orgId（编辑隐藏）、orgCode、orgName、orgType、parentOrgId（树选；顶级 **0**）、status、linkedDeptId、sortOrder、remark |

**【产品】** 查询区布局、组件细节 → `senior-ui-ux-designer` Step 3–4 简化版（仅 PC 时）。

### 5.2 权限点

| 权限标识 | 说明 |
| --- | --- |
| `scm:wms:org:list` | 分页查询 |
| `scm:wms:org:query` | 详情 |
| `scm:wms:org:add` | 新增 |
| `scm:wms:org:edit` | 修改 |
| `scm:wms:org:remove` | 删除 |

**【业务】** B1 建议仅 **集团管理员** 拥有 add/edit/remove；组织仓储管理员可先只读。

---

## 6. 角色与数据范围

| 角色 | 能力 |
| --- | --- |
| 集团管理员 | 全租户组织树 CRUD |
| 组织仓储管理员 | 本组织及下级查看（B1 只读即可） |
| 超管 | 切换租户后维护对应租户组织 |

数据 scope：复用 RuoYi + `tenant_id`；细粒度 org 过滤与 `linked_dept_id` 在 B3 与仓库联调时补强。

---

## 7. 异常与提示（业务文案）

| 场景 | 提示文案 |
| --- | --- |
| 编码重复 | 组织编码已存在，请更换 |
| 上级成环 | 上级组织不能为自己或下级组织 |
| 有子节点删除 | 存在下级组织，无法删除 |
| 停用组织被选 | 该组织已停用，不可选用 |
| 必填缺失 | 请填写组织编码/名称/类型 |

---

## 8. 通知

B1 **无** 消息通知、审批流。

---

## 9. Seed 数据（示例）

**【产品】** 首期演示/测试可导入：

| org_code | org_name | org_type | parent_org_id |
| --- | --- | --- | --- |
| ORG-A | A公司 | 10 | **0**（顶级） |
| ORG-B | B公司 | 20 | ORG-A 的 `id` |
| ORG-B-SH | B上海事业部 | 30 | ORG-B 的 `id` |
| ORG-C | C公司 | 20 | ORG-A 的 `id` |
| ORG-C-SOUTH | C华南大区 | 40 | ORG-C 的 `id` |

脚本路径（实现阶段）：`script/sql/wms/_gen/wms_org_seed.sql`（由 `mysql-dba` / `scm-crud-generate` 产出）。

---

## 10. 功能需求清单（FR，供 Step 7 AC）

| 编号 | 需求 |
| --- | --- |
| FR-ORG-01 | 分页查询组织，支持 orgName/orgType/status 筛选 |
| FR-ORG-02 | 新增组织，org_code 租户内唯一 |
| FR-ORG-03 | 编辑组织，含上级组织树选 |
| FR-ORG-04 | 删除组织（逻辑删除），有子节点时禁止 |
| FR-ORG-05 | 启停组织，停用后不可被新业务引用 |
| FR-ORG-06 | 超管按租户隔离数据 |
| FR-ORG-07 | 菜单与按钮按 `scm:wms:org:*` 权限控制 |

---

## 11. 验收标准（Step 7）

### 11.1 FR ↔ AC 矩阵

| FR | AC |
| --- | --- |
| FR-ORG-01 | AC-ORG-01、AC-ORG-02 |
| FR-ORG-02 | AC-ORG-03、AC-ORG-04、AC-ORG-05 |
| FR-ORG-03 | AC-ORG-06、AC-ORG-07、AC-ORG-08 |
| FR-ORG-04 | AC-ORG-09、AC-ORG-10 |
| FR-ORG-05 | AC-ORG-11、AC-ORG-12 |
| FR-ORG-06 | AC-ORG-13 |
| FR-ORG-07 | AC-ORG-14、AC-ORG-15 |

### 11.2 验收用例（Given / When / Then）

**前置**：菜单「组织档案」已导入；测试账号 `admin`（集团管理员，含 `scm:wms:org:*`）；租户 T1 无组织或仅有 seed。

---

**AC-ORG-01** 分页列表（FR-ORG-01）

- **Given** T1 下存在 ≥2 条启用组织  
- **When** 打开组织档案页，不填查询条件，点搜索  
- **Then** 表格分页展示当前租户组织；仅含 `del_flag=0` 记录  

**AC-ORG-02** 条件查询（FR-ORG-01）

- **Given** 存在 org_name 含「B公司」、org_type=20、status=10 的记录  
- **When** 查询区填写 orgName「B」、orgType=法人、status=启用，点搜索  
- **Then** 结果仅包含匹配记录，且均属当前租户  

**AC-ORG-03** 新增顶级组织（FR-ORG-02）

- **Given** 用户有 `scm:wms:org:add`  
- **When** 新增 org_code=`ORG-TEST`、org_name=`测试法人`、org_type=法人、parentOrgId=**0**、status=启用，保存  
- **Then** 保存成功；列表可见；`parent_org_id=0`；`tenant_id` 为当前租户  

**AC-ORG-04** 编码唯一（FR-ORG-02）

- **Given** T1 已存在 org_code=`ORG-B`  
- **When** 再次新增 org_code=`ORG-B`  
- **Then** 失败，提示「组织编码已存在，请更换」  

**AC-ORG-05** 新增子组织（FR-ORG-02）

- **Given** ORG-A 已存在，id=1001  
- **When** 新增 ORG-B，`parentOrgId=1001`  
- **Then** 保存成功；`parent_org_id=1001`；列表上级显示 A公司  

**AC-ORG-06** 编辑组织（FR-ORG-03）

- **Given** ORG-B-SH 已存在  
- **When** 修改 org_name 为「B上海事业部（更新）」，保存  
- **Then** 保存成功；列表名称已更新；`org_code` 不变  

**AC-ORG-07** 修改上级（FR-ORG-03）

- **Given** ORG-B-SH 上级为 ORG-B  
- **When** 将上级改为 ORG-C（合法节点）  
- **Then** 保存成功；`parent_org_id` 更新为 ORG-C 的 `id`  

**AC-ORG-08** 上级成环（FR-ORG-03）

- **Given** A→B→C 三级结构  
- **When** 编辑 A，将 parentOrgId 设为 C 的 id  
- **Then** 保存失败，提示「上级组织不能为自己或下级组织」  

**AC-ORG-09** 删除叶子节点（FR-ORG-04）

- **Given** ORG-B-SH 无下级、未被仓库引用（B1 恒满足）  
- **When** 勾选删除并确认  
- **Then** 逻辑删除成功；列表不再显示（或 `del_flag=2`）  

**AC-ORG-10** 有子节点禁止删（FR-ORG-04）

- **Given** ORG-B 下仍有 ORG-B-SH  
- **When** 删除 ORG-B  
- **Then** 失败，提示「存在下级组织，无法删除」  

**AC-ORG-11** 停用组织（FR-ORG-05）

- **Given** ORG-C 启用  
- **When** 编辑 status=停用，保存  
- **Then** 保存成功；列表 status 显示停用  

**AC-ORG-12** 停用后不可选（FR-ORG-05）

- **Given** ORG-C 已停用  
- **When** 在新增/编辑表单的「上级组织」树或启用组织下拉中查找 ORG-C  
- **Then** ORG-C 不出现在可选列表（或选后保存被拒并提示「该组织已停用，不可选用」）  

**AC-ORG-13** 租户隔离（FR-ORG-06）

- **Given** 超管切换租户 T1 / T2；T1、T2 各有组织数据  
- **When** 在 T1 下打开组织列表  
- **Then** 仅见 T1 数据；切到 T2 仅见 T2 数据  

**AC-ORG-14** 无权限不可写（FR-ORG-07）

- **Given** 用户仅有 `scm:wms:org:list`，无 add/edit/remove  
- **When** 打开组织档案页  
- **Then** 无新增/修改/删除按钮；调 add 接口返回无权限  

**AC-ORG-15** 菜单可见（FR-ORG-07）

- **Given** 角色已分配「组织档案」菜单及 list 权限  
- **When** 用户重新登录  
- **Then** 侧栏可见「组织档案」，可打开列表页  

### 11.3 B1 冒烟清单（实现后一次过）

- [x] AC-ORG-01～05（列表 + 新增）
- [x] AC-ORG-04、08、10（异常）
- [x] AC-ORG-11～12（启停）
- [x] AC-ORG-13（租户，超管）
- [x] AC-ORG-15（菜单/API 可达）
- [ ] AC-ORG-14（无 add 权限用户 — 脚本未覆盖，需手测）
- [x] `mvn compile` + 菜单 SQL 导入 + 重新登录

---

## 12. Handoff

| 下一步 | Skill |
| --- | --- |
| 后续波次 | 按 [industry-baseline.md](../../../../.cursor/skills/scm-product-manager/industry-baseline.md) 重新开 PRD |
| 可选 | AC-ORG-14 手测；验收脚本补无写权限用例 |

---
