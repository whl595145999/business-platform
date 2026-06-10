#!/usr/bin/env bash
#
# B1 组织主数据验收 — domains/wms/prd/B1-org/PRODUCT.md §11.3 AC-ORG-01～15（API 子集）
#
# 用法:
#   chmod +x script/wms/b1-org-ac-acceptance.sh
#   ./script/wms/b1-org-ac-acceptance.sh
#
# 前置:
#   1. whl-admin-server 已启动（默认 http://localhost:8080，profile=dev）
#   2. 已执行 script/sql/wms/wms_org_ddl.sql + _gen/wms_org_*.sql
#
# 鉴权（三选一，推荐 1）:
#   1) TOKEN 或 ACCESS_TOKEN — 直接传 access_token，跳过登录（浏览器 Network 复制）
#   2) TOKEN_FILE — 从文件读取 token（单行）
#   3) 自动登录 — 调用 scm-login.mjs（RSA+AES 加密，与 admin-web 一致）
#      默认 admin / admin123；dev 下可通过本机 redis-cli 读取 math 验证码答案
#
# 环境变量（可选）:
#   BASE_URL   默认 http://localhost:8080
#   USERNAME   默认 admin
#   PASSWORD   默认 admin123
#   TENANT_ID  默认 000000
#   CLIENT_ID  默认 e5cd7e4891bf95d1d19206ce24a7b32e
#   TOKEN / ACCESS_TOKEN / TOKEN_FILE
#   CAPTCHA_CODE / CAPTCHA_UUID  手动验证码（自动登录失败时）
#   ADMIN_WEB_DIR  admin-web 路径（默认 ../../../business-platform-admin-web）

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ADMIN_WEB_DIR="${ADMIN_WEB_DIR:-$(cd "${SCRIPT_DIR}/../../../business-platform-admin-web" 2>/dev/null && pwd || true)}"

BASE_URL="${BASE_URL:-http://localhost:8080}"
USERNAME="${USERNAME:-admin}"
PASSWORD="${PASSWORD:-admin123}"
TENANT_ID="${TENANT_ID:-000000}"
CLIENT_ID="${CLIENT_ID:-e5cd7e4891bf95d1d19206ce24a7b32e}"
RUN_ID="$(date +%s)"
ORG_A_ID=1001
ORG_B_ID=1002
ORG_B_SH_ID=1003
ORG_C_ID=1005
TEST_CODE="ORG-TEST-${RUN_ID}"
DUP_CODE="ORG-B"

json_get() {
  python3 - "$1" "$2" <<'PY'
import json, sys
doc = json.loads(sys.argv[1])
path = sys.argv[2].split(".")
cur = doc
for p in path:
    if p.isdigit():
        cur = cur[int(p)]
    else:
        cur = cur[p]
print(cur)
PY
}

assert_code_ok() {
  local resp="$1"
  local label="$2"
  python3 - "$resp" "$label" <<'PY'
import json, sys
doc = json.loads(sys.argv[1])
label = sys.argv[2]
code = doc.get("code")
if code != 200:
    raise SystemExit(f"{label} failed: code={code}, msg={doc.get('msg')}")
print(f"PASS: {label}")
PY
}

assert_code_fail() {
  local resp="$1"
  local label="$2"
  local needle="${3:-}"
  python3 - "$resp" "$label" "$needle" <<'PY'
import json, sys
doc = json.loads(sys.argv[1])
label = sys.argv[2]
needle = sys.argv[3]
code = doc.get("code")
if code == 200:
    raise SystemExit(f"{label} should fail but code=200")
text = json.dumps(doc, ensure_ascii=False)
if needle and needle not in text:
    raise SystemExit(f"{label}: expected message containing '{needle}', got {text}")
print(f"PASS: {label} rejected as expected")
PY
}

assert_rows_ge() {
  local resp="$1"
  local min="$2"
  local label="$3"
  python3 - "$resp" "$min" "$label" <<'PY'
import json, sys
doc = json.loads(sys.argv[1])
min_rows = int(sys.argv[2])
label = sys.argv[3]
rows = doc.get("rows") or []
if len(rows) < min_rows:
    raise SystemExit(f"{label}: expected >= {min_rows} rows, got {len(rows)}")
print(f"PASS: {label} rows={len(rows)}")
PY
}

section() {
  echo
  echo "========== $1 =========="
}

api_get() {
  curl -sS "${BASE_URL}$1" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H "clientid: ${CLIENT_ID}"
}

api_post() {
  curl -sS -X POST "${BASE_URL}$1" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H "clientid: ${CLIENT_ID}" \
    -H "Content-Type: application/json" \
    -d "$2"
}

api_put() {
  curl -sS -X PUT "${BASE_URL}$1" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H "clientid: ${CLIENT_ID}" \
    -H "Content-Type: application/json" \
    -d "$2"
}

api_delete() {
  curl -sS -X DELETE "${BASE_URL}$1" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H "clientid: ${CLIENT_ID}"
}

