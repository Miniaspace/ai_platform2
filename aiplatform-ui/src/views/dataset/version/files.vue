<template>
  <div class="app-container">
    <el-card class="box-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>文件管理</span>
          <div class="right-menu">
            <el-button 
              v-if="checkPermission('dataset:file:upload')"
              type="primary" 
              @click="handleOpenUpload"
            >上传文件</el-button>
            <el-button
              v-if="checkPermission('dataset:file:delete')"
              type="danger"
              plain
              @click="handleBatchDelete"
              :disabled="selections.length === 0"
            >批量删除</el-button>
          </div>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" v-hasPermi="['dataset:file:list']">
        <el-form-item label="文件名称" prop="fileName">
          <el-input
            v-model="queryParams.fileName"
            placeholder="请输入文件名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="文件类型" prop="fileType">
          <el-select v-model="queryParams.fileType" placeholder="请选择文件类型" clearable style="width: 200px">
            <el-option
              v-for="dict in fileTypes"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="上传时间">
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

      <!-- 表格工具栏 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            v-if="checkPermission('dataset:file:upload')"
            type="primary"
            plain
            icon="Upload"
            @click="handleOpenUpload"
          >上传</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            v-if="checkPermission('dataset:file:delete') && selections.length > 0"
            type="danger"
            plain
            icon="Delete"
            @click="handleBatchDelete"
          >删除</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!-- 文件列表 -->
      <el-table
        v-loading="loading"
        :data="fileList"
        @selection-change="handleSelectionChange"
        v-show="fileList && fileList.length > 0"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="文件名" prop="fileName" :show-overflow-tooltip="true">
          <template #default="scope">
            <div class="file-name" @click="handlePreview(scope.row)">
              <el-icon><Document /></el-icon>
              <span>{{ scope.row.fileName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="文件类型" prop="fileType" width="120">
          <template #default="scope">
            <el-tag :type="getFileTypeTag(scope.row.fileType)" class="file-type-tag">
              {{ getFileTypeText(scope.row.fileType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="文件大小" prop="fileSize" width="120">
          <template #default="scope">
            {{ formatFileSize(scope.row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column label="上传时间" prop="createTime" width="160" />
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <el-button
              v-if="checkPermission('dataset:file:preview')"
              type="primary"
              link
              icon="View"
              @click="handlePreview(scope.row)"
            >预览</el-button>
            <el-button
              v-if="checkPermission('dataset:file:download')"
              type="primary"
              link
              icon="Download"
              @click="handleDownload(scope.row)"
            >下载</el-button>
            <el-button
              v-if="checkPermission('dataset:file:delete')"
              type="primary"
              link
              icon="Delete"
              @click="handleDelete(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 空状态 -->
      <div v-if="!loading && (!fileList || fileList.length === 0)" class="file-list-empty">
        <el-icon><Document /></el-icon>
        <span>暂无文件记录</span>
        <el-button v-if="checkPermission('dataset:file:upload')" type="primary" @click="handleOpenUpload" style="margin-top: 20px">
          <el-icon><Upload /></el-icon>上传文件
        </el-button>
      </div>

      <!-- 分页 -->
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 标签管理对话框 -->
    <el-dialog
      title="标签管理"
      v-model="tagDialog.visible"
      width="600px"
      append-to-body
    >
      <el-form :inline="true" class="tag-form">
        <el-form-item label="标签">
          <el-select
            v-model="tagDialog.selectedTags"
            multiple
            filterable
            placeholder="请选择标签"
            style="width: 400px"
          >
            <el-option
              v-for="tag in tagDialog.options"
              :key="tag.id"
              :label="tag.name"
              :value="tag.id"
            >
              <span>{{ tag.name }}</span>
              <el-tag
                size="small"
                :type="getTagType(tag.type)"
                class="mx-1"
              >{{ getTagTypeText(tag.type) }}</el-tag>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="tagDialog.visible = false">取 消</el-button>
          <el-button type="primary" @click="submitTags">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog
      title="文件预览"
      v-model="previewDialog.visible"
      width="800px"
      append-to-body
    >
      <div v-if="previewDialog.loading" class="preview-loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>正在加载预览...</span>
      </div>
      <div v-else-if="previewDialog.error" class="preview-error">
        <el-icon><CircleClose /></el-icon>
        <span>{{ previewDialog.error }}</span>
      </div>
      <div v-else class="preview-content">
        <img v-if="previewDialog.url.endsWith('.jpg') || previewDialog.url.endsWith('.png') || previewDialog.url.endsWith('.jpeg') || previewDialog.url.endsWith('.gif') || previewDialog.url.includes('placekitten.com')" :src="previewDialog.url" alt="预览图片" class="preview-image" />
        <iframe v-else-if="previewDialog.url.endsWith('.pdf') || previewDialog.url.includes('dummy.pdf')" :src="previewDialog.url" class="preview-iframe"></iframe>
        <video v-else-if="previewDialog.url.endsWith('.mp4') || previewDialog.url.includes('mp4')" :src="previewDialog.url" controls class="preview-video"></video>
        <audio v-else-if="previewDialog.url.endsWith('.mp3') || previewDialog.url.endsWith('.wav')" :src="previewDialog.url" controls class="preview-audio"></audio>
        <div v-else class="preview-unsupported">
          <el-icon><Warning /></el-icon>
          <span>此文件类型无法在浏览器中预览</span>
          <el-button type="primary" size="small" @click="window.open(previewDialog.url)">下载查看</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 上传文件对话框 -->
    <el-dialog
      title="上传文件"
      v-model="showUploadDialog"
      width="500px"
      append-to-body
    >
      <el-upload
        ref="uploadRef"
        :action="uploadUrl"
        :data="uploadData"
        :before-upload="beforeUpload"
        :http-request="customUpload"
        multiple
        :show-file-list="true"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
      >
        <el-button type="primary">选择文件</el-button>
        <template #tip>
          <div class="el-upload__tip">
            支持各种文件类型，单个文件大小不超过100MB
          </div>
        </template>
      </el-upload>
    </el-dialog>
  </div>
</template>

<script setup name="DatasetVersionFiles">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getToken } from '@/utils/auth'
import { FileTypeValidator } from '@/utils/file-type-validator'
import request from '@/utils/request'
import {
  uploadDatasetVersionFile,
  delDatasetVersionFile,
  batchDelDatasetVersionFile,
  getDatasetVersionFilePreviewUrl,
  getDatasetVersionFileDownloadUrl,
  getDatasetVersionFileTags,
  updateDatasetVersionFileTags
} from '@/api/dataset'
import { 
  listVersionFiles, 
  listFilesByVersionId,
  uploadFile as uploadVersionFile,
  deleteVersionFile,
  batchUpdateFileStatus,
  previewFile,
  downloadFile,
  getFilePreviewInfo
} from '@/api/dataset/version-file'
import useUserStore from '@/store/modules/user'
import { 
  Search, 
  Refresh, 
  Upload, 
  Download, 
  Delete, 
  View, 
  Loading, 
  CircleClose,
  Warning,
  Document
} from '@element-plus/icons-vue'
import { checkPermission } from '@/utils/permission'

const route = useRoute()
// 增加props定义，接收路由参数
const props = defineProps({
  datasetId: {
    type: [String, Number]
  },
  versionId: {
    type: [String, Number]
  }
})

// 增加日志
console.log('路由参数:', route.params)
console.log('Props参数:', props)

// 使用多种方式获取参数，提高兼容性
const datasetId = computed(() => {
  // 尝试所有可能的参数来源
  const id = props.datasetId || 
             route.params.datasetId || 
             route.query.datasetId || 
             route.params.id;
             
  const numId = id ? Number(id) : undefined;
  console.log('获取到的数据集ID:', numId, '原始值:', id, '类型:', typeof numId);
  return numId;
})

const versionId = computed(() => {
  // 尝试所有可能的参数来源
  const id = props.versionId || 
             route.params.versionId || 
             route.query.versionId;
             
  const numId = id ? Number(id) : undefined;
  console.log('获取到的版本ID:', numId, '原始值:', id, '类型:', typeof numId);
  return numId;
})

// 状态数据
const loading = ref(false)
const selections = ref([])
const total = ref(0)
const fileList = ref([])
const dateRange = ref([])
const showUploadDialog = ref(false)
const uploadRef = ref(null)
const showSearch = ref(true)
const formRef = ref(null)

// 全局错误处理状态
const globalError = reactive({
  has: false,
  message: '',
  type: 'warning',
  details: null
})

// 清除全局错误
const clearGlobalError = () => {
  globalError.has = false
  globalError.message = ''
  globalError.details = null
}

// 设置全局错误
const setGlobalError = (message, details = null, type = 'warning') => {
  globalError.has = true
  globalError.message = message
  globalError.details = details
  globalError.type = type
  
  console.error('全局错误:', message, details)
}

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  fileName: undefined,
  fileType: undefined,
  beginTime: undefined,
  endTime: undefined
})

// 上传相关
const uploadUrl = computed(() => {
  if (!datasetId.value || !versionId.value) {
    console.warn('未找到数据集ID或版本ID，无法构建上传URL')
    return ''
  }
  // 在模拟模式下返回一个假的上传URL
  return '/mock-upload-url'
})

const headers = computed(() => ({
  Authorization: 'Bearer ' + getToken()
}))

const uploadData = computed(() => {
  return {
    datasetId: datasetId.value,
    versionId: versionId.value
  }
})

// 文件类型选项
const fileTypes = [
  { value: 'IMAGE', label: '图片' },
  { value: 'VIDEO', label: '视频' },
  { value: 'AUDIO', label: '音频' },
  { value: 'TEXT', label: '文本' },
  { value: 'PDF', label: 'PDF' },
  { value: 'OFFICE', label: 'Office文档' },
  { value: 'OTHER', label: '其他' }
]

// 对话框状态
const tagDialog = reactive({
  visible: false,
  loading: false,
  currentFile: null,
  selectedTags: [],
  options: []
})

const previewDialog = reactive({
  visible: false,
  loading: false,
  url: '',
  error: ''
})

// 计算属性
const canUpload = computed(() => {
  return datasetId.value && versionId.value
})

// 创建一个统一的API请求工具函数
const apiRequest = async (options, fallbackOptions = [], mockData = null) => {
  const allOptions = [options, ...fallbackOptions];
  let lastError;
  
  for (const opt of allOptions) {
    try {
      return await request(opt);
    } catch (error) {
      lastError = error;
      console.error(`API请求失败: ${opt.url}`, error);
    }
  }
  
  // 所有请求都失败，返回模拟数据
  if (mockData) {
    console.warn('使用模拟数据');
    return { code: 200, rows: mockData, total: mockData.length };
  }
  
  throw lastError;
};

// 方法定义
const getList = async () => {
  try {
    if (!datasetId.value || !versionId.value) {
      ElMessage.warning('缺少数据集ID或版本ID参数')
      return
    }
    
    loading.value = true
    
    // 准备查询参数
    const query = {
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
      fileName: queryParams.fileName,
      fileType: queryParams.fileType
    }

    if (dateRange.value && dateRange.value.length === 2) {
      query.beginTime = dateRange.value[0]
      query.endTime = dateRange.value[1]
    }
    
    console.log('发送请求参数:', {
      datasetId: datasetId.value,
      versionId: versionId.value,
      query
    })

    // 使用listFilesByVersionId替代listVersionFiles
    const response = await listFilesByVersionId(versionId.value)
    
    if (response.code === 200) {
      // 在前端进行过滤和分页
      let filteredData = response.rows || []
      
      // 应用过滤条件
      if (query.fileName) {
        filteredData = filteredData.filter(item => 
          item.fileName.toLowerCase().includes(query.fileName.toLowerCase())
        )
      }
      
      if (query.fileType) {
        filteredData = filteredData.filter(item => 
          item.fileType === query.fileType
        )
      }
      
      // 应用分页
      const startIndex = (query.pageNum - 1) * query.pageSize
      const endIndex = startIndex + query.pageSize
      
      // 更新数据
      fileList.value = filteredData.slice(startIndex, endIndex).map(item => normalizeData(item))
      total.value = filteredData.length
    } else {
      ElMessage.error(response.msg || '获取文件列表失败')
    }
    
  } catch (error) {
    console.error('获取文件列表错误:', error)
    ElMessage.error(`数据加载失败: ${error.message || '未知错误'}`)
  } finally {
    loading.value = false
  }
}

// 上传前检查权限
const beforeUpload = (file) => {
  if (!datasetId.value || !versionId.value) {
    ElMessage.warning('请先选择一个版本')
    return false
  }
  
  console.log('上传前检查:', {
    datasetId: datasetId.value,
    versionId: versionId.value,
    file: file.name
  })
  
  // 在模拟模式下，直接返回true允许上传
  return true
}

const handleProgress = (event, file) => {
  console.log('上传进度：', event.percent)
}

const handleSuccess = (response, file) => {
  if (response.code === 200) {
    ElMessage.success('文件上传成功')
    getList()
  } else {
    ElMessage.error(response.msg || '文件上传失败')
  }
}

const handleError = (error) => {
  console.error('文件上传错误:', error)
  ElMessage.error('文件上传失败')
}

// 文件操作方法
const handlePreview = async (row) => {
  if (!checkPermission('dataset:file:preview')) {
    ElMessage.error('您没有预览文件的权限')
    return
  }

  try {
    previewDialog.loading = true
    previewDialog.visible = true
    previewDialog.error = ''

    // 模拟预览URL（根据文件类型生成不同的预览内容）
    let previewUrl;
    
    if (row.fileType === 'IMAGE') {
      // 图片类型使用模拟图片
      previewUrl = 'https://placekitten.com/800/600';
    } else if (row.fileType === 'PDF') {
      // PDF类型使用模拟PDF
      previewUrl = 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf';
    } else if (row.fileType === 'VIDEO') {
      // 视频类型使用模拟视频
      previewUrl = 'https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4';
    } else {
      // 其他类型显示无法预览
      previewDialog.error = '该文件类型暂不支持预览';
      previewUrl = '';
    }
    
    setTimeout(() => {
      previewDialog.url = previewUrl;
      previewDialog.loading = false;
    }, 500);
    
  } catch (error) {
    console.error('获取预览地址错误:', error)
    previewDialog.error = '获取预览地址失败'
    previewDialog.loading = false;
  }
}

const handleDownload = async (row) => {
  if (!checkPermission('dataset:file:download')) {
    ElMessage.error('您没有下载文件的权限')
    return
  }

  try {
    // 模拟下载行为
    ElMessage.success(`已开始下载文件: ${row.fileName}`);
    
    // 创建一个简单的文本文件下载
    const element = document.createElement('a');
    const file = new Blob([`这是模拟的文件内容 - ${row.fileName}`], {type: 'text/plain'});
    element.href = URL.createObjectURL(file);
    element.download = row.fileName;
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
    
  } catch (error) {
    console.error('文件下载错误:', error)
    ElMessage.error('下载失败')
  }
}

const handleDelete = (row) => {
  if (!checkPermission('dataset:file:delete')) {
    ElMessage.error('您没有删除文件的权限')
    return
  }

  ElMessageBox.confirm('是否确认删除该文件?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 模拟删除操作
      fileList.value = fileList.value.filter(item => item.fileId !== row.fileId);
      total.value = total.value - 1;
      ElMessage.success('删除成功');
    } catch (error) {
      console.error('删除文件错误:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleBatchDelete = () => {
  if (!checkPermission('dataset:file:delete')) {
    ElMessage.error('您没有删除文件的权限')
    return
  }

  const fileIds = selections.value.map(item => item.fileId)
  if (fileIds.length === 0) {
    return
  }

  ElMessageBox.confirm('是否确认批量删除选中的文件?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 模拟批量删除
      fileList.value = fileList.value.filter(item => !fileIds.includes(item.fileId));
      total.value = total.value - fileIds.length;
      ElMessage.success('批量删除成功');
      selections.value = [];
    } catch (error) {
      console.error('批量删除文件错误:', error)
      ElMessage.error('批量删除失败')
    }
  }).catch(() => {})
}

// 标签相关方法
const handleTags = async (row) => {
  if (!checkPermission('dataset:file:tag')) {
    ElMessage.error('您没有管理文件标签的权限')
    return
  }

  try {
    tagDialog.loading = true
    tagDialog.visible = true
    tagDialog.currentFile = row

    const response = await getDatasetVersionFileTags(datasetId.value, versionId.value, row.fileId)
    if (response.code === 200) {
      tagDialog.options = response.data.tags || []
      tagDialog.selectedTags = row.tags?.map(tag => tag.id) || []
    } else {
      ElMessage.error(response.msg || '获取标签列表失败')
    }
  } catch (error) {
    console.error('获取标签列表错误:', error)
    ElMessage.error('获取标签列表失败')
  } finally {
    tagDialog.loading = false
  }
}

const submitTags = async () => {
  if (!tagDialog.currentFile) return

  try {
    const response = await updateDatasetVersionFileTags(
      datasetId.value,
      versionId.value,
      tagDialog.currentFile.fileId,
      tagDialog.selectedTags
    )

    if (response.code === 200) {
      ElMessage.success('更新标签成功')
      tagDialog.visible = false
      getList()
    } else {
      ElMessage.error(response.msg || '更新标签失败')
    }
  } catch (error) {
    console.error('更新标签错误:', error)
    ElMessage.error('更新标签失败')
  }
}

// 表格相关方法
const handleSelectionChange = (selection) => {
  selections.value = selection
}

const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  dateRange.value = []
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    fileName: undefined,
    fileType: undefined,
    beginTime: undefined,
    endTime: undefined
  })
  handleQuery()
}

// 工具方法
const formatFileSize = (size) => {
  if (!size) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  let index = 0
  while (size >= 1024 && index < units.length - 1) {
    size /= 1024
    index++
  }
  return size.toFixed(2) + ' ' + units[index]
}

const getFileTypeTag = (type) => {
  const map = {
    IMAGE: 'success',
    VIDEO: 'warning',
    AUDIO: 'info',
    TEXT: 'primary',
    PDF: 'danger',
    OFFICE: '',
    OTHER: 'info'
  }
  return map[type] || 'info'
}

const getFileTypeText = (type) => {
  const fileType = fileTypes.find(item => item.value === type)
  return fileType ? fileType.label : '其他'
}

const getFileStatusType = (status) => {
  // 处理字符串和数值格式
  if (status === 'NORMAL' || status === '0' || status === 0) {
    return 'success'
  } else if (status === 'DELETED' || status === '1' || status === 1) {
    return 'danger'
  } else if (status === 'PROCESSING' || status === '2' || status === 2) {
    return 'warning'
  }
  return ''
}

const getFileStatusText = (status) => {
  // 处理字符串和数值格式
  if (status === 'NORMAL' || status === '0' || status === 0) {
    return '正常'
  } else if (status === 'DELETED' || status === '1' || status === 1) {
    return '已删除'
  } else if (status === 'PROCESSING' || status === '2' || status === 2) {
    return '处理中'
  }
  return status ? status.toString() : '未知'
}

const getTagType = (type) => {
  const map = {
    SYSTEM: 'primary',
    USER: 'success',
    AUTO: 'warning'
  }
  return map[type] || ''
}

const getTagTypeText = (type) => {
  const map = {
    SYSTEM: '系统',
    USER: '用户',
    AUTO: '自动'
  }
  return map[type] || type
}

const normalizeData = (item) => {
  // 标准化字段名称
  const result = { ...item };
  
  // 处理驼峰与下划线字段
  const fieldMappings = {
    file_id: 'fileId',
    file_name: 'fileName',
    file_type: 'fileType',
    file_size: 'fileSize',
    file_status: 'fileStatus',
    create_by: 'createBy',
    create_time: 'createTime'
  };
  
  Object.entries(fieldMappings).forEach(([snakeCase, camelCase]) => {
    if (result[snakeCase] !== undefined && result[camelCase] === undefined) {
      result[camelCase] = result[snakeCase];
    }
  });
  
  return result;
};

// 生命周期钩子
onMounted(() => {
  console.log('组件加载，参数检查:', {
    datasetId: datasetId.value,
    versionId: versionId.value,
    路由参数: route.params,
    查询参数: route.query,
    路由全路径: route.fullPath
  });
  
  // 延迟一点执行，确保参数已经准备好
  setTimeout(() => {
    if (datasetId.value && versionId.value) {
      console.log('开始加载文件列表，参数已确认');
      getList();
    } else {
      console.error('参数错误：缺少数据集ID或版本ID', {
        datasetId: datasetId.value,
        versionId: versionId.value
      });
      ElMessage.error('参数错误：缺少数据集ID或版本ID');
    }
  }, 100);
})

// 添加模板中需要的方法
const handleUploadSuccess = () => {
  ElMessage.success('上传成功')
  getList()
  uploadRef.value?.clearFiles()
  showUploadDialog.value = false
}

const handleUploadError = (error) => {
  console.error('上传错误:', error)
  ElMessage.error('上传失败，请稍后重试')
}

// 打开上传对话框
const handleOpenUpload = () => {
  if (!checkPermission('dataset:file:upload')) {
    ElMessage.error('您没有上传文件的权限')
    return
  }
  
  if (!datasetId.value || !versionId.value) {
    ElMessage.warning('请先选择一个版本')
    return
  }
  
  showUploadDialog.value = true
}

// 自定义上传方法，实现模拟上传
const customUpload = (options) => {
  const { file, onSuccess, onError } = options
  
  // 如果文件太大，模拟失败
  if (file.size > 100 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过100MB')
    onError(new Error('文件大小超限'))
    return
  }
  
  // 模拟上传处理过程
  setTimeout(() => {
    // 创建一个新的模拟文件记录
    const newFile = {
      fileId: Math.floor(Math.random() * 10000000),
      fileName: file.name,
      fileType: file.name.split('.').pop().toUpperCase(),
      fileSize: file.size,
      createTime: new Date().toISOString()
    }
    
    // 添加到文件列表
    fileList.value.unshift(newFile)
    total.value += 1
    
    // 上传成功
    onSuccess()
    
    // 提示成功
    ElMessage.success(`文件 ${file.name} 上传成功`)
  }, 1500) // 模拟网络延迟
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.right-menu {
  display: flex;
  gap: 10px;
}

.file-name {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #409EFF;
}

.file-name:hover {
  text-decoration: underline;
}

.file-name i {
  margin-right: 5px;
}

.preview-container {
  width: 100%;
  height: 70vh;
  overflow: hidden;
}

.preview-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

.preview-error {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #f56c6c;
  font-size: 16px;
}

.tag-form {
  padding: 20px;
}

.mx-1 {
  margin: 0 4px;
}

:deep(.el-upload) {
  width: auto !important;
}

:deep(.delete-btn) {
  color: #f56c6c;
}

.top-right-button {
  float: right;
  margin-left: 10px;
}

.file-list-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 0;
  color: #909399;
}

.file-list-empty i {
  font-size: 48px;
  margin-bottom: 10px;
}

.file-type-tag {
  margin-right: 5px;
}

.preview-loading, .preview-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: #909399;
}

.preview-loading i, .preview-error i {
  font-size: 32px;
  margin-bottom: 10px;
}

.preview-content {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
  overflow: auto;
}

.preview-image {
  max-width: 100%;
  max-height: 500px;
  object-fit: contain;
}

.preview-iframe {
  width: 100%;
  height: 500px;
  border: none;
}

.preview-video, .preview-audio {
  width: 100%;
  max-height: 500px;
}

.preview-unsupported {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: #909399;
}

.preview-unsupported i {
  font-size: 32px;
  margin-bottom: 10px;
}

.preview-unsupported button {
  margin-top: 20px;
}
</style>