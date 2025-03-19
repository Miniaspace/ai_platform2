package com.aiplatform.dataset.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aiplatform.common.core.constant.Constants;
import com.aiplatform.common.core.exception.ServiceException;
import com.aiplatform.common.core.utils.DateUtils;
import com.aiplatform.common.core.utils.StringUtils;
import com.aiplatform.common.security.utils.SecurityUtils;
import com.aiplatform.dataset.domain.entity.DatasetVersion;
import com.aiplatform.dataset.mapper.DatasetVersionMapper;
import com.aiplatform.dataset.service.IDatasetVersionService;
import com.aiplatform.dataset.service.IDatasetVersionFileService;
import com.aiplatform.dataset.service.IDatasetVersionChangeService;
import com.aiplatform.dataset.service.IDatasetVersionTagService;
import com.aiplatform.dataset.service.IDatasetVersionCompareService;

/**
 * 数据集版本Service业务层处理
 * 
 * @author aiplatform
 */
@Service
public class DatasetVersionServiceImpl implements IDatasetVersionService {
    @Autowired
    private DatasetVersionMapper datasetVersionMapper;

    @Autowired
    private IDatasetVersionFileService versionFileService;

    @Autowired
    private IDatasetVersionChangeService versionChangeService;

    @Autowired
    private IDatasetVersionTagService versionTagService;

    @Autowired
    private IDatasetVersionCompareService versionCompareService;

    /**
     * 查询数据集版本
     * 
     * @param versionId 版本ID
     * @return 数据集版本
     */
    @Override
    public DatasetVersion selectDatasetVersionById(Long versionId) {
        return datasetVersionMapper.selectDatasetVersionById(versionId);
    }

    /**
     * 查询数据集版本列表
     * 
     * @param datasetVersion 数据集版本
     * @return 数据集版本
     */
    @Override
    public List<DatasetVersion> selectDatasetVersionList(DatasetVersion datasetVersion) {
        return datasetVersionMapper.selectDatasetVersionList(datasetVersion);
    }

    /**
     * 查询数据集当前版本
     * 
     * @param datasetId 数据集ID
     * @return 数据集版本
     */
    @Override
    public DatasetVersion selectCurrentVersion(Long datasetId) {
        return datasetVersionMapper.selectCurrentVersion(datasetId);
    }

    /**
     * 创建数据集版本
     * 
     * @param datasetVersion 数据集版本
     * @return 结果
     */
    @Override
    @Transactional
    public int createVersion(DatasetVersion datasetVersion) {
        // 检查版本名称是否为空
        if (datasetVersion.getDatasetId() == null) {
            throw new ServiceException("数据集ID不能为空");
        }
        if (StringUtils.isEmpty(datasetVersion.getVersionName())) {
            throw new ServiceException("版本名称不能为空");
        }

        // 检查版本名称是否唯一
        if (!checkVersionNameUnique(datasetVersion.getDatasetId(), datasetVersion.getVersionName())) {
            throw new ServiceException("版本名称已存在");
        }

        // 设置版本基本信息
        datasetVersion.setCreateBy(SecurityUtils.getUsername());
        datasetVersion.setCreateTime(DateUtils.getNowDate());
        datasetVersion.setStatus(Constants.VERSION_STATUS_DRAFT);

        // 如果是基于其他版本创建，则复制文件关联
        if (datasetVersion.getParentVersionId() != null) {
            versionFileService.copyVersionFiles(datasetVersion.getParentVersionId(), datasetVersion.getVersionId());
        }

        return datasetVersionMapper.insertDatasetVersion(datasetVersion);
    }