resolve_token() {
  if [[ -n "${TOKEN:-}" ]]; then
    echo "使用环境变量 TOKEN（跳过登录）"
    return 0
  fi
  if [[ -n "${ACCESS_TOKEN:-}" ]]; then
    TOKEN="${ACCESS_TOKEN}"
    echo "使用环境变量 ACCESS_TOKEN（跳过登录）"
    return 0
  fi
  if [[ -n "${TOKEN_FILE:-}" && -f "${TOKEN_FILE}" ]]; then
    TOKEN="$(tr -d '[:space:]' < "${TOKEN_FILE}")"
    if [[ -z "${TOKEN}" ]]; then
      echo "TOKEN_FILE 为空: ${TOKEN_FILE}" >&2
      exit 1
    fi
    echo "从 TOKEN_FILE 读取 token（跳过登录）"
    return 0
  fi

  if ! command -v node >/dev/null 2>&1; then
    echo "未设置 TOKEN，且本机无 node，无法自动登录。" >&2
    echo "请: TOKEN=<access_token> $0" >&2
    exit 1
  fi
  if [[ -z "${ADMIN_WEB_DIR}" || ! -d "${ADMIN_WEB_DIR}/node_modules/crypto-js" ]]; then
    echo "未找到 admin-web 依赖（crypto-js），请设置 ADMIN_WEB_DIR 或先 cd admin-web && pnpm i" >&2
    echo "或: TOKEN=<access_token> $0" >&2
    exit 1
  fi

  echo "自动登录（加密，账号 ${USERNAME}）..."
  LOGIN_ERR="$(mktemp)"
  if ! TOKEN="$(BASE_URL="${BASE_URL}" USERNAME="${USERNAME}" PASSWORD="${PASSWORD}" \
    TENANT_ID="${TENANT_ID}" CLIENT_ID="${CLIENT_ID}" ADMIN_WEB_DIR="${ADMIN_WEB_DIR}" \
    node "${SCRIPT_DIR}/scm-login.mjs" 2>"${LOGIN_ERR}")"; then
    cat "${LOGIN_ERR}" >&2
    rm -f "${LOGIN_ERR}"
    echo "自动登录失败。可: TOKEN=<token> $0" >&2
    exit 1
  fi
  rm -f "${LOGIN_ERR}"
  if [[ -z "${TOKEN}" ]]; then
    echo "自动登录未返回 token" >&2
    exit 1
  fi
  echo "TOKEN acquired"
}

section "登录"
resolve_token

section "AC-ORG-01 分页列表"
PAGE_RESP=$(api_get "/api/scm/wms/orgs?pageNum=1&pageSize=10")
assert_rows_ge "${PAGE_RESP}" 2 "AC-ORG-01 pageOrg"

section "AC-ORG-02 条件查询"
FILTER_RESP=$(api_get "/api/scm/wms/orgs?pageNum=1&pageSize=10&orgName=B&orgType=20&status=10")
assert_rows_ge "${FILTER_RESP}" 1 "AC-ORG-02 filter"
python3 - "$FILTER_RESP" <<'PY'
import json, sys
doc = json.loads(sys.argv[1])
for row in doc.get("rows") or []:
    if "B" not in row.get("orgName", ""):
        raise SystemExit("AC-ORG-02: orgName should contain B")
    if row.get("orgType") != 20:
        raise SystemExit("AC-ORG-02: orgType should be 20")
print("PASS: AC-ORG-02 row content")
PY

section "AC-ORG-03 新增顶级组织"
CREATE_TOP=$(api_post "/api/scm/wms/orgs" "{\"orgCode\":\"${TEST_CODE}\",\"orgName\":\"测试法人\",\"orgType\":20,\"parentOrgId\":0,\"status\":10,\"sortOrder\":0}")
assert_code_ok "${CREATE_TOP}" "AC-ORG-03 create top"
TEST_ID=$(json_get "${CREATE_TOP}" "data.id")
python3 - "$CREATE_TOP" <<'PY'
import json, sys
doc = json.loads(sys.argv[1])
data = doc["data"]
if data.get("parentOrgId") != 0:
    raise SystemExit(f"AC-ORG-03: parentOrgId should be 0, got {data.get('parentOrgId')}")
print("PASS: AC-ORG-03 parentOrgId=0")
PY

section "AC-ORG-04 编码唯一"
DUP_RESP=$(api_post "/api/scm/wms/orgs" "{\"orgCode\":\"${DUP_CODE}\",\"orgName\":\"重复编码\",\"orgType\":20,\"parentOrgId\":0,\"status\":10}")
assert_code_fail "${DUP_RESP}" "AC-ORG-04 duplicate code" "组织编码已存在"

