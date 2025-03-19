package com.aiplatform.dataset.domain.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 文件上传DTO
 * 
 * @author aiplatform
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadDTO {
    
    /** 数据集ID */
    private Long datasetId;
    
    /** 当前分片 */
    private Integer chunkNumber;
    
    /** 分片大小 */
    private Long chunkSize;
    
    /** 当前分片大小 */
    private Long currentChunkSize;
    
    /** 文件总大小 */
    private Long totalSize;
    
    /** 文件标识 */
    private String identifier;
    
    /** 文件名称 */
    private String filename;
    
    /** 文件相对路径 */
    private String relativePath;
    
    /** 总分片数 */
    private Integer totalChunks;
    
    /** 文件类型 */
    private String fileType;
    
    /** 上传状态 */
    private String status;
    
    /** 错误信息 */
    private String errorMsg;
    
    /** 上传进度 */
    private Integer progress;
    
    /** 已上传大小 */
    private Long uploadedSize;
    
    /** 已上传分片 */
    private int[] uploadedChunks;

    public FileUploadDTO(FileUploadDTO source, int chunkNumber) {
        this.datasetId = source.getDatasetId();
        this.chunkNumber = chunkNumber;
        this.chunkSize = source.getChunkSize();
        this.currentChunkSize = source.getCurrentChunkSize();
        this.totalSize = source.getTotalSize();
        this.identifier = source.getIdentifier();
        this.filename = source.getFilename();
        this.relativePath = source.getRelativePath();
        this.totalChunks = source.getTotalChunks();
        this.fileType = source.getFileType();
    }
} 