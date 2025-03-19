package com.aiplatform.dataset.domain.entity;

import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.aiplatform.common.core.domain.BaseEntity;

/**
 * 数据集版本对象 dataset_version
 *
 * @author aiplatform
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DatasetVersion extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 版本ID */
    private Long versionId;

    /** 数据集ID */
    private Long datasetId;

    /** 版本名称 */
    private String versionName;

    /** 版本描述 */
    private String versionDesc;

    /** 版本状态（DRAFT草稿/PUBLISHED已发布/DISCARDED已废弃） */
    private String status;
    
    /** 版本状态（与status字段对应，用于兼容XML映射） */
    private String versionStatus;

    /** 发布时间 */
    private Date publishTime;

    /** 发布人 */
    private String publishBy;

    /** 父版本ID */
    private Long parentVersionId;

    /** 子版本列表 */
    private List<DatasetVersion> children;

    /** 是否为当前版本（0否 1是） */
    private String isCurrent;

    /** 文件数量 */
    private Integer fileCount;

    /** 文件总大小（字节） */
    private Long totalSize;

    /** 标签列表 */
    private List<DatasetVersionTag> tags;

    // 显式添加status的getter和setter方法，确保不被Lombok覆盖
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        // 同步设置versionStatus，确保两个字段保持一致
        this.versionStatus = status;
    }
    
    // 显式添加versionStatus的getter和setter方法，确保它们被正确生成
    public String getVersionStatus() {
        return this.versionStatus != null ? this.versionStatus : this.status;
    }

    public void setVersionStatus(String versionStatus) {
        this.versionStatus = versionStatus;
        // 同步设置status，确保两个字段保持一致
        this.status = versionStatus;
    }

    public Long getParentVersionId() {
        return parentVersionId;
    }

    public void setParentVersionId(Long parentVersionId) {
        this.parentVersionId = parentVersionId;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public void setPublishBy(String publishBy) {
        this.publishBy = publishBy;
    }

    public void setChildren(List<DatasetVersion> children) {
        this.children = children;
    }
} 