-- 数据集管理菜单权限
INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2000, '数据集管理', 0, 4, 'dataset', NULL, '', 1, 0, 'M', '0', '0', '', 'dataset', 'admin', SYSDATE(), '', NULL, '数据集管理菜单'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2000);

-- 数据集列表菜单
INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2001, '数据集列表', 2000, 1, 'index', 'dataset/index', '', 1, 0, 'C', '0', '0', 'dataset:list', 'list', 'admin', SYSDATE(), '', NULL, '数据集列表菜单'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2001);

-- 数据集详情菜单
INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2002, '数据集详情', 2000, 2, 'detail/:id', 'dataset/detail', '', 1, 0, 'C', '1', '0', 'dataset:detail', 'detail', 'admin', SYSDATE(), '', NULL, '数据集详情菜单'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2002);

-- 数据集管理按钮权限
INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2003, '数据集查询', 2001, 1, '', '', '', 1, 0, 'F', '0', '0', 'dataset:list:query', '#', 'admin', SYSDATE(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2003);

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2004, '数据集新增', 2001, 2, '', '', '', 1, 0, 'F', '0', '0', 'dataset:list:add', '#', 'admin', SYSDATE(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2004);

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2005, '数据集修改', 2001, 3, '', '', '', 1, 0, 'F', '0', '0', 'dataset:list:edit', '#', 'admin', SYSDATE(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2005);

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2006, '数据集删除', 2001, 4, '', '', '', 1, 0, 'F', '0', '0', 'dataset:list:remove', '#', 'admin', SYSDATE(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2006);

-- 为管理员角色分配数据集管理权限
INSERT INTO sys_role_menu(role_id, menu_id)
SELECT 1, menu_id FROM sys_menu WHERE menu_id BETWEEN 2000 AND 2006
AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu 
    WHERE role_id = 1 AND menu_id BETWEEN 2000 AND 2006
); 