package com.aiplatform.dataset.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.aiplatform.common.core.domain.BaseEntity;

/**
 * 数据集版本标签对象 dataset_version_tag
 *
 * @author aiplatform
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DatasetVersionTag extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 标签ID */
    private Long tagId;

    /** 版本ID */
    private Long versionId;

    /** 标签名称 */
    private String tagName;

    /** 标签描述 */
    private String description;

    /** 标签类型（SYSTEM系统标签/CUSTOM自定义标签） */
    private String tagType;

    /** 标签颜色 */
    private String tagColor;

    /** 排序号 */
    private Integer orderNum;

    /** 状态（0正常 1停用） */
    private String status;

    /** 关联的版本对象 */
    private DatasetVersion version;
} 