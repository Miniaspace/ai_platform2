package com.aiplatform.minio.service;

import java.io.InputStream;
import java.util.List;

import io.minio.ComposeSource;
import io.minio.Result;
import io.minio.messages.Item;
import io.minio.http.Method;

/**
 * MinIO服务接口
 *
 * @author aiplatform
 */
public interface IMinioService {
    /**
     * 获取存储桶名称
     *
     * @return 存储桶名称
     */
    String getBucketName();

    /**
     * 判断对象是否存在
     *
     * @param objectName 对象名称
     * @return 是否存在
     */
    boolean doesObjectExist(String objectName);

    /**
     * 上传对象
     *
     * @param objectName 对象名称
     * @param stream 输入流
     * @param size 对象大小
     * @param contentType 内容类型
     */
    void putObject(String objectName, InputStream stream, long size, String contentType);

    /**
     * 合并对象
     *
     * @param targetObjectName 目标对象名称
     * @param sources 源对象列表
     */
    void composeObject(String targetObjectName, List<ComposeSource> sources);

    /**
     * 删除对象
     *
     * @param objectName 对象名称
     */
    void removeObject(String objectName);

    /**
     * 获取对象状态
     *
     * @param objectName 对象名称
     * @return 对象状态
     */
    Object statObject(String objectName);

    /**
     * 获取对象
     *
     * @param objectName 对象名称
     * @return 输入流
     */
    InputStream getObject(String objectName);

    /**
     * 获取对象预签名URL
     *
     * @param objectName 对象名称
     * @param method HTTP方法
     * @param expiry 过期时间（秒）
     * @return 预签名URL
     */
    String getPresignedObjectUrl(String objectName, Method method, int expiry);

    /**
     * 列出对象
     *
     * @param prefix 前缀
     * @param recursive 是否递归
     * @return 对象列表
     */
    Iterable<Result<Item>> listObjects(String prefix, boolean recursive);
} 