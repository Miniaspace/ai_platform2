package com.aiplatform.dataset.domain.entity;

import com.aiplatform.common.core.domain.BaseEntity;
import com.aiplatform.common.core.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 版本变更记录对象 dataset_version_change
 * 
 * @author aiplatform
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DatasetVersionChange extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 变更ID */
    @Excel(name = "变更ID")
    private Long changeId;

    /** 版本ID */
    @Excel(name = "版本ID")
    private Long versionId;

    /** 文件ID */
    @Excel(name = "文件ID")
    private Long fileId;

    /** 变更类型（ADD新增/MODIFY修改/DELETE删除） */
    @Excel(name = "变更类型", readConverterExp = "ADD=新增,MODIFY=修改,DELETE=删除")
    private String changeType;

    /** 原文件ID（修改时使用） */
    @Excel(name = "原文件ID")
    private Long oldFileId;

    /** 变更描述 */
    @Excel(name = "变更描述")
    private String changeDesc;

    /** 关联的文件信息 */
    private transient DatasetFile file;

    /** 关联的原文件信息 */
    private transient DatasetFile oldFile;

    /** 关联的版本信息 */
    private transient DatasetVersion version;
} 