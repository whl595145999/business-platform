# 请求库说明

## 业务开发（供应链平台）

**统一使用 `src/http/http.ts`**，与 PC 端 `business-platform-admin-web` 的 `utils/request.ts` 行为对齐：

- 自动附加 `clientid`、`Authorization`
- 支持 `isToken: false`、`isEncrypt: true`
- 自动解密响应、解析 `data` 或 `rows`/`total`

详见 `docs/开发规范.md`、`docs/admin-web接口对照.md`。

## 脚手架遗留（勿用于业务 API）

| 文件 | 说明 |
|------|------|
| `alova.ts` | unibest 示例，见 `src/api/foo-alova.ts` |
| `vue-query.ts` | 自动生成接口示例，见 `src/service/` |
| `foo.ts` | 演示接口 |

新增业务接口请写在 `src/api/`，并参照 admin-web 同名模块。
