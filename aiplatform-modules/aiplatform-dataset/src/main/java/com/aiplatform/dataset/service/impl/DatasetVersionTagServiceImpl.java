package com.aiplatform.dataset.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aiplatform.common.core.exception.ServiceException;
import com.aiplatform.common.core.utils.DateUtils;
import com.aiplatform.common.core.utils.StringUtils;
import com.aiplatform.common.security.utils.SecurityUtils;
import com.aiplatform.dataset.domain.entity.DatasetVersionTag;
import com.aiplatform.dataset.mapper.DatasetVersionTagMapper;
import com.aiplatform.dataset.service.IDatasetVersionTagService;

/**
 * 数据集版本标签Service业务层处理
 * 
 * @author aiplatform
 */
@Service
public class DatasetVersionTagServiceImpl implements IDatasetVersionTagService {
    @Autowired
    private DatasetVersionTagMapper datasetVersionTagMapper;

    /**
     * 查询版本标签
     * 
     * @param tagId 标签ID
     * @return 版本标签
     */
    @Override
    public DatasetVersionTag selectDatasetVersionTagById(Long tagId) {
        return datasetVersionTagMapper.selectDatasetVersionTagById(tagId);
    }

    /**
     * 查询版本标签列表
     * 
     * @param versionTag 版本标签
     * @return 版本标签
     */
    @Override
    public List<DatasetVersionTag> selectDatasetVersionTagList(DatasetVersionTag versionTag) {
        return datasetVersionTagMapper.selectDatasetVersionTagList(versionTag);
    }

    /**
     * 查询版本的所有标签
     * 
     * @param versionId 版本ID
     * @return 版本标签集合
     */
    @Override
    public List<DatasetVersionTag> selectTagsByVersionId(Long versionId) {
        return datasetVersionTagMapper.selectTagsByVersionId(versionId);
    }

    /**
     * 新增版本标签
     * 
     * @param versionTag 版本标签
     * @return 结果
     */
    @Override
    @Transactional
    public int insertDatasetVersionTag(DatasetVersionTag versionTag) {
        // 检查标签名称是否唯一
        if (!checkTagNameUnique(versionTag.getVersionId(), versionTag.getTagName())) {
            throw new ServiceException("标签名称已存在");
        }

        versionTag.setCreateBy(SecurityUtils.getUsername());
        versionTag.setCreateTime(DateUtils.getNowDate());
        return datasetVersionTagMapper.insertDatasetVersionTag(versionTag);
    }

    /**
     * 批量新增版本标签
     * 
     * @param versionTags 版本标签列表
     * @return 结果
     */
    @Override
    @Transactional
    public int batchInsertDatasetVersionTag(List<DatasetVersionTag> versionTags) {
        if (versionTags == null || versionTags.isEmpty()) {
            return 0;
        }

        // 检查标签名称是否唯一
        for (DatasetVersionTag versionTag : versionTags) {
            if (!checkTagNameUnique(versionTag.getVersionId(), versionTag.getTagName())) {
                throw new ServiceException("标签名称[" + versionTag.getTagName() + "]已存在");
            }
        }

        String username = SecurityUtils.getUsername();
        for (DatasetVersionTag versionTag : versionTags) {
            versionTag.setCreateBy(username);
            versionTag.setCreateTime(DateUtils.getNowDate());
        }

        return datasetVersionTagMapper.batchInsertDatasetVersionTag(versionTags);
    }

    /**
     * 修改版本标签
     * 
     * @param versionTag 版本标签
     * @return 结果
     */
    @Override
    @Transactional
    public int updateDatasetVersionTag(DatasetVersionTag versionTag) {
        DatasetVersionTag dbVersionTag = selectDatasetVersionTagById(versionTag.getTagId());
        if (dbVersionTag == null) {
            throw new ServiceException("标签不存在");
        }

        // 如果修改了标签名称，需要检查唯一性
        if (!dbVersionTag.getTagName().equals(versionTag.getTagName())) {
            if (!checkTagNameUnique(versionTag.getVersionId(), versionTag.getTagName())) {
                throw new ServiceException("标签名称已存在");
            }
        }

        versionTag.setUpdateBy(SecurityUtils.getUsername());
        versionTag.setUpdateTime(DateUtils.getNowDate());

        return datasetVersionTagMapper.updateDatasetVersionTag(versionTag);
    }

    /**
     * 批量删除版本标签
     * 
     * @param tagIds 需要删除的标签ID数组
     * @return 结果
     */
    @Override
    public int deleteDatasetVersionTagByIds(Long[] tagIds) {
        return datasetVersionTagMapper.deleteDatasetVersionTagByIds(tagIds);
    }

    /**
     * 删除版本标签信息
     * 
     * @param tagId 标签ID
     * @return 结果
     */
    @Override
    public int deleteDatasetVersionTagById(Long tagId) {
        return datasetVersionTagMapper.deleteDatasetVersionTagById(tagId);
    }

    /**
     * 删除版本的所有标签
     * 
     * @param versionId 版本ID
     * @return 结果
     */
    @Override
    public int deleteByVersionId(Long versionId) {
        return datasetVersionTagMapper.deleteByVersionId(versionId);
    }

    /**
     * 检查标签名称是否唯一
     * 
     * @param versionId 版本ID
     * @param tagName 标签名称
     * @return 结果
     */
    @Override
    public boolean checkTagNameUnique(Long versionId, String tagName) {
        if (StringUtils.isEmpty(tagName)) {
            return false;
        }
        return datasetVersionTagMapper.checkTagNameUnique(versionId, tagName) == 0;
    }

    /**
     * 根据标签名称查找版本
     *
     * @param datasetId 数据集ID
     * @param tagName 标签名称
     * @return 数据集版本标签
     */
    @Override
    public DatasetVersionTag findVersionByTag(Long datasetId, String tagName) {
        return datasetVersionTagMapper.findVersionByTag(datasetId, tagName);
    }
} 