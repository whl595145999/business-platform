-- B1 组织档案菜单（menu_id 1708，domains/wms/design/B1-org/TECH.md §15.2）
-- 导入：mysql --default-character-set=utf8mb4 ... < wms_org_menu.sql
-- 执行后 admin 需重新登录

SET NAMES utf8mb4;

INSERT INTO sys_menu VALUES('1700', '供应链', '0', '6', 'scm', NULL, '', 1, 0, 'M', '0', '0', '', 'shopping', 103, 1, sysdate(), NULL, NULL, 'SCM 目录')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), order_num = VALUES(order_num), update_time = sysdate();

INSERT INTO sys_menu VALUES('1708', '组织档案', '1700', '0', 'wms/org', 'scm/wms/org/index', '', 1, 0, 'C', '0', '0', 'scm:wms:org:list', 'tree', 103, 1, sysdate(), NULL, NULL, 'WMS 组织主数据')
ON DUPLICATE KEY UPDATE component = VALUES(component), perms = VALUES(perms), order_num = VALUES(order_num), update_time = sysdate();

INSERT INTO sys_menu VALUES('1718', '组织新增', '1708', '1', '', '', '', 1, 0, 'F', '0', '0', 'scm:wms:org:add', '#', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), update_time = sysdate();
INSERT INTO sys_menu VALUES('1719', '组织修改', '1708', '2', '', '', '', 1, 0, 'F', '0', '0', 'scm:wms:org:edit', '#', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), update_time = sysdate();
INSERT INTO sys_menu VALUES('1720', '组织删除', '1708', '3', '', '', '', 1, 0, 'F', '0', '0', 'scm:wms:org:remove', '#', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), update_time = sysdate();
INSERT INTO sys_menu VALUES('1721', '组织查询', '1708', '4', '', '', '', 1, 0, 'F', '0', '0', 'scm:wms:org:query', '#', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), update_time = sysdate();
INSERT INTO sys_menu VALUES('1722', '组织导出', '1708', '5', '', '', '', 1, 0, 'F', '0', '0', 'scm:wms:org:export', '#', 103, 1, sysdate(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), update_time = sysdate();

INSERT IGNORE INTO sys_role_menu VALUES ('1', '1700');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1708');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1718');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1719');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1720');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1721');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '1722');
