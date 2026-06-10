-- B1 组织示例数据 A/B/C 树（domains/wms/prd/B1-org/PRODUCT.md §2.1）
-- 导入：mysql --default-character-set=utf8mb4 ... < wms_org_seed.sql
-- 依赖 wms_org_ddl.sql 已执行

SET NAMES utf8mb4;

INSERT INTO wms_org (
    id, tenant_id, org_code, org_name, org_type, parent_org_id, status, sort_order,
    create_dept, create_by, create_time, update_by, update_time, del_flag
) VALUES
    (1001, '000000', 'ORG-A',       'A公司',         10, 0,    10, 1, 103, 1, NOW(), 1, NOW(), '0'),
    (1002, '000000', 'ORG-B',       'B公司',         20, 1001, 10, 2, 103, 1, NOW(), 1, NOW(), '0'),
    (1003, '000000', 'ORG-B-SH',    'B上海事业部',   30, 1002, 10, 1, 103, 1, NOW(), 1, NOW(), '0'),
    (1004, '000000', 'ORG-B-HZ',    'B杭州事业部',   30, 1002, 10, 2, 103, 1, NOW(), 1, NOW(), '0'),
    (1005, '000000', 'ORG-C',       'C公司',         20, 1001, 10, 3, 103, 1, NOW(), 1, NOW(), '0'),
    (1006, '000000', 'ORG-C-SOUTH', 'C华南大区',     40, 1005, 10, 1, 103, 1, NOW(), 1, NOW(), '0'),
    (1007, '000000', 'ORG-C-CD',    'C成都分公司',   30, 1005, 10, 2, 103, 1, NOW(), 1, NOW(), '0')
ON DUPLICATE KEY UPDATE
    org_name = VALUES(org_name),
    org_type = VALUES(org_type),
    parent_org_id = VALUES(parent_org_id),
    status = VALUES(status),
    sort_order = VALUES(sort_order),
    update_by = VALUES(update_by),
    update_time = VALUES(update_time);

ALTER TABLE wms_org AUTO_INCREMENT = 1100;
