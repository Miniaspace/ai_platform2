package com.aiplatform.dataset.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aiplatform.common.core.constant.Constants;
import com.aiplatform.common.core.utils.DateUtils;
import com.aiplatform.common.security.utils.SecurityUtils;
import com.aiplatform.dataset.domain.entity.DatasetVersionChange;
import com.aiplatform.dataset.mapper.DatasetVersionChangeMapper;
import com.aiplatform.dataset.service.IDatasetVersionChangeService;

/**
 * 数据集版本变更记录Service业务层处理
 * 
 * @author aiplatform
 */
@Service
public class DatasetVersionChangeServiceImpl implements IDatasetVersionChangeService {
    @Autowired
    private DatasetVersionChangeMapper datasetVersionChangeMapper;

    /**
     * 查询版本变更记录
     * 
     * @param changeId 变更ID
     * @return 版本变更记录
     */
    @Override
    public DatasetVersionChange selectDatasetVersionChangeById(Long changeId) {
        return datasetVersionChangeMapper.selectDatasetVersionChangeById(changeId);
    }

    /**
     * 查询版本变更记录列表
     * 
     * @param versionChange 版本变更记录
     * @return 版本变更记录
     */
    @Override
    public List<DatasetVersionChange> selectDatasetVersionChangeList(DatasetVersionChange versionChange) {
        return datasetVersionChangeMapper.selectDatasetVersionChangeList(versionChange);
    }

    /**
     * 查询版本的所有变更记录
     * 
     * @param versionId 版本ID
     * @return 版本变更记录集合
     */
    @Override
    public List<DatasetVersionChange> selectChangesByVersionId(Long versionId) {
        return datasetVersionChangeMapper.selectChangesByVersionId(versionId);
    }

    /**
     * 新增版本变更记录
     * 
     * @param versionChange 版本变更记录
     * @return 结果
     */
    @Override
    public int insertDatasetVersionChange(DatasetVersionChange versionChange) {
        versionChange.setCreateBy(SecurityUtils.getUsername());
        versionChange.setCreateTime(DateUtils.getNowDate());
        return datasetVersionChangeMapper.insertDatasetVersionChange(versionChange);
    }

    /**
     * 批量新增版本变更记录
     * 
     * @param versionChanges 版本变更记录列表
     * @return 结果
     */
    @Override
    public int batchInsertDatasetVersionChange(List<DatasetVersionChange> versionChanges) {
        if (versionChanges == null || versionChanges.isEmpty()) {
            return 0;
        }

        String username = SecurityUtils.getUsername();
        for (DatasetVersionChange versionChange : versionChanges) {
            versionChange.setCreateBy(username);
            versionChange.setCreateTime(DateUtils.getNowDate());
        }

        return datasetVersionChangeMapper.batchInsertDatasetVersionChange(versionChanges);
    }

    /**
     * 修改版本变更记录
     * 
     * @param versionChange 版本变更记录
     * @return 结果
     */
    @Override
    public int updateDatasetVersionChange(DatasetVersionChange versionChange) {
        versionChange.setUpdateBy(SecurityUtils.getUsername());
        versionChange.setUpdateTime(DateUtils.getNowDate());
        return datasetVersionChangeMapper.updateDatasetVersionChange(versionChange);
    }

    /**
     * 批量删除版本变更记录
     * 
     * @param changeIds 需要删除的变更ID数组
     * @return 结果
     */
    @Override
    public int deleteDatasetVersionChangeByIds(Long[] changeIds) {
        return datasetVersionChangeMapper.deleteDatasetVersionChangeByIds(changeIds);
    }

    /**
     * 删除版本变更记录信息
     * 
     * @param changeId 变更ID
     * @return 结果
     */
    @Override
    public int deleteDatasetVersionChangeById(Long changeId) {
        return datasetVersionChangeMapper.deleteDatasetVersionChangeById(changeId);
    }

    /**
     * 删除版本的所有变更记录
     * 
     * @param versionId 版本ID
     * @return 结果
     */
    @Override
    public int deleteByVersionId(Long versionId) {
        return datasetVersionChangeMapper.deleteByVersionId(versionId);
    }

    /**
     * 记录文件新增变更
     * 
     * @param versionId 版本ID
     * @param fileId 文件ID
     * @param changeDesc 变更描述
     * @return 结果
     */
    @Override
    @Transactional
    public int recordFileAdd(Long versionId, Long fileId, String changeDesc) {
        DatasetVersionChange versionChange = new DatasetVersionChange();
        versionChange.setVersionId(versionId);
        versionChange.setFileId(fileId);
        versionChange.setChangeType(Constants.CHANGE_TYPE_ADD);
        versionChange.setChangeDesc(changeDesc);
        versionChange.setCreateBy(SecurityUtils.getUsername());
        versionChange.setCreateTime(DateUtils.getNowDate());
        return datasetVersionChangeMapper.insertDatasetVersionChange(versionChange);
    }

    /**
     * 记录文件修改变更
     * 
     * @param versionId 版本ID
     * @param fileId 文件ID
     * @param oldFileId 原文件ID
     * @param changeDesc 变更描述
     * @return 结果
     */
    @Override
    @Transactional
    public int recordFileModify(Long versionId, Long fileId, Long oldFileId, String changeDesc) {
        DatasetVersionChange versionChange = new DatasetVersionChange();
        versionChange.setVersionId(versionId);
        versionChange.setFileId(fileId);
        versionChange.setOldFileId(oldFileId);
        versionChange.setChangeType(Constants.CHANGE_TYPE_MODIFY);
        versionChange.setChangeDesc(changeDesc);
        versionChange.setCreateBy(SecurityUtils.getUsername());
        versionChange.setCreateTime(DateUtils.getNowDate());
        return datasetVersionChangeMapper.insertDatasetVersionChange(versionChange);
    }

    /**
     * 记录文件删除变更
     * 
     * @param versionId 版本ID
     * @param fileId 文件ID
     * @param changeDesc 变更描述
     * @return 结果
     */
    @Override
    @Transactional
    public int recordFileDelete(Long versionId, Long fileId, String changeDesc) {
        DatasetVersionChange versionChange = new DatasetVersionChange();
        versionChange.setVersionId(versionId);
        versionChange.setFileId(fileId);
        versionChange.setChangeType(Constants.CHANGE_TYPE_DELETE);
        versionChange.setChangeDesc(changeDesc);
        versionChange.setCreateBy(SecurityUtils.getUsername());
        versionChange.setCreateTime(DateUtils.getNowDate());
        return datasetVersionChangeMapper.insertDatasetVersionChange(versionChange);
    }
} 