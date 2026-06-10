#!/usr/bin/env bash
#
# OpenAPI 调用 Demo — 登录 + 规范样例 + 订单全链路
#
# 用法:
#   chmod +x script/demo/openapi-call-demo.sh
#   ./script/demo/openapi-call-demo.sh
#
# 环境变量（可选）:
#   BASE_URL   默认 http://localhost:8081/openapi
#   USERNAME   默认 admin
#   PASSWORD   默认 admin123
#   TENANT_ID  默认 000000

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8081/openapi}"
USERNAME="${USERNAME:-admin}"
PASSWORD="${PASSWORD:-admin123}"
TENANT_ID="${TENANT_ID:-000000}"
CLIENT_ID="a8f3c2e1b9d0476581920abcdef12345"
IDEMPOTENCY_KEY="openapi-demo-$(date +%s)"

json_get() {
  python3 - "$1" "$2" <<'PY'
import json, sys
doc = json.load(sys.stdin)
path = sys.argv[1].split(".")
cur = doc
for p in path:
    cur = cur[p]
print(cur)
PY
}

section() {
  echo
  echo "========== $1 =========="
}

section "1. 登录 POST /openapi/auth/login"
LOGIN_JSON=$(cat <<EOF
{
  "clientId": "${CLIENT_ID}",
  "grantType": "password",
  "tenantId": "${TENANT_ID}",
  "username": "${USERNAME}",
  "password": "${PASSWORD}"
}
EOF
)
LOGIN_RESP=$(curl -sS -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d "${LOGIN_JSON}")
echo "${LOGIN_RESP}" | python3 -m json.tool

TOKEN=$(echo "${LOGIN_RESP}" | json_get "data.access_token")
CLIENT=$(echo "${LOGIN_RESP}" | json_get "data.client_id")
AUTH_HEADER="Authorization: Bearer ${TOKEN}"

section "2. 规范样例 GET /openapi/scm/demo/health"
curl -sS "${BASE_URL}/scm/demo/health" \
  -H "${AUTH_HEADER}" \
  -H "clientid: ${CLIENT}" | python3 -m json.tool

section "3. 创建订单 POST /openapi/scm/demo/orders"
CREATE_JSON=$(cat <<EOF
{
  "idempotencyKey": "${IDEMPOTENCY_KEY}",
  "warehouseCode": "WH01",
  "lines": [{ "skuCode": "DEMO-SKU", "qty": 2 }]
}
EOF
)
CREATE_RESP=$(curl -sS -X POST "${BASE_URL}/scm/demo/orders" \
  -H "${AUTH_HEADER}" \
  -H "clientid: ${CLIENT}" \
  -H "Content-Type: application/json" \
  -d "${CREATE_JSON}")
echo "${CREATE_RESP}" | python3 -m json.tool
ORDER_NO=$(echo "${CREATE_RESP}" | json_get "data.orderNo")
echo "orderNo=${ORDER_NO}"

section "4. 确认订单 POST /openapi/scm/demo/orders/{orderNo}/confirm"
curl -sS -X POST "${BASE_URL}/scm/demo/orders/${ORDER_NO}/confirm" \
  -H "${AUTH_HEADER}" \
  -H "clientid: ${CLIENT}" | python3 -m json.tool

section "5. 快照 GET /openapi/scm/demo/snapshot（取出库 taskNo）"
SNAPSHOT_RESP=$(curl -sS "${BASE_URL}/scm/demo/snapshot" \
  -H "${AUTH_HEADER}" \
  -H "clientid: ${CLIENT}")
echo "${SNAPSHOT_RESP}" | python3 -m json.tool
TASK_NO=$(echo "${SNAPSHOT_RESP}" | python3 -c "import json,sys; d=json.load(sys.stdin); print(d['data']['tasks'][0]['taskNo'])")
echo "taskNo=${TASK_NO}"

section "6. 发货 POST /openapi/scm/demo/outbound-tasks/{taskNo}/ship"
curl -sS -X POST "${BASE_URL}/scm/demo/outbound-tasks/${TASK_NO}/ship" \
  -H "${AUTH_HEADER}" \
  -H "clientid: ${CLIENT}" | python3 -m json.tool

section "7. 最终快照"
curl -sS "${BASE_URL}/scm/demo/snapshot" \
  -H "${AUTH_HEADER}" \
  -H "clientid: ${CLIENT}" | python3 -m json.tool

echo
echo "Demo 完成。规范编写参考: docs/OPENAPI_CALL_DEMO.md"
