# PRD 文档规范（移动仓）

> **全仓 SSOT**：[PRD规范.md](../../business-platform/docs/PRD规范.md)

## 本仓 PRD 目录

仅存放 **移动管理端 / PDA 独有**需求；供应链业务字段与状态机以 [README.md](../../business-platform/docs/domains/README.md) 为准。

```text
docs/prd/
├── README.md
├── HOME_WORKBENCH_PRD.md    # TabBar 管理工作台（首页）
└── PDA_WORKBENCH_PRD.md     # 现场作业壳（B8+）
```

## 快速区分

| 文档 | 系统 | 代码路径 |
| --- | --- | --- |
| [HOME_WORKBENCH_PRD.md](./prd/HOME_WORKBENCH_PRD.md) | 移动管理端 | `pages/index/` |
| [PDA_WORKBENCH_PRD.md](./prd/PDA_WORKBENCH_PRD.md) | PDA 现场 | `pages-sub/pda/` |
| [PRODUCT.md](../../business-platform/docs/domains/wms/prd/B1-org/PRODUCT.md)（主仓） | 全栈业务 | `pages-sub/scm/org/` + 后端 |
| [TECH.md](../../business-platform/docs/domains/wms/design/B1-org/TECH.md) | 后端契约 | 同上 |

## 跳转桩

- [HOME_WORKBENCH_PRD.md](../HOME_WORKBENCH_PRD.md) → [prd/HOME_WORKBENCH_PRD.md](./prd/HOME_WORKBENCH_PRD.md)
