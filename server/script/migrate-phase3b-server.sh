#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

echo "== Phase 3B: Server layer rename =="

mkdir -p whl-server
git mv ruoyi-admin whl-server/whl-admin-server
git mv ruoyi-extend whl-server/whl-extend
git mv whl-server/whl-extend/ruoyi-monitor-admin whl-server/whl-extend/whl-monitor-admin
git mv whl-server/whl-extend/ruoyi-snailjob-server whl-server/whl-extend/whl-snailjob-server

echo "== Phase 3B: move boot classes to com.whl.boot =="

mkdir -p whl-server/whl-admin-server/src/main/java/com/whl/boot
mkdir -p whl-server/whl-admin-server/src/test/java/com/whl/boot/test
git mv whl-server/whl-admin-server/src/main/java/org/dromara/DromaraApplication.java \
  whl-server/whl-admin-server/src/main/java/com/whl/boot/AdminApplication.java
git mv whl-server/whl-admin-server/src/main/java/org/dromara/DromaraServletInitializer.java \
  whl-server/whl-admin-server/src/main/java/com/whl/boot/AdminServletInitializer.java
git mv whl-server/whl-admin-server/src/test/java/org/dromara/test/*.java \
  whl-server/whl-admin-server/src/test/java/com/whl/boot/test/

mkdir -p whl-server/whl-extend/whl-monitor-admin/src/main/java/com/whl/boot/monitor/config
mkdir -p whl-server/whl-extend/whl-monitor-admin/src/main/java/com/whl/boot/monitor/notifier
git mv whl-server/whl-extend/whl-monitor-admin/src/main/java/org/dromara/monitor/admin/MonitorAdminApplication.java \
  whl-server/whl-extend/whl-monitor-admin/src/main/java/com/whl/boot/monitor/MonitorAdminApplication.java
git mv whl-server/whl-extend/whl-monitor-admin/src/main/java/org/dromara/monitor/admin/config/SecurityConfig.java \
  whl-server/whl-extend/whl-monitor-admin/src/main/java/com/whl/boot/monitor/config/SecurityConfig.java
git mv whl-server/whl-extend/whl-monitor-admin/src/main/java/org/dromara/monitor/admin/notifier/CustomNotifier.java \
  whl-server/whl-extend/whl-monitor-admin/src/main/java/com/whl/boot/monitor/notifier/CustomNotifier.java

mkdir -p whl-server/whl-extend/whl-snailjob-server/src/main/java/com/whl/boot/snailjob
git mv whl-server/whl-extend/whl-snailjob-server/src/main/java/org/dromara/snailjob/SnailJobServerApplication.java \
  whl-server/whl-extend/whl-snailjob-server/src/main/java/com/whl/boot/snailjob/SnailJobServerApplication.java

echo "== Phase 3B: update POMs, configs, Docker (see git diff) =="
echo "Run: mvn -q -DskipTests compile"
