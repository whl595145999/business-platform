# PR 检查清单

提交前逐项确认（与 `docs/开发规范.md`、`docs/admin-web接口对照.md` 一致）。

## 接口

- [ ] API 路径、方法与 `business-platform-admin-web` 对应文件一致
- [ ] 使用 `src/http/http.ts`，未用 alova/vue-query 写业务接口
- [ ] 无需 Token 的接口已设 `isToken: false`
- [ ] 登录/注册/改密等已设 `isEncrypt: true`（与 admin-web 相同）
- [ ] 分页接口使用 `pageNum` / `pageSize`，类型为 `IPageResult<T>`

## 权限与路由

- [ ] 按钮/页面使用 `hasPermi('模块:资源:操作')`，与后台菜单 `perms` 一致
- [ ] 管理页在 `src/pages-sub/admin/`（或 `scm/`）分包
- [ ] 动态菜单入口已在 `ADMIN_PAGE_MAP` 配置 component 映射

## 页面与 UI

- [ ] 根节点使用 `AppPage`，未硬编码旧主色（如 `#018d71`）
- [ ] 业务壳用 `AppPage`/`PageHero`/`MenuGrid`/`PermButton`；列表/空状态用 wot-ui（见 UI组件规范）
- [ ] 表单/登录用 `wd-form` + `wd-form-item` + `wd-input compact`；**禁止** `wd-input`/`wd-picker` 的 `label`（标杆 `pages-sub/scm/org/form.vue`）
- [ ] 列表页参考 `pages-sub/admin/user/index.vue`（z-paging + 权限）
- [ ] 使用 `definePage()` 声明标题；登录页除外设 `excludeLoginPath`
- [ ] H5 与目标小程序/App 端自测通过

## 规范

- [ ] 类型：`backend.ts` 通用 + `api/types/{模块}/` 专属
- [ ] 未引入 `ruoyi-uniapp-plus` 或旧脚手架写法
- [ ] 文档/对照表已更新（若新增接口模块）
- [ ] 业务 PRD 更新 `business-platform/docs/domains/`；移动独有 PRD 更新 `docs/prd/`
- [ ] SCM 列表页：卡片点进 **详情**，行内 **详情 \| 编辑**（标杆 `scm/org/index`）
