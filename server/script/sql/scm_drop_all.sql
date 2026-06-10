-- 删除 SCM 业务表（B1 wms_org · B2 prd_* · 遗留表若存在则清理）
-- 不删除 sys_* 等平台表
--
-- 用法:
--   mysql --default-character-set=utf8mb4 -uroot -p business_platform < script/sql/scm_drop_all.sql
--
-- 重建:
--   ./script/wms/scm-db-reinit.sh
--   ./script/wms/scm-db-reinit.sh --with-menu --with-seed

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 未落地波次遗留表（若库中仍存在则清理）
DROP TABLE IF EXISTS oms_order_line;
DROP TABLE IF EXISTS oms_order;
DROP TABLE IF EXISTS wms_outbound_task_line;
DROP TABLE IF EXISTS wms_outbound_task;
DROP TABLE IF EXISTS wms_inbound_order_line;
DROP TABLE IF EXISTS wms_inbound_order;
DROP TABLE IF EXISTS inv_stocktake_order_line;
DROP TABLE IF EXISTS inv_stocktake_order;
DROP TABLE IF EXISTS inv_reserve_record;
DROP TABLE IF EXISTS inv_stock_log;
DROP TABLE IF EXISTS inv_stock;
DROP TABLE IF EXISTS wms_warehouse_mapping;
DROP TABLE IF EXISTS wms_warehouse;
DROP TABLE IF EXISTS wms_org_demo;

-- B2 货品（先子表后主表）
DROP TABLE IF EXISTS prd_sku_barcode;
DROP TABLE IF EXISTS prd_sku;
DROP TABLE IF EXISTS prd_category;
DROP TABLE IF EXISTS prd_brand;

-- B1 组织
DROP TABLE IF EXISTS wms_org;

SET FOREIGN_KEY_CHECKS = 1;
