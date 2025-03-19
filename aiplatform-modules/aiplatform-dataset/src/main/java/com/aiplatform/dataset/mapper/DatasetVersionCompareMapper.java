package com.aiplatform.dataset.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.aiplatform.dataset.domain.entity.DatasetVersionCompare;

/**
 * 数据集版本比较Mapper接口
 *
 * @author aiplatform
 */
@Mapper
public interface DatasetVersionCompareMapper {
    /**
     * 查询数据集版本比较
     *
     * @param compareId 比较ID
     * @return 数据集版本比较
     */
    DatasetVersionCompare selectDatasetVersionCompareById(Long compareId);

    /**
     * 查询数据集版本比较列表
     *
     * @param datasetVersionCompare 数据集版本比较
     * @return 数据集版本比较集合
     */
    List<DatasetVersionCompare> selectDatasetVersionCompareList(DatasetVersionCompare datasetVersionCompare);

    /**
     * 查询源版本的所有比较记录
     * 
     * @param sourceVersionId 源版本ID
     * @return 版本比较记录集合
     */
    public List<DatasetVersionCompare> selectComparesBySourceVersionId(Long sourceVersionId);

    /**
     * 查询目标版本的所有比较记录
     * 
     * @param targetVersionId 目标版本ID
     * @return 版本比较记录集合
     */
    public List<DatasetVersionCompare> selectComparesByTargetVersionId(Long targetVersionId);

    /**
     * 新增数据集版本比较
     *
     * @param datasetVersionCompare 数据集版本比较
     * @return 结果
     */
    int insertDatasetVersionCompare(DatasetVersionCompare datasetVersionCompare);

    /**
     * 修改数据集版本比较
     *
     * @param datasetVersionCompare 数据集版本比较
     * @return 结果
     */
    int updateDatasetVersionCompare(DatasetVersionCompare datasetVersionCompare);

    /**
     * 删除数据集版本比较
     *
     * @param compareId 比较ID
     * @return 结果
     */
    int deleteDatasetVersionCompareById(Long compareId);

    /**
     * 批量删除数据集版本比较
     *
     * @param compareIds 需要删除的数据ID
     * @return 结果
     */
    int deleteDatasetVersionCompareByIds(Long[] compareIds);

    /**
     * 删除版本的所有比较记录
     * 
     * @param versionId 版本ID
     * @return 结果
     */
    public int deleteByVersionId(Long versionId);

    /**
     * 根据版本ID查询比较记录
     *
     * @param sourceVersionId 源版本ID
     * @param targetVersionId 目标版本ID
     * @return 数据集版本比较
     */
    DatasetVersionCompare selectCompareByVersionIds(@Param("sourceVersionId") Long sourceVersionId, @Param("targetVersionId") Long targetVersionId);
} 