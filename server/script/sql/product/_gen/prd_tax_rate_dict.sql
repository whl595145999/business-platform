-- prd_sku 默认税率字典（domains/product/prd/B2-product/PRODUCT.md §2.6）
-- input_tax_rate / output_tax_rate 均引用本 dict；dict_value 存小数税率供单据换算
-- 导入：mysql --default-character-set=utf8mb4 ... < prd_tax_rate_dict.sql

SET NAMES utf8mb4;

INSERT INTO sys_dict_type VALUES(1911, '000000', '货品默认税率', 'prd_tax_rate', 103, 1, sysdate(), NULL, NULL, 'prd_sku.input_tax_rate,prd_sku.output_tax_rate')
ON DUPLICATE KEY UPDATE dict_name = VALUES(dict_name), update_time = sysdate();

INSERT INTO sys_dict_data VALUES(191101, '000000', 1,  '0%',   '0.0000', 'prd_tax_rate', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '免税/零税率')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), dict_value = VALUES(dict_value), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(191102, '000000', 2,  '1%',   '0.0100', 'prd_tax_rate', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), dict_value = VALUES(dict_value), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(191103, '000000', 3,  '3%',   '0.0300', 'prd_tax_rate', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), dict_value = VALUES(dict_value), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(191104, '000000', 4,  '6%',   '0.0600', 'prd_tax_rate', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), dict_value = VALUES(dict_value), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(191105, '000000', 5,  '9%',   '0.0900', 'prd_tax_rate', '', 'default', 'N', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), dict_value = VALUES(dict_value), update_time = sysdate();
INSERT INTO sys_dict_data VALUES(191106, '000000', 6,  '13%',  '0.1300', 'prd_tax_rate', '', 'default', 'Y', 103, 1, sysdate(), NULL, NULL, '常见销项默认')
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), dict_value = VALUES(dict_value), update_time = sysdate();
