import request from '@/utils/request'

// 数据集列表
export function listDataset(query) {
  return request({
    url: '/dataset/list',
    method: 'get',
    params: query
  })
}

// 获取数据集详情
export function getDataset(id) {
  return request({
    url: `/dataset/${id}`,
    method: 'get'
  })
}

// 获取数据集分析数据
export function getDatasetAnalytics(datasetId, type, params) {
  return request({
    url: `/dataset/${datasetId}/analytics/${type}`,
    method: 'get',
    params
  })
}

// 新增数据集
export function addDataset(data) {
  return request({
    url: '/dataset',
    method: 'post',
    data: data
  })
}

// 修改数据集
export function updateDataset(id, data) {
  return request({
    url: '/dataset',
    method: 'put',
    data: {
      datasetId: id,
      ...data
    }
  })
}

// 删除数据集
export function delDataset(id) {
  return request({
    url: `/dataset/${id}`,
    method: 'delete'
  })
}

// 获取数据集版本列表
export function listDatasetVersion(datasetId, query) {
  return request({
    url: `/dataset/${datasetId}/version`,
    method: 'get',
    params: query
  })
}

// 获取版本详情
export function getDatasetVersion(datasetId, versionId) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}`,
    method: 'get'
  })
}

// 新增版本
export function addDatasetVersion(datasetId, data) {
  return request({
    url: `/dataset/${datasetId}/version`,
    method: 'post',
    data: data
  })
}

// 修改版本
export function updateDatasetVersion(datasetId, versionId, data) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}`,
    method: 'put',
    data: data
  })
}

// 删除版本
export function delDatasetVersion(datasetId, versionId) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}`,
    method: 'delete'
  })
}

// 获取版本文件列表
export function listDatasetVersionFile(datasetId, versionId, query) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/files`,
    method: 'get',
    params: query
  })
}

// 上传文件
export function uploadDatasetVersionFile(datasetId, versionId, data) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/files`,
    method: 'post',
    data: data
  })
}

// 删除文件
export function delDatasetVersionFile(datasetId, versionId, fileId) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/files/${fileId}`,
    method: 'delete'
  })
}

// 批量删除文件
export function batchDelDatasetVersionFile(datasetId, versionId, fileIds) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/files/batch`,
    method: 'delete',
    data: { fileIds }
  })
}

// 获取文件预览URL
export function getDatasetVersionFilePreviewUrl(datasetId, versionId, fileId) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/files/${fileId}/preview`,
    method: 'get'
  })
}

// 获取文件下载URL
export function getDatasetVersionFileDownloadUrl(datasetId, versionId, fileId) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/files/${fileId}/download`,
    method: 'get'
  })
}

// 获取版本标签列表
export function listDatasetVersionTag(datasetId, versionId, query) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/tags`,
    method: 'get',
    params: query
  })
}

// 新增标签
export function addDatasetVersionTag(datasetId, versionId, data) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/tags`,
    method: 'post',
    data: data
  })
}

// 修改标签
export function updateDatasetVersionTag(datasetId, versionId, tagId, data) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/tags/${tagId}`,
    method: 'put',
    data: data
  })
}

// 删除标签
export function delDatasetVersionTag(datasetId, versionId, tagId) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/tags/${tagId}`,
    method: 'delete'
  })
}

// 获取文件标签列表
export function getDatasetVersionFileTags(datasetId, versionId, fileId) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/files/${fileId}/tags`,
    method: 'get'
  })
}

// 为文件添加标签
export function addDatasetVersionFileTag(datasetId, versionId, fileId, data) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/files/${fileId}/tags`,
    method: 'post',
    data: data
  })
}

// 删除文件标签
export function delDatasetVersionFileTag(datasetId, versionId, fileId, tagId) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/files/${fileId}/tags/${tagId}`,
    method: 'delete'
  })
}

// 更新文件标签
export function updateDatasetVersionFileTags(datasetId, versionId, fileId, tagIds) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/files/${fileId}/tags`,
    method: 'put',
    data: { tagIds }
  })
}

// 获取版本比较结果
export function compareDatasetVersion(datasetId, sourceId, targetId, query) {
  return request({
    url: '/dataset/version/compare',
    method: 'get',
    params: {
      datasetId,
      sourceId,
      targetId,
      ...query
    }
  })
}

// 自定义查询文件接口 - 避开数据库结构问题
export function customQueryFiles(data) {
  return request({
    url: '/dataset/custom/files/query',
    method: 'post',
    data: data
  })
} 