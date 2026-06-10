# Module Development Guide

新增供应链模块必须遵循本指南。

## 1. 先写模块规格

使用：

- `.ai/templates/module/module-spec.md`

输出模块的目标、状态机、字段、接口、权限、风险。

## 2. 选择领域知识

例如采购订单：

- `.ai/knowledge/purchase/order-lifecycle.md`
- `.ai/knowledge/inventory/stock-ledger.md`

## 3. 生成前确认

必须确认：

- 表名
- 实体名
- 菜单名
- 权限前缀
- API 路径
- 字典项
- 是否影响库存
- 是否涉及金额
- 是否主子表

## 4. 开发顺序

```text
DB → Backend → API Types → Admin Web → Admin App → Review → Docs
```

## 5. 提交前检查

执行：

- `.ai/checklists/review-checklist.md`

任何涉及库存、金额、审批、租户隔离的模块必须重点 Review。
