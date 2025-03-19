-- ----------------------------
-- 1. 数据集版本表
-- ----------------------------
CREATE TABLE dataset_version (
    version_id          BIGINT PRIMARY KEY,                -- 版本ID
    dataset_id          BIGINT NOT NULL,                   -- 数据集ID
    version_name        VARCHAR(50) NOT NULL,              -- 版本名称
    version_desc        VARCHAR(500),                      -- 版本描述
    parent_version_id   BIGINT,                           -- 父版本ID
    version_status      CHAR(1) DEFAULT '0',              -- 版本状态（0草稿 1发布 2废弃）
    is_current         TINYINT(1) DEFAULT 0,              -- 是否为当前版本
    create_by          VARCHAR(64),                       -- 创建者
    create_time        DATETIME,                          -- 创建时间
    update_by          VARCHAR(64),                       -- 更新者
    update_time        DATETIME,                          -- 更新时间
    remark             VARCHAR(500),                      -- 备注
    CONSTRAINT fk_dataset_version FOREIGN KEY (dataset_id) REFERENCES dataset(dataset_id)
);

-- ----------------------------
-- 2. 版本文件关联表
-- ----------------------------
CREATE TABLE dataset_version_file (
    version_id          BIGINT NOT NULL,                  -- 版本ID
    file_id             BIGINT NOT NULL,                  -- 文件ID
    file_name           VARCHAR(255) NOT NULL,            -- 文件名称
    file_path           VARCHAR(500) NOT NULL,            -- 文件路径
    file_type           VARCHAR(50) NOT NULL,             -- 文件类型
    file_size           BIGINT NOT NULL,                  -- 文件大小
    md5                 VARCHAR(32),                      -- 文件MD5值
    sha1                VARCHAR(40),                      -- 文件SHA1值
    file_hash           VARCHAR(64),                      -- 文件哈希值
    metadata            TEXT,                             -- 文件元数据（JSON格式）
    file_status         CHAR(1) DEFAULT '0',              -- 文件状态（0正常 1删除）
    create_by           VARCHAR(64),                      -- 创建者
    create_time         DATETIME,                         -- 创建时间
    update_by           VARCHAR(64),                      -- 更新者
    update_time         DATETIME,                         -- 更新时间
    remark              VARCHAR(500),                     -- 备注
    PRIMARY KEY (version_id, file_id),
    CONSTRAINT fk_version_file_version FOREIGN KEY (version_id) REFERENCES dataset_version(version_id),
    CONSTRAINT fk_version_file_file FOREIGN KEY (file_id) REFERENCES dataset_file(file_id)
);

-- ----------------------------
-- 3. 版本变更记录表
-- ----------------------------
CREATE TABLE dataset_version_change (
    change_id           BIGINT PRIMARY KEY,               -- 变更ID
    version_id          BIGINT NOT NULL,                  -- 版本ID
    change_type         CHAR(1) NOT NULL,                 -- 变更类型（1新增 2修改 3删除）
    file_id             BIGINT,                          -- 文件ID
    old_file_id         BIGINT,                          -- 原文件ID（修改时使用）
    change_desc         VARCHAR(500),                     -- 变更描述
    create_by           VARCHAR(64),                      -- 创建者
    create_time         DATETIME,                         -- 创建时间
    update_by           VARCHAR(64),                      -- 更新者
    update_time         DATETIME,                         -- 更新时间
    remark              VARCHAR(500),                     -- 备注
    CONSTRAINT fk_version_change_version FOREIGN KEY (version_id) REFERENCES dataset_version(version_id),
    CONSTRAINT fk_version_change_file FOREIGN KEY (file_id) REFERENCES dataset_file(file_id)
);

-- ----------------------------
-- 4. 版本标签表
-- ----------------------------
CREATE TABLE dataset_version_tag (
    tag_id              BIGINT PRIMARY KEY,               -- 标签ID
    version_id          BIGINT NOT NULL,                  -- 版本ID
    tag_name            VARCHAR(50) NOT NULL,             -- 标签名称
    tag_desc            VARCHAR(500),                     -- 标签描述
    create_by           VARCHAR(64),                      -- 创建者
    create_time         DATETIME,                         -- 创建时间
    update_by           VARCHAR(64),                      -- 更新者
    update_time         DATETIME,                         -- 更新时间
    remark              VARCHAR(500),                     -- 备注
    CONSTRAINT fk_version_tag_version FOREIGN KEY (version_id) REFERENCES dataset_version(version_id)
);

-- ----------------------------
-- 5. 版本比较记录表
-- ----------------------------
CREATE TABLE dataset_version_compare (
    compare_id          BIGINT PRIMARY KEY,               -- 比较ID
    source_version_id   BIGINT NOT NULL,                  -- 源版本ID
    target_version_id   BIGINT NOT NULL,                  -- 目标版本ID
    compare_result      TEXT,                            -- 比较结果（JSON格式）
    create_by           VARCHAR(64),                      -- 创建者
    create_time         DATETIME,                         -- 创建时间
    update_by           VARCHAR(64),                      -- 更新者
    update_time         DATETIME,                         -- 更新时间
    remark              VARCHAR(500),                     -- 备注
    CONSTRAINT fk_version_compare_source FOREIGN KEY (source_version_id) REFERENCES dataset_version(version_id),
    CONSTRAINT fk_version_compare_target FOREIGN KEY (target_version_id) REFERENCES dataset_version(version_id)
); 