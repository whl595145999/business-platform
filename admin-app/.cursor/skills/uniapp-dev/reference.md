# UniApp Reference

## 技术栈版本

**SSOT**：[开发规范.md](../../../docs/开发规范.md) §2（勿在 Skill 重复维护完整版本表）。  
升级 `@wot-ui/ui`：改 `package.json` + 回归 mp-weixin + 只更新 docs §2。

## @wot-ui/ui 2.0.8 — 表单速查

```vue
<!-- CRUD 表单标杆：pages-sub/scm/org/form.vue -->
<AppPage :safe-top="false">
  <view class="bp-crud-page">
    <wd-form :model="form" border title-width="200rpx">
      <wd-form-item title="名称">
        <wd-input v-model="form.name" compact clearable placeholder="请输入" />
      </wd-form-item>
      <wd-form-item
        title="类型"
        :value="typeLabel"
        placeholder="请选择"
        is-link
        clickable
        @click="pickerVisible = true"
      />
    </wd-form>
  </view>
  <view class="bp-crud-footer">
    <PermButton type="primary" block perm="..." @click="submit">保存</PermButton>
  </view>
  <wd-picker
    v-model="pickerValue"
    v-model:visible="pickerVisible"
    :columns="columns"
    @confirm="onConfirm"
  />
</AppPage>
```

| v1 误用（小程序无标题） | v2 正确 |
| --- | --- |
| `<wd-input label="名称" />` | `wd-form-item title` + `wd-input compact` |
| `<wd-picker label="类型" v-model="form.type" />` | `wd-form-item` 展示 + `wd-picker` 数组 `v-model` + `v-model:visible` |

## 文档（本仓库 docs/）

| 文档 | 用途 |
| --- | --- |
| 开发规范.md | 技术栈版本表、环境、MP 联调 |
| UI组件规范.md | Token、wot-ui §5、CRUD 布局 |
| CRUD模块规范.md | 模块分层 |
| admin-web接口对照.md | 路径对照 |
| PR检查清单.md | 提交前 |

## 命令

```bash
pnpm dev        # H5（localhost + /dev-api 代理）
pnpm dev:mp     # 微信小程序（需 .env.development.local 局域网 IP）
pnpm dev:app    # App
pnpm scaffold:crud -- --module X --domain system ...
```

## 列表页必备

```typescript
import { useListRefresh } from '@/hooks/useListRefresh'
const pagingRef = ref<{ reload?: (showLoading?: boolean) => void } | null>(null)
useListRefresh(pagingRef)
// onShow 须从 @dcloudio/uni-app 导入，勿从 vue
```

## ADMIN_PAGE_MAP

`src/utils/menu.ts` — 与 `sys_menu.component` 一致（PDA 页无 admin 菜单项）。

## API 三通道

| 通道 | 移动 api | 后端 |
| --- | --- | --- |
| system | `src/api/system/{module}/` | RuoYi |
| scm | `src/api/scm/*.ts` | `/api/scm/...` |
| pda | `src/api/pda/`（建议） | `/api/pda/wms/...` |

SCM 对齐 PC：`business-platform-admin-web/src/api/scm/wms.ts`。

## 分包

| 路径 | 波次 |
| --- | --- |
| `pages-sub/admin/` | 已有 |
| `pages-sub/scm/` | B1+ |
| `pages-sub/pda/` | B8+ |
