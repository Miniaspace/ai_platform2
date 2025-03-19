import request from '@/utils/request'

// 查询目录列表
export function listDirectory(query) {
  return request({
    url: '/api/v1/data/directory/list',
    method: 'get',
    params: query
  })
}

// 查询目录树结构
export function treeDirectory() {
  return request({
    url: '/api/v1/data/directory/tree',
    method: 'get'
  })
}

// 查询目录详细
export function getDirectory(directoryId) {
  return request({
    url: '/api/v1/data/directory/' + directoryId,
    method: 'get'
  })
}

// 新增目录
export function addDirectory(data) {
  return request({
    url: '/api/v1/data/directory',
    method: 'post',
    data: data
  })
}

// 修改目录
export function updateDirectory(data) {
  return request({
    url: '/api/v1/data/directory',
    method: 'put',
    data: data
  })
}

// 删除目录
export function delDirectory(directoryId) {
  return request({
    url: '/api/v1/data/directory/' + directoryId,
    method: 'delete'
  })
}

// 移动目录
export function moveDirectory(data) {
  return request({
    url: '/api/v1/data/directory/move',
    method: 'put',
    data: data
  })
}

// 复制目录
export function copyDirectory(data) {
  return request({
    url: '/api/v1/data/directory/copy',
    method: 'post',
    data: data
  })
}

// 设置目录权限
export function setDirectoryPermission(data) {
  return request({
    url: '/api/v1/data/directory/permission',
    method: 'put',
    data: data
  })
} 