# AGENTS.md

本仓库采用 **AI Native SCM Platform** 的协作方式。AI、开发者、Cursor、Trae、Claude Code、ChatGPT 都必须优先读取本文件，再进入具体子项目。

## 项目定位

`business-platform` 是一个供应链业务平台，采用前后端分离结构：

- `server/`：RuoYi-Vue-Plus / Spring Boot 后端
- `admin-web/`：Vue3 + TypeScript + Element Plus 管理后台
- `admin-app/`：uni-app + Vue3 + TypeScript + Wot UI 移动端

## AI 工作原则

1. **先理解业务，再生成代码。** 涉及商品、采购、库存、仓储、供应商、客户、销售、财务等模块时，先读取 `.ai/knowledge/`。
2. **先设计，再开发。** 新模块必须先走 `.ai/workflows/create-module.md`。
3. **先复用，再新增。** 优先复用现有 RuoYi-Vue-Plus、admin-web、admin-app 的结构与封装。
4. **兼容优先。** 不直接删除 `server/.cursor`、`admin-web/.cursor`、`admin-app/.cursor`、`.trae` 等历史配置。
5. **文档即代码。** 每次新增业务模块，应同步补充知识库、接口说明、权限说明和变更记录。
6. **最小破坏。** 目录治理先采用桥接方式，不做大规模迁移。

## 入口顺序

AI 或开发者处理需求时，建议按顺序读取：

1. `AGENTS.md`
2. `.ai/README.md`
3. `.ai/workflows/README.md`
4. `.ai/specs/README.md`
5. `.ai/skills/README.md`
6. 对应领域的 `.ai/knowledge/<domain>/README.md`
7. 对应子项目的 `AGENTS.md` 或 `.cursor/skills/README.md`

## 子项目兼容入口

- 后端 Skill：`server/.cursor/skills/`
- 管理端 Skill：`admin-web/.cursor/skills/`
- 移动端 Skill：`admin-app/.cursor/skills/`
- Trae 规则：`admin-app/.trae/rules/`

根目录 `.ai/` 是统一治理入口，历史目录继续作为平台专属能力保留。
