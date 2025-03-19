package com.aiplatform.dataset.utils;

import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

/**
 * 文件类型验证工具类
 */
public class FileTypeValidator {
    
    /** 允许的文件类型映射 */
    private static final Map<String, List<String>> ALLOWED_FILE_TYPES = new HashMap<>();
    
    static {
        // 图片文件
        ALLOWED_FILE_TYPES.put("image", Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"
        ));
        
        // 文本文件
        ALLOWED_FILE_TYPES.put("text", Arrays.asList(
            "text/plain", "text/csv", "text/xml", "text/html", "text/markdown"
        ));
        
        // 文档文件
        ALLOWED_FILE_TYPES.put("document", Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ));
        
        // 音频文件
        ALLOWED_FILE_TYPES.put("audio", Arrays.asList(
            "audio/mpeg", "audio/wav", "audio/ogg", "audio/midi"
        ));
        
        // 视频文件
        ALLOWED_FILE_TYPES.put("video", Arrays.asList(
            "video/mp4", "video/mpeg", "video/quicktime", "video/x-msvideo"
        ));

        // 机器人数据文件
        ALLOWED_FILE_TYPES.put("robot", Arrays.asList(
            // ROS包和消息
            "application/x-ros-bag",        // ROS bag文件
            "application/x-ros-msg",        // ROS消息文件
            "application/x-ros-launch",     // ROS launch文件
            
            // 点云数据
            "application/x-pcd",            // Point Cloud Data
            "application/x-ply",            // Polygon File Format
            
            // 3D模型
            "model/urdf+xml",              // URDF (Unified Robot Description Format)
            "model/stl",                    // STL (STereoLithography)
            "model/obj",                    // OBJ (Wavefront)
            
            // 传感器数据
            "application/x-lidar",          // LiDAR数据
            "application/x-imu",            // IMU数据
            
            // 配置和地图
            "application/x-yaml",           // YAML配置文件
            "application/x-pgm",            // PGM地图文件
            "application/x-yaml-map",       // YAML地图文件
            
            // 轨迹和路径
            "application/x-trajectory",      // 轨迹数据
            "application/x-path",           // 路径数据
            
            // 标定数据
            "application/x-calibration",    // 标定数据文件
            
            // JSON和YAML（通用格式）
            "application/json",
            "application/x-yaml"
        ));
    }
    
    /**
     * 验证文件类型是否允许
     *
     * @param file 文件
     * @param allowedTypes 允许的文件类型列表
     * @return 验证结果
     */
    public static boolean validate(MultipartFile file, String... allowedTypes) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        
        // 如果未指定允许类型，则允许所有已定义的类型
        if (allowedTypes == null || allowedTypes.length == 0) {
            return ALLOWED_FILE_TYPES.values().stream()
                .anyMatch(types -> types.contains(contentType));
        }
        
        // 验证指定的类型
        return Arrays.stream(allowedTypes)
            .map(ALLOWED_FILE_TYPES::get)
            .filter(types -> types != null)
            .anyMatch(types -> types.contains(contentType));
    }
    
    /**
     * 获取文件类型分类
     *
     * @param contentType 文件Content-Type
     * @return 文件类型分类，未知类型返回null
     */
    public static String getFileTypeCategory(String contentType) {
        if (contentType == null) {
            return null;
        }
        
        return ALLOWED_FILE_TYPES.entrySet().stream()
            .filter(entry -> entry.getValue().contains(contentType))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);
    }
    
    /**
     * 获取允许的Content-Type列表
     *
     * @param fileType 文件类型分类
     * @return Content-Type列表
     */
    public static List<String> getAllowedContentTypes(String fileType) {
        return ALLOWED_FILE_TYPES.get(fileType);
    }
} 