package com.aiplatform.dataset.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aiplatform.common.core.utils.DateUtils;
import com.aiplatform.common.security.utils.SecurityUtils;
import com.aiplatform.dataset.domain.entity.DatasetVersion;
import com.aiplatform.dataset.domain.entity.DatasetVersionFile;
import com.aiplatform.dataset.mapper.DatasetVersionFileMapper;
import com.aiplatform.dataset.mapper.DatasetVersionMapper;
import com.aiplatform.dataset.service.IDatasetVersionFileService;
import com.aiplatform.dataset.service.IDatasetVersionChangeService;

/**
 * 数据集版本文件关联Service业务层处理
 * 
 * @author aiplatform
 */
@Service
public class DatasetVersionFileServiceImpl implements IDatasetVersionFileService {
    @Autowired
    private DatasetVersionFileMapper versionFileMapper;

    @Autowired
    private DatasetVersionMapper versionMapper;

    @Autowired
    private IDatasetVersionChangeService versionChangeService;

    /**
     * 查询版本文件关联
     * 
     * @param versionId 版本ID
     * @param fileId 文件ID
     * @return 版本文件关联
     */
    @Override
    public DatasetVersionFile selectDatasetVersionFile(Long versionId, Long fileId) {
        return versionFileMapper.selectDatasetVersionFile(versionId, fileId);
    }

    /**
     * 查询版本文件关联列表
     * 
     * @param versionFile 版本文件关联
     * @return 版本文件关联
     */
    @Override
    public List<DatasetVersionFile> selectDatasetVersionFileList(DatasetVersionFile versionFile) {
        return versionFileMapper.selectDatasetVersionFileList(versionFile);
    }

    /**
     * 查询版本的所有文件
     * 
     * @param versionId 版本ID
     * @return 版本文件关联集合
     */
    @Override
    public List<DatasetVersionFile> selectFilesByVersionId(Long versionId) {
        return versionFileMapper.selectFilesByVersionId(versionId);
    }

    /**
     * 新增版本文件关联
     * 
     * @param versionFile 版本文件关联
     * @return 结果
     */
    @Override
    @Transactional
    public int insertDatasetVersionFile(DatasetVersionFile versionFile) {
        // 设置创建者和创建时间
        String username = SecurityUtils.getUsername();
        Date now = DateUtils.getNowDate();
        versionFile.setCreateBy(username);
        versionFile.setCreateTime(now);
        
        // 记录文件添加变更
        versionChangeService.recordFileAdd(versionFile.getVersionId(), versionFile.getFileId(), "添加文件");
        
        return versionFileMapper.insertDatasetVersionFile(versionFile);
    }

    /**
     * 批量新增版本文件关联
     * 
     * @param versionFiles 版本文件关联列表
     * @return 结果
     */
    @Override
    @Transactional
    public int batchInsertDatasetVersionFile(List<DatasetVersionFile> versionFiles) {
        if (versionFiles == null || versionFiles.isEmpty()) {
            return 0;
        }

        String username = SecurityUtils.getUsername();
        for (DatasetVersionFile versionFile : versionFiles) {
            versionFile.setCreateBy(username);
            versionFile.setCreateTime(DateUtils.getNowDate());
            
            // 记录文件添加变更
            versionChangeService.recordFileAdd(versionFile.getVersionId(), versionFile.getFileId(), "批量添加文件");
        }

        return versionFileMapper.batchInsertDatasetVersionFile(versionFiles);
    }

    /**
     * 修改版本文件关联
     * 
     * @param versionFile 版本文件关联
     * @return 结果
     */
    @Override
    @Transactional
    public int updateDatasetVersionFile(DatasetVersionFile versionFile) {
        // 设置更新者和更新时间
        String username = SecurityUtils.getUsername();
        Date now = DateUtils.getNowDate();
        versionFile.setUpdateBy(username);
        versionFile.setUpdateTime(now);
        
        // 记录文件修改变更
        versionChangeService.recordFileModify(versionFile.getVersionId(), versionFile.getFileId(), 
            versionFile.getFileId(), "修改文件");
        
        return versionFileMapper.updateDatasetVersionFile(versionFile);
    }

    /**
     * 删除版本文件关联
     * 
     * @param versionId 版本ID
     * @param fileId 文件ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteDatasetVersionFile(Long versionId, Long fileId) {
        // 记录文件删除变更
        versionChangeService.recordFileDelete(versionId, fileId, "删除文件");
        
        return versionFileMapper.deleteDatasetVersionFile(versionId, fileId);
    }

    /**
     * 批量删除版本文件关联
     * 
     * @param versionId 版本ID
     * @param fileIds 文件ID数组
     * @return 结果
     */
    @Override
    @Transactional
    public int batchDeleteDatasetVersionFile(Long versionId, Long[] fileIds) {
        if (fileIds == null || fileIds.length == 0) {
            return 0;
        }

        for (Long fileId : fileIds) {
            // 记录文件删除变更
            versionChangeService.recordFileDelete(versionId, fileId, "批量删除文件");
        }

        return versionFileMapper.batchDeleteDatasetVersionFile(versionId, fileIds);
    }

