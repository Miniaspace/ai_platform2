package com.aiplatform.common.core.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件类型验证工具类
 */
public class FileTypeValidator {
    private static final Set<String> SUPPORTED_TYPES = new HashSet<>(Arrays.asList(
        // 图片
        "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp",
        // 文档
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.ms-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "text/plain",
        // 音频
        "audio/mpeg", "audio/wav", "audio/mp3", "audio/ogg",
        // 视频
        "video/mp4", "video/mpeg", "video/quicktime", "video/x-msvideo", "video/x-ms-wmv",
        // 压缩文件
        "application/zip", "application/x-rar-compressed", "application/x-7z-compressed",
        // 机器人数据
        "application/x-ros-bag",
        "application/x-ros-msg",
        "application/x-ros-launch",
        "application/x-pointcloud",
        "application/x-3d-model",
        "application/x-sensor-data",
        "application/x-config",
        "application/x-map",
        "application/x-trajectory",
        "application/x-calibration"
    ));

    /**
     * 验证文件类型是否支持
     * @param contentType 文件类型
     * @return 是否支持
     */
    public static boolean validate(String contentType) {
        return contentType != null && SUPPORTED_TYPES.contains(contentType.toLowerCase());
    }

    /**
     * 获取支持的文件类型列表
     * @return 支持的文件类型列表
     */
    public static Set<String> getSupportedTypes() {
        return new HashSet<>(SUPPORTED_TYPES);
    }
} 