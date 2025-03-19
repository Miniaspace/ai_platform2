<!-- 数据集版本管理页面 -->
<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>版本管理</span>
          <div class="button-group">
            <el-upload
              class="upload-demo"
              :action="uploadUrl"
              :headers="headers"
              :data="uploadData"
              :before-upload="beforeUpload"
              :on-progress="handleProgress"
              :on-success="handleSuccess"
              :on-error="handleError"
              :show-file-list="false"
              :directory="true"
              multiple
            >
              <el-button type="primary">上传文件夹</el-button>
            </el-upload>
            <el-upload
              class="upload-demo"
              :action="uploadUrl"
              :headers="headers"
              :data="uploadData"
              :before-upload="beforeUpload"
              :on-progress="handleProgress"
              :on-success="handleSuccess"
              :on-error="handleError"
              :show-file-list="false"
              multiple
            >
              <el-button type="primary">上传文件</el-button>
            </el-upload>
            <el-button type="primary" @click="handleCreateVersion">新建版本</el-button>
          </div>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="版本号" prop="version">
          <el-input
            v-model="queryParams.version"
            placeholder="请输入版本号"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 版本列表 -->
      <el-table
        v-loading="loading"
        :data="versionList"
        style="width: 100%"
        border
        row-key="versionId"
      >
        <el-table-column label="版本号" prop="versionNumber" width="120" />
        <el-table-column label="描述" prop="description" min-width="200" show-overflow-tooltip />
        <el-table-column label="文件数量" prop="fileCount" width="100" align="center" />
        <el-table-column label="创建时间" prop="createTime" width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="300" align="center" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleViewFiles(scope.row)">查看文件</el-button>
            <el-button link type="primary" @click="handleManageTags(scope.row)">标签管理</el-button>
            <el-button link type="primary" @click="handleCompare(scope.row)">版本比较</el-button>
            <el-button link type="danger" @click="handleDeleteVersion(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-show="total > 0"
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 30, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 新建版本对话框 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="500px"
      append-to-body
      destroy-on-close
    >
      <el-form
        ref="versionForm"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入版本描述"
            :rows="4"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelForm">取 消</el-button>
          <el-button type="primary" @click="submitForm(versionForm)">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listDatasetVersion, addDatasetVersion, delDatasetVersion } from '@/api/dataset/version'
import { uploadFile } from '@/api/dataset/version-file'
import { getToken } from '@/utils/auth'
import { FileTypeValidator } from '@/utils/file-type-validator'

const route = useRoute()
const router = useRouter()

// 数据集ID
const datasetId = ref(route.params.datasetId)

// 上传相关
const uploadUrl = ref(`${import.meta.env.VITE_APP_BASE_API}/dataset/version/file/upload`)
const headers = {
  Authorization: 'Bearer ' + getToken()
}

const currentVersion = ref(null)
const uploadData = reactive({
  datasetId: computed(() => datasetId.value),
  versionId: computed(() => currentVersion.value),
  chunkNumber: 1,
  chunkSize: 5 * 1024 * 1024, // 5MB chunks
  currentChunkSize: 0,
  totalSize: 0,
  identifier: '',
  filename: '',
  relativePath: '',
  totalChunks: 1,
  fileType: ''
})

// 数据加载和表单状态
const loading = ref(false)
const total = ref(0)
const versionList = ref([])
const versionForm = ref(null)

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  version: undefined,
  dateRange: []
})

// 表单参数
const form = reactive({
  description: ''
})

// 对话框
const dialog = reactive({
  title: '',
  visible: false
})

// 表单校验规则
const rules = {
  description: [
    { required: true, message: '版本描述不能为空', trigger: 'blur' }
  ]
}

// 获取版本列表
const getList = async () => {
  if (!datasetId.value) {
    ElMessage.error('数据集ID不能为空')
    return
  }
  
  console.log('获取版本列表, datasetId:', datasetId.value)
  
  loading.value = true
  try {
    const params = {
      ...queryParams,
      datasetId: datasetId.value
    }
    console.log('请求参数:', params)
    
    const response = await listDatasetVersion(params)
    console.log('API响应:', response)
    
    if (response.code === 200) {
      versionList.value = response.rows || []
      total.value = response.total || 0
      
      // 设置当前版本 - 兼容字符串和数字类型
      const current = versionList.value.find(v => v.isCurrent === '1' || v.isCurrent === 1)
      if (current) {
        currentVersion.value = current.versionId
      } else if (versionList.value.length > 0) {
        currentVersion.value = versionList.value[0].versionId
      }
    } else {
      ElMessage.error(response.msg || '获取版本列表失败')
    }
  } catch (error) {
    console.error('获取版本列表错误:', error)
    ElMessage.error('获取版本列表失败')
  } finally {
    loading.value = false
  }
}

