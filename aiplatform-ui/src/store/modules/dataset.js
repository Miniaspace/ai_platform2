import { defineStore } from 'pinia'

export const useDatasetStore = defineStore('dataset', {
  state: () => ({
    // 当前数据集信息
    currentDataset: {
      datasetId: undefined,
      datasetName: '',
      description: '',
      createTime: '',
      updateTime: ''
    },
    // 当前版本信息
    currentVersion: {
      versionId: undefined,
      versionName: '',
      description: '',
      status: '',
      createTime: ''
    },
    // 版本列表
    versionList: [],
    // 标签列表
    tagList: [],
    // 文件列表
    fileList: []
  }),

  getters: {
    // 获取当前数据集ID
    getCurrentDatasetId: (state) => state.currentDataset.datasetId,
    // 获取当前版本ID
    getCurrentVersionId: (state) => state.currentVersion.versionId,
    // 获取版本状态
    getVersionStatus: (state) => state.currentVersion.status,
    // 判断是否可以编辑版本
    canEditVersion: (state) => state.currentVersion.status !== 'PUBLISHED',
    // 获取标签数量
    getTagCount: (state) => state.tagList.length,
    // 获取文件数量
    getFileCount: (state) => state.fileList.length
  },

  actions: {
    // 设置当前数据集
    setCurrentDataset(dataset) {
      this.currentDataset = dataset
    },

    // 设置当前版本
    setCurrentVersion(version) {
      this.currentVersion = version
    },

    // 更新版本列表
    setVersionList(versions) {
      this.versionList = versions
    },

    // 更新标签列表
    setTagList(tags) {
      this.tagList = tags
    },

    // 更新文件列表
    setFileList(files) {
      this.fileList = files
    },

    // 添加标签
    addTag(tag) {
      this.tagList.push(tag)
    },

    // 删除标签
    removeTag(tagId) {
      const index = this.tagList.findIndex(tag => tag.tagId === tagId)
      if (index !== -1) {
        this.tagList.splice(index, 1)
      }
    },

    // 添加文件
    addFiles(files) {
      this.fileList.push(...files)
    },

    // 删除文件
    removeFiles(fileIds) {
      this.fileList = this.fileList.filter(file => !fileIds.includes(file.fileId))
    },

    // 重置状态
    resetState() {
      this.currentDataset = {
        datasetId: undefined,
        datasetName: '',
        description: '',
        createTime: '',
        updateTime: ''
      }
      this.currentVersion = {
        versionId: undefined,
        versionName: '',
        description: '',
        status: '',
        createTime: ''
      }
      this.versionList = []
      this.tagList = []
      this.fileList = []
    }
  }
}) 