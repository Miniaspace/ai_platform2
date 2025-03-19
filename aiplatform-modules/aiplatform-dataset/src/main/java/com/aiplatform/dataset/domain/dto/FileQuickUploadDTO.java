package com.aiplatform.dataset.domain.dto;

/**
 * 文件秒传DTO
 */
public class FileQuickUploadDTO {
    
    /** 数据集ID */
    private Long datasetId;
    
    /** 文件名称 */
    private String filename;
    
    /** 文件大小 */
    private Long fileSize;
    
    /** 文件类型 */
    private String fileType;
    
    /** 文件MD5值 */
    private String md5;
    
    /** 文件SHA1值 */
    private String sha1;

    // Getters and Setters
    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }
} 