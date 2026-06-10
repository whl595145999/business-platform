# admin-web 与移动端接口对照

> PC 端路径：`business-platform-admin-web/src/api/`  
> 移动端路径：`business-platform-admin-app/src/api/`  
> **规则：路径、HTTP 方法、请求体字段与 PC 端保持一致。**

## 认证模块

| admin-web | 移动端 | 方法 | 备注 |
|-----------|--------|------|------|
| `api/login.ts` → `login` | `api/auth.ts` → `login` | POST `/auth/login` | `isEncrypt: true` |
| `logout` | `logout` | POST `/auth/logout` | |
| `getCodeImg` | `getCodeImg` | GET `/auth/code` | `isToken: false` |
| `getTenantList` | `getTenantList` | GET `/auth/tenant/list` | |
| `register` | `register` | POST `/auth/register` | `isEncrypt: true` |
| `getInfo` | `getUserInfo` | GET `/system/user/getInfo` | |

## 用户模块（已对齐）

| admin-web | 移动端 | 方法 |
|-----------|--------|------|
| `system/user/index.ts` → `listUser` | `system/user.ts` → `listUser` | GET `/system/user/list` |
| `getUser` | `getUser` | GET `/system/user/:id` |
| `addUser` | `addUser` | POST `/system/user` |
| `updateUser` | `updateUser` | PUT `/system/user` |
| `delUser` | `delUser` | DELETE `/system/user/:id` |
| `resetUserPwd` | `resetUserPwd` | PUT `/system/user/resetPwd`（加密） |
| `changeUserStatus` | `changeUserStatus` | PUT `/system/user/changeStatus` |
| `getUserProfile` | `getUserProfile` | GET `/system/user/profile` |
| `updateUserProfile` | `updateUserProfile` | PUT `/system/user/profile` |
| `updateUserPwd` | `updateUserPwd` | PUT `/system/user/profile/updatePwd`（加密） |
| `uploadAvatar` | `uploadAvatar` | POST `/system/user/profile/avatar` |

## 角色模块（已对齐）

| admin-web | 移动端 | 方法 |
|-----------|--------|------|
| `system/role/index.ts` → `listRole` | `system/role.ts` → `listRole` | GET `/system/role/list` |
| `getRole` | `getRole` | GET `/system/role/:id` |
| `addRole` | `addRole` | POST `/system/role` |
| `updateRole` | `updateRole` | PUT `/system/role` |
| `delRole` | `delRole` | DELETE `/system/role/:id` |
| `changeRoleStatus` | `changeRoleStatus` | PUT `/system/role/changeStatus` |

## 部门模块（已对齐）

| admin-web | 移动端 | 方法 |
|-----------|--------|------|
| `system/dept/index.ts` → `listDept` | `system/dept.ts` → `listDept` | GET `/system/dept/list` |
| `getDept` | `getDept` | GET `/system/dept/:id` |
| `listDeptExcludeChild` | `listDeptExcludeChild` | GET `/system/dept/list/exclude/:id` |
| `addDept` | `addDept` | POST `/system/dept` |
| `updateDept` | `updateDept` | PUT `/system/dept` |
| `delDept` | `delDept` | DELETE `/system/dept/:id` |

## 岗位模块（已对齐）

| admin-web | 移动端 | 方法 |
|-----------|--------|------|
| `system/post/index.ts` → `listPost` | `system/post.ts` → `listPost` | GET `/system/post/list` |
| `getPost` | `getPost` | GET `/system/post/:id` |
| `addPost` | `addPost` | POST `/system/post` |
| `updatePost` | `updatePost` | PUT `/system/post` |
| `delPost` | `delPost` | DELETE `/system/post/:id` |

## SCM — 组织档案（org / wms）

