# Business Platform

供应链业务平台，包含后端服务、PC 管理后台、移动端应用。

## 项目结构

```text
business-platform/
├── server/       # 后端服务，基于 RuoYi-Vue-Plus
├── admin-web/    # PC 管理后台，Vue3 + TypeScript + Element Plus
├── admin-app/    # 移动端，uni-app + Vue3 + TypeScript + Wot UI
├── .ai/          # AI Native 开发治理入口
├── docs/         # 项目文档
└── AGENTS.md     # AI / Agent 总入口
```

## AI Native 治理

本仓库新增 `.ai/` 作为统一 AI 开发入口，用于沉淀：

- Skills
- Workflows
- Specs
- Knowledge
- Templates
- Checklists
- Architecture Decisions

历史 Cursor / Trae 配置仍保留在各子项目中，`.ai/` 先做统一索引和治理，不破坏现有开发流程。

## 开发前必读

- `AGENTS.md`
- `AI_GUIDE.md`
- `.ai/README.md`
- `.ai/workflows/README.md`
- `.ai/specs/README.md`
