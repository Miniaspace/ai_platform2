package com.aiplatform.dataset.service;

import java.util.List;
import com.aiplatform.dataset.domain.entity.DatasetVersionCompare;

/**
 * 数据集版本比较Service接口
 * 
 * @author aiplatform
 */
public interface IDatasetVersionCompareService {
    /**
     * 查询版本比较记录
     * 
     * @param compareId 比较记录ID
     * @return 版本比较记录
     */
    public DatasetVersionCompare selectDatasetVersionCompareById(Long compareId);

    /**
     * 查询版本比较记录列表
     * 
     * @param versionCompare 版本比较记录
     * @return 版本比较记录集合
     */
    public List<DatasetVersionCompare> selectDatasetVersionCompareList(DatasetVersionCompare versionCompare);

    /**
     * 查询指定版本的比较记录
     * 
     * @param sourceVersionId 源版本ID
     * @param targetVersionId 目标版本ID
     * @return 版本比较记录
     */
    public DatasetVersionCompare selectCompareByVersionIds(Long sourceVersionId, Long targetVersionId);

    /**
     * 新增版本比较记录
     * 
     * @param versionCompare 版本比较记录
     * @return 结果
     */
    public int insertDatasetVersionCompare(DatasetVersionCompare versionCompare);

    /**
     * 修改版本比较记录
     * 
     * @param versionCompare 版本比较记录
     * @return 结果
     */
    public int updateDatasetVersionCompare(DatasetVersionCompare versionCompare);

    /**
     * 批量删除版本比较记录
     * 
     * @param compareIds 需要删除的比较记录ID数组
     * @return 结果
     */
    public int deleteDatasetVersionCompareByIds(Long[] compareIds);

    /**
     * 删除版本比较记录信息
     * 
     * @param compareId 比较记录ID
     * @return 结果
     */
    public int deleteDatasetVersionCompareById(Long compareId);

    /**
     * 删除指定版本的比较记录
     * 
     * @param versionId 版本ID
     * @return 结果
     */
    public int deleteByVersionId(Long versionId);

    /**
     * 比较两个版本的差异
     * 
     * @param sourceVersionId 源版本ID
     * @param targetVersionId 目标版本ID
     * @return 比较结果
     */
    public DatasetVersionCompare compareVersions(Long sourceVersionId, Long targetVersionId);

    /**
     * 获取版本比较详情
     * 
     * @param compareId 比较记录ID
     * @return 比较详情
     */
    public DatasetVersionCompare getCompareDetails(Long compareId);
} 