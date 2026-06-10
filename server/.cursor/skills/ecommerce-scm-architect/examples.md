# ecommerce-scm-architect — 示例

## 例 1 — 新采购域（从零）

```text
@ecommerce-scm-architect 采购域 Step 1–10
```

**Step 10 产出**：`docs/domains/oms/prd/B6-procurement/PRODUCT.md` + `docs/domains/oms/design/B6-procurement/TECH.md`

---

## 例 2 — 增量：已有 B1 组织 PRD，只补售后

```text
@ecommerce-scm-architect 已有 B1 组织 PRD，只补充退货换货 Step 4、5、9
```

**分流**：增量 → 先读 [domains/README.md](../../docs/domains/README.md) 与已落地 [B1-org/PRODUCT.md](../../docs/domains/wms/prd/B1-org/PRODUCT.md) → 从 Step 4 起

**约束**：

- RETURN 虚拟仓规则与当期 PRD 虚拟仓章一致（待 B3 定稿）

**Step 10**：输出对应波次 `PRODUCT.md` 增量补丁章节，而非整份重写

---

## 例 3 — 异常场景（Step 9）

```text
@ecommerce-scm-architect B5 入出库 Step 9 异常场景
```

**前置**：Agent 应先摘要 Step 4–5 相关现状（从当期波次 PRD），再写 Step 9

---

## 例 4 — PRD 定稿后进入开发

```text
@scm-platform-dev 按 B1 组织 PRODUCT + TECH 排开发顺序
```

**引用**：[PRODUCT.md](../../docs/domains/wms/prd/B1-org/PRODUCT.md) · [TECH.md](../../docs/domains/wms/design/B1-org/TECH.md)

---

## 例 5 — B2 货品 TECH（Step 6–7）

```text
@ecommerce-scm-architect B2 货品 Step 6 库表 + Step 7 ProductFacade API
```

**引用**：[B2-product/PRODUCT.md](../../docs/domains/product/prd/B2-product/PRODUCT.md) · DDL `script/sql/product/`

**产出**：`docs/domains/product/design/B2-product/TECH.md` · 实现 `whl-scm-product-*`

---

## 反例

| 用户说法 | 问题 | 正确做法 |
| --- | --- | --- |
| `@ecommerce-scm-architect 重写整份域总览` | 浪费、易漂移 | 增量 Step + 版本表 |
| 跳过产品章直接写 TECH | 业务缺口 | 先 `scm-product-manager` 写 `prd/PRODUCT.md` |
