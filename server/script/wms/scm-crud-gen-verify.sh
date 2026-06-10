#!/usr/bin/env bash
# scm_crud 生成器验收辅助脚本（DDL + 检查清单）
# 用法：./script/wms/scm-crud-gen-verify.sh [mysql_host_opts...]
# 示例：./script/wms/scm-crud-gen-verify.sh -h127.0.0.1 -uroot -p

set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
DDL="$ROOT/script/sql/wms/wms_org_ddl.sql"
DICT="$ROOT/script/sql/wms/_gen/wms_org_dict.sql"

echo "==> 1. 执行 wms_org DDL + 字典"
if command -v mysql >/dev/null 2>&1; then
  mysql --default-character-set=utf8mb4 "$@" < "$DDL"
  mysql --default-character-set=utf8mb4 "$@" < "$DICT"
  echo "    DDL + 字典已导入"
else
  echo "    跳过：未找到 mysql CLI，请手动执行 $DDL 与 $DICT"
fi

echo ""
echo "==> 2. 代码生成 UI 操作清单"
cat <<'EOF'
  [ ] Admin → 系统工具 → 代码生成 → 导入 wms_org
  [ ] 编辑 → 模板 = SCM 单表（scm_crud）
  [ ] 确认：scmDomain=wms, scmResource=orgs, scmMenuId=1708
        scmMenuComponent=scm/wms/org/index, scmPermPrefix=scm:wms:org
  [ ] org_type 字典=wms_org_type，status 字典=wms_enable_status
  [ ] 主键 id：自增=1，插入=0
  [ ] 预览 GET /tool/gen/preview/{tableId} — 应含 vm/scm/*.vm 产出
  [ ] 下载 zip GET /tool/gen/batchGenCode
EOF

echo ""
echo "==> 3. zip 内应包含（businessName=org）"
cat <<'EOF'
  scm-gen/wms/org/whl-scm/wms/whl-scm-wms-api/.../OrgFacade.java
  scm-gen/wms/org/whl-scm/wms/whl-scm-wms-biz/.../OrgAppService.java
  scm-gen/wms/org/admin-web/src/views/scm/wms/org/index.vue
  scm-gen/wms/org/MERGE/*
  scm-gen/wms/org/script/sql/wms/_gen/wms_org_menu.sql
EOF

echo ""
echo "==> 4. 合并后冒烟 API"
cat <<'EOF'
  GET  /api/scm/wms/orgs?pageNum=1&pageSize=10
  GET  /api/scm/wms/orgs/{orgId}
  POST /api/scm/wms/orgs
  PUT  /api/scm/wms/orgs/{orgId}
  DELETE /api/scm/wms/orgs/{orgId}
  GET  /api/scm/wms/orgs/options
EOF

echo ""
echo "==> 5. 编译（在项目根目录）"
echo "    mvn -pl whl-scm/wms/whl-scm-wms-api,whl-scm/wms/whl-scm-wms-biz,whl-scm/wms/whl-scm-wms-admin-adapter,whl-platform/whl-platform-generator -am compile"
echo ""
echo "文档：docs/GENERATOR_SCM.md"
echo "Skill：.cursor/skills/scm-crud-generate/SKILL.md"
