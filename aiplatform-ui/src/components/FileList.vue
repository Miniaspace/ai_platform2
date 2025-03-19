<template>
  <div class="file-list">
    <el-table
      :data="fileList"
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55"> </el-table-column>
      <el-table-column prop="name" label="文件名" min-width="200">
        <template #default="scope">
          <span class="file-name">
            <i :class="getFileIcon(scope.row)" class="mr-2"></i>
            {{ scope.row.name }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="size" label="大小" width="120">
        <template #default="scope">
          {{ formatFileSize(scope.row.size) }}
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="修改时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.updateTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button
            size="mini"
            type="text"
            @click="handleDownload(scope.row)"
          >下载</el-button>
          <el-button
            size="mini"
            type="text"
            @click="handlePreview(scope.row)"
          >预览</el-button>
          <el-button
            size="mini"
            type="text"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: 'FileList',
  data() {
    return {
      fileList: [],
      selectedFiles: []
    }
  },
  methods: {
    handleSelectionChange(selection) {
      this.selectedFiles = selection
      this.$emit('selection-change', selection)
    },
    handleDownload(file) {
      this.$emit('download', file)
    },
    handlePreview(file) {
      this.$emit('preview', file)
    },
    handleDelete(file) {
      this.$emit('delete', file)
    },
    getFileIcon(file) {
      const iconMap = {
        'image': 'el-icon-picture',
        'pdf': 'el-icon-document',
        'text': 'el-icon-document-copy',
        'video': 'el-icon-video-camera',
        'audio': 'el-icon-headset',
        'other': 'el-icon-document'
      }
      return iconMap[file.type] || iconMap.other
    },
    formatFileSize(size) {
      if (!size) return '0 B'
      const units = ['B', 'KB', 'MB', 'GB', 'TB']
      let index = 0
      while (size >= 1024 && index < units.length - 1) {
        size /= 1024
        index++
      }
      return `${size.toFixed(2)} ${units[index]}`
    },
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleString()
    },
    loadData(data) {
      this.fileList = data
    }
  }
}
</script>

<style scoped>
.file-list {
  padding: 10px;
}
.file-name {
  display: flex;
  align-items: center;
}
.mr-2 {
  margin-right: 8px;
}
</style> 