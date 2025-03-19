package com.aiplatform.minio.service.impl;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aiplatform.minio.config.MinioConfig;
import com.aiplatform.minio.service.IMinioService;

import io.minio.ComposeObjectArgs;
import io.minio.ComposeSource;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.messages.Item;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;

/**
 * MinIO服务实现类
 *
 * @author aiplatform
 */
@Slf4j
@Service
public class MinioServiceImpl implements IMinioService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    @Override
    public String getBucketName() {
        return minioConfig.getBucketName();
    }

    @Override
    public boolean doesObjectExist(String objectName) {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(getBucketName())
                    .object(objectName)
                    .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void putObject(String objectName, InputStream stream, long size, String contentType) {
        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(getBucketName())
                    .object(objectName)
                    .stream(stream, size, -1)
                    .contentType(contentType)
                    .build()
            );
        } catch (Exception e) {
            log.error("上传对象失败", e);
            throw new RuntimeException("上传对象失败", e);
        }
    }

    @Override
    public void composeObject(String targetObjectName, List<ComposeSource> sources) {
        try {
            minioClient.composeObject(
                ComposeObjectArgs.builder()
                    .bucket(getBucketName())
                    .object(targetObjectName)
                    .sources(sources)
                    .build()
            );
        } catch (Exception e) {
            log.error("合并对象失败", e);
            throw new RuntimeException("合并对象失败", e);
        }
    }

    @Override
    public void removeObject(String objectName) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(getBucketName())
                    .object(objectName)
                    .build()
            );
        } catch (Exception e) {
            log.error("删除对象失败", e);
            throw new RuntimeException("删除对象失败", e);
        }
    }

    @Override
    public Object statObject(String objectName) {
        try {
            return minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(getBucketName())
                    .object(objectName)
                    .build()
            );
        } catch (Exception e) {
            log.error("获取对象状态失败", e);
            throw new RuntimeException("获取对象状态失败", e);
        }
    }

    @Override
    public InputStream getObject(String objectName) {
        try {
            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(getBucketName())
                    .object(objectName)
                    .build()
            );
        } catch (Exception e) {
            log.error("获取对象失败", e);
            throw new RuntimeException("获取对象失败", e);
        }
    }

    @Override
    public String getPresignedObjectUrl(String objectName, Method method, int expiry) {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .bucket(getBucketName())
                    .object(objectName)
                    .method(method)
                    .expiry(expiry)
                    .build()
            );
        } catch (Exception e) {
            log.error("获取预签名URL失败", e);
            throw new RuntimeException("获取预签名URL失败", e);
        }
    }

    @Override
    public Iterable<Result<Item>> listObjects(String prefix, boolean recursive) {
        try {
            return minioClient.listObjects(
                ListObjectsArgs.builder()
                    .bucket(getBucketName())
                    .prefix(prefix)
                    .recursive(recursive)
                    .build()
            );
        } catch (Exception e) {
            log.error("列出对象失败", e);
            throw new RuntimeException("列出对象失败", e);
        }
    }
} 