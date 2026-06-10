# Java Backend Reference

## 文档

| 文档 | 用途 |
| --- | --- |
| ARCHITECTURE.md | 模块全景 |
| MODULE_RULES.md | 依赖红线 |
| API_CONTRACT.md | Facade、三通道 Vo |
| NAMING.md | 包名、类名 |
| CODE_CONVENTION.md | SMALLINT 码 |

## Facade 示例

```java
public interface OrgFacade {
    ScmPageResult<OrgDto> page(PageOrgQuery query);
    OrgDto get(GetOrgQuery query);
    OrgDto create(CreateOrgCommand command);
    OrgDto update(UpdateOrgCommand command);
    void delete(DeleteOrgCommand command);
}
```

## 模块路径

```text
whl-scm/wms/whl-scm-wms-api/src/main/java/com/whl/scm/wms/api/
whl-scm/wms/whl-scm-wms-biz/src/main/java/com/whl/scm/wms/biz/
whl-scm/wms/whl-scm-wms-admin-adapter/.../adapter/admin/controller/
```

## Review 检查项

- `@Transactional` 仅单域
- 查询带 tenant_id
- 幂等键重复处理
- Adapter 无 if/else 改库存
- `*-api` 无 Spring 依赖