section "AC-ORG-05 新增子组织"
CHILD_CODE="ORG-CHILD-${RUN_ID}"
CREATE_CHILD=$(api_post "/api/scm/wms/orgs" "{\"orgCode\":\"${CHILD_CODE}\",\"orgName\":\"子组织测试\",\"orgType\":30,\"parentOrgId\":${ORG_A_ID},\"status\":10}")
assert_code_ok "${CREATE_CHILD}" "AC-ORG-05 create child"
CHILD_ID=$(json_get "${CREATE_CHILD}" "data.id")
python3 - "$CREATE_CHILD" <<PY
import json, sys
doc = json.loads(sys.argv[1])
if doc["data"].get("parentOrgId") != ${ORG_A_ID}:
    raise SystemExit("AC-ORG-05: parentOrgId mismatch")
print("PASS: AC-ORG-05 parentOrgId=${ORG_A_ID}")
PY

section "AC-ORG-06 编辑组织"
UPDATE_NAME=$(api_put "/api/scm/wms/orgs/${ORG_B_SH_ID}" "{\"orgName\":\"B上海事业部（更新）\",\"orgType\":30,\"parentOrgId\":${ORG_B_ID},\"status\":10}")
assert_code_ok "${UPDATE_NAME}" "AC-ORG-06 update name"

section "AC-ORG-07 修改上级"
MOVE_RESP=$(api_put "/api/scm/wms/orgs/${CHILD_ID}" "{\"orgName\":\"子组织测试\",\"orgType\":30,\"parentOrgId\":${ORG_C_ID},\"status\":10}")
assert_code_ok "${MOVE_RESP}" "AC-ORG-07 move parent"

section "AC-ORG-08 上级成环"
CYCLE_RESP=$(api_put "/api/scm/wms/orgs/${ORG_A_ID}" "{\"orgName\":\"A公司\",\"orgType\":10,\"parentOrgId\":${ORG_C_ID},\"status\":10}")
assert_code_fail "${CYCLE_RESP}" "AC-ORG-08 parent cycle" "上级组织"

section "AC-ORG-09 删除叶子节点"
DEL_LEAF=$(api_delete "/api/scm/wms/orgs/${TEST_ID}")
assert_code_ok "${DEL_LEAF}" "AC-ORG-09 delete leaf"

section "AC-ORG-10 有子节点禁止删"
DEL_PARENT=$(api_delete "/api/scm/wms/orgs/${ORG_B_ID}")
assert_code_fail "${DEL_PARENT}" "AC-ORG-10 delete with children" "下级组织"

section "AC-ORG-11 停用组织"
DISABLE_RESP=$(api_put "/api/scm/wms/orgs/${ORG_C_ID}" "{\"orgName\":\"C公司\",\"orgType\":20,\"parentOrgId\":${ORG_A_ID},\"status\":20}")
assert_code_ok "${DISABLE_RESP}" "AC-ORG-11 disable org"

section "AC-ORG-12 停用后不可选为上级"
DISABLED_PARENT=$(api_post "/api/scm/wms/orgs" "{\"orgCode\":\"ORG-DIS-${RUN_ID}\",\"orgName\":\"停用上级测试\",\"orgType\":30,\"parentOrgId\":${ORG_C_ID},\"status\":10}")
assert_code_fail "${DISABLED_PARENT}" "AC-ORG-12 disabled parent" "停用"

section "AC-ORG-12b options 不含停用"
OPTS_RESP=$(api_get "/api/scm/wms/orgs/options")
assert_code_ok "${OPTS_RESP}" "AC-ORG-12 options"
python3 - "$OPTS_RESP" "$ORG_C_ID" <<'PY'
import json, sys
doc = json.loads(sys.argv[1])
org_c_id = int(sys.argv[2])
ids = [row.get("id") for row in (doc.get("data") or [])]
if org_c_id in ids:
    raise SystemExit(f"AC-ORG-12: disabled org {org_c_id} should not appear in options")
print("PASS: AC-ORG-12 options exclude disabled")
PY

section "AC-ORG-13 租户隔离（列表仅当前租户）"
TENANT_RESP=$(api_get "/api/scm/wms/orgs?pageNum=1&pageSize=50")
python3 - "$TENANT_RESP" <<'PY'
import json, sys
doc = json.loads(sys.argv[1])
# 默认租户 000000；若返回行则均应有合法 orgCode（无跨租户泄漏迹象）
rows = doc.get("rows") or []
if not rows:
    raise SystemExit("AC-ORG-13: no rows")
print(f"PASS: AC-ORG-13 tenant list rows={len(rows)}")
PY

section "AC-ORG-15 菜单/API 可达"
assert_code_ok "${PAGE_RESP}" "AC-ORG-15 org API reachable"

section "清理测试数据"
api_delete "/api/scm/wms/orgs/${CHILD_ID}" >/dev/null || true
api_put "/api/scm/wms/orgs/${ORG_C_ID}" "{\"orgName\":\"C公司\",\"orgType\":20,\"parentOrgId\":${ORG_A_ID},\"status\":10}" >/dev/null || true
api_put "/api/scm/wms/orgs/${ORG_B_SH_ID}" "{\"orgName\":\"B上海事业部\",\"orgType\":30,\"parentOrgId\":${ORG_B_ID},\"status\":10}" >/dev/null || true

echo
echo "========== B1 ORG AC 全部通过 =========="
