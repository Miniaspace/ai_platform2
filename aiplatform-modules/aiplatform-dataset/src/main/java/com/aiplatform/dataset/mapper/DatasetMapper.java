package com.aiplatform.dataset.mapper;

import java.util.List;
import com.aiplatform.dataset.domain.entity.Dataset;

/**
 * 数据集管理Mapper接口
 * 
 * @author aiplatform
 */
public interface DatasetMapper
{
    /**
     * 查询数据集
     * 
     * @param datasetId 数据集ID
     * @return 数据集
     */
    public Dataset selectDatasetById(Long datasetId);

    /**
     * 查询数据集列表
     * 
     * @param dataset 数据集
     * @return 数据集集合
     */
    public List<Dataset> selectDatasetList(Dataset dataset);

    /**
     * 新增数据集
     * 
     * @param dataset 数据集
     * @return 结果
     */
    public int insertDataset(Dataset dataset);

    /**
     * 修改数据集
     * 
     * @param dataset 数据集
     * @return 结果
     */
    public int updateDataset(Dataset dataset);

    /**
     * 删除数据集
     * 
     * @param datasetId 数据集ID
     * @return 结果
     */
    public int deleteDatasetById(Long datasetId);

    /**
     * 批量删除数据集
     * 
     * @param datasetIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteDatasetByIds(Long[] datasetIds);
} 