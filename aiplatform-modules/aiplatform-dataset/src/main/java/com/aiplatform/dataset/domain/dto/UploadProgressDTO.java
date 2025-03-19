package com.aiplatform.dataset.domain.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 上传进度DTO
 *
 * @author aiplatform
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadProgressDTO {
    /** 文件ID */
    private Long fileId;

    /** 文件名称 */
    private String fileName;

    /** 已上传大小 */
    private Long uploadedSize;

    /** 总大小 */
    private Long totalSize;

    /** 上传进度（百分比） */
    private Integer progress;

    /** 上传状态（init/uploading/success/failed） */
    private String status;

    /** 错误信息 */
    private String errorMsg;
} 