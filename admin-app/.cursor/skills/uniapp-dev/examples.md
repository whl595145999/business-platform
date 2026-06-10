# uniapp-dev — 示例

> 技术栈：**@wot-ui/ui 2.0.8**（v2 表单 API）。CRUD 表单标杆：`pages-sub/scm/org/form.vue`。

## 例 1 — 移动首页改版（管理端）

```text
@uniapp-dev 移动工作台首页改版，从 Step 1
```

| Step | 要点 |
| --- | --- |
| 1 | 入口：Tab `pages/index`；只读聚合，非 CRUD |
| 3 | 通道：**无 scm 分页**；调统计/菜单接口 |
| 5 | `AppPage` + `MenuGrid`（**wot-ui 2.0.8** `wd-grid`）；[UI组件规范.md](../../../docs/UI组件规范.md) |
| 6 | 兼容 H5 + mp-weixin |
| 8 | `@code-review-scm` §E |

**不用** `generate-crud-module`。

---

## 例 2 — SCM 复杂页（非标准 CRUD）

```text
@uniapp-dev 入库单移动审批页（只读+确认），从 Step 1
```

| Step | 要点 |
| --- | --- |
| 3 | 通道：**scm** · 对齐 PC `api/scm/wms.ts` |
| 5 | 手写 `pages-sub/scm/inbound/approve.vue`；只读用 `wd-cell-group` |
| 禁止 | scaffold:crud、PDA API |

若仅为 **列表+表单 CRUD** → 转 `@generate-crud-module`。

---

## 例 3 — PDA 收货（B8+）

```text
@uniapp-dev PDA 入库收货确认页，从 Step 1
```

**前置**：`java-backend-dev` 已落地 `POST /api/pda/wms/inbound/confirm-receive`。

| Step | 要点 |
| --- | --- |
| 1 | `pages-sub/pda/inbound/receive.vue` |
| 2 | 大按钮、扫码；线框 `@senior-ui-ux-designer` |
| 3 | 通道 **pda** · `idempotencyKey` |
| 禁止 | `/api/scm/...`、z-paging 标准列表 |

---

## 例 4 — 修复小程序表单无标签（wot-ui v2）

```text
@uniapp-dev admin/post/form.vue 微信小程序 wd-input 无字段名，按 @wot-ui/ui 2.0.8 对齐 org/form 标杆
```

| Step | 要点 |
| --- | --- |
| 2 | 布局改为 `bp-crud-page` + `bp-crud-footer` |
| 5 | `wd-form` + `wd-form-item`；选择器拆 `wd-picker` + `v-model:visible` |
| 6 | **mp-weixin** 截图对比：左侧 title、右侧输入/选中值 |
| 禁止 | 保留 `label="..."` 在 `wd-input`/`wd-picker` |

---

## 反例

| 用户说 | 错误 | 正确 |
| --- | --- | --- |
| 移动组织 CRUD | uniapp-dev 从零写 | `@generate-crud-module` §2 org |
| 表单用 `wd-input label` | v1 API，MP 无标题 | `wd-form-item` + `compact` |
| wot-ui 写 `latest` | 版本漂移 | `package.json` 锁定 **2.0.8** |
| PDA 走 admin API | `pageInbound` + `/api/scm` | `/api/pda/wms` + `Pda*Vo` |
