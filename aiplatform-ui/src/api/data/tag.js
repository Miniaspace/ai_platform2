import request from '@/utils/request'

// 查询标签列表
export function listTag(query) {
  return request({
    url: '/api/v1/data/tag/list',
    method: 'get',
    params: query
  })
}

// 查询标签详细
export function getTag(tagId) {
  return request({
    url: '/api/v1/data/tag/' + tagId,
    method: 'get'
  })
}

// 新增标签
export function addTag(data) {
  return request({
    url: '/api/v1/data/tag',
    method: 'post',
    data: data
  })
}

// 修改标签
export function updateTag(data) {
  return request({
    url: '/api/v1/data/tag',
    method: 'put',
    data: data
  })
}

// 删除标签
export function delTag(tagId) {
  return request({
    url: '/api/v1/data/tag/' + tagId,
    method: 'delete'
  })
}

// 为文件添加标签
export function addFileTag(data) {
  return request({
    url: '/api/v1/data/tag/file/add',
    method: 'post',
    data: data
  })
}

// 移除文件标签
export function removeFileTag(data) {
  return request({
    url: '/api/v1/data/tag/file/remove',
    method: 'delete',
    data: data
  })
}

// 查询文件标签
export function listFileTag(fileId) {
  return request({
    url: '/api/v1/data/tag/file/' + fileId,
    method: 'get'
  })
}

// 批量添加标签
export function batchAddTag(data) {
  return request({
    url: '/api/v1/data/tag/batch/add',
    method: 'post',
    data: data
  })
}

// 批量移除标签
export function batchRemoveTag(data) {
  return request({
    url: '/api/v1/data/tag/batch/remove',
    method: 'delete',
    data: data
  })
} 