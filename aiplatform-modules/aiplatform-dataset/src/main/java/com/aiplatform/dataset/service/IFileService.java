package com.aiplatform.dataset.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.aiplatform.common.core.web.domain.AjaxResult;
import com.aiplatform.dataset.domain.dto.FileUploadDTO;
import com.aiplatform.dataset.domain.dto.UploadProgressDTO;
import com.aiplatform.dataset.domain.dto.ResumeUploadDTO;
import com.aiplatform.dataset.domain.dto.FileQuickUploadDTO;
import com.aiplatform.dataset.domain.entity.DatasetVersionFile;

/**
 * 文件服务接口
 */
public interface IFileService {

    /**
     * 获取数据集版本文件
     *
     * @param fileId 文件ID
     * @return 数据集版本文件
     */
    DatasetVersionFile getDatasetVersionFile(Long fileId);

    /**
     * 获取文件下载URL
     *
     * @param fileId 文件ID
     * @return 下载URL
     */
    String getDownloadUrl(Long fileId);

    /**
     * 下载文件
     *
     * @param fileId 文件ID
     * @param response HTTP响应
     */
    void downloadFile(Long fileId, HttpServletResponse response);

    /**
     * 预览文件
     *
     * @param fileId 文件ID
     * @return 预览URL
     */
    String previewFile(Long fileId);

    /**
     * 获取文件列表
     *
     * @param datasetId 数据集ID
     * @return 文件列表
     */
    List<DatasetVersionFile> listFiles(Long datasetId);

    /**
     * 插入数据集文件
     *
     * @param versionFile 数据集版本文件
     * @return 插入的文件
     */
    void insertDatasetFile(DatasetVersionFile versionFile);

    /**
     * 上传文件分片
     *
     * @param identifier 文件标识
     * @param chunkNumber 分片编号
     * @param file 分片文件
     * @throws IOException IO异常
     */
    void uploadChunk(String identifier, Integer chunkNumber, MultipartFile file) throws IOException;

    /**
     * 获取文件预览URL
     *
     * @param fileId 文件ID
     * @param previewType 预览类型
     * @return 预览URL
     */
    String getPreviewUrl(Long fileId, String previewType);

    /**
     * 获取文件预览信息
     *
     * @param fileId 文件ID
     * @return 预览信息
     */
    Map<String, Object> getPreviewInfo(Long fileId);

    /**
     * 获取上传进度
     *
     * @param identifier 文件标识
     * @return 上传进度
     */
    UploadProgressDTO getUploadProgress(String identifier);

    /**
     * 获取上传进度
     *
     * @param identifier 文件标识
     * @param fileName 文件名
     * @return 上传进度
     */
    UploadProgressDTO getUploadProgress(String identifier, String fileName);

    /**
     * 获取上传状态
     *
     * @param identifier 文件标识
     * @param totalChunks 总分片数
     * @return 上传状态
     */
    ResumeUploadDTO getUploadStatus(String identifier, Integer totalChunks);

    /**
     * 获取上传状态
     *
     * @param identifier 文件标识
     * @param fileName 文件名
     * @param totalChunks 总分片数
     * @return 上传状态
     */
    ResumeUploadDTO getUploadStatus(String identifier, String fileName, Integer totalChunks);

    /**
     * 检查分片是否存在
     *
     * @param uploadDTO 上传信息
     * @return 检查结果
     */
    AjaxResult checkChunkExist(FileUploadDTO uploadDTO);

    /**
     * 上传分片
     *
     * @param file 分片文件
     * @param uploadDTO 上传信息
     * @return 上传结果
     */
    AjaxResult uploadChunk(MultipartFile file, FileUploadDTO uploadDTO);

    /**
     * 合并分片
     *
     * @param uploadDTO 上传信息
     * @return 合并结果
     */
    AjaxResult mergeChunks(FileUploadDTO uploadDTO);

    /**
     * 清理未完成的上传
     *
     * @param identifier 文件标识
     * @return 清理结果
     */
    AjaxResult cleanIncompleteUpload(String identifier);

    /**
     * 检查文件是否存在
     *
     * @param quickUploadDTO 秒传信息
     * @return 检查结果
     */
    AjaxResult checkFileExists(FileQuickUploadDTO quickUploadDTO);

    /**
     * 秒传文件
     *
     * @param quickUploadDTO 秒传信息
     * @return 秒传结果
     */
    AjaxResult quickUpload(FileQuickUploadDTO quickUploadDTO);

    /**
     * 检查分片是否存在
     *
     * @param identifier 文件标识
     * @param chunkNumber 分片编号
     * @return 是否存在
     */
    boolean checkChunkExists(String identifier, Integer chunkNumber);

    /**
     * 合并分片
     *
     * @param identifier 文件标识
     * @param fileName 文件名
     * @param totalChunks 总分片数
     * @throws IOException IO异常
     */
    void mergeChunks(String identifier, String fileName, Integer totalChunks) throws IOException;

    /**
     * 删除文件
     *
     * @param fileIds 文件ID数组
     * @return 删除成功的文件数量
     */
    int deleteFiles(Long[] fileIds);

    /**
     * 保存文件信息
     */
    DatasetVersionFile saveFileInfo(String identifier, String fileName, Long versionId, Long parentId);

    /**
     * 获取文件URL
     */
    String getFileUrl(String filePath);

    /**
     * 获取文件URL（指定过期时间）
     */
    String getFileUrl(String filePath, int expiry);
} 