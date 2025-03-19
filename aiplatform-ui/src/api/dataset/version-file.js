import request from '@/utils/request'

// 查询版本文件列表
export function listVersionFiles(datasetId, versionId, query) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/files`,
    method: 'get',
    params: query
  })
}

// 删除文件
export function deleteVersionFile(datasetId, versionId, fileId) {
  return request({
    url: `/dataset/version/file/${versionId}/${fileId}`,
    method: 'delete'
  })
}

// 更新文件状态
export function updateFileStatus(datasetId, versionId, fileId, fileStatus) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/files/${fileId}/status`,
    method: 'put',
    data: { fileStatus }
  })
}

// 批量更新文件状态
export function batchUpdateFileStatus(datasetId, versionId, fileIds, fileStatus) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/files/status`,
    method: 'put',
    data: { fileIds, fileStatus }
  })
}

// 上传文件
export function uploadFile(datasetId, versionId, file, onUploadProgress) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: `/dataset/version/${versionId}/upload`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress
  })
}

// 预览文件
export function previewFile(datasetId, versionId, fileId) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/files/${fileId}/preview`,
    method: 'get'
  })
}

// 下载文件
export function downloadFile(datasetId, versionId, fileId) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/files/${fileId}/download`,
    method: 'get',
    responseType: 'blob'
  })
}

// 检查文件分片
export function checkChunk(data) {
  return request({
    url: '/dataset/file/chunk/check',
    method: 'get',
    params: data
  })
}

// 上传文件分片
export function uploadChunk(data) {
  return request({
    url: '/dataset/file/chunk',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: data
  })
}

// 合并文件分片
export function mergeChunks(data) {
  return request({
    url: '/dataset/file/chunk/merge',
    method: 'post',
    data: data
  })
}

// 获取上传进度
export function getUploadProgress(uploadId) {
  return request({
    url: `/dataset/file/progress/${uploadId}`,
    method: 'get'
  })
}

// 获取文件预览URL
export function getPreviewUrl(fileId) {
  return request({
    url: `/dataset/file/preview/${fileId}`,
    method: 'get'
  })
}

// 获取文件下载URL
export function getDownloadUrl(fileId) {
  return request({
    url: `/dataset/file/download/${fileId}`,
    method: 'get'
  })
}

// 查询版本的所有文件
export function listFilesByVersionId(versionId) {
  return request({
    url: `/dataset/version/${versionId}/files`,
    method: 'get'
  })
}

// 获取文件详细信息
export function getVersionFile(versionId, fileId) {
  return request({
    url: `/dataset/version/${versionId}/files/${fileId}`,
    method: 'get'
  })
}

// 新增版本文件关联
export function addVersionFile(data) {
  return request({
    url: '/dataset/version/file',
    method: 'post',
    data: data
  })
}

// 批量新增版本文件关联
export function batchAddVersionFile(data) {
  return request({
    url: '/dataset/version/file/batch',
    method: 'post',
    data: data
  })
}

// 复制版本文件
export function copyVersionFiles(sourceVersionId, targetVersionId) {
  return request({
    url: '/dataset/version/file/copy/' + sourceVersionId + '/' + targetVersionId,
    method: 'post'
  })
}

// 比较版本文件
export function compareVersionFiles(sourceVersionId, targetVersionId) {
  return request({
    url: `/dataset/version/compare/${sourceVersionId}/${targetVersionId}`,
    method: 'get'
  })
}

// 获取文件预览信息
export function getFilePreviewInfo(fileId) {
  return request({
    url: '/dataset/file/preview/info/' + fileId,
    method: 'get'
  })
} 