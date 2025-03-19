<template>
  <div class="file-preview">
    <!-- 图片预览 -->
    <div v-if="isImage" class="image-preview">
      <el-image
        :src="fileInfo.previewUrl"
        :preview-src-list="[fileInfo.previewUrl]"
        fit="contain"
      />
    </div>

    <!-- PDF预览 -->
    <div v-else-if="isPdf" class="pdf-preview">
      <iframe :src="fileInfo.previewUrl" width="100%" height="600"></iframe>
    </div>

    <!-- 文本预览 -->
    <div v-else-if="isText" class="text-preview">
      <pre><code>{{ fileInfo.content }}</code></pre>
    </div>

    <!-- 其他文件类型 -->
    <div v-else class="other-preview">
      <el-empty description="暂不支持该文件类型的预览">
        <el-button type="primary" @click="handleDownload">下载文件</el-button>
      </el-empty>
    </div>

    <!-- 文件信息 -->
    <div class="file-info">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="文件名">{{ fileInfo.fileName }}</el-descriptions-item>
        <el-descriptions-item label="文件大小">{{ formatFileSize(fileInfo.fileSize) }}</el-descriptions-item>
        <el-descriptions-item label="文件类型">{{ fileInfo.fileType }}</el-descriptions-item>
        <el-descriptions-item label="上传时间">{{ parseTime(fileInfo.uploadTime) }}</el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { parseTime } from '@/utils/index'

const props = defineProps({
  fileInfo: {
    type: Object,
    required: true
  }
})

// 判断文件类型
const isImage = computed(() => {
  const imageTypes = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp']
  return imageTypes.includes(props.fileInfo.fileType?.toLowerCase())
})

const isPdf = computed(() => {
  return props.fileInfo.fileType?.toLowerCase() === 'pdf'
})

const isText = computed(() => {
  const textTypes = ['txt', 'json', 'xml', 'csv', 'md', 'log']
  return textTypes.includes(props.fileInfo.fileType?.toLowerCase())
})

// 格式化文件大小
const formatFileSize = (size) => {
  if (!size) return '0 B'
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB'
  if (size < 1024 * 1024 * 1024) return (size / (1024 * 1024)).toFixed(2) + ' MB'
  return (size / (1024 * 1024 * 1024)).toFixed(2) + ' GB'
}

// 下载文件
const handleDownload = () => {
  if (props.fileInfo.downloadUrl) {
    window.open(props.fileInfo.downloadUrl)
  }
}
</script>

<style scoped>
.file-preview {
  padding: 20px;
}
.image-preview {
  text-align: center;
  margin-bottom: 20px;
}
.image-preview .el-image {
  max-width: 100%;
  max-height: 500px;
}
.text-preview {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
.text-preview pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}
.pdf-preview {
  margin-bottom: 20px;
}
.other-preview {
  margin-bottom: 20px;
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.file-info {
  margin-top: 20px;
}
</style> 