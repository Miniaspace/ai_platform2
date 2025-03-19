package com.aiplatform.dataset.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aiplatform.dataset.mapper.DatasetMapper;
import com.aiplatform.dataset.domain.entity.Dataset;
import com.aiplatform.dataset.service.IDatasetService;

/**
 * 数据集管理Service业务层处理
 * 
 * @author aiplatform
 */
@Service
public class DatasetServiceImpl implements IDatasetService
{
    @Autowired
    private DatasetMapper datasetMapper;

    /**
     * 查询数据集
     * 
     * @param datasetId 数据集ID
     * @return 数据集
     */
    @Override
    public Dataset selectDatasetById(Long datasetId)
    {
        return datasetMapper.selectDatasetById(datasetId);
    }

    /**
     * 查询数据集列表
     * 
     * @param dataset 数据集
     * @return 数据集
     */
    @Override
    public List<Dataset> selectDatasetList(Dataset dataset)
    {
        return datasetMapper.selectDatasetList(dataset);
    }

    /**
     * 新增数据集
     * 
     * @param dataset 数据集
     * @return 结果
     */
    @Override
    public int insertDataset(Dataset dataset)
    {
        return datasetMapper.insertDataset(dataset);
    }

    /**
     * 修改数据集
     * 
     * @param dataset 数据集
     * @return 结果
     */
    @Override
    public int updateDataset(Dataset dataset)
    {
        return datasetMapper.updateDataset(dataset);
    }

    /**
     * 批量删除数据集
     * 
     * @param datasetIds 需要删除的数据集ID
     * @return 结果
     */
    @Override
    public int deleteDatasetByIds(Long[] datasetIds)
    {
        return datasetMapper.deleteDatasetByIds(datasetIds);
    }

    /**
     * 删除数据集信息
     * 
     * @param datasetId 数据集ID
     * @return 结果
     */
    @Override
    public int deleteDatasetById(Long datasetId)
    {
        return datasetMapper.deleteDatasetById(datasetId);
    }
} 