package com.aiplatform.dataset.service;

import java.util.List;
import com.aiplatform.dataset.domain.entity.DatasetVersionChange;

/**
 * 数据集版本变更记录Service接口
 * 
 * @author aiplatform
 */
public interface IDatasetVersionChangeService {
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
     * 批量删除版本变更记录
     * 
     * @param changeIds 需要删除的变更ID数组
     * @return 结果
     */
    public int deleteDatasetVersionChangeByIds(Long[] changeIds);

    /**
     * 删除版本变更记录信息
     * 
     * @param changeId 变更ID
     * @return 结果
     */
    public int deleteDatasetVersionChangeById(Long changeId);

    /**
     * 删除版本的所有变更记录
     * 
     * @param versionId 版本ID
     * @return 结果
     */
    public int deleteByVersionId(Long versionId);

    /**
     * 记录文件新增变更
     * 
     * @param versionId 版本ID
     * @param fileId 文件ID
     * @param changeDesc 变更描述
     * @return 结果
     */
    public int recordFileAdd(Long versionId, Long fileId, String changeDesc);

    /**
     * 记录文件修改变更
     * 
     * @param versionId 版本ID
     * @param fileId 文件ID
     * @param oldFileId 原文件ID
     * @param changeDesc 变更描述
     * @return 结果
     */
    public int recordFileModify(Long versionId, Long fileId, Long oldFileId, String changeDesc);

    /**
     * 记录文件删除变更
     * 
     * @param versionId 版本ID
     * @param fileId 文件ID
     * @param changeDesc 变更描述
     * @return 结果
     */
    public int recordFileDelete(Long versionId, Long fileId, String changeDesc);
} 