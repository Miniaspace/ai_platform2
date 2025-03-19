package com.aiplatform.dataset.service;

import java.util.List;
import com.aiplatform.dataset.domain.entity.DatasetVersion;

/**
 * 数据集版本Service接口
 * 
 * @author aiplatform
 */
public interface IDatasetVersionService {
    /**
     * 查询数据集版本
     * 
     * @param versionId 版本ID
     * @return 数据集版本
     */
    public DatasetVersion selectDatasetVersionById(Long versionId);

    /**
     * 查询数据集版本列表
     * 
     * @param datasetVersion 数据集版本
     * @return 数据集版本集合
     */
    public List<DatasetVersion> selectDatasetVersionList(DatasetVersion datasetVersion);

    /**
     * 查询数据集当前版本
     * 
     * @param datasetId 数据集ID
     * @return 数据集版本
     */
    public DatasetVersion selectCurrentVersion(Long datasetId);

    /**
     * 创建数据集版本
     * 
     * @param datasetVersion 数据集版本
     * @return 结果
     */
    public int createVersion(DatasetVersion datasetVersion);

    /**
     * 更新数据集版本
     * 
     * @param datasetVersion 数据集版本
     * @return 结果
     */
    public int updateVersion(DatasetVersion datasetVersion);

    /**
     * 切换数据集版本
     * 
     * @param datasetId 数据集ID
     * @param versionId 版本ID
     * @return 结果
     */
    public int switchVersion(Long datasetId, Long versionId);

    /**
     * 发布数据集版本
     * 
     * @param versionId 版本ID
     * @return 结果
     */
    public int publishVersion(Long versionId);

    /**
     * 废弃数据集版本
     * 
     * @param versionId 版本ID
     * @return 结果
     */
    public int discardVersion(Long versionId);

    /**
     * 批量删除数据集版本
     * 
     * @param versionIds 需要删除的版本ID数组
     * @return 结果
     */
    public int deleteVersionByIds(Long[] versionIds);

    /**
     * 删除数据集版本信息
     * 
     * @param versionId 版本ID
     * @return 结果
     */
    public int deleteVersionById(Long versionId);

    /**
     * 检查版本名称是否唯一
     * 
     * @param datasetId 数据集ID
     * @param versionName 版本名称
     * @return 结果
     */
    public boolean checkVersionNameUnique(Long datasetId, String versionName);

    /**
     * 获取版本树
     * 
     * @param datasetId 数据集ID
     * @return 版本树
     */
    public List<DatasetVersion> getVersionTree(Long datasetId);
} 