# ADR-0001: 引入 AI Native 治理目录

## Status

Accepted

## Context

项目中已有多套 AI / Agent 规则：

- `server/.cursor/skills`
- `admin-web/.cursor/skills`
- `admin-app/.cursor/skills`
- `admin-app/.trae/rules`
- 各子项目 `AGENTS.md`

这些规则有价值，但入口分散，后续维护成本较高。

## Decision

新增根目录 `.ai/` 作为统一 AI Native 治理入口，先采用桥接方式索引历史配置，不移动、不删除历史目录。

## Consequences

- 新人和 AI 有统一入口
- 历史 Cursor / Trae 使用方式不受影响
- 后续可以逐步迁移、合并、去重
