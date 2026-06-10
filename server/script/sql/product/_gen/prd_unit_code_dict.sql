-- prd_sku.unit_code 计量单位字典（domains/product/prd/B2-product/PRODUCT.md §2.6）
-- 导入：mysql --default-character-set=utf8mb4 ... < prd_unit_code_dict.sql

SET NAMES utf8mb4;

INSERT INTO sys_dict_type VALUES(1910, '000000', '货品计量单位', 'prd_unit_code', 103, 1, sysdate(), NULL, NULL, 'prd_sku.unit_code')
ON DUPLICATE KEY UPDATE dict_name = VALUES(dict_name), update_time = sysdate();

INSERT INTO sys_dict_data VALUES(19101, '000000', 1,  '件', 'jian',   'prd_unit_code', '', 'default', 'Y', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19102, '000000', 2,  '盒', 'he',     'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19103, '000000', 3,  '箱', 'xiang',  'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19104, '000000', 4,  '张', 'zhang',  'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19105, '000000', 5,  '包', 'bao',    'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19106, '000000', 6,  '个', 'ge',     'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19107, '000000', 7,  '袋', 'dai',    'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19108, '000000', 8,  '罐', 'guan',   'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19109, '000000', 9,  '瓶', 'ping',   'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19110, '000000', 10, '桶', 'tong',   'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19111, '000000', 11, '条', 'tiao',   'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19112, '000000', 12, '支', 'zhi',    'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19113, '000000', 13, '对', 'dui',    'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19114, '000000', 14, '根', 'gen',    'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19115, '000000', 15, '套', 'tao',    'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19116, '000000', 16, '台', 'tai',    'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19117, '000000', 17, '片', 'pian',   'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19118, '000000', 18, '粒', 'li',     'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19119, '000000', 19, '把', 'ba',     'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19120, '000000', 20, '只', 'zhi_zhi','prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19121, '000000', 21, '排', 'pai',    'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19122, '000000', 22, '辆', 'liang',  'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19123, '000000', 23, '组', 'zu',     'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19124, '000000', 24, '卷', 'juan',   'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19125, '000000', 25, '本', 'ben',    'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19126, '000000', 26, '栋', 'dong',   'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19127, '000000', 27, '双', 'shuang', 'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19128, '000000', 28, '打', 'da',     'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19129, '000000', 29, '克', 'g',      'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19130, '000000', 30, '份', 'fen',    'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19131, '000000', 31, '块', 'kuai',   'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(19132, '000000', 32, 'PCS', 'PCS',   'prd_unit_code', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = sysdate();
