package com.aiplatform.dataset.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aiplatform.common.core.web.controller.BaseController;
import com.aiplatform.common.core.web.domain.AjaxResult;
import com.aiplatform.common.core.web.page.TableDataInfo;
import com.aiplatform.common.log.annotation.Log;
import com.aiplatform.common.log.enums.BusinessType;
import com.aiplatform.common.security.annotation.RequiresPermissions;
import com.aiplatform.dataset.domain.dto.FileUploadDTO;
import com.aiplatform.dataset.domain.entity.DatasetFile;
import com.aiplatform.dataset.service.IFileService;
import com.aiplatform.dataset.domain.dto.UploadProgressDTO;
import com.aiplatform.dataset.domain.dto.ResumeUploadDTO;
import com.aiplatform.dataset.domain.dto.FileQuickUploadDTO;
import com.aiplatform.dataset.domain.entity.DatasetVersionFile;
import com.aiplatform.common.core.utils.FileTypeValidator;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件管理Controller
 * 
 * @author aiplatform
 */
@RestController
@RequestMapping("/dataset/file")
@Slf4j
public class FileController extends BaseController {
    
    @Autowired
    private IFileService fileService;

    private static final long MAX_FILE_SIZE = 2L * 1024 * 1024 * 1024; // 2GB

    /**
     * 查询文件列表
     */
    @GetMapping("/list")
    public AjaxResult list(@RequestParam("datasetId") Long datasetId) {
        List<DatasetVersionFile> list = fileService.listFiles(datasetId);
        return success(list);
    }

    /**
     * 获取文件信息
     */
    @GetMapping("/{fileId}")
    public AjaxResult getInfo(@PathVariable("fileId") Long fileId) {
        return success(fileService.getDatasetVersionFile(fileId));
    }

    /**
     * 获取文件预览信息
     */
    @GetMapping("/preview/{fileId}")
    public AjaxResult getPreviewInfo(@PathVariable("fileId") Long fileId) {
        return success(fileService.getPreviewInfo(fileId));
    }

    /**
     * 获取文件预览URL
     */
    @GetMapping("/preview/{fileId}/{previewType}")
    public AjaxResult getPreviewUrl(@PathVariable("fileId") Long fileId, @PathVariable("previewType") String previewType) {
        return success(fileService.getPreviewUrl(fileId, previewType));
    }

    /**
     * 获取文件下载URL
     */
    @GetMapping("/download/{fileId}")
    public AjaxResult getDownloadUrl(@PathVariable("fileId") Long fileId) {
        return success(fileService.getDownloadUrl(fileId));
    }

    /**
     * 下载文件
     */
    @GetMapping("/download/{fileId}/content")
    public void download(@PathVariable("fileId") Long fileId, HttpServletResponse response) {
        fileService.downloadFile(fileId, response);
    }

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    @RequiresPermissions("dataset:file:upload")
    public AjaxResult uploadFile(@RequestParam("file") MultipartFile file, FileUploadDTO uploadDTO) {
        try {
            // 验证文件类型
            if (!FileTypeValidator.validate(file.getContentType())) {
                log.warn("Unsupported file type: {}", file.getContentType());
                return AjaxResult.error("不支持的文件类型");
            }

            // 验证文件大小
            if (file.getSize() > MAX_FILE_SIZE) {
                log.warn("File size exceeds limit: {} bytes", file.getSize());
                return AjaxResult.error("文件大小超过限制(2GB)");
            }

            log.info("Starting file upload: {}, size: {} bytes", file.getOriginalFilename(), file.getSize());
            return fileService.uploadChunk(file, uploadDTO);
        } catch (Exception e) {
            log.error("File upload failed", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 检查分片是否存在
     */
    @GetMapping("/chunk/check")
    @RequiresPermissions("dataset:file:upload")
    public AjaxResult checkChunkExist(FileUploadDTO uploadDTO) {
        try {
            log.info("Checking chunk existence: identifier={}, chunkNumber={}", uploadDTO.getIdentifier(), uploadDTO.getChunkNumber());
            return fileService.checkChunkExist(uploadDTO);
        } catch (Exception e) {
            log.error("Check chunk failed", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 合并分片
     */
    @PostMapping("/chunk/merge")
    @RequiresPermissions("dataset:file:upload")
    public AjaxResult mergeChunks(@RequestBody FileUploadDTO uploadDTO) {
        try {
            log.info("Merging chunks for file: identifier={}", uploadDTO.getIdentifier());
            return fileService.mergeChunks(uploadDTO);
        } catch (Exception e) {
            log.error("Merge chunks failed", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 清理未完成的上传
     */
    @DeleteMapping("/chunk/clean")
    public AjaxResult cleanIncompleteUpload(@RequestParam("identifier") String identifier) {
        return fileService.cleanIncompleteUpload(identifier);
    }

    /**
     * 检查文件是否存在（秒传）
     */
    @GetMapping("/quick/check")
    public AjaxResult checkFileExists(FileQuickUploadDTO quickUploadDTO) {
        return fileService.checkFileExists(quickUploadDTO);
    }

    /**
     * 秒传文件
     */
    @PostMapping("/quick")
    public AjaxResult quickUpload(FileQuickUploadDTO quickUploadDTO) {
        return fileService.quickUpload(quickUploadDTO);
    }

    /**
     * 获取上传进度
     */
    @GetMapping("/progress")
    @RequiresPermissions("dataset:file:upload")
    public AjaxResult getUploadProgress(@RequestParam String identifier) {
        try {
            log.info("Getting upload progress for identifier: {}", identifier);
            return AjaxResult.success(fileService.getUploadProgress(identifier));
        } catch (Exception e) {
            log.error("Get upload progress failed", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 获取上传状态
     */
    @GetMapping("/status")
    public AjaxResult getUploadStatus(@RequestParam("identifier") String identifier, @RequestParam("totalChunks") Integer totalChunks) {
        ResumeUploadDTO status = fileService.getUploadStatus(identifier, totalChunks);
        return success(status);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{fileIds}")
    public AjaxResult remove(@PathVariable Long[] fileIds) {
        return toAjax(fileService.deleteFiles(fileIds));
    }
} 