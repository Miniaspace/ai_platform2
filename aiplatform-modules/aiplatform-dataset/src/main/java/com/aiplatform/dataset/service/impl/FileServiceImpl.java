package com.aiplatform.dataset.service.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.aiplatform.common.core.utils.DateUtils;
import com.aiplatform.common.security.utils.SecurityUtils;
import com.aiplatform.dataset.domain.dto.UploadProgressDTO;
import com.aiplatform.dataset.utils.FileTypeValidator;
import com.aiplatform.minio.config.MinioConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.aiplatform.common.core.utils.StringUtils;
import com.aiplatform.common.core.web.domain.AjaxResult;
import com.aiplatform.dataset.domain.dto.FileUploadDTO;
import com.aiplatform.dataset.domain.dto.FileQuickUploadDTO;
import com.aiplatform.dataset.domain.entity.DatasetFile;
import com.aiplatform.dataset.domain.entity.DatasetVersionFile;
import com.aiplatform.dataset.mapper.DatasetFileMapper;
import com.aiplatform.dataset.service.IFileService;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import com.aiplatform.dataset.domain.dto.ResumeUploadDTO;
import com.aiplatform.common.core.constant.Constants;
import com.aiplatform.minio.service.IMinioService;
import io.minio.ComposeSource;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

/**
 * 文件管理Service业务层处理
 * 
 * @author aiplatform
 */
@Slf4j
@Service
public class FileServiceImpl implements IFileService {
    
    @Autowired
    private IMinioService minioService;
    
    @Autowired
    private MinioConfig minioConfig;
    
    @Autowired
    private DatasetFileMapper datasetFileMapper;

    @Override
    public AjaxResult checkChunkExist(FileUploadDTO uploadDTO) {
        try {
            String chunkPath = getChunkPath(uploadDTO);
            boolean exists = minioService.doesObjectExist(chunkPath);
            return AjaxResult.success().put("exists", exists);
        } catch (Exception e) {
            return AjaxResult.success().put("exists", false);
        }
    }

    @Override
    public AjaxResult uploadChunk(MultipartFile file, FileUploadDTO uploadDTO) {
        try {
            // 参数校验
            if (file == null || file.isEmpty()) {
                return AjaxResult.error("文件不能为空");
            }
            if (uploadDTO == null) {
                return AjaxResult.error("上传信息不能为空");
            }
            if (uploadDTO.getIdentifier() == null) {
                return AjaxResult.error("文件标识符不能为空");
            }
            if (uploadDTO.getChunkNumber() == null) {
                return AjaxResult.error("分片编号不能为空");
            }
            if (uploadDTO.getTotalChunks() == null) {
                return AjaxResult.error("总分片数不能为空");
            }
            if (uploadDTO.getFilename() == null) {
                return AjaxResult.error("文件名不能为空");
            }

            // 验证文件类型
            if (!FileTypeValidator.validate(file)) {
                return AjaxResult.error("不支持的文件类型");
            }

            String chunkPath = getChunkPath(uploadDTO);
            minioService.putObject(chunkPath, file.getInputStream(), file.getSize(), file.getContentType());

            // 保存上传信息（仅在第一个分片时）
            if (uploadDTO.getChunkNumber() == 1) {
                saveUploadInfo(uploadDTO);
            }

            return AjaxResult.success();
        } catch (Exception e) {
            log.error("Upload chunk failed", e);
            return AjaxResult.error("Upload chunk failed: " + e.getMessage());
        }
    }