    /**
     * 更新数据集版本
     * 
     * @param datasetVersion 数据集版本
     * @return 结果
     */
    @Override
    public int updateVersion(DatasetVersion datasetVersion) {
        DatasetVersion dbVersion = selectDatasetVersionById(datasetVersion.getVersionId());
        if (dbVersion == null) {
            throw new ServiceException("版本不存在");
        }

        // 已发布或已废弃的版本不允许修改
        if (Constants.VERSION_STATUS_PUBLISHED.equals(dbVersion.getStatus()) 
            || Constants.VERSION_STATUS_DISCARDED.equals(dbVersion.getStatus())) {
            throw new ServiceException("当前版本状态不允许修改");
        }

        // 检查版本名称是否为空
        if (StringUtils.isEmpty(datasetVersion.getVersionName())) {
            throw new ServiceException("版本名称不能为空");
        }

        // 如果修改了版本名称，需要检查唯一性
        if (!dbVersion.getVersionName().equals(datasetVersion.getVersionName())) {
            if (!checkVersionNameUnique(datasetVersion.getDatasetId(), datasetVersion.getVersionName())) {
                throw new ServiceException("版本名称已存在");
            }
        }

        datasetVersion.setUpdateBy(SecurityUtils.getUsername());
        datasetVersion.setUpdateTime(DateUtils.getNowDate());

        return datasetVersionMapper.updateDatasetVersion(datasetVersion);
    }

    /**
     * 切换数据集版本
     * 
     * @param datasetId 数据集ID
     * @param versionId 版本ID
     * @return 结果
     */
    @Override
    @Transactional
    public int switchVersion(Long datasetId, Long versionId) {
        DatasetVersion version = selectDatasetVersionById(versionId);
        if (version == null || !datasetId.equals(version.getDatasetId())) {
            throw new ServiceException("版本不存在或不属于当前数据集");
        }

        return datasetVersionMapper.updateCurrentVersion(datasetId, versionId);
    }

    /**
     * 发布数据集版本
     * 
     * @param versionId 版本ID
     * @return 结果
     */
    @Override
    @Transactional
    public int publishVersion(Long versionId) {
        DatasetVersion version = selectDatasetVersionById(versionId);
        if (version == null) {
            throw new ServiceException("版本不存在");
        }

        // 只有草稿状态的版本可以发布
        if (!Constants.VERSION_STATUS_DRAFT.equals(version.getStatus())) {
            throw new ServiceException("只有草稿状态的版本可以发布");
        }

        version.setStatus(Constants.VERSION_STATUS_PUBLISHED);
        version.setPublishTime(DateUtils.getNowDate());
        version.setPublishBy(SecurityUtils.getUsername());
        version.setUpdateBy(SecurityUtils.getUsername());
        version.setUpdateTime(DateUtils.getNowDate());

        return datasetVersionMapper.updateDatasetVersion(version);
    }

    /**
     * 废弃数据集版本
     * 
     * @param versionId 版本ID
     * @return 结果
     */
    @Override
    @Transactional
    public int discardVersion(Long versionId) {
        DatasetVersion version = selectDatasetVersionById(versionId);
        if (version == null) {
            throw new ServiceException("版本不存在");
        }

        // 已废弃的版本不能重复废弃
        if (Constants.VERSION_STATUS_DISCARDED.equals(version.getStatus())) {
            throw new ServiceException("版本已废弃");
        }

        // 如果是当前版本，不允许废弃
        DatasetVersion currentVersion = selectCurrentVersion(version.getDatasetId());
        if (currentVersion != null && versionId.equals(currentVersion.getVersionId())) {
            throw new ServiceException("当前使用的版本不能废弃");
        }

        version.setStatus(Constants.VERSION_STATUS_DISCARDED);
        version.setUpdateBy(SecurityUtils.getUsername());
        version.setUpdateTime(DateUtils.getNowDate());

        return datasetVersionMapper.updateDatasetVersion(version);
    }

