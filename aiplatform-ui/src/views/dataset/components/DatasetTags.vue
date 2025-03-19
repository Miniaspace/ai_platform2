<template>
  <div class="dataset-tags">
    <div class="operation-bar">
      <el-button type="primary" @click="handleAdd">新建标签</el-button>
    </div>

    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true">
      <el-form-item label="标签名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入标签名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="标签类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择标签类型" clearable style="width: 200px">
          <el-option
            v-for="dict in tagTypes"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">搜索</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 标签列表 -->
    <el-table
      v-loading="loading"
      :data="tagList"
      style="width: 100%"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="标签名称" prop="name" min-width="150" />
      <el-table-column label="标签类型" prop="type" width="120">
        <template #default="scope">
          <el-tag :type="getTagType(scope.row.type)">
            {{ getTagTypeText(scope.row.type) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="描述" prop="description" min-width="200" show-overflow-tooltip />
      <el-table-column label="文件数量" prop="fileCount" width="100" align="center" />
      <el-table-column label="创建者" prop="createBy" width="120" />
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="handleUpdate(scope.row)"
          >编辑</el-button>
          <el-button
            link
            type="primary"
            @click="handleViewFiles(scope.row)"
          >查看文件</el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row)"
          >删除</el-button>
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

    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="500px"
      append-to-body
    >
      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="名称" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入标签名称"
          />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择标签类型">
            <el-option
              v-for="dict in tagTypes"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入标签描述"
            :rows="4"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted, defineProps, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listDatasetVersionTag, addDatasetVersionTag, updateDatasetVersionTag, delDatasetVersionTag } from '@/api/dataset'

const props = defineProps({
  datasetId: {
    type: [String, Number],
    required: true
  }
})

const route = useRoute()
const router = useRouter()

const currentDatasetId = computed(() => props.datasetId || route.params.id)

// 当前版本ID
const currentVersionId = ref(route.query.versionId)

// 数据定义
const loading = ref(false)
const total = ref(0)
const tagList = ref([])
const queryForm = ref(null)
const tagForm = ref(null)

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  datasetId: computed(() => currentDatasetId.value),
  name: undefined,
  type: undefined
})

// 标签类型选项
const tagTypes = [
  { value: 'CATEGORY', label: '分类' },
  { value: 'PROPERTY', label: '属性' },
  { value: 'STATUS', label: '状态' },
  { value: 'CUSTOM', label: '自定义' }
]

// 表单参数
const form = reactive({
  id: undefined,
  name: undefined,
  type: undefined,
  description: undefined
})

// 对话框
const dialog = reactive({
  title: '',
  visible: false
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '标签名称不能为空', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '标签类型不能为空', trigger: 'change' }
  ]
}

// 获取标签类型显示文本
const getTagTypeText = (type) => {
  const typeObj = tagTypes.find(item => item.value === type)
  return typeObj ? typeObj.label : '未知类型'
}

// 获取标签类型对应的标签样式
const getTagType = (type) => {
  const typeMap = {
    'CATEGORY': '',
    'PROPERTY': 'success',
    'STATUS': 'warning',
    'CUSTOM': 'info'
  }
  return typeMap[type] || ''
}