    @Override
    public AjaxResult mergeChunks(FileUploadDTO uploadDTO) {
        try {
            String filePath = getFilePath(uploadDTO);
            
            // 1. 检查所有分片是否都已上传
            for (int i = 1; i <= uploadDTO.getTotalChunks(); i++) {
                String chunkPath = getChunkPath(new FileUploadDTO(uploadDTO, i));
                if (!minioService.doesObjectExist(chunkPath)) {
                    return AjaxResult.error("分片文件不完整,请重新上传");
                }
            }

            // 2. 准备合并源
            List<ComposeSource> sources = new ArrayList<>();
            for (int i = 1; i <= uploadDTO.getTotalChunks(); i++) {
                String chunkPath = getChunkPath(new FileUploadDTO(uploadDTO, i));
                sources.add(ComposeSource.builder().bucket(minioService.getBucketName()).object(chunkPath).build());
            }

            // 3. 合并文件
            minioService.composeObject(filePath, sources);

            // 4. 删除分片文件
            for (int i = 1; i <= uploadDTO.getTotalChunks(); i++) {
                String chunkPath = getChunkPath(new FileUploadDTO(uploadDTO, i));
                minioService.removeObject(chunkPath);
            }

            // 5. 保存文件信息到数据库
            DatasetVersionFile datasetFile = new DatasetVersionFile();
            datasetFile.setVersionId(uploadDTO.getDatasetId());
            datasetFile.setFileName(uploadDTO.getFilename());
            datasetFile.setFilePath(filePath);
            datasetFile.setFileSize(uploadDTO.getTotalSize());
            datasetFile.setFileType(uploadDTO.getFileType());
            
            insertDatasetFile(datasetFile);

            return AjaxResult.success("文件上传成功");
        } catch (Exception e) {
            log.error("合并文件分片失败", e);
            return AjaxResult.error("合并文件分片失败: " + e.getMessage());
        }
    }

