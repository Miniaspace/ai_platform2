package com.aiplatform.dataset.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.aiplatform.dataset.domain.entity.DatasetVersion;

/**
 * 数据集版本Mapper接口
 *
 * @author aiplatform
 */
@Mapper
public interface DatasetVersionMapper {
    /**
     * 查询数据集版本
     *
     * @param versionId 版本ID
     * @return 数据集版本
     */
    DatasetVersion selectDatasetVersionById(Long versionId);

    /**
     * 查询数据集版本列表
     *
     * @param datasetVersion 数据集版本
     * @return 数据集版本集合
     */
    List<DatasetVersion> selectDatasetVersionList(DatasetVersion datasetVersion);

    /**
     * 查询数据集的当前版本
     *
     * @param datasetId 数据集ID
     * @return 数据集版本
     */
    DatasetVersion selectCurrentVersion(Long datasetId);

    /**
     * 新增数据集版本
     *
     * @param datasetVersion 数据集版本
     * @return 结果
     */
    int insertDatasetVersion(DatasetVersion datasetVersion);

    /**
     * 修改数据集版本
     *
     * @param datasetVersion 数据集版本
     * @return 结果
     */
    int updateDatasetVersion(DatasetVersion datasetVersion);

    /**
     * 删除数据集版本
     *
     * @param versionId 版本ID
     * @return 结果
     */
    int deleteDatasetVersionById(Long versionId);

    /**
     * 批量删除数据集版本
     *
     * @param versionIds 需要删除的数据ID
     * @return 结果
     */
    int deleteDatasetVersionByIds(Long[] versionIds);

    /**
     * 更新数据集的当前版本
     *
     * @param datasetId 数据集ID
     * @param versionId 版本ID
     * @return 结果
     */
    int updateCurrentVersion(Long datasetId, Long versionId);

    /**
     * 切换版本
     *
     * @param datasetId 数据集ID
     * @param versionId 版本ID
     * @return 结果
     */
    int switchVersion(@Param("datasetId") Long datasetId, @Param("versionId") Long versionId);

    /**
     * 检查版本名称是否唯一
     *
     * @param datasetId 数据集ID
     * @param versionName 版本名称
     * @return 结果
     */
    int checkVersionNameUnique(@Param("datasetId") Long datasetId, @Param("versionName") String versionName);

    /**
     * 查询数据集的所有版本
     *
     * @param datasetId 数据集ID
     * @return 数据集版本列表
     */
    List<DatasetVersion> selectVersionsByDatasetId(Long datasetId);
} 