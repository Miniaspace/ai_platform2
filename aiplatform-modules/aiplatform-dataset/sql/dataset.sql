-- ----------------------------
-- 1. 数据集表
-- ----------------------------
drop table if exists dataset;
create table dataset (
  dataset_id          bigint(20)      not null auto_increment    comment '数据集ID',
  dataset_name        varchar(50)     not null                   comment '数据集名称',
  description         varchar(500)    default null               comment '数据集描述',
  dataset_type        varchar(20)     not null                   comment '数据集类型（IMAGE图像/TEXT文本/AUDIO音频/VIDEO视频）',
  label_type          varchar(20)     default '0'               comment '标注类型（0分类 1检测 2分割）',
  total_files         int(11)         default 0                  comment '文件总数',
  total_size          bigint(20)      default 0                  comment '总大小（字节）',
  current_version_id  bigint(20)      default null              comment '当前版本ID',
  status              varchar(20)     default 'NORMAL'           comment '状态（NORMAL正常/LOCKED锁定/DELETED已删除）',
  del_flag            char(1)         default '0'               comment '删除标志（0代表存在 2代表删除）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  version           varchar(32)     default '1.0.0'            comment '版本号',
  primary key (dataset_id)
) engine=innodb auto_increment=100 comment = '数据集表';

-- ----------------------------
-- 2. 数据集版本表
-- ----------------------------
drop table if exists dataset_version;
create table dataset_version (
  version_id          bigint(20)      not null auto_increment    comment '版本ID',
  dataset_id          bigint(20)      not null                   comment '数据集ID',
  version_name        varchar(50)     not null                   comment '版本名称',
  description         varchar(500)    default null               comment '版本描述',
  parent_version_id   bigint(20)      default null              comment '父版本ID',
  file_count          int(11)         default 0                  comment '文件数量',
  total_size          bigint(20)      default 0                  comment '总大小（字节）',
  status              varchar(20)     default 'DRAFT'            comment '状态（DRAFT草稿/PUBLISHED已发布/DISCARDED已废弃）',
  create_by           varchar(64)     default ''                 comment '创建者',
  create_time         datetime                                   comment '创建时间',
  update_by           varchar(64)     default ''                 comment '更新者',
  update_time         datetime                                   comment '更新时间',
  remark              varchar(500)    default null               comment '备注',
  primary key (version_id)
) engine=innodb auto_increment=100 comment = '数据集版本表';

-- ----------------------------
-- 3. 版本文件关联表
-- ----------------------------
drop table if exists dataset_version_file;
create table dataset_version_file (
  version_id          bigint(20)      not null                   comment '版本ID',
  file_id             bigint(20)      not null                   comment '文件ID',
  file_name           varchar(255)    not null                   comment '文件名',
  file_path           varchar(500)    not null                   comment '文件路径',
  file_size           bigint(20)      not null                   comment '文件大小（字节）',
  file_type           varchar(50)     not null                   comment '文件类型',
  file_hash           varchar(64)     not null                   comment '文件哈希值',
  metadata            text            default null               comment '文件元数据（JSON格式）',
  create_by           varchar(64)     default ''                 comment '创建者',
  create_time         datetime                                   comment '创建时间',
  update_by           varchar(64)     default ''                 comment '更新者',
  update_time         datetime                                   comment '更新时间',
  primary key (version_id, file_id)
) engine=innodb comment = '版本文件关联表';

-- ----------------------------
-- 4. 版本变更记录表
-- ----------------------------
drop table if exists dataset_version_change;
create table dataset_version_change (
  change_id           bigint(20)      not null auto_increment    comment '变更ID',
  version_id          bigint(20)      not null                   comment '版本ID',
  file_id             bigint(20)      not null                   comment '文件ID',
  change_type         varchar(20)     not null                   comment '变更类型（ADD新增/MODIFY修改/DELETE删除）',
  old_file_id         bigint(20)      default null              comment '原文件ID（修改时使用）',
  change_desc         varchar(500)    default null               comment '变更描述',
  create_by           varchar(64)     default ''                 comment '创建者',
  create_time         datetime                                   comment '创建时间',
  primary key (change_id)
) engine=innodb auto_increment=100 comment = '版本变更记录表';

-- ----------------------------
-- 5. 版本标签表
-- ----------------------------
drop table if exists dataset_version_tag;
create table dataset_version_tag (
  tag_id              bigint(20)      not null auto_increment    comment '标签ID',
  version_id          bigint(20)      not null                   comment '版本ID',
  tag_name            varchar(50)     not null                   comment '标签名称',
  description         varchar(500)    default null               comment '标签描述',
  create_by           varchar(64)     default ''                 comment '创建者',
  create_time         datetime                                   comment '创建时间',
  update_by           varchar(64)     default ''                 comment '更新者',
  update_time         datetime                                   comment '更新时间',
  primary key (tag_id)
) engine=innodb auto_increment=100 comment = '版本标签表';

