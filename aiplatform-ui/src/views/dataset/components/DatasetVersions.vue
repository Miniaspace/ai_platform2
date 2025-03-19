<template>
  <div class="dataset-versions">
    <div class="operation-bar">
      <!-- 当前版本信息 -->
      <div class="version-info">
        <span class="label">当前选中版本：</span>
        <span class="value" :class="{ 'no-version': !currentVersionId }">
          {{ currentVersion ? currentVersion.versionName : '未选择' }}
        </span>
      </div>
      
      <!-- 使用原生input实现文件夹上传 -->
      <div class="upload-demo">
        <input
          type="file"
          webkitdirectory
          directory
          multiple
          style="display: none"
          ref="folderInput"
          @change="handleFolderInputChange"
        />
        <el-tooltip :content="!currentVersionId ? '请先选择一个版本' : '上传文件夹（仅支持Chrome/Edge）'">
          <el-button type="primary" @click="triggerFolderUpload" :disabled="!currentVersionId">上传文件夹</el-button>
        </el-tooltip>
      </div>
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
        :disabled="!currentVersionId"
        multiple
      >
        <el-tooltip :content="!currentVersionId ? '请先选择一个版本' : '上传文件'">
          <el-button type="primary" :disabled="!currentVersionId">上传文件</el-button>
        </el-tooltip>
      </el-upload>
      <el-button type="primary" @click="handleCreateVersion">新建版本</el-button>
    </div>

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
      @row-click="handleRowClick"
      :row-class-name="tableRowClassName"
    >
      <el-table-column label="版本号" prop="versionName" width="120" />
      <el-table-column label="描述" prop="versionDesc" min-width="200" show-overflow-tooltip />
      <el-table-column label="状态" prop="status" width="100" align="center">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="文件数量" prop="fileCount" width="100" align="center" />
      <el-table-column label="文件状态" prop="fileStatus" width="100" align="center">
        <template #default="scope">
          <el-tag :type="getFileStatusType(scope.row.fileStatus)">{{ getFileStatusText(scope.row.fileStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" show-overflow-tooltip />
      <el-table-column label="操作" width="380" align="center" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="handleViewFiles(scope.row)" v-hasPermi="['dataset:version:query']">查看文件</el-button>
          <el-button link type="primary" @click="handleManageTags(scope.row)" v-hasPermi="['dataset:version:query']">标签管理</el-button>
          <el-button link type="primary" @click="handleCompare(scope.row)" v-hasPermi="['dataset:version:query']">版本比较</el-button>
          <el-button 
            link 
            type="primary" 
            @click="handleSelectVersion(scope.row)"
            v-if="currentVersionId !== scope.row.versionId"
            v-hasPermi="['dataset:version:switch']"
          >选为当前版本</el-button>
          <el-button 
            link 
            type="success" 
            @click="handlePublishVersion(scope.row)"
            v-if="scope.row.status === 'DRAFT' || scope.row.status === '0'"
            v-hasPermi="['dataset:version:publish']"
          >发布版本</el-button>
          <el-button link type="danger" @click="handleDeleteVersion(scope.row)" v-hasPermi="['dataset:version:remove']">删除</el-button>
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
        <el-form-item label="版本号" prop="version">
          <el-input
            v-model="form.version"
            placeholder="请输入版本号，如v1.0.0"
          />
        </el-form-item>
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
import { ref, reactive, watch, onMounted, computed, defineProps, defineEmits } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  listDatasetVersion,
  getDatasetVersion,
  addDatasetVersion,
  updateDatasetVersion,
  deleteDatasetVersion,
  setCurrentVersion,
  listVersionFiles,
  compareVersions,
  publishVersion
} from '@/api/dataset/version'
import { uploadFile } from '@/api/dataset/version-file'
import { getToken } from '@/utils/auth'
import { FileTypeValidator } from '@/utils/file-type-validator'

const props = defineProps({
  datasetId: {
    type: [String, Number],
    required: true
  }
})

const emit = defineEmits(['version-selected'])

const router = useRouter()
const route = useRoute()

// 上传相关
const uploadUrl = computed(() => {
  if (!currentVersionId.value) {
    return '';
  }
  
  // 尝试使用不同的API路径格式
  const url = `${import.meta.env.VITE_APP_BASE_API}/dataset/file/upload?versionId=${currentVersionId.value}&datasetId=${props.datasetId}`;
  console.log('文件上传地址:', url);
  return url;
})

const headers = computed(() => {
  return {
    Authorization: 'Bearer ' + getToken()
  }
})

// 上传数据
const uploadData = computed(() => {
  return {
    datasetId: props.datasetId,
    versionId: currentVersionId.value,
    chunkNumber: 1,
    totalChunks: 1,
    identifier: '', // 将在beforeUpload中设置
    filename: '', // 将在beforeUpload中设置
    fileType: '', // 将在beforeUpload中设置
    totalSize: 0 // 将在beforeUpload中设置
  }
})

// 当前版本
const currentVersionId = ref(null)

// 数据加载和表单状态
const loading = ref(false)
const total = ref(0)
const versionList = ref([])
const versionForm = ref(null)
const dateRange = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  version: undefined,
  dateRange: []
})

