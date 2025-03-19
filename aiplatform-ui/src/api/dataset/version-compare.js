import request from '@/utils/request'

// 比较两个版本的差异
export function compareVersions(data) {
  return request({
    url: '/dataset/version/compare',
    method: 'post',
    data: data
  })
}

// 获取文件预览信息
export function getFilePreview(fileId) {
  return request({
    url: '/dataset/version/file/preview/' + fileId,
    method: 'get'
  })
}

// 获取文件差异信息
export function getFileDiff(sourceVersionId, targetVersionId, fileId) {
  return request({
    url: '/dataset/version/compare/diff',
    method: 'get',
    params: {
      sourceVersionId,
      targetVersionId,
      fileId,
      includeFileStatus: true
    }
  })
}

// 获取版本比较结果
export function getVersionCompare(datasetId, sourceVersionId, targetVersionId) {
  return request({
    url: `/dataset/${datasetId}/version/compare`,
    method: 'get',
    params: {
      sourceVersionId,
      targetVersionId,
      includeFileStatus: true
    }
  })
}

// 获取文件状态变更历史
export function getFileStatusHistory(datasetId, versionId, fileId) {
  return request({
    url: `/dataset/${datasetId}/version/${versionId}/files/${fileId}/status/history`,
    method: 'get'
  })
}

// 导出版本比较结果
export function exportCompareResult(data) {
  return request({
    url: '/dataset/version/compare/export',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
} 