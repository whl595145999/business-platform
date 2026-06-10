# UI 组件与设计规范

> 供应链业务平台移动端：**业务组件 + @wot-ui/ui 2.0.8 + design token**，避免重复封装已有 UI 库能力。  
> 官方文档：[wot-ui.cn](https://wot-ui.cn)（本仓库锁定 **v2.0.8**，升级前须全量回归小程序表单/选择器）。

## 1. 视觉原则（企业移动端）

| 原则 | 做法 |
|------|------|
| 留白优先 | 页面灰底 `#f7f8fa`，内容白卡片，模块间距 24～32rpx |
| 弱阴影 | 默认**边框分层**，不用大面积渐变和重阴影 |
| 图标统一 | 功能图标放在 `bp-icon-box` 淡色底衬里 |
| 字号三档 | 标题 34rpx / 正文 28rpx / 辅助 24rpx，禁止页面随意写 px |
| 顶栏 | 白底 + 顶部 6rpx 品牌色条，**不用全屏渐变** |
| **跨端布局** | `AppPage` 注入 CSS 变量；`utils/layout.ts` 统一计算；仅 `#ifdef MP-WEIXIN` 读取胶囊 |

主色：`#1677ff`（修改 `src/styles/tokens.scss` 即可全局生效）

### 跨端布局（App / H5 / 小程序）

| 变量 | 含义 |
|------|------|
| `--bp-custom-nav-padding-top` | custom 导航页内容区起始位置 |
| `--bp-content-max-width` | 标题区最大宽度（MP 避让胶囊） |
| `--bp-page-bottom` | TabBar 页底部留白 |

自定义导航页：`<AppPage :safe-top="false" tabbar layout-vars>` + `PageHero`。

## 2. 分工原则

| 层级 | 负责内容 | 示例 |
|------|----------|------|
| **业务组件** `src/components/` | 仅保留 wot-ui 无法表达的复合/业务逻辑 | `AppPage`、`PageHero`、`MenuGrid`、`PermButton` |
| **wot-ui** | 表单、列表、宫格、空状态、卡片 | `wd-cell`、`wd-grid`、`wd-empty`、`wd-card` |
| **UnoCSS + token** | 布局、Carbon 图标、品牌色 | `text-primary`、`i-carbon-*` |
| **design token** | 全局颜色/圆角 | `src/styles/tokens.scss` |

**不要**再封装 `wd-cell` / `wd-empty` 的薄包装组件。

## 2. 设计 Token

文件：`src/styles/tokens.scss`

| Token | 默认值 |
|-------|--------|
| `--bp-color-primary` | `#1677ff` |
| `--bp-bg-page` | `#f5f5f5` |
| `--bp-text-primary` | `#1f1f1f` |

wot-ui 主题：`src/styles/wot-theme.scss`（`--wot-color-theme` 与 token 同步）

## 3. 业务组件（仅 4 个）

自动导入，无需手动 import。

### AppPage

页面根容器 + 安全区 + 背景。

```vue
<AppPage :safe-top="false" :gray="true" />
```

### PageHero

首页/我的顶部渐变区。

```vue
<PageHero title="你好" subtitle="供应链业务平台" :avatar="url" />
```

### MenuGrid

动态菜单（对接 `GET /system/menu/getRouters`）。

```vue
<MenuGrid :groups="menuGroups" @select="navigateMenu" />
```

**菜单小图标（行业常见做法）**

| 层级 | 说明 |
|------|------|
| 后台 | `sys_menu.icon` 存 Element Plus 图标名（如 `user`、`tree-table`） |
| 移动端 | 映射为 UnoCSS Iconify：`i-carbon-*`（见 `src/constants/menu-icons.ts`） |
| 优先 | 按 `component` 固定图标（如 `system/menu/index` → `i-carbon-menu`） |
| 兜底 | 再按 `meta.icon` 查 `RUOYI_ICON_MAP`，默认 `i-carbon-application` |

新增 admin 页面时：在 `ADMIN_PAGE_MAP` 与 `COMPONENT_ICON_MAP` 各加一行；图标类名写入 `MENU_ICON_SAFELIST`（供 `uno.config.ts` 打包）。

### PermButton

权限按钮（内部 `wd-button` + `hasPermi`）。

```vue
<PermButton perm="system:user:add" type="primary">新增</PermButton>
```

## 4. CRUD 页面标准（用户 / 角色等）

| 页面 | 路由 | 布局 |
|------|------|------|
| 列表 | `index.vue` | 搜索 + **新增** + 卡片 + 底部 **详情 \| 编辑** |
| 详情 | `detail.vue` | `wd-cell-group` 只读 + 底部固定 **编辑 / 删除** |
| 表单 | `form.vue` | 分组标题 + `wd-form` / `wd-form-item` 表单 + 底部固定 **保存 / 删除** |

工具类：`bp-list-toolbar`、`bp-row-actions`、`bp-crud-page`、`bp-crud-footer`（见 `src/styles/utilities.scss`）。

- 点击卡片主体 → **详情**（非直接进编辑）
- 列表行右侧 → **详情**、**编辑**（按权限显示）
- 表单/详情底部按钮栏贴底，留安全区

## 5. wot-ui 2.0.8 常用对照

| 场景 | 组件 | 示例 |
|------|------|------|
| 列表菜单 | `wd-cell-group` + `wd-cell` | 我的页 |
| 指标卡片 | `wd-card` | 首页统计 |
| 功能宫格 | `wd-grid` / `wd-grid-item` | MenuGrid 内 |
| 空状态 | `wd-empty` | 无菜单、无权限 |
| **CRUD 表单** | `wd-form` + `wd-form-item` + `wd-input compact` | `pages-sub/scm/org/form.vue` |
| **选择器** | `wd-form-item`（`is-link`）+ `wd-picker`（`v-model:visible`） | 同上 |
| 登录简表 | `wd-form` + `wd-form-item` + `wd-input compact` | `pages/auth/login.vue` |
| 搜索 | `wd-search` | 用户列表 |
| 标签 | `wd-tag` | 用户状态 |

### CRUD 表单（v2 必遵，小程序尤其）

**禁止**在 `wd-input` / `wd-picker` 上使用 `label`（v2 已移除，小程序只显示值不显示标题）。

```vue
<wd-form :model="form" border title-width="200rpx">
  <wd-form-item title="组织名称">
    <wd-input v-model="form.orgName" compact clearable placeholder="请输入" />
  </wd-form-item>
  <wd-form-item
    title="组织类型"
    :value="typeLabel"
    placeholder="请选择"
    is-link
    clickable
    @click="pickerVisible = true"
  />
</wd-form>
<wd-picker v-model="pickerValue" v-model:visible="pickerVisible" :columns="columns" @confirm="onConfirm" />
```

布局配套：`bp-crud-page` + `bp-crud-footer`（见 §4）。

### wd-cell 带 Carbon 图标

```vue
<wd-cell title="编辑资料" is-link clickable @click="onEdit">
  <template #prefix>
    <view class="i-carbon-user-profile text-40rpx text-primary" />
  </template>
</wd-cell>
```

### wd-empty 带自定义图标

```vue
<wd-empty tip="暂无数据">
  <template #image>
    <view class="i-carbon-document-blank text-96rpx text-[var(--bp-text-disabled)]" />
  </template>
</wd-empty>
```

## 6. 页面模板

| 类型 | 结构 | 参考 |
|------|------|------|
| 首页 | AppPage → PageHero（gradient）→ 业务指标 → 最近使用 → MenuGrid / wd-empty | `pages/index/index.vue` · [prd/HOME_WORKBENCH_PRD.md](./prd/HOME_WORKBENCH_PRD.md) |
| PDA 现场 | AppPage → 大按钮扫码 → 2 列宫格 | `pages-sub/pda/index.vue` · [prd/PDA_WORKBENCH_PRD.md](./prd/PDA_WORKBENCH_PRD.md) |
| 我的 | AppPage → PageHero → wd-cell-group | `pages/me/me.vue` |
| 登录 | AppPage → wd-card 表单 | `pages/auth/login.vue` |
| 列表 | AppPage → wd-search → z-paging → wd-card | `pages-sub/admin/user/index.vue` |

## 7. 新增页面检查

- [ ] 根节点 `AppPage`
- [ ] 能用 wot-ui 的不用自写组件
- [ ] 仅业务复合场景新增 `src/components/`
- [ ] 颜色用 token，禁止硬编码旧色
- [ ] 权限按钮用 `PermButton`

---

*与 [开发规范.md](./开发规范.md) 配套。*