| admin-web | 移动端 | 方法 |
|-----------|--------|------|
| `api/scm/wms.ts` → `pageOrg` | `api/scm/org.ts` → `pageOrg` | GET `/api/scm/wms/orgs` |
| `getOrg` | `getOrg` | GET `/api/scm/wms/orgs/:id` |
| `addOrg` | `addOrg` | POST `/api/scm/wms/orgs` |
| `updateOrg` | `updateOrg` | PUT `/api/scm/wms/orgs/:id` |
| `delOrg` | `delOrg` | DELETE `/api/scm/wms/orgs/:id` |
| `listOrgOptions` | `listOrgOptions` | GET `/api/scm/wms/orgs/options` |

权限：`scm:wms:org:list` / `add` / `edit` / `remove`。

## PDA 现场通道（B8+ · 无 admin 菜单）

> 业务 PRD：待 B8 重建（见 [domains/README.md](../../../business-platform/docs/domains/README.md)）· 移动壳：[PDA_WORKBENCH_PRD.md](./prd/PDA_WORKBENCH_PRD.md)  
> **禁止**使用 `/api/scm/...` 或 admin `Vo`；写操作 Body 须含 `idempotencyKey`。

| 能力 | 移动端（规划） | 方法 | 后端模块 |
|------|----------------|------|----------|
| 收货确认 | `api/pda/inbound.ts` → `confirmReceive` | POST `/api/pda/wms/inbound/confirm-receive` | `whl-scm-wms-pda-adapter` |
| 出库复核 | `api/pda/outbound.ts` → `confirmShip` | POST `/api/pda/wms/outbound/confirm-ship` | 同上 |
| 盘点提交 | `api/pda/stocktake.ts` → `submitStocktake` | POST `/api/pda/wms/stocktake/submit` | 同上 |

| 约定 | 说明 |
|------|------|
| 出参类型 | `Pda{Noun}Vo`（非 `InboundVo` 等 admin 类型） |
| 写操作 | Request 含 `idempotencyKey` |
| 页面分包 | `pages-sub/pda/`（不进 `ADMIN_PAGE_MAP`） |
| 权限前缀 | 建议 `pda:wms:*`（定稿后与菜单 SQL 一致） |

*B8 前接口与 `src/api/pda/` 未实现；落地后补全本表「移动端」列。*

## 菜单 / 字典

| admin-web | 移动端 | 方法 |
|-----------|--------|------|
| `system/menu/getRouters` | `system/menu.ts` → `getRouters` | GET `/system/menu/getRouters` |
| `system/dict/data/type/{type}` | `system/dict.ts` → `getDicts` | GET `/system/dict/data/type/{dictType}` |

## 页面路由映射

后台菜单 `component` → 移动端路径（`src/utils/menu.ts` → `ADMIN_PAGE_MAP`）：

| component | 移动端页面 |
|-----------|------------|
| `system/user/index` | `/pages-sub/admin/user/index` |
| `system/role/index` | `/pages-sub/admin/role/index` |
| `scm/wms/org/index` | `/pages-sub/scm/org/index` |
| … | 见 `ADMIN_PAGE_MAP` |

**SCM 子页（无 component 映射）**：

| 页面 | 路径 |
|------|------|
| 组织详情 | `/pages-sub/scm/org/detail?id={id}` |
| 组织表单 | `/pages-sub/scm/org/form?id={id}` |
| PDA 工作台 | `/pages-sub/pda/index`（B8+，无后台菜单） |

## 环境变量对照

| admin-web | 移动端 |
|-----------|--------|
| `VITE_APP_CLIENT_ID` | `VITE_CLIENT_ID` |
| `VITE_APP_ENCRYPT` | `VITE_APP_ENCRYPT` |
| `VITE_APP_RSA_PUBLIC_KEY` | `VITE_APP_RSA_PUBLIC_KEY` |
| `VITE_APP_BASE_API`（如 `/dev-api`） | H5：`VITE_APP_PROXY_PREFIX` + 代理 |

---

*新增模块时：先补本表，再写 `src/api` 与页面。*
