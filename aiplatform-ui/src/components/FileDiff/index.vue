<template>
  <div class="file-diff">
    <!-- 文件信息 -->
    <div class="file-info">
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="info-card source">
            <h4>源文件</h4>
            <p>{{ oldFile.fileName }}</p>
            <p>大小：{{ formatFileSize(oldFile.fileSize) }}</p>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-card target">
            <h4>目标文件</h4>
            <p>{{ newFile.fileName }}</p>
            <p>大小：{{ formatFileSize(newFile.fileSize) }}</p>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 差异类型 -->
    <div class="diff-type">
      <el-tag :type="diffTypeTag">{{ diffTypeText }}</el-tag>
    </div>

    <!-- 差异内容 -->
    <div class="diff-content" v-if="showDiff">
      <!-- 文本差异 -->
      <div v-if="isTextDiff" class="text-diff">
        <div class="diff-header">
          <el-radio-group v-model="diffView" size="small">
            <el-radio-button label="split">分栏视图</el-radio-button>
            <el-radio-button label="unified">统一视图</el-radio-button>
          </el-radio-group>
        </div>
        <div class="diff-body">
          <!-- 分栏视图 -->
          <div v-if="diffView === 'split'" class="split-view">
            <div class="diff-pane left">
              <pre v-for="(line, index) in oldContent" :key="'old-' + index" :class="getLineClass(line)">
                <span class="line-number">{{ line.number }}</span>
                <code>{{ line.content }}</code>
              </pre>
            </div>
            <div class="diff-pane right">
              <pre v-for="(line, index) in newContent" :key="'new-' + index" :class="getLineClass(line)">
                <span class="line-number">{{ line.number }}</span>
                <code>{{ line.content }}</code>
              </pre>
            </div>
          </div>
          <!-- 统一视图 -->
          <div v-else class="unified-view">
            <pre v-for="(line, index) in unifiedContent" :key="index" :class="getLineClass(line)">
              <span class="line-number">{{ line.number }}</span>
              <code>{{ line.content }}</code>
            </pre>
          </div>
        </div>
      </div>

      <!-- 图片差异 -->
      <div v-else-if="isImageDiff" class="image-diff">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="image-container">
              <h4>源图片</h4>
              <el-image :src="oldFile.previewUrl" fit="contain" />
            </div>
          </el-col>
          <el-col :span="12">
            <div class="image-container">
              <h4>目标图片</h4>
              <el-image :src="newFile.previewUrl" fit="contain" />
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 二进制文件差异 -->
      <div v-else class="binary-diff">
        <el-alert
          title="二进制文件差异"
          type="info"
          :description="'文件内容已更改，但无法显示具体差异。'"
          show-icon
        />
      </div>
    </div>

    <!-- 元数据差异 -->
    <div class="metadata-diff" v-if="diffType === 'METADATA' || diffType === 'BOTH'">
      <h4>元数据变更</h4>
      <el-table :data="metadataChanges" style="width: 100%">
        <el-table-column prop="field" label="字段" width="180" />
        <el-table-column prop="oldValue" label="原值" />
        <el-table-column prop="newValue" label="新值" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  oldFile: {
    type: Object,
    required: true
  },
  newFile: {
    type: Object,
    required: true
  },
  diffType: {
    type: String,
    required: true
  }
})

// 差异视图模式：split/unified
const diffView = ref('split')

// 判断差异类型
const isTextDiff = computed(() => {
  const textTypes = ['txt', 'json', 'xml', 'csv', 'md', 'log']
  return textTypes.includes(props.oldFile.fileType?.toLowerCase())
})

const isImageDiff = computed(() => {
  const imageTypes = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp']
  return imageTypes.includes(props.oldFile.fileType?.toLowerCase())
})

// 是否显示差异内容
const showDiff = computed(() => {
  return props.diffType === 'CONTENT' || props.diffType === 'BOTH'
})

// 差异类型标签
const diffTypeTag = computed(() => {
  const tagMap = {
    'CONTENT': 'warning',
    'METADATA': 'info',
    'BOTH': 'danger'
  }
  return tagMap[props.diffType] || 'info'
})

// 差异类型文本
const diffTypeText = computed(() => {
  const textMap = {
    'CONTENT': '内容变更',
    'METADATA': '元数据变更',
    'BOTH': '内容和元数据变更'
  }
  return textMap[props.diffType] || props.diffType
})

// 获取行样式类
const getLineClass = (line) => {
  return {
    'line-added': line.type === 'add',
    'line-deleted': line.type === 'delete',
    'line-normal': line.type === 'normal'
  }
}

// 格式化文件大小
const formatFileSize = (size) => {
  if (!size) return '0 B'
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB'
  if (size < 1024 * 1024 * 1024) return (size / (1024 * 1024)).toFixed(2) + ' MB'
  return (size / (1024 * 1024 * 1024)).toFixed(2) + ' GB'
}

// 计算差异内容
const oldContent = computed(() => {
  return props.oldFile.content?.split('\n').map((line, index) => ({
    number: index + 1,
    content: line,
    type: 'normal'
  })) || []
})

const newContent = computed(() => {
  return props.newFile.content?.split('\n').map((line, index) => ({
    number: index + 1,
    content: line,
    type: 'normal'
  })) || []
})

const unifiedContent = computed(() => {
  // 这里应该实现统一视图的差异计算逻辑
  return []
})

// 元数据变更
const metadataChanges = computed(() => {
  const changes = []
  if (props.oldFile.metadata && props.newFile.metadata) {
    const allKeys = new Set([...Object.keys(props.oldFile.metadata), ...Object.keys(props.newFile.metadata)])
    allKeys.forEach(key => {
      const oldValue = props.oldFile.metadata[key]
      const newValue = props.newFile.metadata[key]
      if (oldValue !== newValue) {
        changes.push({
          field: key,
          oldValue: oldValue || '',
          newValue: newValue || ''
        })
      }
    })
  }
  return changes
})
</script>

<style scoped>
.file-diff {
  padding: 20px;
}
.info-card {
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
}
.info-card.source {
  background-color: #f0f9eb;
}
.info-card.target {
  background-color: #f4f4f5;
}
.info-card h4 {
  margin: 0 0 10px 0;
}
.info-card p {
  margin: 5px 0;
}
.diff-type {
  margin-bottom: 20px;
}
.diff-header {
  margin-bottom: 15px;
}
.diff-body {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}
.split-view {
  display: flex;
}
.diff-pane {
  flex: 1;
  overflow-x: auto;
}
.diff-pane.left {
  border-right: 1px solid #dcdfe6;
}
pre {
  margin: 0;
  padding: 5px 10px;
  font-family: monospace;
  line-height: 1.5;
}
.line-number {
  display: inline-block;
  width: 40px;
  margin-right: 10px;
  color: #909399;
  text-align: right;
}
.line-added {
  background-color: #f0f9eb;
}
.line-deleted {
  background-color: #fef0f0;
}
.image-container {
  text-align: center;
}
.image-container h4 {
  margin-bottom: 10px;
}
.metadata-diff {
  margin-top: 20px;
}
.metadata-diff h4 {
  margin-bottom: 15px;
}
</style> 