    /**
     * 删除版本的所有文件关联
     * 
     * @param versionId 版本ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteByVersionId(Long versionId) {
        List<DatasetVersionFile> versionFiles = selectFilesByVersionId(versionId);
        for (DatasetVersionFile versionFile : versionFiles) {
            // 记录文件删除变更
            versionChangeService.recordFileDelete(versionId, versionFile.getFileId(), "删除版本所有文件");
        }

        return versionFileMapper.deleteByVersionId(versionId);
    }

    /**
     * 复制版本文件关联
     * 
     * @param sourceVersionId 源版本ID
     * @param targetVersionId 目标版本ID
     * @return 结果
     */
    @Override
    @Transactional
    public int copyVersionFiles(Long sourceVersionId, Long targetVersionId) {
        // 1. 查询源版本
        DatasetVersion sourceVersion = versionMapper.selectDatasetVersionById(sourceVersionId);
        if (sourceVersion == null) {
            throw new RuntimeException("源版本不存在");
        }

        // 2. 查询目标版本
        DatasetVersion targetVersion = versionMapper.selectDatasetVersionById(targetVersionId);
        if (targetVersion == null) {
            throw new RuntimeException("目标版本不存在");
        }

        // 3. 查询源版本的所有文件
        DatasetVersionFile query = new DatasetVersionFile();
        query.setVersionId(sourceVersionId);
        List<DatasetVersionFile> sourceFiles = versionFileMapper.selectDatasetVersionFileList(query);

        // 4. 复制文件记录到目标版本
        int count = 0;
        String username = SecurityUtils.getUsername();
        Date now = DateUtils.getNowDate();

        for (DatasetVersionFile sourceFile : sourceFiles) {
            // 创建新的文件记录
            DatasetVersionFile targetFile = new DatasetVersionFile();
            targetFile.setVersionId(targetVersionId);
            targetFile.setFileName(sourceFile.getFileName());
            targetFile.setFilePath(sourceFile.getFilePath());
            targetFile.setFileSize(sourceFile.getFileSize());
            targetFile.setFileType(sourceFile.getFileType());
            targetFile.setMd5(sourceFile.getMd5());
            targetFile.setSha1(sourceFile.getSha1());
            targetFile.setFileStatus(sourceFile.getFileStatus());
            targetFile.setDatasetId(targetVersion.getDatasetId());
            targetFile.setParentId(sourceFile.getFileId());
            targetFile.setCreateBy(username);
            targetFile.setCreateTime(now);

            // 设置变更类型
            if (sourceFile.getFileStatus().equals("1")) {
                // 如果源文件已删除，则标记为删除
                targetFile.setChangeType("delete");
            } else if (sourceFile.getParentId() == null) {
                // 如果源文件没有父文件，则标记为新增
                targetFile.setChangeType("add");
            } else {
                // 否则标记为修改
                targetFile.setChangeType("modify");
            }

            // 插入新文件记录
            count += versionFileMapper.insertDatasetVersionFile(targetFile);
        }

        return count;
    }

    /**
     * 比较两个版本的文件差异
     * 
     * @param sourceVersionId 源版本ID
     * @param targetVersionId 目标版本ID
     * @return 文件差异列表
     */
    @Override
    public List<DatasetVersionFile> compareVersionFiles(Long sourceVersionId, Long targetVersionId) {
        List<DatasetVersionFile> sourceFiles = selectFilesByVersionId(sourceVersionId);
        List<DatasetVersionFile> targetFiles = selectFilesByVersionId(targetVersionId);

        // 使用文件ID作为键创建映射
        Map<Long, DatasetVersionFile> sourceFileMap = sourceFiles.stream()
            .collect(Collectors.toMap(DatasetVersionFile::getFileId, file -> file));
        Map<Long, DatasetVersionFile> targetFileMap = targetFiles.stream()
            .collect(Collectors.toMap(DatasetVersionFile::getFileId, file -> file));

        List<DatasetVersionFile> diffFiles = new ArrayList<>();

        // 查找新增和修改的文件
        for (DatasetVersionFile targetFile : targetFiles) {
            DatasetVersionFile sourceFile = sourceFileMap.get(targetFile.getFileId());
            if (sourceFile == null) {
                // 新增的文件
                targetFile.setChangeType("ADD");
                diffFiles.add(targetFile);
            } else if (!sourceFile.equals(targetFile)) {
                // 修改的文件
                targetFile.setChangeType("MODIFY");
                diffFiles.add(targetFile);
            }
        }

        // 查找删除的文件
        for (DatasetVersionFile sourceFile : sourceFiles) {
            if (!targetFileMap.containsKey(sourceFile.getFileId())) {
                // 删除的文件
                sourceFile.setChangeType("DELETE");
                diffFiles.add(sourceFile);
            }
        }

        return diffFiles;
    }
} 