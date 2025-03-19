import request from '@/utils/request'

// 查询数据集版本列表
export function listDatasetVersion(datasetId, query) {
  return request({
    url: '/dataset/version/list',
    method: 'get',
    params: {
      ...query,
      datasetId
    }
  })
}

// 查询数据集版本详细信息
export function getDatasetVersion(datasetId, versionId) {
  return request({
    url: `/dataset/version/${versionId}`,
    method: 'get',
    params: {
      datasetId
    }
  })
}

// 新增数据集版本
export function addDatasetVersion(datasetId, data) {
  return request({
    url: '/dataset/version',
    method: 'post',
    data: {
      ...data,
      datasetId
    }
  })
}

// 修改数据集版本
export function updateDatasetVersion(datasetId, versionId, data) {
  return request({
    url: `/dataset/version/${versionId}`,
    method: 'put',
    data: {
      ...data,
      datasetId
    }
  })
}

// 删除数据集版本
export function deleteDatasetVersion(datasetId, versionId) {
  return request({
    url: `/dataset/version/${versionId}`,
    method: 'delete',
    params: {
      datasetId
    }
  })
}

// 发布版本
export function publishVersion(datasetId, versionId) {
  return request({
    url: `/dataset/version/publish/${versionId}`,
    method: 'put',
    params: {
      datasetId
    }
  })
}

// 废弃版本
export function discardVersion(datasetId, versionId) {
  return request({
    url: `/dataset/version/${versionId}/discard`,
    method: 'put',
    params: {
      datasetId
    }
  })
}

// 获取版本文件列表
export function listVersionFiles(datasetId, versionId, query) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/files`,
    method: 'get',
    params: {
      ...query,
      includeFileStatus: true // 添加此参数以获取文件状态信息
    }
  })
}

// 比较两个版本的差异
export function compareVersions(datasetId, sourceVersionId, targetVersionId, query) {
  return request({
    url: `/dataset/${datasetId}/version/compare`,
    method: 'get',
    params: {
      sourceVersionId,
      targetVersionId,
      includeFileStatus: true, // 添加此参数以获取文件状态信息
      ...query
    }
  })
}

// 获取当前版本
export function getCurrentVersion(datasetId) {
  return request({
    url: `/dataset/version/current/${datasetId}`,
    method: 'get'
  })
}

// 设置当前版本
export function setCurrentVersion(datasetId, versionId) {
  return request({
    url: `/dataset/version/${versionId}/current`,
    method: 'put',
    params: {
      datasetId
    }
  })
}

// 获取版本树
export function getVersionTree(datasetId) {
  return request({
    url: `/dataset/version/tree/${datasetId}`,
    method: 'get'
  })
}

// 检查版本名称是否唯一
export function checkVersionNameUnique(datasetId, versionName) {
  return request({
    url: `/dataset/version/checkVersionNameUnique`,
    method: 'get',
    params: {
      datasetId,
      versionName
    }
  })
} 