// 处理文件上传
const beforeUpload = (file) => {
  if (!currentVersion.value) {
    ElMessage.warning('请先选择或创建一个版本')
    return false
  }
  
  // Update upload data
  uploadData.totalSize = file.size
  uploadData.filename = file.name
  uploadData.fileType = file.type
  uploadData.identifier = file.name + '-' + file.size // Simple identifier
  uploadData.currentChunkSize = file.size
  uploadData.totalChunks = Math.ceil(file.size / uploadData.chunkSize)
  uploadData.relativePath = file.webkitRelativePath || file.name
  
  // 验证文件类型
  if (!FileTypeValidator.validate(file.type)) {
    ElMessage.error('不支持的文件类型')
    return false
  }

  // 验证文件大小（2GB）
  const maxSize = 2 * 1024 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过2GB')
    return false
  }

  return true
}

const handleProgress = (event, file) => {
  console.log('上传进度：', event.percent)
}

const handleSuccess = (response, file) => {
  if (response.code === 200) {
    ElMessage.success('文件上传成功')
    getList() // 刷新列表
  } else {
    ElMessage.error(response.msg || '文件上传失败')
  }
}

const handleError = (error) => {
  console.error('文件上传错误:', error)
  ElMessage.error('文件上传失败')
}

// 查看文件
const handleViewFiles = (row) => {
  router.push({
    name: 'DatasetDetail',
    params: { id: datasetId.value },
    query: { tab: 'version', versionId: row.versionId }
  })
}

// 标签管理
const handleManageTags = (row) => {
  router.push({
    path: `/dataset/version/${datasetId.value}/${row.versionId}/tags`
  })
}

// 版本比较
const handleCompare = (row) => {
  router.push({
    path: `/dataset/version/${datasetId.value}/${row.versionId}/compare`
  })
}

// 删除版本
const handleDeleteVersion = (row) => {
  ElMessageBox.confirm('确认要删除该版本吗？', '警告', {
    type: 'warning'
  }).then(async () => {
    try {
      const response = await delDatasetVersion(datasetId.value, row.versionId)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        getList()
      } else {
        ElMessage.error(response.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除版本错误:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 新建版本
const handleCreateVersion = () => {
  if (!datasetId.value) {
    ElMessage.error('数据集ID不能为空')
    return
  }
  dialog.title = '新建版本'
  dialog.visible = true
  form.description = ''
}

// 取消表单
const cancelForm = () => {
  dialog.visible = false
  form.description = ''
  if (versionForm.value) {
    versionForm.value.resetFields()
  }
}

// 提交表单
const submitForm = async (formEl) => {
  if (!formEl) return
  if (!datasetId.value) {
    ElMessage.error('数据集ID不能为空')
    return
  }
  
  await formEl.validate(async (valid) => {
    if (valid) {
      try {
        const response = await addDatasetVersion(datasetId.value, {
          description: form.description
        })

        if (response.code === 200) {
          ElMessage.success('创建成功')
          dialog.visible = false
          if (formEl) {
            formEl.resetFields()
          }
          await getList()
        } else {
          ElMessage.error(response.msg || '创建失败')
        }
      } catch (error) {
        console.error('创建版本错误:', error)
        ElMessage.error(error.message || '创建失败')
      }
    }
  })
}

// 搜索和重置
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.version = undefined
  queryParams.dateRange = []
  handleQuery()
}

const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

// 监听路由参数变化
watch(() => route.params.datasetId, (newVal) => {
  if (newVal) {
    datasetId.value = newVal
    getList()
  } else {
    ElMessage.error('数据集ID不能为空')
    router.push('/dataset/list')
  }
})

// 初始化
onMounted(() => {
  if (!datasetId.value) {
    ElMessage.error('数据集ID不能为空')
    router.push('/dataset/list')
    return
  }
  console.log('初始化数据集ID:', datasetId.value)
  getList()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.button-group {
  display: flex;
  gap: 10px;
}
.upload-demo {
  margin-right: 10px;
}
</style> 