package com.aiplatform.dataset.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aiplatform.common.core.exception.ServiceException;
import com.aiplatform.common.core.utils.DateUtils;
import com.aiplatform.common.security.utils.SecurityUtils;
import com.aiplatform.dataset.domain.entity.DatasetVersionCompare;
import com.aiplatform.dataset.mapper.DatasetVersionCompareMapper;
import com.aiplatform.dataset.service.IDatasetVersionCompareService;
import com.aiplatform.dataset.service.IDatasetVersionFileService;

/**
 * 数据集版本比较Service业务层处理
 * 
 * @author aiplatform
 */
@Service
public class DatasetVersionCompareServiceImpl implements IDatasetVersionCompareService {
    @Autowired
    private DatasetVersionCompareMapper datasetVersionCompareMapper;

    @Autowired
    private IDatasetVersionFileService versionFileService;

    /**
     * 查询版本比较记录
     * 
     * @param compareId 比较记录ID
     * @return 版本比较记录
     */
    @Override
    public DatasetVersionCompare selectDatasetVersionCompareById(Long compareId) {
        return datasetVersionCompareMapper.selectDatasetVersionCompareById(compareId);
    }

    /**
     * 查询版本比较记录列表
     * 
     * @param versionCompare 版本比较记录
     * @return 版本比较记录
     */
    @Override
    public List<DatasetVersionCompare> selectDatasetVersionCompareList(DatasetVersionCompare versionCompare) {
        return datasetVersionCompareMapper.selectDatasetVersionCompareList(versionCompare);
    }

    /**
     * 查询指定版本的比较记录
     * 
     * @param sourceVersionId 源版本ID
     * @param targetVersionId 目标版本ID
     * @return 版本比较记录
     */
    @Override
    public DatasetVersionCompare selectCompareByVersionIds(Long sourceVersionId, Long targetVersionId) {
        return datasetVersionCompareMapper.selectCompareByVersionIds(sourceVersionId, targetVersionId);
    }

    /**
     * 新增版本比较记录
     * 
     * @param versionCompare 版本比较记录
     * @return 结果
     */
    @Override
    @Transactional
    public int insertDatasetVersionCompare(DatasetVersionCompare versionCompare) {
        // 检查是否已存在相同版本的比较记录
        DatasetVersionCompare existingCompare = selectCompareByVersionIds(
            versionCompare.getSourceVersionId(), versionCompare.getTargetVersionId());
        if (existingCompare != null) {
            throw new ServiceException("已存在相同版本的比较记录");
        }

        versionCompare.setCreateBy(SecurityUtils.getUsername());
        versionCompare.setCreateTime(DateUtils.getNowDate());

        // 执行版本比较并保存结果
        compareVersions(versionCompare.getSourceVersionId(), versionCompare.getTargetVersionId());

        return datasetVersionCompareMapper.insertDatasetVersionCompare(versionCompare);
    }

    /**
     * 修改版本比较记录
     * 
     * @param versionCompare 版本比较记录
     * @return 结果
     */
    @Override
    @Transactional
    public int updateDatasetVersionCompare(DatasetVersionCompare versionCompare) {
        versionCompare.setUpdateBy(SecurityUtils.getUsername());
        versionCompare.setUpdateTime(DateUtils.getNowDate());

        // 重新执行版本比较并更新结果
        compareVersions(versionCompare.getSourceVersionId(), versionCompare.getTargetVersionId());

        return datasetVersionCompareMapper.updateDatasetVersionCompare(versionCompare);
    }

    /**
     * 批量删除版本比较记录
     * 
     * @param compareIds 需要删除的比较记录ID数组
     * @return 结果
     */
    @Override
    public int deleteDatasetVersionCompareByIds(Long[] compareIds) {
        return datasetVersionCompareMapper.deleteDatasetVersionCompareByIds(compareIds);
    }

    /**
     * 删除版本比较记录信息
     * 
     * @param compareId 比较记录ID
     * @return 结果
     */
    @Override
    public int deleteDatasetVersionCompareById(Long compareId) {
        return datasetVersionCompareMapper.deleteDatasetVersionCompareById(compareId);
    }

    /**
     * 删除指定版本的比较记录
     * 
     * @param versionId 版本ID
     * @return 结果
     */
    @Override
    public int deleteByVersionId(Long versionId) {
        return datasetVersionCompareMapper.deleteByVersionId(versionId);
    }

    /**
     * 比较两个版本的差异
     * 
     * @param sourceVersionId 源版本ID
     * @param targetVersionId 目标版本ID
     * @return 比较结果
     */
    @Override
    @Transactional
    public DatasetVersionCompare compareVersions(Long sourceVersionId, Long targetVersionId) {
        // 检查比较记录是否存在
        DatasetVersionCompare versionCompare = selectCompareByVersionIds(sourceVersionId, targetVersionId);
        if (versionCompare == null) {
            versionCompare = new DatasetVersionCompare();
            versionCompare.setSourceVersionId(sourceVersionId);
            versionCompare.setTargetVersionId(targetVersionId);
            versionCompare.setCreateBy(SecurityUtils.getUsername());
            versionCompare.setCreateTime(DateUtils.getNowDate());
        } else {
            versionCompare.setUpdateBy(SecurityUtils.getUsername());
            versionCompare.setUpdateTime(DateUtils.getNowDate());
        }

        // 执行文件比较
        versionFileService.compareVersionFiles(sourceVersionId, targetVersionId);

        // 更新比较状态
        versionCompare.setCompareStatus("1"); // 1表示比较完成
        
        // 保存或更新比较记录
        if (versionCompare.getCompareId() == null) {
            datasetVersionCompareMapper.insertDatasetVersionCompare(versionCompare);
        } else {
            datasetVersionCompareMapper.updateDatasetVersionCompare(versionCompare);
        }

        return versionCompare;
    }

    /**
     * 获取版本比较详情
     * 
     * @param compareId 比较记录ID
     * @return 比较详情
     */
    @Override
    public DatasetVersionCompare getCompareDetails(Long compareId) {
        DatasetVersionCompare versionCompare = selectDatasetVersionCompareById(compareId);
        if (versionCompare == null) {
            throw new ServiceException("比较记录不存在");
        }

        // 重新执行文件比较以获取最新结果
        versionFileService.compareVersionFiles(
            versionCompare.getSourceVersionId(), versionCompare.getTargetVersionId());

        return versionCompare;
    }
} 