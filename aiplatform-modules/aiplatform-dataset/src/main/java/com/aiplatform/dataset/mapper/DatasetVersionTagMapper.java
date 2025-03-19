package com.aiplatform.dataset.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.aiplatform.dataset.domain.entity.DatasetVersionTag;

/**
 * 数据集版本标签Mapper接口
 * 
 * @author aiplatform
 */
@Mapper
public interface DatasetVersionTagMapper {
    /**
     * 查询数据集版本标签
     * 
     * @param tagId 标签ID
     * @return 数据集版本标签
     */
    public DatasetVersionTag selectDatasetVersionTagById(Long tagId);

    /**
     * 查询数据集版本标签列表
     * 
     * @param datasetVersionTag 数据集版本标签
     * @return 数据集版本标签集合
     */
    public List<DatasetVersionTag> selectDatasetVersionTagList(DatasetVersionTag datasetVersionTag);

    /**
     * 查询版本的所有标签
     * 
     * @param versionId 版本ID
     * @return 版本标签集合
     */
    public List<DatasetVersionTag> selectTagsByVersionId(Long versionId);

    /**
     * 新增数据集版本标签
     * 
     * @param datasetVersionTag 数据集版本标签
     * @return 结果
     */
    public int insertDatasetVersionTag(DatasetVersionTag datasetVersionTag);

    /**
     * 批量新增版本标签
     * 
     * @param versionTags 版本标签列表
     * @return 结果
     */
    public int batchInsertDatasetVersionTag(List<DatasetVersionTag> versionTags);

    /**
     * 修改数据集版本标签
     * 
     * @param datasetVersionTag 数据集版本标签
     * @return 结果
     */
    public int updateDatasetVersionTag(DatasetVersionTag datasetVersionTag);

    /**
     * 删除数据集版本标签
     * 
     * @param tagId 标签ID
     * @return 结果
     */
    public int deleteDatasetVersionTagById(Long tagId);

    /**
     * 批量删除数据集版本标签
     * 
     * @param tagIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteDatasetVersionTagByIds(Long[] tagIds);

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
    int checkTagNameUnique(@Param("versionId") Long versionId, @Param("tagName") String tagName);

    /**
     * 根据标签名称查找版本
     *
     * @param datasetId 数据集ID
     * @param tagName 标签名称
     * @return 数据集版本标签
     */
    DatasetVersionTag findVersionByTag(@Param("datasetId") Long datasetId, @Param("tagName") String tagName);
} 