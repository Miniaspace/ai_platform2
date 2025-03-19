-- 添加标注类型字段
ALTER TABLE dataset ADD COLUMN label_type varchar(20) DEFAULT '0' COMMENT '标注类型（0分类 1检测 2分割）' AFTER dataset_type;

-- 添加删除标志字段
ALTER TABLE dataset ADD COLUMN del_flag char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）';

-- 更新数据集管理菜单结构
-- 1. 添加数据集详情菜单
INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2018, '数据集详情', 2000, 2, 'detail/:id', 'dataset/detail', '', 1, 0, 'C', '1', '0', 'dataset:detail', 'detail', 'admin', SYSDATE(), '', NULL, '数据集详情页面');

-- 2. 更新版本管理菜单为详情页子页面
UPDATE sys_menu 
SET parent_id = 2018,
    path = 'version',
    component = 'dataset/components/DatasetVersions',
    visible = '1'
WHERE menu_id = 2002;

-- 3. 更新标签管理菜单为详情页子页面
UPDATE sys_menu 
SET parent_id = 2018,
    path = 'tags',
    component = 'dataset/components/DatasetTags',
    visible = '1'
WHERE menu_id = 2003;

-- 4. 添加数据集分析菜单
INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2019, '数据集分析', 2018, 4, 'analytics', 'dataset/components/DatasetAnalytics', '', 1, 0, 'C', '1', '0', 'dataset:analytics', 'chart', 'admin', SYSDATE(), '', NULL, '数据集分析页面');

-- 5. 添加数据集详情按钮权限
INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2020, '基本信息查看', 2018, 1, '', '', '', 1, 0, 'F', '0', '0', 'dataset:detail:view', '#', 'admin', SYSDATE(), '', NULL, '');

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2021, '基本信息编辑', 2018, 2, '', '', '', 1, 0, 'F', '0', '0', 'dataset:detail:edit', '#', 'admin', SYSDATE(), '', NULL, '');

-- 6. 添加数据集分析按钮权限
INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2022, '分析数据查看', 2019, 1, '', '', '', 1, 0, 'F', '0', '0', 'dataset:analytics:view', '#', 'admin', SYSDATE(), '', NULL, '');

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2023, '分析数据导出', 2019, 2, '', '', '', 1, 0, 'F', '0', '0', 'dataset:analytics:export', '#', 'admin', SYSDATE(), '', NULL, ''); 