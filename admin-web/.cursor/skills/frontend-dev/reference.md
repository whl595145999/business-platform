# Frontend Reference

## SCM 页面

| 波次 | 路径 | 功能 |
| --- | --- | --- |
| B1 | `views/scm/wms/org` | 组织档案 |
| B2 | `views/scm/product/category` | 货品类目 |
| B2 | `views/scm/product/brand` | 品牌档案 |
| B2 | `views/scm/product/sku` | 货品（含条码子表） |
| B3 | `views/scm/wms/warehouse` | 仓库（待建） |
| B3 | `views/scm/wms/warehouse-mapping` | 虚拟仓映射（待建） |
| B5+ | `views/scm/wms/inbound` 等 | 单据（待建） |

业务波次：[industry-baseline.md](../../../../business-platform/.cursor/skills/scm-product-manager/industry-baseline.md) §3

PRD / 技术设计：[domains/README.md](../../../../business-platform/docs/domains/README.md)

## 常用代码（B1 组织示例）

```ts
import { pageOrg, ... } from '@/api/scm/wms';
import { OrgVO, OrgQuery, OrgForm } from '@/api/scm/types';
import { useScmTenant } from '@/composables/useScmTenant';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { wms_enable_status } = toRefs<any>(proxy?.useDict('wms_enable_status'));
```

## B2 货品 API

```ts
import { pageSku, listCategoryTree, listBrandOptions } from '@/api/scm/product';
```

## 生成器 MERGE 区域

- `src/api/scm/types.ts` — `#region scm-gen:{domain}-{business}`
- `src/api/scm/{domain}.ts` 或 `product.ts` — 同上

详见 [scm-crud-generate/workflow Step 4](../../../../business-platform/.cursor/skills/scm-crud-generate/workflow.md#step-4--落盘与-mergeagent)
