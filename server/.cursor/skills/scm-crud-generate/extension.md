# SCM CRUD Generate — Extension Guide

## 扩展点总览

```text
DDL / 表结构
    ↓ GenUtils（域推断、pk 默认、列 htmlType）
gen_table + gen_table_column
    ↓ genInfoForm options（scmDomain 等）
Velocity vm/scm/*.vm + macros.vm
    ↓ zip / preview
人工 MERGE（ErrorCode / types / api / menu）
    ↓
编译 + 菜单 + 冒烟
```

## A. 新增 SCM 域（如 `prd_*`）

1. Maven：创建 `whl-scm-prd-api/biz/admin-adapter`（参照 wms 模块）
2. `GenUtils.resolveScmDomain`：增加 `prd_` → `prd`
3. [reference.md](reference.md) §8 注册表加一行
4. `admin-web/src/api/scm/prd.ts` 新建（或 snippet merge）
5. 菜单：随当期 PRD 写入 `script/sql/{domain}/_gen/*_menu.sql`（示例 `script/sql/wms/_gen/wms_org_menu.sql`）

**Skill 无需改 workflow**，只需更新注册表文档。

## B. 新增 HTML 字段类型

1. `GenConstants.java`：`HTML_XXX = "xxxSelect"`
2. `GenUtils.initColumnField`：列名模式 → htmlType
3. `editTable.vue`：显示类型下拉加选项
4. `vm/scm/macros/formField.vue.vm`：搜索区 + 对话框分支
5. `vm/scm/macros/tableColumn.vue.vm`：列表列渲染（可选）

## C. 自定义业务键（非 id）

适用：对外暴露 `{business}_code` 而非数字 id。

1. genInfoForm：`scmBusinessKey = org_code`
2. 模板变量 `businessKeyColumn` / `businessKeyJavaField` / `businessKeyJavaType`
3. Controller：`@PathVariable String orgCode`
4. DeleteCommand / UpdateCommand：带 code 而非 id
5. 前端：row 操作传 `orgCode`

默认仍为 `id`（Long AUTO_INCREMENT）。

## D. 自定义校验 / 关联逻辑

生成器只产出 **标准 CRUD + 分页**；以下在 merge 后手写：

- 组织树 parent 校验（参照 OrgAppService.assertParentValid）
- 删除前子节点检查
- 跨 Facade 调用（如 warehouse 引用 org options）

模板留 `// GEN-TODO: custom validation` 占位。

## E. 新增 Velocity 模板片段

| 文件 | 用途 |
|------|------|
| `vm/scm/macros/javaDoc.vm` | `@author whl`、Facade 类注释 |
| `vm/scm/macros/queryWrapper.vm` | buildQueryWrapper 片段 |
| `vm/scm/macros/formField.vue.vm` | 按 htmlType 渲染表单项 |
| `vm/scm/macros/snippetHeader.vm` | `#region scm-gen:{domain}-{business}` |

新表类型（如主子表）= 新 `tplCategory` + 新模板目录，**不修改** `scm_crud` 现有模板。

## F. ScmPageResult 复用

每个域 `*-api` 只保留一份：

```java
@Data
@NoArgsConstructor
public class ScmPageResult<T> {

    /** 当前页数据 */
    private List<T> rows;

    /** 总记录数 */
    private long total;
}
```

生成器：首次生成该域时输出 `ScmPageResult.java.vm`；后续表检测到文件已存在则跳过（snippet 提示）。

## G. 与移动端 Skill 联动

PC 生成完成后，调用 [generate-crud-module](../../../../business-platform-admin-app/.cursor/skills/generate-crud-module/SKILL.md)：

- 对照 `admin-web/src/api/scm/{domain}.ts` 的 `pageXxx` / `getXxx` 路径
- perm 与 PC 一致：`scm:{domain}:{business}:list`

## H. 验收清单（任意新表通用）

- [ ] DDL 含 `tenant_id` + `AUTO_INCREMENT` pk
- [ ] 导入后 tplCategory = `scm_crud`
- [ ] preview 含 Facade/AppService/Controller/**Vo.java.vm**/vue/menu（共 25 个 `vm/scm/*`）
- [ ] zip merge 后 `mvn -pl whl-scm-{domain}-* compile` 通过
- [ ] `GET ?pageNum=1&pageSize=10` → rows/total
- [ ] CRUD + 菜单权限四件套 list/add/edit/remove
- [ ] `#region scm-gen:*` 无冲突
