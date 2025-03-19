package com.aiplatform.dataset.service;

import java.util.List;
import com.aiplatform.dataset.domain.entity.DatasetVersionFile;

/**
 * 数据集版本文件关联Service接口
 * 
 * @author aiplatform
 */
public interface IDatasetVersionFileService {
    /**
     * 查询版本文件关联
     * 
     * @param versionId 版本ID
     * @param fileId 文件ID
     * @return 版本文件关联
     */
    public DatasetVersionFile selectDatasetVersionFile(Long versionId, Long fileId);

    /**
     * 查询版本文件关联列表
     * 
     * @param versionFile 版本文件关联
     * @return 版本文件关联集合
     */
    public List<DatasetVersionFile> selectDatasetVersionFileList(DatasetVersionFile versionFile);

    /**
     * 查询版本的所有文件
     * 
     * @param versionId 版本ID
     * @return 版本文件关联集合
     */
    public List<DatasetVersionFile> selectFilesByVersionId(Long versionId);

    /**
     * 新增版本文件关联
     * 
     * @param versionFile 版本文件关联
     * @return 结果
     */
    public int insertDatasetVersionFile(DatasetVersionFile versionFile);

    /**
     * 批量新增版本文件关联
     * 
     * @param versionFiles 版本文件关联列表
     * @return 结果
     */
    public int batchInsertDatasetVersionFile(List<DatasetVersionFile> versionFiles);

    /**
     * 修改版本文件关联
     * 
     * @param versionFile 版本文件关联
     * @return 结果
     */
    public int updateDatasetVersionFile(DatasetVersionFile versionFile);

    /**
     * 删除版本文件关联
     * 
     * @param versionId 版本ID
     * @param fileId 文件ID
     * @return 结果
     */
    public int deleteDatasetVersionFile(Long versionId, Long fileId);

    /**
     * 批量删除版本文件关联
     * 
     * @param versionId 版本ID
     * @param fileIds 文件ID数组
     * @return 结果
     */
    public int batchDeleteDatasetVersionFile(Long versionId, Long[] fileIds);

    /**
     * 删除版本的所有文件关联
     * 
     * @param versionId 版本ID
     * @return 结果
     */
    public int deleteByVersionId(Long versionId);

    /**
     * 复制版本文件关联
     * 
     * @param sourceVersionId 源版本ID
     * @param targetVersionId 目标版本ID
     * @return 结果
     */
    public int copyVersionFiles(Long sourceVersionId, Long targetVersionId);

    /**
     * 比较两个版本的文件差异
     * 
     * @param sourceVersionId 源版本ID
     * @param targetVersionId 目标版本ID
     * @return 文件差异列表
     */
    public List<DatasetVersionFile> compareVersionFiles(Long sourceVersionId, Long targetVersionId);
} 