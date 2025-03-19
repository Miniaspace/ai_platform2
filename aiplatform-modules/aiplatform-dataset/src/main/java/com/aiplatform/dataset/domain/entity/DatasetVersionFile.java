package com.aiplatform.dataset.domain.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据集版本文件对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetVersionFile {
    /** 文件ID */
    private Long fileId;

    /** 文件名称 */
    private String fileName;

    /** 文件路径 */
    private String filePath;

    /** 文件大小 */
    private Long fileSize;

    /** 文件类型 */
    private String fileType;

    /** 文件MD5 */
    private String md5;

    /** 文件SHA1 */
    private String sha1;

    /** 文件哈希值 */
    private String fileHash;

    /** 文件元数据（JSON格式） */
    private String metadata;

    /** 文件状态（0正常 1删除） */
    private String fileStatus;

    /** 版本ID */
    private Long versionId;

    /** 数据集ID */
    private Long datasetId;

    /** 父文件ID */
    private Long parentId;

    /** 变更类型 */
    private String changeType;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Date updateTime;
} 