    @Override
    public void downloadFile(Long fileId, HttpServletResponse response) {
        DatasetVersionFile file = getDatasetVersionFile(fileId);
        if (file == null) {
            throw new RuntimeException("文件不存在");
        }
        try (InputStream inputStream = minioService.getObject(file.getFilePath())) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + file.getFileName());
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException("下载文件失败", e);
        }
    }

    @Override
    public String previewFile(Long fileId) {
        DatasetVersionFile file = getDatasetVersionFile(fileId);
        if (file == null) {
            throw new RuntimeException("文件不存在");
        }
        return minioService.getPresignedObjectUrl(file.getFilePath(), Method.GET, 3600);
    }

    @Override
    public int deleteFiles(Long[] fileIds) {
        int count = 0;
        for (Long fileId : fileIds) {
            DatasetVersionFile file = getDatasetVersionFile(fileId);
            if (file != null) {
                minioService.removeObject(file.getFilePath());
                datasetFileMapper.deleteById(fileId);
                count++;
            }
        }
        return count;
    }

    @Override
    public List<DatasetVersionFile> listFiles(Long datasetId) {
        DatasetFile query = new DatasetFile();
        query.setDatasetId(datasetId);
        List<DatasetFile> files = datasetFileMapper.selectDatasetFileList(query);
        return convertToDatasetVersionFiles(files);
    }

    @Override
    public UploadProgressDTO getUploadProgress(String identifier) {
        try {
            UploadProgressDTO progressDTO = UploadProgressDTO.builder()
                .fileId(null)
                .fileName("")
                .uploadedSize(0L)
                .totalSize(0L)
                .progress(0)
                .status("init")
                .errorMsg("")
                .build();
            
            // 获取已上传的分片列表
            String prefix = "chunks/" + identifier + "/";
            Iterable<Result<Item>> results = minioService.listObjects(prefix, true);
            
            // 统计已上传分片
            long uploadedSize = 0;
            int uploadedChunks = 0;
            Integer totalChunks = null;
            Long totalSize = null;
            
            for (Result<Item> result : results) {
                Item item = result.get();
                String objectName = item.objectName();
                if (objectName.endsWith("/info")) {
                    // 读取信息文件获取总分片数和总大小
                    InputStream stream = minioService.getObject(objectName);
                    byte[] bytes = IOUtils.toByteArray(stream);
                    String info = new String(bytes);
                    String[] parts = info.split(",");
                    totalChunks = Integer.parseInt(parts[0]);
                    totalSize = Long.parseLong(parts[1]);
                    stream.close();
                } else {
                    uploadedChunks++;
                    uploadedSize += item.size();
                }
            }
            
            if (totalChunks == null || totalSize == null) {
                progressDTO.setStatus("failed");
                progressDTO.setErrorMsg("未找到上传信息");
                return progressDTO;
            }
            
            // 设置进度信息
            progressDTO.setUploadedSize(uploadedSize);
            progressDTO.setTotalSize(totalSize);
            
            // 计算进度百分比
            int progress = (int) ((uploadedChunks - 1) * 100.0 / totalChunks);
            progressDTO.setProgress(progress);
            
            // 设置状态
            if (uploadedChunks - 1 == totalChunks) {
                progressDTO.setStatus("success"); // 上传完成
            } else {
                progressDTO.setStatus("uploading"); // 上传中
            }
            
            return progressDTO;
        } catch (Exception e) {
            log.error("获取上传进度失败", e);
            return UploadProgressDTO.builder()
                .fileId(null)
                .fileName("")
                .uploadedSize(0L)
                .totalSize(0L)
                .progress(0)
                .status("failed")
                .errorMsg("获取上传进度失败: " + e.getMessage())
                .build();
        }
    }

    @Override
    public ResumeUploadDTO getUploadStatus(String identifier, Integer totalChunks) {
        try {
            ResumeUploadDTO resumeDTO = new ResumeUploadDTO();
            resumeDTO.setIdentifier(identifier);
            resumeDTO.setTotalChunks(totalChunks);
            
            // 获取已上传的分片列表
            List<Integer> uploadedChunks = new ArrayList<>();
            long totalSize = 0;
            
            String prefix = "chunks/" + identifier + "/";
            Iterable<Result<Item>> results = minioService.listObjects(prefix, true);
            for (Result<Item> result : results) {
                Item item = result.get();
                String objectName = item.objectName();
                // 从路径中提取分片编号
                if (!objectName.endsWith("/info")) {
                    String[] parts = objectName.split("/");
                    String chunkNumberStr = parts[parts.length - 1];
                    uploadedChunks.add(Integer.parseInt(chunkNumberStr));
                    totalSize += item.size();
                }
            }
            
            resumeDTO.setUploadedChunks(uploadedChunks.stream().mapToInt(Integer::intValue).toArray());
            resumeDTO.setTotalSize(totalSize);
            
            // 判断上传状态
            if (uploadedChunks.size() == totalChunks) {
                resumeDTO.setStatus("success"); // 已完成
            } else {
                resumeDTO.setStatus("uploading"); // 未完成
            }
            
            return resumeDTO;
        } catch (Exception e) {
            log.error("获取上传状态失败", e);
            return null;
        }
    }

    @Override
    public AjaxResult cleanIncompleteUpload(String identifier) {
        try {
            // 列出所有相关的分片
            String prefix = "chunks/" + identifier + "/";
            
            // 删除所有分片
            Iterable<Result<Item>> results = minioService.listObjects(prefix, true);
            for (Result<Item> result : results) {
                Item item = result.get();
                minioService.removeObject(item.objectName());
            }
            
            return AjaxResult.success("清理未完成的上传文件成功");
        } catch (Exception e) {
            log.error("清理未完成的上传文件失败", e);
            return AjaxResult.error("清理未完成的上传文件失败: " + e.getMessage());
        }
    }

    @Override
    public AjaxResult checkFileExists(FileQuickUploadDTO quickUploadDTO) {
        try {
            // 查询数据库中是否存在相同MD5和SHA1的文件
            DatasetFile existingFile = datasetFileMapper.selectDatasetFileByHash(
                quickUploadDTO.getMd5(), 
                quickUploadDTO.getSha1()
            );
            
            if (existingFile != null) {
                return AjaxResult.success().put("exists", true).put("fileId", existingFile.getFileId());
            }
            
            return AjaxResult.success().put("exists", false);
        } catch (Exception e) {
            log.error("检查文件是否存在失败", e);
            return AjaxResult.error("检查文件是否存在失败: " + e.getMessage());
        }
    }

    @Override
    public AjaxResult quickUpload(FileQuickUploadDTO quickUploadDTO) {
        try {
            // 查询原文件
            DatasetFile sourceFile = datasetFileMapper.selectDatasetFileByHash(
                quickUploadDTO.getMd5(), 
                quickUploadDTO.getSha1()
            );
            
            if (sourceFile == null) {
                return AjaxResult.error("源文件不存在，无法执行秒传");
            }
            
            // 创建新的文件记录
            DatasetVersionFile newFile = DatasetVersionFile.builder()
                .datasetId(quickUploadDTO.getDatasetId())
                .fileName(quickUploadDTO.getFilename())
                .filePath(sourceFile.getFilePath())
                .fileSize(quickUploadDTO.getFileSize())
                .fileType(quickUploadDTO.getFileType())
                .md5(quickUploadDTO.getMd5())
                .sha1(quickUploadDTO.getSha1())
                .fileStatus("0")
                .build();
            
            // 保存新文件记录
            insertDatasetFile(newFile);
            
            return AjaxResult.success("文件秒传成功");
        } catch (Exception e) {
            log.error("文件秒传失败", e);
            return AjaxResult.error("文件秒传失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getPreviewInfo(Long fileId) {
        DatasetVersionFile file = getDatasetVersionFile(fileId);
        if (file == null) {
            throw new RuntimeException("文件不存在");
        }

        Map<String, Object> previewInfo = new HashMap<>();
        previewInfo.put("fileName", file.getFileName());
        previewInfo.put("fileType", file.getFileType());
        previewInfo.put("fileSize", file.getFileSize());
        previewInfo.put("previewUrl", getPreviewUrl(fileId, file.getFileType()));
        return previewInfo;
    }

    @Override
    public String getPreviewUrl(Long fileId, String previewType) {
        try {
            DatasetVersionFile file = getDatasetVersionFile(fileId);
            if (file == null) {
                return null;
            }
            
            // 根据预览类型生成不同的预览URL
            switch (previewType) {
                case "image":
                case "pdf":
                case "text":
                    // 直接返回MinIO预签名URL
                    return minioService.getPresignedObjectUrl(
                        file.getFilePath(),
                        Method.GET,
                        3600
                    );
                    
                case "office":
                    // 使用Office Online预览
                    String officePreviewUrl = getOfficePreviewUrl(file);
                    return officePreviewUrl;
                    
                case "robot":
                    // 使用机器人数据专用预览器
                    String robotPreviewUrl = getRobotDataPreviewUrl(file);
                    return robotPreviewUrl;
                    
                default:
                    return null;
            }
        } catch (Exception e) {
            log.error("获取预览URL失败", e);
            return null;
        }
    }

    /**
     * 根据文件类型获取预览类型
     */
    private String getPreviewType(String fileType) {
        if (fileType.startsWith("image/")) {
            return "image";
        } else if (fileType.equals("application/pdf")) {
            return "pdf";
        } else if (fileType.startsWith("text/")) {
            return "text";
        } else if (fileType.startsWith("application/vnd.openxmlformats-officedocument") ||
                   fileType.startsWith("application/vnd.ms-")) {
            return "office";
        } else if (fileType.startsWith("application/x-ros") ||
                   fileType.startsWith("application/x-lidar") ||
                   fileType.startsWith("model/")) {
            return "robot";
        }
        return "unknown";
    }

    /**
     * 获取Office文档预览URL
     */
    private String getOfficePreviewUrl(DatasetVersionFile file) {
        // TODO: 集成Office Online或其他文档预览服务
        return null;
    }

    /**
     * 获取机器人数据预览URL
     */
    private String getRobotDataPreviewUrl(DatasetVersionFile file) {
        // TODO: 集成机器人数据专用预览服务
        return null;
    }

    /**
     * 保存上传信息
     */
    private void saveUploadInfo(FileUploadDTO uploadDTO) throws Exception {
        String infoPath = "chunks/" + uploadDTO.getIdentifier() + "/info";
        String info = uploadDTO.getTotalChunks() + "," + uploadDTO.getTotalSize();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(info.getBytes());
        minioService.putObject(infoPath, bais, bais.available(), "text/plain");
    }

    private String getChunkPath(FileUploadDTO uploadDTO) {
        return String.format("chunks/%s/%d", uploadDTO.getIdentifier(), uploadDTO.getChunkNumber());
    }

    private String getFilePath(FileUploadDTO uploadDTO) {
        return String.format("datasets/%d/%s/%s", 
            uploadDTO.getDatasetId(),
            uploadDTO.getIdentifier(),
            uploadDTO.getFilename()
        );
    }

    @Override
    public boolean checkChunkExists(String identifier, Integer chunkNumber) {
        String chunkPath = getChunkPath(identifier, chunkNumber);
        return minioService.doesObjectExist(chunkPath);
    }

    @Override
    public void uploadChunk(String identifier, Integer chunkNumber, MultipartFile file) throws IOException {
        String chunkPath = getChunkPath(identifier, chunkNumber);
        minioService.putObject(chunkPath, file.getInputStream(), file.getSize(), file.getContentType());
    }

    @Override
    public void mergeChunks(String identifier, String fileName, Integer totalChunks) throws IOException {
        List<ComposeSource> sources = new ArrayList<>();
        for (int i = 1; i <= totalChunks; i++) {
            String chunkPath = getChunkPath(identifier, i);
            sources.add(ComposeSource.builder().bucket(minioService.getBucketName()).object(chunkPath).build());
        }

        String filePath = getFilePath(identifier, fileName);
        minioService.composeObject(filePath, sources);

        // 删除分片
        for (int i = 1; i <= totalChunks; i++) {
            String chunkPath = getChunkPath(identifier, i);
            minioService.removeObject(chunkPath);
        }
    }

    @Override
    public DatasetVersionFile saveFileInfo(String identifier, String fileName, Long versionId, Long parentId) {
        DatasetVersionFile versionFile = DatasetVersionFile.builder()
            .fileName(fileName)
            .filePath(identifier)
            .versionId(versionId)
            .parentId(parentId)
            .fileStatus("0")  // 使用字符串 "0" 表示正常状态
            .build();
        datasetFileMapper.insert(convertToDatasetFile(versionFile));
        return versionFile;
    }

    @Override
    public String getDownloadUrl(Long fileId) {
        DatasetVersionFile file = getDatasetVersionFile(fileId);
        if (file == null) {
            throw new RuntimeException("文件不存在");
        }
        return minioService.getPresignedObjectUrl(file.getFilePath(), Method.GET, 3600);
    }

    @Override
    public UploadProgressDTO getUploadProgress(String identifier, String fileName) {
        DatasetFile file = datasetFileMapper.selectDatasetFileByMd5(identifier);
        
        UploadProgressDTO progress = new UploadProgressDTO();
        progress.setFileId(file != null ? file.getFileId() : null);
        progress.setFileName(fileName);
        progress.setUploadedSize(0L);
        progress.setTotalSize(0L);
        progress.setProgress(0);
        progress.setStatus("init");
        progress.setErrorMsg("");

        if (file != null) {
            progress.setUploadedSize(file.getFileSize());
            progress.setTotalSize(file.getFileSize());
            progress.setProgress(100);
            progress.setStatus("success");
        }

        return progress;
    }

    @Override
    public ResumeUploadDTO getUploadStatus(String identifier, String fileName, Integer totalChunks) {
        DatasetFile file = datasetFileMapper.selectDatasetFileByMd5(identifier);
        if (file != null) {
            return ResumeUploadDTO.builder()
                    .fileId(file.getFileId())
                    .fileName(fileName)
                    .identifier(identifier)
                    .uploadedChunks(new int[0])
                    .totalChunks(0)
                    .totalSize(file.getFileSize())
                    .status("success")
                    .build();
        }

        int[] uploadedChunks = new int[totalChunks];
        int uploadedCount = 0;
        for (int i = 1; i <= totalChunks; i++) {
            String chunkPath = getChunkPath(identifier, i);
            if (minioService.doesObjectExist(chunkPath)) {
                uploadedChunks[uploadedCount++] = i;
            }
        }

        return ResumeUploadDTO.builder()
                .fileId(null)
                .fileName(fileName)
                .identifier(identifier)
                .uploadedChunks(Arrays.copyOf(uploadedChunks, uploadedCount))
                .totalChunks(totalChunks)
                .totalSize(0L)
                .status(uploadedCount == totalChunks ? "merging" : "uploading")
                .build();
    }

    private String getChunkPath(String identifier, Integer chunkNumber) {
        return String.format("chunks/%s/%d", identifier, chunkNumber);
    }

    private String getFilePath(String identifier, String fileName) {
        return String.format("files/%s/%s", identifier, fileName);
    }

    private String getFileType(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    private DatasetFile convertToDatasetFile(DatasetVersionFile versionFile) {
        if (versionFile == null) {
            return null;
        }
        DatasetFile file = new DatasetFile();
        file.setFileId(versionFile.getFileId());
        file.setFileName(versionFile.getFileName());
        file.setFilePath(versionFile.getFilePath());
        file.setFileSize(versionFile.getFileSize());
        file.setFileType(versionFile.getFileType());
        file.setMd5(versionFile.getMd5());
        file.setSha1(versionFile.getSha1());
        file.setFileStatus(versionFile.getFileStatus());
        file.setDatasetId(versionFile.getDatasetId());
        file.setVersionId(versionFile.getVersionId());
        file.setParentId(versionFile.getParentId());
        return file;
    }

    private DatasetVersionFile convertToDatasetVersionFile(DatasetFile file) {
        if (file == null) {
            return null;
        }
        return DatasetVersionFile.builder()
            .fileId(file.getFileId())
            .fileName(file.getFileName())
            .filePath(file.getFilePath())
            .fileSize(file.getFileSize())
            .fileType(file.getFileType())
            .md5(file.getMd5())
            .sha1(file.getSha1())
            .fileStatus(file.getFileStatus())
            .datasetId(file.getDatasetId())
            .versionId(file.getVersionId())
            .parentId(file.getParentId())
            .build();
    }

    @Override
    public String getFileUrl(String filePath) {
        return minioService.getPresignedObjectUrl(
            filePath,
            Method.GET,  // 使用 io.minio.http.Method.GET
            3600  // 一小时过期
        );
    }

    @Override
    public String getFileUrl(String filePath, int expiry) {
        return minioService.getPresignedObjectUrl(
            filePath,
            Method.GET,  // 使用 io.minio.http.Method.GET
            expiry
        );
    }

    @Override
    public void insertDatasetFile(DatasetVersionFile datasetFile) {
        DatasetFile file = convertToDatasetFile(datasetFile);
        datasetFileMapper.insert(file);
    }

    private DatasetFile createDatasetFile(MultipartFile file, String filePath) throws IOException {
        DatasetFile datasetFile = new DatasetFile();
        datasetFile.setFileName(file.getOriginalFilename());
        datasetFile.setFilePath(filePath);
        datasetFile.setFileSize(file.getSize());
        datasetFile.setFileType(file.getContentType());
        datasetFile.setMd5(DigestUtils.md5Hex(file.getInputStream()));
        datasetFile.setSha1(DigestUtils.sha1Hex(file.getInputStream()));
        datasetFile.setFileStatus("0");  // 使用字符串 "0" 表示正常状态
        return datasetFile;
    }

    @Override
    public DatasetVersionFile getDatasetVersionFile(Long fileId) {
        DatasetFile file = datasetFileMapper.selectDatasetFileById(fileId);
        return file != null ? convertToDatasetVersionFile(file) : null;
    }

    private List<DatasetVersionFile> convertToDatasetVersionFiles(List<DatasetFile> files) {
        if (files == null) {
            return new ArrayList<>();
        }
        return files.stream()
            .map(this::convertToDatasetVersionFile)
            .collect(Collectors.toList());
    }
} 