-- ----------------------------
-- 6. 版本比较记录表
-- ----------------------------
drop table if exists dataset_version_compare;
create table dataset_version_compare (
  compare_id          bigint(20)      not null auto_increment    comment '比较ID',
  source_version_id   bigint(20)      not null                   comment '源版本ID',
  target_version_id   bigint(20)      not null                   comment '目标版本ID',
  added_count         int(11)         default 0                  comment '新增文件数',
  modified_count      int(11)         default 0                  comment '修改文件数',
  deleted_count       int(11)         default 0                  comment '删除文件数',
  compare_result      text            default null               comment '比较结果（JSON格式）',
  create_by           varchar(64)     default ''                 comment '创建者',
  create_time         datetime                                   comment '创建时间',
  primary key (compare_id)
) engine=innodb auto_increment=100 comment = '版本比较记录表';

-- ----------------------------
-- 7. 菜单数据
-- ----------------------------
-- 数据集管理菜单
INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2000, '数据集管理', 0, 4, 'dataset', NULL, '', 1, 0, 'M', '0', '0', '', 'dataset', 'admin', SYSDATE(), '', NULL, '数据集管理菜单');

-- 数据集列表菜单
INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2001, '数据集列表', 2000, 1, 'list', 'dataset/index', '', 1, 0, 'C', '0', '0', 'dataset:list', 'list', 'admin', SYSDATE(), '', NULL, '数据集列表菜单');

-- 版本管理菜单
INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2002, '版本管理', 2000, 2, 'version', 'dataset/version/index', '', 1, 0, 'C', '0', '0', 'dataset:version:list', 'version', 'admin', SYSDATE(), '', NULL, '版本管理菜单');

-- 标签管理菜单
INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2003, '标签管理', 2000, 3, 'tag', 'dataset/version/tags', '', 1, 0, 'C', '0', '0', 'dataset:version:tag', 'tag', 'admin', SYSDATE(), '', NULL, '标签管理菜单');

-- 版本比较菜单
INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2004, '版本比较', 2000, 4, 'compare', 'dataset/version/compare', '', 1, 0, 'C', '0', '0', 'dataset:version:compare', 'compare', 'admin', SYSDATE(), '', NULL, '版本比较菜单');

-- 数据集管理按钮
INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2005, '数据集查询', 2001, 1, '', '', '', 1, 0, 'F', '0', '0', 'dataset:list:query', '#', 'admin', SYSDATE(), '', NULL, '');

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2006, '数据集新增', 2001, 2, '', '', '', 1, 0, 'F', '0', '0', 'dataset:list:add', '#', 'admin', SYSDATE(), '', NULL, '');

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2007, '数据集修改', 2001, 3, '', '', '', 1, 0, 'F', '0', '0', 'dataset:list:edit', '#', 'admin', SYSDATE(), '', NULL, '');

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2008, '数据集删除', 2001, 4, '', '', '', 1, 0, 'F', '0', '0', 'dataset:list:remove', '#', 'admin', SYSDATE(), '', NULL, '');

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2009, '数据集导出', 2001, 5, '', '', '', 1, 0, 'F', '0', '0', 'dataset:list:export', '#', 'admin', SYSDATE(), '', NULL, '');

-- 版本管理按钮
INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2010, '版本查询', 2002, 1, '', '', '', 1, 0, 'F', '0', '0', 'dataset:version:query', '#', 'admin', SYSDATE(), '', NULL, '');

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2011, '版本新增', 2002, 2, '', '', '', 1, 0, 'F', '0', '0', 'dataset:version:add', '#', 'admin', SYSDATE(), '', NULL, '');

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2012, '版本修改', 2002, 3, '', '', '', 1, 0, 'F', '0', '0', 'dataset:version:edit', '#', 'admin', SYSDATE(), '', NULL, '');

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2013, '版本删除', 2002, 4, '', '', '', 1, 0, 'F', '0', '0', 'dataset:version:remove', '#', 'admin', SYSDATE(), '', NULL, '');

-- 标签管理按钮
INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2014, '标签查询', 2003, 1, '', '', '', 1, 0, 'F', '0', '0', 'dataset:version:tag:query', '#', 'admin', SYSDATE(), '', NULL, '');

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2015, '标签新增', 2003, 2, '', '', '', 1, 0, 'F', '0', '0', 'dataset:version:tag:add', '#', 'admin', SYSDATE(), '', NULL, '');

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2016, '标签修改', 2003, 3, '', '', '', 1, 0, 'F', '0', '0', 'dataset:version:tag:edit', '#', 'admin', SYSDATE(), '', NULL, '');

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES(2017, '标签删除', 2003, 4, '', '', '', 1, 0, 'F', '0', '0', 'dataset:version:tag:remove', '#', 'admin', SYSDATE(), '', NULL, ''); 