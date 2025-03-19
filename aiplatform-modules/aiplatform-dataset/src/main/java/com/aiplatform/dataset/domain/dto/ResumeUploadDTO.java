package com.aiplatform.dataset.domain.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 断点续传DTO
 *
 * @author aiplatform
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeUploadDTO {
    /** 文件ID */
    private Long fileId;

    /** 文件名称 */
    private String fileName;

    /** 文件唯一标识 */
    private String identifier;

    /** 已上传分片索引列表 */
    private int[] uploadedChunks;

    /** 分片大小（字节） */
    private Long chunkSize;

    /** 总分片数 */
    private Integer totalChunks;

    /** 文件总大小（字节） */
    private Long totalSize;

    /** 上传状态（init初始化/uploading上传中/merging合并中/success成功/failed失败） */
    private String status;
} 