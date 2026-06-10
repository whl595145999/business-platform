# scm_crud 分步工作流（人 / Agent 分工）

> **一次 zip = 后端 Java + admin-web `index.vue` + MERGE 片段**。不要拆成「只生成后台」或「只生成前台」两条线。  
> 执行方式：**每次一个 Step，等用户回复「继续」**。Step 末：`Step N 完成，请确认后继续 Step N+1。`  
> **前置**：当期波次 PRD + AC 已定（见 [industry-baseline.md](../scm-product-manager/industry-baseline.md)）；`whl-scm/{domain}` 模块须已按架构师方案创建后再 Step 4 落盘。

---

## 角色边界

| 角色 | 能做 | 不能做 |
| --- | --- | --- |
| **用户** | RuoYi 导入表、选 `scm_crud`、填 options、预览、下载 zip | — |
| **Agent** | DDL/dict SQL、解压 zip、MERGE、编译、冒烟、dict/OPTIONS 微调 | 代替用户在 Admin UI 点生成按钮 |

用户完成 Step 2–3 后，把 **zip 路径** 或解压目录告诉 Agent，再进入 Step 4。

---

## Step 1 — 库表与字典（Agent，可委托 mysql-dba）

**产出**

- `script/sql/{domain}/{table}_ddl.sql`：`tenant_id` + `AUTO_INCREMENT id` + 域前缀表名
- 可选 `*_dict.sql`：`sys_dict` / `sys_dict_data`（运营可配下拉）

**检查**

- [ ] 读 [reference.md §1–3.1](reference.md#1-命名与模块映射)
- [ ] status/type 用 **SMALLINT 步长 10**
- [ ] 审计字段 + `del_flag CHAR(1)`
- [ ] 列默认符合 [DATABASE.md §3.5](../../docs/DATABASE.md)（`BIGINT`/`INT`/`SMALLINT`/`TINYINT` → `0` · `VARCHAR` → `''`）

**辅助脚本**（标杆 `wms_org`）

```bash
cd business-platform
./script/wms/scm-crud-gen-verify.sh -h127.0.0.1 -uroot -p business_platform
```

---

## Step 2 — RuoYi 配置（用户，Agent 只输出清单）

**用户操作**

1. Admin → **系统工具 → 代码生成 → 导入** 目标表
2. 编辑 → 模板 **SCM 单表（Facade CRUD / scm_crud）**
3. 填 `gen_table.options`（见 [reference.md §4](reference.md#4-gen_tableoptions扩展键)）
4. 字段：`isQuery`、`htmlType`、字典类型（WMS 默认 `org_type`→`wms_org_type`、`status`→`wms_enable_status`）
5. 主键 `id`：**自增=1**、**插入=0**
6. **提交**

**Agent 输出**：把上表打成 checklist，等用户确认「已提交」。

详细 UI 说明：[GENERATOR_SCM.md](../../docs/GENERATOR_SCM.md) §2.5、§3。

---

## Step 3 — 预览与下载 zip（用户）

**用户操作**

- 预览 `GET /tool/gen/preview/{tableId}` — 应含 **25** 个 `vm/scm/*` 产出（含 `Vo.java.vm`、`index.vue.vm`）
- 下载 `GET /tool/gen/batchGenCode`

**Agent**：等用户提供 zip 路径；**不要**在 zip 未就绪时写 Java 或 Vue。

---

## Step 4 — 落盘与 MERGE（Agent）

zip 内 `scm-gen/{domain}/{business}/` 结构见 [GENERATOR_SCM.md](../../docs/GENERATOR_SCM.md) §2.1。

**MERGE 清单**（仅 append `#region scm-gen:{domain}-{business}`，禁止覆盖）

- [ ] `MERGE/ErrorCode-*.snippet` → `{Domain}ErrorCode.java`
- [ ] `MERGE/types-*.snippet` → `admin-web/src/api/scm/types.ts`
- [ ] `MERGE/api-*.snippet` → `admin-web/src/api/scm/{domain}.ts`（补全 import）
- [ ] 复制 Java 到 `whl-scm/{domain}/...`（含 `controller/vo/{Business}Vo.java`）
- [ ] 复制 `views/scm/{domain}/{business}/index.vue`
- [ ] `ScmPageResult.java` **全项目只需 merge 一次**
- [ ] 菜单 SQL → `sys_menu` / `sys_role_menu`
- [ ] MODULE_RULES §8.2 登记 Controller 一行
- [ ] **不要**覆盖 `ScmTenantResolver` / `useScmTenant.ts` / 手写 org、warehouse

页面引用 **`@/api/scm/{domain}.ts`**，不是 zip 内 `_gen/*.ts`。

细节：[reference.md §7](reference.md#7-merge-区域约定)

---

## Step 5 — 编译与冒烟（Agent）

```bash
cd business-platform
mvn compile -pl whl-scm -am -q
./script/wms/scm-crud-template-smoke.sh   # 改 Velocity 后 + 重启 admin
```

**业务冒烟**

- [ ] 导入菜单 SQL → **重新登录**
- [ ] 分页列表、新增、编辑、删除
- [ ] 超管租户切换（若适用）

验收标杆：[examples.md §1](examples.md#例-1b1-组织-wms_org验收标杆) · 扩展检查：[extension.md §H](extension.md#h-验收清单)

---

## Step 6 — PC 联调收尾（Agent，可转 frontend-dev）

生成器已出 `index.vue` 时 **不要从零重写页面**，只做：

- [ ] dict 未命中 → 补 `sys_dict` 或静态 `XXX_OPTIONS`（B1 组织见 [frontend-dev/reference.md](../../../../business-platform-admin-web/.cursor/skills/frontend-dev/reference.md)）
- [ ] 查询区勿内联 width（日期范围 308px 除外）
- [ ] 权限前缀与 `scmPermPrefix` 一致

加载 [frontend-dev](../../../../business-platform-admin-web/.cursor/skills/frontend-dev/SKILL.md) **生成器分支** Step G1–G4。

---

## Step 7 — 移动端（可选）

后端 + PC 稳定后，加载 [generate-crud-module](../../../../business-platform-admin-app/.cursor/skills/generate-crud-module/SKILL.md)，API 路径对齐 admin-web。

---

## 全栈串联（主数据）

```text
mysql-dba Step 1
  → scm-crud-generate Step 1–5（本 workflow）
  → frontend-dev 生成器分支（Step 6）
  → generate-crud-module（Step 7，可选）
```

单据/状态机：**不走本 workflow**，用 [java-backend-dev](../java-backend-dev/SKILL.md)。
