<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>文件管理</span>
          <div class="right-btns">
            <el-upload
              class="upload-demo"
              :action="uploadUrl"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              :show-file-list="false"
            >
              <el-button type="primary" size="small">上传文件</el-button>
            </el-upload>
            <el-button
              type="danger"
              size="small"
              :disabled="!selectedFiles.length"
              @click="handleBatchDelete"
            >批量删除</el-button>
          </div>
        </div>
      </template>
      
      <div class="content">
        <div class="directory-container">
          <directory-tree
            ref="directoryTree"
            @node-click="handleNodeClick"
            @add="handleAddDirectory"
            @delete="handleDeleteDirectory"
          />
        </div>
        <div class="file-container">
          <file-list
            ref="fileList"
            @selection-change="handleSelectionChange"
            @download="handleDownload"
            @preview="handlePreview"
            @delete="handleDelete"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import DirectoryTree from '@/components/DirectoryTree.vue'
import FileList from '@/components/FileList.vue'

export default {
  name: 'FileManagement',
  components: {
    DirectoryTree,
    FileList
  },
  data() {
    return {
      uploadUrl: process.env.VUE_APP_BASE_API + '/data/file/upload',
      selectedFiles: [],
      currentDirectory: null
    }
  },
  created() {
    this.loadDirectoryTree()
    this.loadFileList()
  },
  methods: {
    async loadDirectoryTree() {
      try {
        const response = await this.$http.get('/data/directory/tree')
        this.$refs.directoryTree.loadData(response.data)
      } catch (error) {
        this.$message.error('加载目录树失败')
      }
    },
    async loadFileList(directoryId) {
      try {
        const response = await this.$http.get('/data/file/list', {
          params: { directoryId }
        })
        this.$refs.fileList.loadData(response.data)
      } catch (error) {
        this.$message.error('加载文件列表失败')
      }
    },
    handleNodeClick(data) {
      this.currentDirectory = data
      this.loadFileList(data.id)
    },
    async handleAddDirectory(parentDir) {
      try {
        const { value: name } = await this.$prompt('请输入目录名称', '新建目录', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        })
        if (name) {
          await this.$http.post('/data/directory/create', {
            name,
            parentId: parentDir.id
          })
          this.loadDirectoryTree()
          this.$message.success('创建目录成功')
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('创建目录失败')
        }
      }
    },
    async handleDeleteDirectory({ data }) {
      try {
        await this.$confirm('确认删除该目录吗？', '提示', {
          type: 'warning'
        })
        await this.$http.delete(`/data/directory/${data.id}`)
        this.loadDirectoryTree()
        this.$message.success('删除目录成功')
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除目录失败')
        }
      }
    },
    handleSelectionChange(selection) {
      this.selectedFiles = selection
    },
    async handleDownload(file) {
      try {
        const response = await this.$http.get(`/data/file/download/${file.id}`)
        window.open(response.data.url, '_blank')
      } catch (error) {
        this.$message.error('下载文件失败')
      }
    },
    async handlePreview(file) {
      try {
        const response = await this.$http.get(`/data/file/preview/${file.id}`)
        window.open(response.data.url, '_blank')
      } catch (error) {
        this.$message.error('预览文件失败')
      }
    },
    async handleDelete(file) {
      try {
        await this.$confirm('确认删除该文件吗？', '提示', {
          type: 'warning'
        })
        await this.$http.delete(`/data/file/${file.id}`)
        this.loadFileList(this.currentDirectory?.id)
        this.$message.success('删除文件成功')
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除文件失败')
        }
      }
    },
    async handleBatchDelete() {
      try {
        await this.$confirm('确认删除选中的文件吗？', '提示', {
          type: 'warning'
        })
        const fileIds = this.selectedFiles.map(file => file.id)
        await this.$http.delete('/data/file/batch', {
          data: { ids: fileIds }
        })
        this.loadFileList(this.currentDirectory?.id)
        this.$message.success('批量删除成功')
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('批量删除失败')
        }
      }
    },
    beforeUpload(file) {
      if (!this.currentDirectory) {
        this.$message.warning('请先选择目录')
        return false
      }
      return true
    },
    handleUploadSuccess() {
      this.$message.success('上传成功')
      this.loadFileList(this.currentDirectory?.id)
    },
    handleUploadError() {
      this.$message.error('上传失败')
    }
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.right-btns {
  display: flex;
  gap: 10px;
}
.content {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}
.directory-container {
  width: 300px;
  border-right: 1px solid #eee;
}
.file-container {
  flex: 1;
}
</style> 