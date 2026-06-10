-- wms_org 码表（domains/wms/design/B1-org/TECH.md §14.5）
-- 导入：mysql --default-character-set=utf8mb4 ... < wms_org_dict.sql

SET NAMES utf8mb4;

INSERT INTO sys_dict_type VALUES(1901, '000000', 'WMS 组织类型', 'wms_org_type', 103, 1, sysdate(), NULL, NULL, 'wms_org.org_type')
ON DUPLICATE KEY UPDATE dict_name = VALUES(dict_name), update_time = sysdate();

INSERT INTO sys_dict_type VALUES(1902, '000000', 'WMS 启用状态', 'wms_enable_status', 103, 1, sysdate(), NULL, NULL, 'wms_org.status')
ON DUPLICATE KEY UPDATE dict_name = VALUES(dict_name), update_time = sysdate();

INSERT INTO sys_dict_data VALUES(19001, '000000', 1, '集团',   '10', 'wms_org_type', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19002, '000000', 2, '法人',   '20', 'wms_org_type', '', 'default', 'Y', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19003, '000000', 3, '事业部', '30', 'wms_org_type', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19004, '000000', 4, '区域',   '40', 'wms_org_type', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();

INSERT INTO sys_dict_data VALUES(19011, '000000', 1, '启用', '10', 'wms_enable_status', '', 'success', 'Y', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19012, '000000', 2, '停用', '20', 'wms_enable_status', '', 'danger',  'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
