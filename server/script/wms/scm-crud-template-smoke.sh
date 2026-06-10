#!/usr/bin/env bash
# scm_crud Velocity 模板与文档静态自检（无需启动服务）
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
GEN="$ROOT/whl-platform/whl-platform-generator/src/main/resources/vm/scm"
FAIL=0

check() {
  if [[ ! -e "$1" ]]; then
    echo "FAIL missing: $1"
    FAIL=1
  fi
}

echo "==> 1. scm 模板文件"
for f in \
  ScmPageResult.java.vm PageQuery.java.vm GetQuery.java.vm ListOptionsQuery.java.vm \
  CreateCommand.java.vm UpdateCommand.java.vm DeleteCommand.java.vm Dto.java.vm Facade.java.vm \
  Entity.java.vm Mapper.java.vm AppService.java.vm ScmController.java.vm SaveRequest.java.vm Vo.java.vm \
  scm_menu.sql.vm api.ts.vm types.snippet.vm api-merge.snippet.vm ErrorCode.snippet.vm index.vue.vm \
  common/java-doc-footer.vm common/ts-file-header.vm common/vue-file-header.vm common/java-lombok-data.vm; do
  check "$GEN/$f"
done
[[ $FAIL -eq 0 ]] && echo "    OK $(find "$GEN" -type f | wc -l | tr -d ' ') files"

echo ""
echo "==> 2. demo SQL / 文档"
check "$ROOT/script/sql/wms/wms_org_ddl.sql"
check "$ROOT/script/sql/wms/_gen/wms_org_dict.sql"
check "$ROOT/docs/GENERATOR_SCM.md"
[[ $FAIL -eq 0 ]] && echo "    OK"

echo ""
echo "==> 3. api-merge 不含重复 import"
if grep -E "^import .*from '@/api/scm/types'" "$GEN/api-merge.snippet.vm"; then
  echo "FAIL api-merge.snippet.vm 仍含 types import（应合并至 wms.ts 顶部）"
  FAIL=1
else
  echo "    OK"
fi

echo ""
echo "==> 4. 辅助脚本语法"
bash -n "$ROOT/script/wms/scm-crud-gen-verify.sh"
bash -n "$0"
echo "    OK"

echo ""
echo "==> 5. Query/Command/Dto 使用 @Data 类（非 record）"
if grep -E '^public record ' "$GEN"/GetQuery.java.vm "$GEN"/Dto.java.vm "$GEN"/CreateCommand.java.vm 2>/dev/null; then
  echo "FAIL 仍存在 public record 模板"
  FAIL=1
else
  echo "    OK"
fi

echo ""
echo "==> 6. POJO 仅 @Data + @NoArgsConstructor"
if grep -E '@AllArgsConstructor' "$GEN"/GetQuery.java.vm "$GEN"/Dto.java.vm 2>/dev/null; then
  echo "FAIL 仍含 @AllArgsConstructor（应使用 setter 组装）"
  FAIL=1
else
  echo "    OK"
fi

echo ""
echo "==> 7. whl-scm 无 public record（契约统一 POJO）"
if grep -RE '^[[:space:]]*public record ' "$ROOT/whl-scm" --include='*.java' 2>/dev/null; then
  echo "FAIL whl-scm 仍存在 public record"
  FAIL=1
else
  echo "    OK"
fi

echo ""
echo "==> 8. ScmController 返回 Vo 而非 Dto"
if grep -E 'R<.*Dto>|TableDataInfo<.*Dto>' "$GEN/ScmController.java.vm" 2>/dev/null; then
  echo "FAIL ScmController.java.vm 仍直接返回 Dto"
  FAIL=1
elif ! grep -q 'TableDataInfo<${ClassName}Vo>' "$GEN/ScmController.java.vm"; then
  echo "FAIL ScmController.java.vm 未使用 Vo 作为 HTTP 出参"
  FAIL=1
else
  echo "    OK"
fi

if [[ $FAIL -ne 0 ]]; then
  exit 1
fi
echo ""
echo "静态自检通过。下一步: mvn compile + Admin 预览 /tool/gen/preview"
