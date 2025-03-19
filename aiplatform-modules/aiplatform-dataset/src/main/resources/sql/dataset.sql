-- ----------------------------
-- 1. 数据集表
-- ----------------------------
drop table if exists dataset;
create table dataset (
  dataset_id        bigint(20)      not null auto_increment    comment '数据集ID',
  dataset_name      varchar(50)     not null                   comment '数据集名称',
  description       varchar(500)    default null               comment '数据集描述',
  dataset_type      char(1)         not null                   comment '数据集类型（0图像 1文本 2表格 3音频 4视频）',
  label_type        char(1)         default '0'                comment '标注类型（0分类 1检测 2分割）',
  status            char(1)         default '0'                comment '数据集状态（0未标注 1标注中 2已标注）',
  storage_path      varchar(255)    default ''                 comment '存储路径',
  version           varchar(32)     default '1.0.0'            comment '版本号',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (dataset_id)
) engine=innodb auto_increment=100 comment = '数据集表';

-- ----------------------------
-- 2. 数据集文件表
-- ----------------------------
drop table if exists dataset_file;
create table dataset_file (
  file_id           bigint(20)      not null auto_increment    comment '文件ID',
  dataset_id        bigint(20)      not null                   comment '数据集ID',
  file_name         varchar(255)    not null                   comment '文件名称',
  file_path         varchar(255)    not null                   comment '文件路径',
  file_type         varchar(32)     default ''                 comment '文件类型',
  file_size         bigint(20)      default 0                  comment '文件大小',
  file_md5          varchar(32)     default ''                 comment '文件MD5',
  label_status      char(1)         default '0'                comment '标注状态（0未标注 1已标注）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (file_id)
) engine=innodb auto_increment=100 comment = '数据集文件表';

-- ----------------------------
-- 3. 数据集标注表
-- ----------------------------
drop table if exists dataset_label;
create table dataset_label (
  label_id          bigint(20)      not null auto_increment    comment '标注ID',
  dataset_id        bigint(20)      not null                   comment '数据集ID',
  file_id           bigint(20)      not null                   comment '文件ID',
  label_content     text            not null                   comment '标注内容（JSON格式）',
  label_type        char(1)         not null                   comment '标注类型（0分类 1检测 2分割）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (label_id)
) engine=innodb auto_increment=100 comment = '数据集标注表';

-- ----------------------------
-- 4. 数据集版本表
-- ----------------------------
drop table if exists dataset_version;
create table dataset_version (
  version_id        bigint(20)      not null auto_increment    comment '版本ID',
  dataset_id        bigint(20)      not null                   comment '数据集ID',
  version           varchar(32)      not null                   comment '版本号',
  version_desc      varchar(500)    default null               comment '版本描述',
  storage_path      varchar(255)    default ''                 comment '存储路径',
  file_count        int(11)         default 0                  comment '文件数量',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (version_id)
) engine=innodb auto_increment=100 comment = '数据集版本表'; 