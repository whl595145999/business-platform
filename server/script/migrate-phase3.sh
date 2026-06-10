#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

echo "== Phase 3: rename Maven modules =="
git mv ruoyi-common whl-framework
git mv ruoyi-modules whl-platform

for dir in whl-framework/ruoyi-common-*; do
  [ -d "$dir" ] || continue
  base="$(basename "$dir")"
  git mv "$dir" "whl-framework/${base/ruoyi-common-/whl-framework-}"
done

git mv whl-platform/ruoyi-demo whl-platform/whl-platform-demo
git mv whl-platform/ruoyi-generator whl-platform/whl-platform-generator
git mv whl-platform/ruoyi-job whl-platform/whl-platform-job
git mv whl-platform/ruoyi-system whl-platform/whl-platform-system
git mv whl-platform/ruoyi-workflow whl-platform/whl-platform-workflow

echo "== Phase 3: move Java package directories =="

move_common_module_packages() {
  local module_dir="$1"
  for java_root in "$module_dir"/src/main/java "$module_dir"/src/test/java; do
    [ -d "$java_root/org/dromara/common" ] || continue
    for pkg_dir in "$java_root"/org/dromara/common/*; do
      [ -d "$pkg_dir" ] || continue
      sub="$(basename "$pkg_dir")"
      mkdir -p "$java_root/com/whl/framework"
      git mv "$pkg_dir" "$java_root/com/whl/framework/$sub"
    done
    rmdir "$java_root/org/dromara/common" 2>/dev/null || true
    rmdir "$java_root/org/dromara" 2>/dev/null || true
    rmdir "$java_root/org" 2>/dev/null || true
  done
}

for module_dir in whl-framework/whl-framework-*; do
  [ -d "$module_dir" ] || continue
  move_common_module_packages "$module_dir"
done

move_platform_package() {
  local module_dir="$1"
  local pkg_name="$2"
  for java_root in "$module_dir"/src/main/java "$module_dir"/src/test/java; do
    [ -d "$java_root/org/dromara/$pkg_name" ] || continue
    mkdir -p "$java_root/com/whl/platform"
    git mv "$java_root/org/dromara/$pkg_name" "$java_root/com/whl/platform/$pkg_name"
    rmdir "$java_root/org/dromara" 2>/dev/null || true
    rmdir "$java_root/org" 2>/dev/null || true
  done
}

move_platform_package whl-platform/whl-platform-system system
move_platform_package whl-platform/whl-platform-demo demo
move_platform_package whl-platform/whl-platform-workflow workflow
move_platform_package whl-platform/whl-platform-generator generator
move_platform_package whl-platform/whl-platform-job job

for java_root in ruoyi-admin/src/main/java ruoyi-admin/src/test/java; do
  [ -d "$java_root/org/dromara/web" ] || continue
  mkdir -p "$java_root/com/whl/platform"
  git mv "$java_root/org/dromara/web" "$java_root/com/whl/platform/web"
done

echo "== Phase 3: bulk text replace =="

mapfile -t FILES < <(find . \( -path './target' -o -path '*/target' -o -path './.git' -o -name '.flattened-pom.xml' \) -prune -o \
  \( -name '*.java' -o -name '*.xml' -o -name '*.yml' -o -name '*.yaml' -o -name '*.properties' -o -name '*.vm' -o -name '*.imports' -o -name '*.md' -o -name 'pom.xml' \) -print)

for file in "${FILES[@]}"; do
  [ -f "$file" ] || continue
  perl -pi -e '
    s/ruoyi-common-bom/whl-framework-bom/g;
    s/ruoyi-common-social/whl-framework-social/g;
    s/ruoyi-common-core/whl-framework-core/g;
    s/ruoyi-common-doc/whl-framework-doc/g;
    s/ruoyi-common-excel/whl-framework-excel/g;
    s/ruoyi-common-idempotent/whl-framework-idempotent/g;
    s/ruoyi-common-job/whl-framework-job/g;
    s/ruoyi-common-log/whl-framework-log/g;
    s/ruoyi-common-mail/whl-framework-mail/g;
    s/ruoyi-common-mybatis/whl-framework-mybatis/g;
    s/ruoyi-common-oss/whl-framework-oss/g;
    s/ruoyi-common-ratelimiter/whl-framework-ratelimiter/g;
    s/ruoyi-common-redis/whl-framework-redis/g;
    s/ruoyi-common-satoken/whl-framework-satoken/g;
    s/ruoyi-common-security/whl-framework-security/g;
    s/ruoyi-common-sensitive/whl-framework-sensitive/g;
    s/ruoyi-common-sms/whl-framework-sms/g;
    s/ruoyi-common-social/whl-framework-social/g;
    s/ruoyi-common-sse/whl-framework-sse/g;
    s/ruoyi-common-tenant/whl-framework-tenant/g;
    s/ruoyi-common-translation/whl-framework-translation/g;
    s/ruoyi-common-web/whl-framework-web/g;
    s/ruoyi-common-websocket/whl-framework-websocket/g;
    s/ruoyi-common-encrypt/whl-framework-encrypt/g;
    s/ruoyi-common-json/whl-framework-json/g;
    s/ruoyi-workflow/whl-platform-workflow/g;
    s/ruoyi-generator/whl-platform-generator/g;
    s/ruoyi-system/whl-platform-system/g;
    s/ruoyi-modules/whl-platform/g;
    s/ruoyi-demo/whl-platform-demo/g;
    s/ruoyi-job/whl-platform-job/g;
    s/<artifactId>ruoyi-common<\/artifactId>/<artifactId>whl-framework<\/artifactId>/g;
    s/<module>ruoyi-common<\/module>/<module>whl-framework<\/module>/g;
    s/<module>ruoyi-modules<\/module>/<module>whl-platform<\/module>/g;
    s/org\.dromara\.common/com.whl.framework/g;
    s/org\.dromara\.system/com.whl.platform.system/g;
    s/org\.dromara\.workflow/com.whl.platform.workflow/g;
    s/org\.dromara\.generator/com.whl.platform.generator/g;
    s/org\.dromara\.demo/com.whl.platform.demo/g;
    s/org\.dromara\.job/com.whl.platform.job/g;
    s/org\.dromara\.web/com.whl.platform.web/g;
  ' "$file"
done

# application.yml scan packages
perl -pi -e '
  s/mapperPackage: org\.dromara\.\*\*\.mapper,com\.whl\.\*\*\.mapper/mapperPackage: com.whl.platform.**.mapper,com.whl.**.mapper/g;
  s/typeAliasesPackage: org\.dromara\.\*\*\.domain,com\.whl\.scm\.\*\*\.domain/typeAliasesPackage: com.whl.platform.**.domain,com.whl.scm.**.domain/g;
' ruoyi-admin/src/main/resources/application.yml

perl -pi -e '
  s/packages-to-scan: org\.dromara\.demo/packages-to-scan: com.whl.platform.demo/g;
  s/packages-to-scan: org\.dromara\.web/packages-to-scan: com.whl.platform.web/g;
  s/packages-to-scan: org\.dromara\.system/packages-to-scan: com.whl.platform.system/g;
  s/packages-to-scan: org\.dromara\.generator/packages-to-scan: com.whl.platform.generator/g;
  s/packages-to-scan: org\.dromara\.workflow/packages-to-scan: com.whl.platform.workflow/g;
' ruoyi-admin/src/main/resources/application.yml

perl -pi -e 's/scanBasePackages = \{"org\.dromara", "com\.whl"\}/scanBasePackages = {"org.dromara", "com.whl"}/' ruoyi-admin/src/main/java/org/dromara/DromaraApplication.java

echo "== Phase 3 migration script finished =="
