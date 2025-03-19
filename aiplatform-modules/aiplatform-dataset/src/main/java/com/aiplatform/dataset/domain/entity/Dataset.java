package com.aiplatform.dataset.domain.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.aiplatform.common.core.annotation.Excel;
import com.aiplatform.common.core.web.domain.BaseEntity;

/**
 * 数据集对象 dataset
 * 
 * @author aiplatform
 */
public class Dataset extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 数据集ID */
    private Long datasetId;

    /** 数据集名称 */
    @Excel(name = "数据集名称")
    private String datasetName;

    /** 数据集描述 */
    @Excel(name = "数据集描述")
    private String description;

    /** 数据集类型（0图像 1文本 2表格 3音频 4视频） */
    @Excel(name = "数据集类型", readConverterExp = "0=图像,1=文本,2=表格,3=音频,4=视频")
    private String datasetType;

    /** 标注类型（0分类 1检测 2分割） */
    @Excel(name = "标注类型", readConverterExp = "0=分类,1=检测,2=分割")
    private String labelType;

    /** 数据集状态（0未标注 1标注中 2已标注） */
    @Excel(name = "数据集状态", readConverterExp = "0=未标注,1=标注中,2=已标注")
    private String status;

    /** 存储路径 */
    @Excel(name = "存储路径")
    private String storagePath;

    /** 版本号 */
    @Excel(name = "版本号")
    private String version;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    // getter and setter methods

    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatasetType() {
        return datasetType;
    }

    public void setDatasetType(String datasetType) {
        this.datasetType = datasetType;
    }

    public String getLabelType() {
        return labelType;
    }

    public void setLabelType(String labelType) {
        this.labelType = labelType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("datasetId", getDatasetId())
            .append("datasetName", getDatasetName())
            .append("description", getDescription())
            .append("datasetType", getDatasetType())
            .append("labelType", getLabelType())
            .append("status", getStatus())
            .append("storagePath", getStoragePath())
            .append("version", getVersion())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
} 