package com.aiplatform.dataset.service;

import java.util.List;
import com.aiplatform.dataset.domain.entity.Dataset;

/**
 * 数据集管理Service接口
 * 
 * @author aiplatform
 */
public interface IDatasetService
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
     * 创建数据集
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
     * 批量删除数据集
     * 
     * @param datasetIds 需要删除的数据集ID
     * @return 结果
     */
    public int deleteDatasetByIds(Long[] datasetIds);

    /**
     * 删除数据集信息
     * 
     * @param datasetId 数据集ID
     * @return 结果
     */
    public int deleteDatasetById(Long datasetId);
} 