import request from '@/utils/request'

// 查询文件列表
export function listFile(query) {
  return request({
    url: '/api/v1/data/file/list',
    method: 'get',
    params: query
  })
}

// 查询文件详细
export function getFile(fileId) {
  return request({
    url: '/api/v1/data/file/' + fileId,
    method: 'get'
  })
}

// 新增文件
export function addFile(data) {
  return request({
    url: '/api/v1/data/file',
    method: 'post',
    data: data
  })
}

// 修改文件
export function updateFile(data) {
  return request({
    url: '/api/v1/data/file',
    method: 'put',
    data: data
  })
}

// 删除文件
export function delFile(fileId) {
  return request({
    url: '/api/v1/data/file/' + fileId,
    method: 'delete'
  })
}

// 上传文件
export function uploadFile(data) {
  return request({
    url: '/api/v1/data/file/upload',
    method: 'post',
    data: data
  })
}

// 下载文件
export function downloadFile(fileId) {
  return request({
    url: '/api/v1/data/file/download/' + fileId,
    method: 'get',
    responseType: 'blob'
  })
}

// 预览文件
export function previewFile(fileId) {
  return request({
    url: '/api/v1/data/file/preview/' + fileId,
    method: 'get'
  })
}

// 移动文件
export function moveFile(data) {
  return request({
    url: '/api/v1/data/file/move',
    method: 'put',
    data: data
  })
}

// 复制文件
export function copyFile(data) {
  return request({
    url: '/api/v1/data/file/copy',
    method: 'post',
    data: data
  })
}

// 分享文件
export function shareFile(data) {
  return request({
    url: '/api/v1/data/file/share',
    method: 'post',
    data: data
  })
} 