package com.aiplatform.dataset.domain.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.aiplatform.common.core.annotation.Excel;
import com.aiplatform.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 数据集文件对象 dataset_file
 * 
 * @author aiplatform
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DatasetFile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 文件ID */
    private Long fileId;

    /** 数据集ID */
    @Excel(name = "数据集ID")
    private Long datasetId;

    /** 文件名称 */
    @Excel(name = "文件名称")
    private String fileName;

    /** 文件路径 */
    @Excel(name = "文件路径")
    private String filePath;

    /** 文件类型 */
    @Excel(name = "文件类型")
    private String fileType;

    /** 文件大小（字节） */
    @Excel(name = "文件大小")
    private Long fileSize;

    /** 文件MD5 */
    @Excel(name = "文件MD5")
    private String md5;

    /** 文件SHA1 */
    private String sha1;

    /** 标注状态（0未标注 1已标注） */
    @Excel(name = "标注状态", readConverterExp = "0=未标注,1=已标注")
    private String labelStatus;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 文件状态（0正常 1删除） */
    private String fileStatus;

    /** 版本ID */
    private Long versionId;

    /** 父ID */
    private Long parentId;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
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

    public String getLabelStatus() {
        return labelStatus;
    }

    public void setLabelStatus(String labelStatus) {
        this.labelStatus = labelStatus;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(String fileStatus) {
        this.fileStatus = fileStatus;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("fileId", getFileId())
            .append("datasetId", getDatasetId())
            .append("fileName", getFileName())
            .append("filePath", getFilePath())
            .append("fileType", getFileType())
            .append("fileSize", getFileSize())
            .append("md5", getMd5())
            .append("sha1", getSha1())
            .append("labelStatus", getLabelStatus())
            .append("delFlag", getDelFlag())
            .append("fileStatus", getFileStatus())
            .append("versionId", getVersionId())
            .append("parentId", getParentId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }

    public static DatasetFileBuilder builder() {
        return new DatasetFileBuilder();
    }

    public static class DatasetFileBuilder {
        private DatasetFile file;

        public DatasetFileBuilder() {
            file = new DatasetFile();
        }

        public DatasetFileBuilder fileId(Long fileId) {
            file.setFileId(fileId);
            return this;
        }

        public DatasetFileBuilder datasetId(Long datasetId) {
            file.setDatasetId(datasetId);
            return this;
        }

        public DatasetFileBuilder fileName(String fileName) {
            file.setFileName(fileName);
            return this;
        }

        public DatasetFileBuilder filePath(String filePath) {
            file.setFilePath(filePath);
            return this;
        }

        public DatasetFileBuilder fileSize(Long fileSize) {
            file.setFileSize(fileSize);
            return this;
        }

        public DatasetFileBuilder fileType(String fileType) {
            file.setFileType(fileType);
            return this;
        }

        public DatasetFileBuilder md5(String md5) {
            file.setMd5(md5);
            return this;
        }

        public DatasetFileBuilder sha1(String sha1) {
            file.setSha1(sha1);
            return this;
        }

        public DatasetFileBuilder labelStatus(String labelStatus) {
            file.setLabelStatus(labelStatus);
            return this;
        }

        public DatasetFileBuilder delFlag(String delFlag) {
            file.setDelFlag(delFlag);
            return this;
        }

        public DatasetFileBuilder fileStatus(String fileStatus) {
            file.setFileStatus(fileStatus);
            return this;
        }

        public DatasetFileBuilder versionId(Long versionId) {
            file.setVersionId(versionId);
            return this;
        }

        public DatasetFileBuilder parentId(Long parentId) {
            file.setParentId(parentId);
            return this;
        }

        public DatasetFile build() {
            return file;
        }
    }
} 