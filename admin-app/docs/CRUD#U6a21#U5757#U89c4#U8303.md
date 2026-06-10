# CRUD 模块规范（可扩展）

> 新增业务模块的标准路径。标杆实现：**用户**（`user`）、**角色**（`role`）、**部门**（`dept`）、**岗位**（`post`，Skill 验证实例）。  
> AI 生成请使用 Skill：`.cursor/skills/generate-crud-module/SKILL.md`

---

## 1. 分层架构

```
admin-web (PC)          mobile (本项目)
─────────────────       ─────────────────────────────
api/system/user/   →    api/system/user.ts
types + index.ts   →    api/types/system/user.ts
views/system/user/ →    pages-sub/admin/user/index.vue
                        utils/menu.ts (ADMIN_PAGE_MAP)
```

**原则**：路径、方法、权限标识与 PC 一致；UI 用移动端模板（表单遵循 **@wot-ui/ui 2.0.8** v2 API，标杆 `pages-sub/scm/org/form.vue`）。

---

## 2. 模块清单（生成物）

| 序号 | 产物 | 必须 |
|------|------|------|
| 1 | `src/api/types/{domain}/{module}.ts` | 是 |
| 2 | `src/api/{domain}/{module}.ts` | 是 |
| 3 | `src/pages-sub/{pkg}/{module}/index.vue` | 是（列表） |
| 4 | `src/pages-sub/{pkg}/{module}/form.vue` | 按需 |
| 5 | `ADMIN_PAGE_MAP` 条目 | 是 |
| 6 | `docs/admin-web接口对照.md` 更新 | 是 |

---

## 3. 命名约定

| 类型 | 规则 | 示例 |
|------|------|------|
| 目录 | kebab-case | `pages-sub/admin/role/` |
| 类型 | `I` + 模块 + `Vo/Form/Query` | `IRoleVo` |
| API | 动词 + 模块 | `listRole`, `delRole` |
| 权限 | `{domain}:{module}:{action}` | `system:role:list` |
| 分包 | 系统 `admin`，供应链 `scm` | `pages-sub/scm/supplier/` |

---

## 4. 列表页标准（复制 user 标杆）

```
AppPage
  └─ wd-search (bp-card)
  └─ z-paging
       └─ bp-card 行（头像/标题/状态/副字段）
       └─ wd-empty（无权限 / 无数据）
```

- 权限：`hasPermi('{perm}:list')`
- 分页：`pageNum` / `pageSize`，响应 `IPageResult<T>`
- 操作按钮：`PermButton` + `perm`
- **form 返回刷新**：列表页 `useListRefresh(pagingRef)`（`src/hooks/useListRefresh.ts`）

---

## 5. 命令行脚手架

```bash
pnpm scaffold:crud -- \
  --module role \
  --domain system \
  --title 角色管理 \
  --perm system:role \
  --id-field roleId \
  --name-field roleName
```

生成 types、api、list 页面骨架；**仍需**对照 admin-web 补全字段与接口。

---

## 6. 供应链扩展（scm 域）

```
src/api/scm/supplier.ts
src/api/types/scm/supplier.ts
src/pages-sub/scm/supplier/index.vue
```

`ADMIN_PAGE_MAP` 增加后台菜单 component 映射（路径按后台配置）。

---

## 7. 后续演进（可选）

| 阶段 | 内容 |
|------|------|
| P1 | 列表 CRUD（当前） |
| P2 | form 页 + 字典/下拉 |
| P3 | 详情只读页 |
| P4 | CLI 从 admin-web 自动读 VO 字段（需 PC 项目路径） |

---

## 8. 检查清单

- [ ] admin-web 同模块 API 已对照
- [ ] 仅用 `http.ts`
- [ ] 列表页结构同 user 标杆
- [ ] 有 form 时列表页已加 `useListRefresh`
- [ ] `ADMIN_PAGE_MAP` 已注册
- [ ] 接口对照.md 已更新
- [ ] `docs/PR检查清单.md` 已勾选

---

*配套： [开发规范.md](./开发规范.md) · [UI组件规范.md](./UI组件规范.md)*
