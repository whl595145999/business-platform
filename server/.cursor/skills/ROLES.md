# 四类角色心智模型（卖 · 履约 · 界面 · 技术）

> Skill 分流一句话；细节见 [SKILL_ROUTING.md](SKILL_ROUTING.md)。

```text
电商产品经理（senior-product-manager）
  → 负责「卖」：转化、体验、运营端、故事、埋点

供应链产品经理（scm-product-manager）
  → 负责「履约」：仓·单·库存·主数据·逆向

资深 UI/UX 设计师（senior-ui-ux-designer）
  → 负责「界面与交互」：信息架构、线框、组件、规范对齐、设计走查（Audit）、开发 Handoff
  → 三种模式：Audit（现有页点评）/ 增量（单页）/ 全量（双端·PDA·新模块）

解决方案架构师（ecommerce-scm-architect）
  → 负责「技术整体方案」：域·库表·API·三通道·部署（不写业务 PRD 替代 PM）
```

## 交界与 Handoff

| 交界 | 电商产品 | 供应链产品 | UI/UX | 架构师 |
| --- | --- | --- | --- | --- |
| 订单 | 下单体验、支付前 | 确认后占库存、出库、退 | — | OMS↔Inventory Facade |
| 商品 | 展示、类目、卖点 | 是否仓发、效期/配额 | 展示组件 | `prd_*` · [B2 PRODUCT.md](../../docs/domains/product/prd/B2-product/PRODUCT.md) |
| 组织/仓 | 后台好用（故事） | 字段、流程、权限业务 | 线框、查询区、双端 | `wms_*`、MERGE、生成器边界 |
| PDA 现场 | 不涉及 | 收货/发货/盘点流程 | 仅精简流程说明 | `pda-adapter`、`Pda*Vo` |

## 推荐顺序

```text
卖/体验需求     → senior-product-manager
履约/单据库存   → scm-product-manager
界面/交互定稿   → senior-ui-ux-designer（Audit / 增量 / 全量，见 SKILL.md）
技术落地方案   → ecommerce-scm-architect（常在 PM PRD 之后，Step 6–10）
编码           → scm-platform-dev
```

**禁止**：四个设计 Skill 各从 Step 1 跑满；Handoff 写 **Skill 全名 + 步骤标题**（避免都叫「Step 6」）。
