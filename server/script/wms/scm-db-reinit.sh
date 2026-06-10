#!/usr/bin/env bash
# SCM 库表重建：先删后建 B1 wms_org + B2 prd_*
#
# 用法:
#   ./script/wms/scm-db-reinit.sh
#   MYSQL_CHARSET=utf8mb4 MYSQL_USER=root MYSQL_PWD=root ./script/wms/scm-db-reinit.sh
#   ./script/wms/scm-db-reinit.sh --with-menu    # 导入组织菜单
#   ./script/wms/scm-db-reinit.sh --with-seed    # 导入组织 seed
#   ./script/wms/scm-db-reinit.sh --with-menu --with-seed

set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
cd "$ROOT"

WITH_MENU=0
WITH_SEED=0
for arg in "$@"; do
  case "$arg" in
    --with-menu) WITH_MENU=1 ;;
    --with-seed) WITH_SEED=1 ;;
    -h|--help)
      sed -n '2,12p' "$0"
      exit 0
      ;;
    *)
      echo "未知参数: $arg（支持 --with-menu --with-seed）" >&2
      exit 1
      ;;
  esac
done

MYSQL_HOST="${MYSQL_HOST:-localhost}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PWD="${MYSQL_PWD:-root}"
MYSQL_DB="${MYSQL_DB:-business_platform}"
MYSQL_CHARSET="${MYSQL_CHARSET:-utf8mb4}"

MYSQL=(mysql
  --default-character-set="${MYSQL_CHARSET}"
  -h"${MYSQL_HOST}"
  -P"${MYSQL_PORT}"
  -u"${MYSQL_USER}"
  -p"${MYSQL_PWD}"
  "${MYSQL_DB}"
)

run_sql() {
  local file="$1"
  echo ">> ${file}"
  "${MYSQL[@]}" < "${file}"
}

SQL_WMS="${ROOT}/script/sql/wms"
SQL_PRODUCT="${ROOT}/script/sql/product"

echo "== SCM 库表重建（B1 wms_org + B2 prd_*）=="
echo "   db=${MYSQL_DB} charset=${MYSQL_CHARSET}"

run_sql "${ROOT}/script/sql/scm_drop_all.sql"
run_sql "${SQL_WMS}/wms_org_ddl.sql"
run_sql "${SQL_WMS}/_gen/wms_org_dict.sql"

run_sql "${SQL_PRODUCT}/prd_category_ddl.sql"
run_sql "${SQL_PRODUCT}/prd_brand_ddl.sql"
run_sql "${SQL_PRODUCT}/prd_sku_ddl.sql"
run_sql "${SQL_PRODUCT}/prd_sku_barcode_ddl.sql"
run_sql "${SQL_PRODUCT}/_gen/prd_unit_code_dict.sql"
run_sql "${SQL_PRODUCT}/_gen/prd_tax_rate_dict.sql"

if [[ "${WITH_MENU}" -eq 1 ]]; then
  run_sql "${SQL_WMS}/_gen/wms_org_menu.sql"
  run_sql "${SQL_PRODUCT}/_gen/prd_product_menu.sql"
fi

if [[ "${WITH_SEED}" -eq 1 ]]; then
  run_sql "${SQL_WMS}/_gen/wms_org_seed.sql"
fi

echo "== 完成 =="
echo "验证: mysql --default-character-set=${MYSQL_CHARSET} -u${MYSQL_USER} -p ${MYSQL_DB} -e \"SHOW TABLES LIKE 'wms_org'; SHOW TABLES LIKE 'prd_%';\""
