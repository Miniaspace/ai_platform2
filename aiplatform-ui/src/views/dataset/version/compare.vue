<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>版本比较</span>
          <div class="version-info">
            <span>源版本：{{ sourceVersion.version }}</span>
            <span class="mx-2">vs</span>
            <span>目标版本：{{ targetVersion.version }}</span>
          </div>
        </div>
      </template>

      <el-table :data="compareList" style="width: 100%">
        <el-table-column prop="fileName" label="文件名" min-width="200" />
        <el-table-column prop="changeType" label="变更类型" width="120">
          <template #default="scope">
            <el-tag :type="getChangeTypeTag(scope.row.changeType)">
              {{ getChangeTypeText(scope.row.changeType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="oldPath" label="原路径" min-width="200" />
        <el-table-column prop="newPath" label="新路径" min-width="200" />
        <el-table-column prop="fileStatus" label="文件状态" width="120">
          <template #default="scope">
            <el-tag :type="getFileStatusType(scope.row.fileStatus)">
              {{ getFileStatusText(scope.row.fileStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button
              size="mini"
              type="text"
              @click="handleViewDiff(scope.row)"
              v-if="scope.row.changeType === 'MODIFIED'"
            >查看差异</el-button>
            <el-button
              size="mini"
              type="text"
              @click="handlePreview(scope.row)"
            >预览</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="total > 0"
        :current-page="queryParams.pageNum"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="queryParams.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 差异对比对话框 -->
    <el-dialog
      :title="diffDialog.title"
      v-model="diffDialog.visible"
      width="80%"
      append-to-body
    >
      <div class="diff-container">
        <div class="diff-header">
          <div class="source">源文件</div>
          <div class="target">目标文件</div>
        </div>
        <div class="diff-content" v-loading="diffDialog.loading">
          <div class="source-content" v-if="diffDialog.sourceUrl">
            <img :src="diffDialog.sourceUrl" alt="Source File" />
          </div>
          <div class="target-content" v-if="diffDialog.targetUrl">
            <img :src="diffDialog.targetUrl" alt="Target File" />
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getDatasetVersion, compareDatasetVersion, getDatasetVersionFilePreviewUrl } from '@/api/dataset'

export default {
  name: 'VersionCompare',
  data() {
    return {
      // 遮罩层
      loading: true,
      // 源版本信息
      sourceVersion: {},
      // 目标版本信息
      targetVersion: {},
      // 比较结果列表
      compareList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10
      },
      // 总条数
      total: 0,
      // 差异对话框
      diffDialog: {
        visible: false,
        loading: false,
        title: '',
        sourceUrl: '',
        targetUrl: ''
      }
    }
  },
  created() {
    this.getVersionInfo()
    this.getList()
  },
  methods: {
    /** 获取版本信息 */
    async getVersionInfo() {
      const { datasetId } = this.$route.params
      const { sourceId, targetId } = this.$route.query
      try {
        const [sourceRes, targetRes] = await Promise.all([
          getDatasetVersion(datasetId, sourceId),
          getDatasetVersion(datasetId, targetId)
        ])
        this.sourceVersion = sourceRes.data
        this.targetVersion = targetRes.data
      } catch (error) {
        this.$message.error(error.response?.data?.message || '获取版本信息失败')
      }
    },

    /** 获取比较结果列表 */
    async getList() {
      this.loading = true
      const { datasetId } = this.$route.params
      const { sourceId, targetId } = this.$route.query
      try {
        const { data } = await compareDatasetVersion(datasetId, sourceId, targetId, {
          page: this.queryParams.pageNum,
          size: this.queryParams.pageSize
        })
        this.compareList = data.items
        this.total = data.total
      } catch (error) {
        this.$message.error(error.response?.data?.message || '获取比较结果失败')
      }
      this.loading = false
    },

    /** 查看差异按钮操作 */
    async handleViewDiff(row) {
      this.diffDialog.visible = true
      this.diffDialog.loading = true
      this.diffDialog.title = `文件差异 - ${row.fileName}`

      const { datasetId } = this.$route.params
      const { sourceId, targetId } = this.$route.query
      try {
        if (row.changeType === 'MODIFIED') {
          // 如果是修改的文件，需要获取两个版本的预览URL
          const [sourceRes, targetRes] = await Promise.all([
            getDatasetVersionFilePreviewUrl(datasetId, sourceId, row.sourceFileId),
            getDatasetVersionFilePreviewUrl(datasetId, targetId, row.targetFileId)
          ])
          this.diffDialog.sourceUrl = sourceRes.data.url
          this.diffDialog.targetUrl = targetRes.data.url
        } else if (row.changeType === 'ADDED') {
          // 如果是新增的文件，只需要获取目标版本的预览URL
          const { data } = await getDatasetVersionFilePreviewUrl(datasetId, targetId, row.targetFileId)
          this.diffDialog.targetUrl = data.url
          this.diffDialog.sourceUrl = ''
        } else if (row.changeType === 'DELETED') {
          // 如果是删除的文件，只需要获取源版本的预览URL
          const { data } = await getDatasetVersionFilePreviewUrl(datasetId, sourceId, row.sourceFileId)
          this.diffDialog.sourceUrl = data.url
          this.diffDialog.targetUrl = ''
        }
      } catch (error) {
        this.$message.error(error.response?.data?.message || '获取文件预览失败')
        this.diffDialog.visible = false
      }

      this.diffDialog.loading = false
    },

    /** 预览文件按钮操作 */
    async handlePreview(row) {
      const { datasetId } = this.$route.params
      const { sourceId, targetId } = this.$route.query
      const versionId = row.changeType === 'DELETED' ? sourceId : targetId
      const fileId = row.changeType === 'DELETED' ? row.sourceFileId : row.targetFileId

      try {
        const { data } = await getDatasetVersionFilePreviewUrl(datasetId, versionId, fileId)
        window.open(data.url, '_blank')
      } catch (error) {
        this.$message.error(error.response?.data?.message || '获取预览地址失败')
      }
    },

    /** 获取变更类型标签样式 */
    getChangeTypeTag(type) {
      const map = {
        'ADDED': 'success',
        'MODIFIED': 'warning',
        'DELETED': 'danger',
        'RENAMED': 'info'
      }
      return map[type] || ''
    },

    /** 获取变更类型文本 */
    getChangeTypeText(type) {
      const map = {
        'ADDED': '新增',
        'MODIFIED': '修改',
        'DELETED': '删除',
        'RENAMED': '重命名'
      }
      return map[type] || type
    },

    /** 获取文件状态类型 */
    getFileStatusType(fileStatus) {
      switch(fileStatus) {
        case '0':
          return 'success'
        case '1':
          return 'danger'
        case '2':
          return 'warning'
        default:
          return 'info'
      }
    },

    /** 获取文件状态文本 */
    getFileStatusText(fileStatus) {
      switch(fileStatus) {
        case '0':
          return '正常'
        case '1':
          return '已删除'
        case '2':
          return '已修改'
        default:
          return '未知'
      }
    },

    /** 每页显示条数改变 */
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.getList()
    },

    /** 当前页改变 */
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getList()
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
.version-info {
  display: flex;
  align-items: center;
  gap: 10px;
}
.mx-2 {
  margin: 0 10px;
}
.diff-container {
  height: 600px;
  display: flex;
  flex-direction: column;
}
.diff-header {
  display: flex;
  border-bottom: 1px solid #ddd;
  background: #f5f7fa;
}
.diff-header > div {
  flex: 1;
  padding: 10px;
  text-align: center;
  font-weight: bold;
}
.diff-content {
  flex: 1;
  overflow: auto;
  padding: 10px;
}
.source-content, .target-content {
  text-align: center;
  padding: 10px;
}
</style> 