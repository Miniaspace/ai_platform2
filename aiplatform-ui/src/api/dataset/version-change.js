import request from '@/utils/request'

// 查询版本变更记录列表
export function listVersionChanges(query) {
  return request({
    url: '/dataset/version/change/list',
    method: 'get',
    params: query
  })
}

// 查询版本的所有变更记录
export function listChangesByVersionId(versionId) {
  return request({
    url: '/dataset/version/change/list/' + versionId,
    method: 'get'
  })
}

// 获取变更记录详细信息
export function getVersionChange(changeId) {
  return request({
    url: '/dataset/version/change/' + changeId,
    method: 'get'
  })
}

// 新增变更记录
export function addVersionChange(data) {
  return request({
    url: '/dataset/version/change',
    method: 'post',
    data: data
  })
}

// 批量新增变更记录
export function batchAddVersionChange(data) {
  return request({
    url: '/dataset/version/change/batch',
    method: 'post',
    data: data
  })
}

// 修改变更记录
export function updateVersionChange(data) {
  return request({
    url: '/dataset/version/change',
    method: 'put',
    data: data
  })
}

// 删除变更记录
export function deleteVersionChange(changeIds) {
  return request({
    url: '/dataset/version/change/' + changeIds,
    method: 'delete'
  })
}

// 记录文件添加变更
export function recordFileAdd(versionId, fileId, changeDesc) {
  return request({
    url: '/dataset/version/change/record/add/' + versionId + '/' + fileId,
    method: 'post',
    data: {
      changeDesc,
      fileStatus: '0' // 正常状态
    }
  })
}

// 记录文件修改变更
export function recordFileModify(versionId, fileId, oldFileId, changeDesc) {
  return request({
    url: '/dataset/version/change/record/modify/' + versionId + '/' + fileId + '/' + oldFileId,
    method: 'post',
    data: {
      changeDesc,
      fileStatus: '2' // 已修改状态
    }
  })
}

// 记录文件删除变更
export function recordFileDelete(versionId, fileId, changeDesc) {
  return request({
    url: '/dataset/version/change/record/delete/' + versionId + '/' + fileId,
    method: 'post',
    data: {
      changeDesc,
      fileStatus: '1' // 已删除状态
    }
  })
}

// 获取文件变更历史
export function getFileChangeHistory(versionId, fileId) {
  return request({
    url: `/dataset/version/change/history/${versionId}/${fileId}`,
    method: 'get'
  })
} 