// 获取标签列表
const getList = async () => {
  if (!props.datasetId) {
    ElMessage.error('数据集ID不能为空')
    return
  }
  
  if (!currentVersionId.value) {
    ElMessage.warning('请先选择一个版本')
    return
  }

  loading.value = true
  try {
    // Prepare query parameters properly
    const params = {
      datasetId: props.datasetId,
      versionId: currentVersionId.value,
      name: queryParams.name,
      type: queryParams.type,
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize
    }

    const response = await listDatasetVersionTag(props.datasetId, currentVersionId.value, params)
    
    if (response.code === 200) {
      tagList.value = response.rows || []
      total.value = response.total || 0
    } else {
      ElMessage.error(response.msg || '获取标签列表失败')
    }
  } catch (error) {
    console.error('获取标签列表错误:', error)
    ElMessage.error('获取标签列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索按钮操作
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置查询参数
const resetQuery = () => {
  queryForm.value?.resetFields()
  queryParams.pageNum = 1
  queryParams.pageSize = 10
  queryParams.name = undefined
  queryParams.type = undefined
  handleQuery()
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

// 新增按钮操作
const handleAdd = () => {
  reset()
  dialog.title = '新增标签'
  dialog.visible = true
  // 确保form对象有默认值
  form.name = ''
  form.type = 'CUSTOM'
  form.description = ''
}

// 修改按钮操作
const handleUpdate = (row) => {
  reset()
  // 确保复制所有必要的字段
  form.id = row.id 
  form.name = row.name
  form.type = row.type
  form.description = row.description
  dialog.title = '修改标签'
  dialog.visible = true
}

// 查看文件按钮操作
const handleViewFiles = (row) => {
  router.push({
    path: `/dataset/${props.datasetId}/version`,
    query: { 
      versionId: currentVersionId.value,
      tagId: row.tagId 
    }
  })
}

// 删除标签操作
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确认删除该标签吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 确保使用正确的id
      const tagId = row.id || row.tagId
      if (!tagId) {
        ElMessage.error('标签ID不能为空')
        return
      }
      
      const response = await delDatasetVersionTag(props.datasetId, currentVersionId.value, tagId)
      if (response.code === 200) {
        ElMessage.success('删除标签成功')
        getList()
      } else {
        ElMessage.error(response.msg || '删除标签失败')
      }
    } catch (error) {
      console.error('删除标签错误:', error)
      ElMessage.error('删除标签失败')
    }
  }).catch(() => {})
}

// 表单重置
const reset = () => {
  // 使用显式赋值重置所有字段
  form.id = undefined
  form.name = ''
  form.type = undefined
  form.description = ''
  
  // 如果表单引用存在，也重置表单验证状态
  if (form.value) {
    form.value.resetFields()
  }
}

// 取消按钮
const cancel = () => {
  dialog.visible = false
  reset()
}

// 提交表单
const submitForm = async () => {
  try {
    const data = {
      name: form.name,
      type: form.type,
      description: form.description,
      datasetId: props.datasetId,
      versionId: currentVersionId.value
    }
    
    let response
    if (form.id) {
      // Update existing tag
      response = await updateDatasetVersionTag(props.datasetId, currentVersionId.value, form.id, data)
    } else {
      // Add new tag
      response = await addDatasetVersionTag(props.datasetId, currentVersionId.value, data)
    }
    
    if (response.code === 200) {
      ElMessage.success(form.id ? '修改成功' : '新增成功')
      dialog.visible = false
      getList()
    } else {
      ElMessage.error(response.msg || (form.id ? '修改失败' : '新增失败'))
    }
  } catch (error) {
    console.error('提交表单错误:', error)
    ElMessage.error(form.id ? '修改失败' : '新增失败')
  }
}

// 提交新增标签
const submitAdd = async () => {
  try {
    const params = {
      name: tagForm.value.name,
      color: tagForm.value.color
    }
    
    const response = await addDatasetVersionTag(props.datasetId, currentVersionId.value, params)
    if (response.code === 200) {
      ElMessage.success('添加标签成功')
      tagDialog.value.visible = false
      getList()
    } else {
      ElMessage.error(response.msg || '添加标签失败')
    }
  } catch (error) {
    console.error('添加标签错误:', error)
    ElMessage.error('添加标签失败')
  }
}

// 监听路由参数变化
watch(
  () => route.query.versionId,
  (newVersionId) => {
    if (newVersionId) {
      currentVersionId.value = newVersionId
      getList()
    }
  },
  { immediate: true }
)

// 在组件挂载时加载数据
onMounted(() => {
  if (props.datasetId) {
    getList()
  }
})
</script>

<style scoped>
.dataset-tags {
  width: 100%;
}

.operation-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}
</style> 