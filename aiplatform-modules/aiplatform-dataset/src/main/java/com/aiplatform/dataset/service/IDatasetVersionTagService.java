package com.aiplatform.dataset.service;

import java.util.List;
import com.aiplatform.dataset.domain.entity.DatasetVersionTag;

/**
 * 数据集版本标签Service接口
 * 
 * @author aiplatform
 */
public interface IDatasetVersionTagService {
    /**
     * 查询版本标签
     * 
     * @param tagId 标签ID
     * @return 版本标签
     */
    public DatasetVersionTag selectDatasetVersionTagById(Long tagId);

    /**
     * 查询版本标签列表
     * 
     * @param versionTag 版本标签
     * @return 版本标签集合
     */
    public List<DatasetVersionTag> selectDatasetVersionTagList(DatasetVersionTag versionTag);

    /**
     * 查询版本的所有标签
     * 
     * @param versionId 版本ID
     * @return 版本标签集合
     */
    public List<DatasetVersionTag> selectTagsByVersionId(Long versionId);

    /**
     * 新增版本标签
     * 
     * @param versionTag 版本标签
     * @return 结果
     */
    public int insertDatasetVersionTag(DatasetVersionTag versionTag);

    /**
     * 批量新增版本标签
     * 
     * @param versionTags 版本标签列表
     * @return 结果
     */
    public int batchInsertDatasetVersionTag(List<DatasetVersionTag> versionTags);

    /**
     * 修改版本标签
     * 
     * @param versionTag 版本标签
     * @return 结果
     */
    public int updateDatasetVersionTag(DatasetVersionTag versionTag);

    /**
     * 批量删除版本标签
     * 
     * @param tagIds 需要删除的标签ID数组
     * @return 结果
     */
    public int deleteDatasetVersionTagByIds(Long[] tagIds);

    /**
     * 删除版本标签信息
     * 
     * @param tagId 标签ID
     * @return 结果
     */
    public int deleteDatasetVersionTagById(Long tagId);

    /**
     * 删除版本的所有标签
     * 
     * @param versionId 版本ID
     * @return 结果
     */
    public int deleteByVersionId(Long versionId);

    /**
     * 检查标签名称是否唯一
     * 
     * @param versionId 版本ID
     * @param tagName 标签名称
     * @return 结果
     */
    public boolean checkTagNameUnique(Long versionId, String tagName);

    /**
     * 根据标签名称查找版本
     *
     * @param datasetId 数据集ID
     * @param tagName 标签名称
     * @return 数据集版本标签
     */
    DatasetVersionTag findVersionByTag(Long datasetId, String tagName);
} 