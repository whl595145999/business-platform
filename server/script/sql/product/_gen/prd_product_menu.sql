-- B2 货品主数据菜单（1711-1713 挂供应链 1700，与组织档案同级）
-- 导入：mysql --default-character-set=utf8mb4 ... < prd_product_menu.sql
-- 执行后 admin 需重新登录

SET NAMES utf8mb4;

-- 清理已废弃 menu_id（1710 货品目录 · 1745 未实现导出）
DELETE FROM sys_role_menu WHERE menu_id IN (1710, 1745);
DELETE FROM sys_menu WHERE menu_id IN (1710, 1745);

INSERT INTO sys_menu VALUES('1711', '货品类目', '1700', '1', 'product/categories', 'scm/product/category/index', '', 1, 0, 'C', '0', '0', 'scm:product:category:list', 'tree', 103, 1, sysdate(), NULL, NULL, '货品类目树')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), path = VALUES(path), order_num = VALUES(order_num), component = VALUES(component), perms = VALUES(perms), visible = '0', update_time = sysdate();

INSERT INTO sys_menu VALUES('1712', '品牌档案', '1700', '2', 'product/brands', 'scm/product/brand/index', '', 1, 0, 'C', '0', '0', 'scm:product:brand:list', 'star', 103, 1, sysdate(), NULL, NULL, '货品品牌')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), path = VALUES(path), order_num = VALUES(order_num), component = VALUES(component), perms = VALUES(perms), visible = '0', update_time = sysdate();

INSERT INTO sys_menu VALUES('1713', '货品', '1700', '3', 'product/skus', 'scm/product/sku/index', '', 1, 0, 'C', '0', '0', 'scm:product:sku:list', 'list', 103, 1, sysdate(), NULL, NULL, '货品 SKU')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), path = VALUES(path), order_num = VALUES(order_num), component = VALUES(component), perms = VALUES(perms), visible = '0', update_time = sysdate();

INSERT INTO sys_menu VALUES('1731', '类目新增', '1711', '1', '', '', '', 1, 0, 'F', '0', '0', 'scm:product:category:add', '#', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), update_time = sysdate();
INSERT INTO sys_menu VALUES('1732', '类目修改', '1711', '2', '', '', '', 1, 0, 'F', '0', '0', 'scm:product:category:edit', '#', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), update_time = sysdate();
INSERT INTO sys_menu VALUES('1733', '类目删除', '1711', '3', '', '', '', 1, 0, 'F', '0', '0', 'scm:product:category:remove', '#', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), update_time = sysdate();
INSERT INTO sys_menu VALUES('1734', '类目查询', '1711', '4', '', '', '', 1, 0, 'F', '0', '0', 'scm:product:category:query', '#', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), update_time = sysdate();
INSERT INTO sys_menu VALUES('1735', '类目导出', '1711', '5', '', '', '', 1, 0, 'F', '0', '0', 'scm:product:category:export', '#', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), update_time = sysdate();

INSERT INTO sys_menu VALUES('1736', '品牌新增', '1712', '1', '', '', '', 1, 0, 'F', '0', '0', 'scm:product:brand:add', '#', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), update_time = sysdate();
INSERT INTO sys_menu VALUES('1737', '品牌修改', '1712', '2', '', '', '', 1, 0, 'F', '0', '0', 'scm:product:brand:edit', '#', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), update_time = sysdate();
INSERT INTO sys_menu VALUES('1738', '品牌删除', '1712', '3', '', '', '', 1, 0, 'F', '0', '0', 'scm:product:brand:remove', '#', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), update_time = sysdate();
INSERT INTO sys_menu VALUES('1739', '品牌查询', '1712', '4', '', '', '', 1, 0, 'F', '0', '0', 'scm:product:brand:query', '#', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), update_time = sysdate();
INSERT INTO sys_menu VALUES('1740', '品牌导出', '1712', '5', '', '', '', 1, 0, 'F', '0', '0', 'scm:product:brand:export', '#', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), update_time = sysdate();

INSERT INTO sys_menu VALUES('1741', '货品新增', '1713', '1', '', '', '', 1, 0, 'F', '0', '0', 'scm:product:sku:add', '#', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), update_time = sysdate();
INSERT INTO sys_menu VALUES('1742', '货品修改', '1713', '2', '', '', '', 1, 0, 'F', '0', '0', 'scm:product:sku:edit', '#', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), update_time = sysdate();
INSERT INTO sys_menu VALUES('1743', '货品删除', '1713', '3', '', '', '', 1, 0, 'F', '0', '0', 'scm:product:sku:remove', '#', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), update_time = sysdate();
INSERT INTO sys_menu VALUES('1744', '货品查询', '1713', '4', '', '', '', 1, 0, 'F', '0', '0', 'scm:product:sku:query', '#', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), update_time = sysdate();

INSERT IGNORE INTO sys_role_menu VALUES ('1', '1711');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1712');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1713');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1731');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1732');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1733');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1734');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1735');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1736');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1737');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1738');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1739');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1740');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1741');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1742');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1743');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1744');
