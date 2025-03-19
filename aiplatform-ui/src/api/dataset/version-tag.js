import request from '@/utils/request'

// 查询版本标签列表
export function listVersionTags(datasetId, versionId, query) {
  return request({
    url: `/dataset/version/tag/list`,
    method: 'get',
    params: {
      ...query, 
      datasetId,
      versionId
    }
  })
}

// 查询版本标签详细信息
export function getVersionTag(datasetId, versionId, tagId) {
  return request({
    url: `/dataset/version/tag/${tagId}`,
    method: 'get'
  })
}

// 新增版本标签
export function addVersionTag(datasetId, versionId, data) {
  return request({
    url: `/dataset/version/tag`,
    method: 'post',
    data: {
      ...data,
      datasetId,
      versionId
    }
  })
}

// 修改版本标签
export function updateVersionTag(datasetId, versionId, tagId, data) {
  return request({
    url: `/dataset/version/tag`,
    method: 'put',
    data: {
      ...data,
      id: tagId,
      datasetId,
      versionId
    }
  })
}

// 删除版本标签
export function delVersionTag(datasetId, versionId, tagId) {
  return request({
    url: `/dataset/version/tag/${tagId}`,
    method: 'delete'
  })
}

// 根据标签名称查找版本
export function findVersionByTag(datasetId, tagName) {
  return request({
    url: `/dataset/version/tag/find/${datasetId}/${tagName}`,
    method: 'get'
  })
}

// 检查标签名称是否唯一
export function checkTagNameUnique(datasetId, versionId, tagName) {
  return request({
    url: `/dataset/version/tag/check`,
    method: 'get',
    params: {
      datasetId,
      versionId,
      tagName
    }
  })
} 