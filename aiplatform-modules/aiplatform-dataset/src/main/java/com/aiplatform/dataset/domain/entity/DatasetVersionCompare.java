package com.aiplatform.dataset.domain.entity;

import com.aiplatform.common.core.web.domain.BaseEntity;
import com.aiplatform.common.core.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据集版本比较记录对象 dataset_version_compare
 * 
 * @author aiplatform
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DatasetVersionCompare extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 比较ID */
    @Excel(name = "比较ID")
    private Long compareId;

    /** 源版本ID */
    @Excel(name = "源版本ID")
    private Long sourceVersionId;

    /** 目标版本ID */
    @Excel(name = "目标版本ID")
    private Long targetVersionId;

    /** 比较状态（COMPARING比较中/SUCCESS成功/FAILED失败） */
    @Excel(name = "比较状态")
    private String compareStatus;

    /** 比较结果（JSON格式） */
    @Excel(name = "比较结果")
    private String compareResult;

    /** 错误信息 */
    @Excel(name = "错误信息")
    private String errorMsg;

    /** 关联的源版本信息 */
    private transient DatasetVersion sourceVersion;

    /** 关联的目标版本信息 */
    private transient DatasetVersion targetVersion;

    public void setCompareStatus(String compareStatus) {
        this.compareStatus = compareStatus;
    }
} 