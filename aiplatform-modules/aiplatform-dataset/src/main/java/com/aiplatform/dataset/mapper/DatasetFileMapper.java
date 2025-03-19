package com.aiplatform.dataset.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.aiplatform.dataset.domain.entity.DatasetFile;

/**
 * 数据集文件Mapper接口
 *
 * @author aiplatform
 */
@Mapper
public interface DatasetFileMapper {
    /**
     * 查询数据集文件列表
     *
     * @param datasetFile 数据集文件
     * @return 数据集文件列表
     */
    List<DatasetFile> selectDatasetFileList(DatasetFile datasetFile);

    /**
     * 根据ID查询数据集文件
     *
     * @param fileId 文件ID
     * @return 数据集文件
     */
    DatasetFile selectDatasetFileById(Long fileId);

    /**
     * 根据哈希值查询数据集文件
     *
     * @param md5 MD5值
     * @param sha1 SHA1值
     * @return 数据集文件
     */
    DatasetFile selectDatasetFileByHash(String md5, String sha1);

    /**
     * 根据MD5查询数据集文件
     *
     * @param md5 MD5值
     * @return 数据集文件
     */
    DatasetFile selectDatasetFileByMd5(String md5);

    /**
     * 新增数据集文件
     *
     * @param datasetFile 数据集文件
     * @return 影响行数
     */
    int insert(DatasetFile datasetFile);

    /**
     * 修改数据集文件
     *
     * @param datasetFile 数据集文件
     * @return 影响行数
     */
    int update(DatasetFile datasetFile);

    /**
     * 删除数据集文件
     *
     * @param fileId 文件ID
     * @return 影响行数
     */
    int deleteById(Long fileId);

    /**
     * 批量删除数据集文件
     *
     * @param fileIds 文件ID数组
     * @return 影响行数
     */
    int deleteByIds(Long[] fileIds);

    /**
     * 根据文件路径查询文件
     *
     * @param filePath 文件路径
     * @return 数据集文件
     */
    DatasetFile selectDatasetFileByPath(String filePath);

    /**
     * 根据版本ID查询文件列表
     *
     * @param versionId 版本ID
     * @return 数据集文件集合
     */
    List<DatasetFile> selectDatasetFileByVersionId(Long versionId);

    /**
     * 根据父目录ID查询文件列表
     *
     * @param parentId 父目录ID
     * @return 数据集文件集合
     */
    List<DatasetFile> selectDatasetFileByParentId(Long parentId);

    /**
     * 根据版本ID和文件路径查询文件
     *
     * @param versionId 版本ID
     * @param filePath 文件路径
     * @return 数据集文件
     */
    DatasetFile selectDatasetFileByVersionIdAndPath(@Param("versionId") Long versionId, @Param("filePath") String filePath);

    /**
     * 根据版本ID和文件名查询文件
     *
     * @param versionId 版本ID
     * @param fileName 文件名
     * @return 数据集文件
     */
    DatasetFile selectDatasetFileByVersionIdAndName(@Param("versionId") Long versionId, @Param("fileName") String fileName);
} 