    /**
     * 批量删除数据集版本
     * 
     * @param versionIds 需要删除的版本ID数组
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteVersionByIds(Long[] versionIds) {
        if (versionIds == null || versionIds.length == 0) {
            return 0;
        }

        for (Long versionId : versionIds) {
            deleteVersionById(versionId);
        }

        return versionIds.length;
    }

    /**
     * 删除数据集版本信息
     * 
     * @param versionId 版本ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteVersionById(Long versionId) {
        // 1. 检查版本是否存在
        DatasetVersion version = selectDatasetVersionById(versionId);
        if (version == null) {
            throw new ServiceException("版本不存在");
        }

        // 2. 检查是否为当前版本
        DatasetVersion currentVersion = selectCurrentVersion(version.getDatasetId());
        if (currentVersion != null && versionId.equals(currentVersion.getVersionId())) {
            throw new ServiceException("当前使用的版本不能删除");
        }

        // 3. 检查版本状态
        if ("PUBLISHED".equals(version.getStatus()) || "1".equals(version.getStatus())) {
            throw new ServiceException("已发布的版本不能删除");
        }

        try {
            // 4. 开始删除相关数据
            // 4.1 删除版本文件关联
            versionFileService.deleteByVersionId(versionId);
            
            // 4.2 删除版本变更记录
            versionChangeService.deleteByVersionId(versionId);
            
            // 4.3 删除版本标签
            versionTagService.deleteByVersionId(versionId);
            
            // 4.4 删除版本比较记录
            versionCompareService.deleteByVersionId(versionId);

            // 5. 最后删除版本本身
            return datasetVersionMapper.deleteDatasetVersionById(versionId);
        } catch (Exception e) {
            throw new ServiceException("删除版本失败：" + e.getMessage());
        }
    }

    /**
     * 检查版本名称是否唯一
     * 
     * @param datasetId 数据集ID
     * @param versionName 版本名称
     * @return 结果
     */
    @Override
    public boolean checkVersionNameUnique(Long datasetId, String versionName) {
        if (datasetId == null || StringUtils.isEmpty(versionName)) {
            return false;
        }
        // 修复版本名称唯一性检查逻辑 - 返回true表示名称唯一，可以使用
        // 通过SQL结果显示，查询结果为0，表示没有同名版本存在
        int count = datasetVersionMapper.checkVersionNameUnique(datasetId, versionName);
        return count == 0;
    }

    /**
     * 获取版本树
     * 
     * @param datasetId 数据集ID
     * @return 版本树
     */
    @Override
    public List<DatasetVersion> getVersionTree(Long datasetId) {
        List<DatasetVersion> allVersions = datasetVersionMapper.selectVersionsByDatasetId(datasetId);
        return buildVersionTree(allVersions);
    }

    /**
     * 构建版本树
     * 
     * @param versions 所有版本列表
     * @return 版本树
     */
    private List<DatasetVersion> buildVersionTree(List<DatasetVersion> versions) {
        List<DatasetVersion> returnList = new ArrayList<DatasetVersion>();
        List<Long> tempList = new ArrayList<Long>();
        
        for (DatasetVersion version : versions) {
            tempList.add(version.getVersionId());
        }

        for (DatasetVersion version : versions) {
            // 如果是顶级节点，遍历该父节点的所有子节点
            if (!tempList.contains(version.getParentVersionId())) {
                recursionFn(versions, version);
                returnList.add(version);
            }
        }

        if (returnList.isEmpty()) {
            returnList = versions;
        }

        return returnList;
    }

    /**
     * 递归列表
     * 
     * @param versions 所有版本列表
     * @param version 当前版本
     */
    private void recursionFn(List<DatasetVersion> versions, DatasetVersion version) {
        // 得到子节点列表
        List<DatasetVersion> childList = getChildList(versions, version);
        version.setChildren(childList);
        for (DatasetVersion child : childList) {
            if (hasChild(versions, child)) {
                recursionFn(versions, child);
            }
        }
    }

    /**
     * 得到子节点列表
     * 
     * @param versions 所有版本列表
     * @param version 当前版本
     * @return 子节点列表
     */
    private List<DatasetVersion> getChildList(List<DatasetVersion> versions, DatasetVersion version) {
        List<DatasetVersion> childList = new ArrayList<DatasetVersion>();
        for (DatasetVersion child : versions) {
            if (version.getVersionId().equals(child.getParentVersionId())) {
                childList.add(child);
            }
        }
        return childList;
    }

    /**
     * 判断是否有子节点
     * 
     * @param versions 所有版本列表
     * @param version 当前版本
     * @return 结果
     */
    private boolean hasChild(List<DatasetVersion> versions, DatasetVersion version) {
        return getChildList(versions, version).size() > 0;
    }
} 