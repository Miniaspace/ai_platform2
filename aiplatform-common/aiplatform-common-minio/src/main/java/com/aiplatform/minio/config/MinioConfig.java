package com.aiplatform.minio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.minio.MinioClient;
import lombok.Data;

/**
 * MinIO配置类
 *
 * @author aiplatform
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {
    /** 服务地址 */
    private String url;

    /** 访问密钥 */
    private String accessKey;

    /** 密钥 */
    private String secretKey;

    /** 存储桶名称 */
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("MinIO url is not configured. Please check your application.yml/properties file.");
        }
        if (accessKey == null || accessKey.isEmpty()) {
            throw new IllegalArgumentException("MinIO accessKey is not configured. Please check your application.yml/properties file.");
        }
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("MinIO secretKey is not configured. Please check your application.yml/properties file.");
        }
        
        return MinioClient.builder()
            .endpoint(url)
            .credentials(accessKey, secretKey)
            .build();
    }
} 