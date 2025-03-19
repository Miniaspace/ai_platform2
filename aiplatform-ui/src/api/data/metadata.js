import request from '@/utils/request'

// 查询元数据列表
export function listMetadata(query) {
  return request({
    url: '/api/v1/data/metadata/list',
    method: 'get',
    params: query
  })
}

// 查询元数据详细
export function getMetadata(metadataId) {
  return request({
    url: '/api/v1/data/metadata/' + metadataId,
    method: 'get'
  })
}

// 新增元数据
export function addMetadata(data) {
  return request({
    url: '/api/v1/data/metadata',
    method: 'post',
    data: data
  })
}

// 修改元数据
export function updateMetadata(data) {
  return request({
    url: '/api/v1/data/metadata',
    method: 'put',
    data: data
  })
}

// 删除元数据
export function delMetadata(metadataId) {
  return request({
    url: '/api/v1/data/metadata/' + metadataId,
    method: 'delete'
  })
}

// 导入元数据
export function importMetadata(data) {
  return request({
    url: '/api/v1/data/metadata/import',
    method: 'post',
    data: data
  })
}

// 导出元数据
export function exportMetadata(query) {
  return request({
    url: '/api/v1/data/metadata/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

// 获取元数据模板
export function getMetadataTemplate() {
  return request({
    url: '/api/v1/data/metadata/template',
    method: 'get',
    responseType: 'blob'
  })
}

// 批量更新元数据
export function batchUpdateMetadata(data) {
  return request({
    url: '/api/v1/data/metadata/batch',
    method: 'put',
    data: data
  })
}

// 查询文件元数据
export function getFileMetadata(fileId) {
  return request({
    url: '/api/v1/data/metadata/file/' + fileId,
    method: 'get'
  })
}

// 更新文件元数据
export function updateFileMetadata(data) {
  return request({
    url: '/api/v1/data/metadata/file',
    method: 'put',
    data: data
  })
} 