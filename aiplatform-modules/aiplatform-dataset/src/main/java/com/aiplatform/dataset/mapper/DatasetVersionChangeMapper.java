package com.aiplatform.dataset.mapper;

import java.util.List;
import com.aiplatform.dataset.domain.entity.DatasetVersionChange;

/**
 * 数据集版本变更记录Mapper接口
 * 
 * @author aiplatform
 */
public interface DatasetVersionChangeMapper {
    /**
     * 查询版本变更记录
     * 
     * @param changeId 变更ID
     * @return 版本变更记录
     */
    public DatasetVersionChange selectDatasetVersionChangeById(Long changeId);

    /**
     * 查询版本变更记录列表
     * 
     * @param versionChange 版本变更记录
     * @return 版本变更记录集合
     */
    public List<DatasetVersionChange> selectDatasetVersionChangeList(DatasetVersionChange versionChange);

    /**
     * 查询版本的所有变更记录
     * 
     * @param versionId 版本ID
     * @return 版本变更记录集合
     */
    public List<DatasetVersionChange> selectChangesByVersionId(Long versionId);

    /**
     * 新增版本变更记录
     * 
     * @param versionChange 版本变更记录
     * @return 结果
     */
    public int insertDatasetVersionChange(DatasetVersionChange versionChange);

    /**
     * 批量新增版本变更记录
     * 
     * @param versionChanges 版本变更记录列表
     * @return 结果
     */
    public int batchInsertDatasetVersionChange(List<DatasetVersionChange> versionChanges);

    /**
     * 修改版本变更记录
     * 
     * @param versionChange 版本变更记录
     * @return 结果
     */
    public int updateDatasetVersionChange(DatasetVersionChange versionChange);

    /**
     * 删除版本变更记录
     * 
     * @param changeId 变更ID
     * @return 结果
     */
    public int deleteDatasetVersionChangeById(Long changeId);

    /**
     * 批量删除版本变更记录
     * 
     * @param changeIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteDatasetVersionChangeByIds(Long[] changeIds);

    /**
     * 删除版本的所有变更记录
     * 
     * @param versionId 版本ID
     * @return 结果
     */
    public int deleteByVersionId(Long versionId);
} 