// 表单参数
const form = reactive({
  version: '',
  description: ''
})

// 对话框
const dialog = reactive({
  title: '',
  visible: false
})

// 表单校验规则
const rules = {
  version: [
    { required: true, message: '版本号不能为空', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '版本描述不能为空', trigger: 'blur' }
  ]
}

// 当前选中的版本对象
const currentVersion = ref(null)

// 获取版本列表
const getList = async () => {
  if (!props.datasetId) {
    ElMessage.error('数据集ID不能为空')
    return
  }
  
  loading.value = true
  try {
    // Remove mock data and use actual API call
    const params = {
      ...queryParams
    }
    const response = await listDatasetVersion(props.datasetId, params)
    
    if (response.code === 200) {
      versionList.value = response.rows || []
      total.value = response.total || 0
      
      // 设置当前版本 - 兼容字符串和数字类型
      const current = versionList.value.find(v => v.isCurrent === '1' || v.isCurrent === 1)
      if (current) {
        currentVersionId.value = current.versionId
        currentVersion.value = current
        emit('version-selected', current.versionId)
      } else if (versionList.value.length > 0) {
        currentVersionId.value = versionList.value[0].versionId
        currentVersion.value = versionList.value[0]
        emit('version-selected', versionList.value[0].versionId)
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

// 文件上传前检查
const beforeUpload = (file) => {
  console.log('beforeUpload被调用，当前版本ID:', currentVersionId.value);
  console.log('上传URL:', uploadUrl.value);
  console.log('上传的文件对象:', file);
  
  if (!currentVersionId.value) {
    ElMessage.warning('请先选择或创建一个版本')
    return false
  }
  
  // 设置上传数据
  uploadData.value.identifier = file.name + '_' + file.size + '_' + file.lastModified;
  uploadData.value.filename = file.name;
  uploadData.value.fileType = file.type || getFileTypeFromName(file.name);
  uploadData.value.totalSize = file.size;
  
  // 文件夹上传调试
  console.log('上传文件:', file.name, file.type, '路径:', file.webkitRelativePath || '非文件夹上传');
  
  // 检测是否是文件夹上传
  if (file.webkitRelativePath) {
    console.log('检测到文件夹上传，路径:', file.webkitRelativePath);
    uploadData.value.relativePath = file.webkitRelativePath;
  }
  
  return true
}

const handleProgress = (event, file) => {
  console.log('上传进度:', event.percent, '%', file.name)
}

const handleSuccess = (response, file) => {
  if (response.code === 200) {
    ElMessage.success(`文件 ${file.name} 上传成功`)
    getList()
  } else {
    ElMessage.error(response.msg || `文件 ${file.name} 上传失败`)
  }
}

const handleError = (error, file) => {
  console.error('文件上传错误:', error)
  let errorMsg = '文件上传失败'
  if (error.response) {
    try {
      const response = JSON.parse(error.response)
      errorMsg = response.msg || errorMsg
    } catch (e) {
      // 解析失败，使用默认错误信息
    }
  }
  ElMessage.error(`${file.name}: ${errorMsg}`)
}

// 选择当前版本
const handleSelectVersion = async (row) => {
  try {
    const datasetId = Number(props.datasetId)
    const versionId = Number(row.versionId)
    
    const response = await setCurrentVersion(datasetId, versionId)
    
    if (response.code === 200) {
      ElMessage.success('设置当前版本成功')
      currentVersionId.value = versionId
      currentVersion.value = row
      emit('version-selected', versionId)
      getList()
    } else {
      ElMessage.error(response.msg || '设置当前版本失败')
    }
  } catch (error) {
    console.error('设置当前版本错误:', error)
    ElMessage.error('设置当前版本失败')
  }
}

// 搜索按钮操作
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置查询参数
const resetQuery = () => {
  dateRange.value = []
  queryParams.pageNum = 1
  queryParams.version = undefined
  getList()
}

// 处理页面大小变化
const handleSizeChange = (size) => {
  queryParams.pageSize = size
  getList()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  queryParams.pageNum = page
  getList()
}

// 新建版本按钮
const handleCreateVersion = () => {
  form.version = ''
  form.description = ''
  dialog.title = '新建版本'
  dialog.visible = true
}

// 取消表单
const cancelForm = () => {
  dialog.visible = false
  form.version = ''
  form.description = ''
}

// 提交表单
const submitForm = async (formEl) => {
  if (!formEl) return;
  
  await formEl.validate(async (valid) => {
    if (valid) {
      try {
        // 根据后端控制器要求的DatasetVersion实体结构组装数据
        const versionData = {
          datasetId: Number(props.datasetId),
          versionName: form.version,
          versionDesc: form.description
        }
        
        console.log('提交版本数据:', versionData);  // 添加日志便于调试
        
        const response = await addDatasetVersion(Number(props.datasetId), versionData)
        
        if (response.code === 200) {
          ElMessage.success('新建版本成功')
          dialog.visible = false
          getList() // 刷新列表
          
          // 如果创建成功，自动选择为当前版本
          if (response.data) {
            currentVersionId.value = response.data
            currentVersion.value = response.data
            emit('version-selected', response.data)
          }
        } else {
          ElMessage.error(response.msg || '新建版本失败')
        }
      } catch (error) {
        console.error('新建版本错误:', error)
        ElMessage.error('新建版本失败')
      }
    } else {
      ElMessage.warning('请正确填写表单信息')
      return false
    }
  })
}

// 删除版本
const handleDeleteVersion = (row) => {
  if (row.status === 'PUBLISHED' || row.status === '1') {
    ElMessage.warning('已发布的版本不能删除')
    return
  }

  ElMessageBox.confirm(
    `确定要删除版本"${row.versionName}"吗？\n删除后将同时删除该版本的所有文件、标签和变更记录，且不可恢复。`,
    '警告',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      customClass: 'delete-confirm-dialog'
    }
  ).then(async () => {
    try {
      const datasetId = Number(props.datasetId)
      const versionId = Number(row.versionId)
      
      // 显示加载提示
      const loading = ElMessage({
        message: '正在删除版本，请稍候...',
        duration: 0,
        type: 'info'
      })
      
      const response = await deleteDatasetVersion(datasetId, versionId)
      
      // 关闭加载提示
      loading.close()
      
      if (response.code === 200) {
        ElMessage.success('删除版本成功')
        // 如果删除的是当前选中的版本，清空选中状态
        if (currentVersionId.value === row.versionId) {
          currentVersionId.value = null
          currentVersion.value = null
          emit('version-selected', null)
        }
        getList()
      } else {
        ElMessage.error(response.msg || '删除版本失败')
      }
    } catch (error) {
      console.error('删除版本错误:', error)
      ElMessage.error(error.response?.data?.message || '删除版本失败')
    }
  }).catch(() => {
    // 用户取消删除操作
  })
}

// 查看文件
const handleViewFiles = (row) => {
  try {
    // 确保转换为数字类型
    const dsId = Number(props.datasetId);
    const vrId = Number(row.versionId);
    
    console.log('跳转到文件管理，参数:', { 
      datasetId: dsId, 
      versionId: vrId,
      datasetId类型: typeof dsId,
      versionId类型: typeof vrId 
    });
    
    // 同时使用params和query传递参数，提高兼容性
    router.push({
      name: 'DatasetVersionFiles',
      query: {
        datasetId: dsId,
        versionId: vrId
      }
    });
  } catch (error) {
    console.error('导航到文件页面出错:', error);
    ElMessage.error('导航失败');
  }
}

// 标签管理
const handleManageTags = (row) => {
  if (row.status === 'DRAFT' || row.status === '0') {
    router.push({
      name: 'DatasetDetail',
      params: { id: props.datasetId },
      query: { tab: 'tag', versionId: row.versionId }
    })
  } else {
    ElMessage.warning('只能选择草稿状态的版本进行标签管理')
  }
}

// 版本比较
const handleCompare = (row) => {
  router.push(`/dataset/version/${props.datasetId}/${row.versionId}/compare`)
}

// 发布版本
const handlePublishVersion = async (row) => {
  try {
    const response = await publishVersion(props.datasetId, row.versionId)
    if (response.code === 200) {
      ElMessage.success('版本发布成功')
      getList() // 刷新列表
    } else {
      ElMessage.error(response.msg || '版本发布失败')
    }
  } catch (error) {
    console.error('版本发布错误:', error)
    ElMessage.error('版本发布失败')
  }
}

// 监听datasetId变化
watch(() => props.datasetId, (newValue) => {
  if (newValue) {
    getList()
  }
})

// 在组件挂载时加载数据
onMounted(() => {
  if (props.datasetId) {
    getList()
  }
})

// 触发文件夹选择
const folderInput = ref(null)
const triggerFolderUpload = () => {
  if (!currentVersionId.value) {
    ElMessage.warning('请先选择或创建一个版本')
    return
  }
  folderInput.value.click()
}

// 处理文件夹选择
const handleFolderInputChange = async (e) => {
  const files = e.target.files
  if (!files || files.length === 0) return

  console.log('选择的文件数量:', files.length)
  console.log('第一个文件路径:', files[0].webkitRelativePath)

  // 逐个上传文件
  for (let i = 0; i < files.length; i++) {
    const file = files[i]
    try {
      // 创建表单数据
      const formData = new FormData()
      formData.append('file', file)
      formData.append('datasetId', props.datasetId)
      formData.append('versionId', currentVersionId.value)
      formData.append('path', file.webkitRelativePath.split('/').slice(0, -1).join('/'))
      
      // 添加文件标识和其他必要参数
      const identifier = `${file.name}_${file.size}_${file.lastModified}`
      formData.append('identifier', identifier)
      formData.append('filename', file.name)
      formData.append('fileType', file.type || getFileTypeFromName(file.name))
      formData.append('totalSize', file.size)
      formData.append('chunkNumber', '1')
      formData.append('totalChunks', '1')
      formData.append('relativePath', file.webkitRelativePath)
      
      console.log('上传文件:', {
        name: file.name,
        path: file.webkitRelativePath,
        identifier: identifier,
        datasetId: props.datasetId,
        versionId: currentVersionId.value,
        fileType: file.type || getFileTypeFromName(file.name),
        totalSize: file.size
      })
      
      // 手动发送请求
      const response = await fetch(uploadUrl.value, {
        method: 'POST',
        headers: {
          ...headers.value
        },
        body: formData
      })
      
      const result = await response.json()
      console.log('上传响应:', result)
      
      if (result.code === 200) {
        console.log(`文件 ${file.name} 上传成功`)
      } else {
        console.error(`文件 ${file.name} 上传失败:`, result.msg)
        ElMessage.error(`文件 ${file.name} 上传失败: ${result.msg || '未知错误'}`)
      }
    } catch (error) {
      console.error(`文件 ${file.name} 上传出错:`, error)
      ElMessage.error(`文件 ${file.name} 上传出错`)
    }
  }
  
  ElMessage.success(`成功上传 ${files.length} 个文件`)
  getList() // 刷新列表
  
  // 清空input，允许再次选择相同文件夹
  folderInput.value.value = null
}

// 获取状态类型
const getStatusType = (status) => {
  switch(status) {
    case 'DRAFT':
    case '0':
      return 'info'
    case 'PUBLISHED':
    case '1':
      return 'success'
    case 'DISCARDED':
    case '2':
      return 'danger'
    default:
      return 'info'
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch(status) {
    case 'DRAFT':
    case '0':
      return '草稿'
    case 'PUBLISHED':
    case '1':
      return '已发布'
    case 'DISCARDED':
    case '2':
      return '已废弃'
    default:
      return '未知'
  }
}

// 获取文件状态类型
const getFileStatusType = (fileStatus) => {
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
}

// 获取文件状态文本
const getFileStatusText = (fileStatus) => {
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
}

// 表格行样式
const tableRowClassName = ({ row }) => {
  if (row.versionId === currentVersionId.value) {
    return 'selected-row'
  }
  return ''
}

// 处理行点击
const handleRowClick = (row) => {
  if (row.status === 'DRAFT' || row.status === '0') {
    currentVersionId.value = row.versionId
    currentVersion.value = row
    emit('version-selected', row.versionId)
  } else {
    ElMessage.warning('只能选择草稿状态的版本进行文件上传')
  }
}

// 获取文件类型
const getFileTypeFromName = (filename) => {
  const ext = filename.split('.').pop().toLowerCase();
  const mimeTypes = {
    'jpg': 'image/jpeg',
    'jpeg': 'image/jpeg',
    'png': 'image/png',
    'gif': 'image/gif',
    'pdf': 'application/pdf',
    'doc': 'application/msword',
    'docx': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'xls': 'application/vnd.ms-excel',
    'xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'txt': 'text/plain'
  };
  return mimeTypes[ext] || 'application/octet-stream';
}
</script>

<style scoped>
.dataset-versions {
  width: 100%;
}

.operation-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 20px;
  gap: 10px;
}

.version-info {
  margin-right: auto;
  display: flex;
  align-items: center;
  gap: 8px;
}

.version-info .label {
  color: #606266;
  font-size: 14px;
}

.version-info .value {
  font-size: 14px;
  font-weight: bold;
  color: #409EFF;
}

.version-info .value.no-version {
  color: #909399;
}

.upload-demo {
  margin-right: 10px;
}

:deep(.selected-row) {
  background-color: #f0f9eb;
}

:deep(.el-table .selected-row td) {
  background-color: #f0f9eb !important;
}
</style> 