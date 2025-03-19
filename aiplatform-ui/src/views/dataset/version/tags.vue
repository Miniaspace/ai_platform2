<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>标签管理</span>
          <el-button
            type="primary"
            @click="handleAdd"
          >新建标签</el-button>
        </div>
      </template>

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
              size="mini"
              type="text"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleFiles(scope.row)"
            >查看文件</el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleDelete(scope.row)"
              class="delete-btn"
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
    </el-card>

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
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listDatasetVersionTag, addDatasetVersionTag, updateDatasetVersionTag, delDatasetVersionTag } from '@/api/dataset'

const route = useRoute()
const router = useRouter()

// 获取路由参数
const datasetId = ref(route.params.datasetId)
const versionId = ref(route.params.versionId)

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

// 获取标签列表
const getList = async () => {
  if (!datasetId.value || !versionId.value) {
    ElMessage.error('缺少必要的参数')
    return
  }

  loading.value = true
  try {
    const response = await listDatasetVersionTag(datasetId.value, versionId.value, queryParams)
    if (response.code === 200) {
      tagList.value = response.rows
      total.value = response.total
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

// 新增按钮操作
const handleAdd = () => {
  reset()
  dialog.title = '新增标签'
  dialog.visible = true
}

// 修改按钮操作
const handleUpdate = (row) => {
  reset()
  form.id = row.id
  form.name = row.name
  form.type = row.type
  form.description = row.description
  dialog.title = '修改标签'
  dialog.visible = true
}

// 提交表单
const submitForm = async () => {
  if (!tagForm.value) return
  
  try {
    await tagForm.value.validate()
    
    const data = {
      name: form.name,
      type: form.type,
      description: form.description
    }
    
    const response = form.id
      ? await updateDatasetVersionTag(datasetId.value, versionId.value, form.id, data)
      : await addDatasetVersionTag(datasetId.value, versionId.value, data)
    
    if (response.code === 200) {
      ElMessage.success(form.id ? '修改成功' : '新增成功')
      dialog.visible = false
      getList()
    } else {
      ElMessage.error(response.msg || (form.id ? '修改失败' : '新增失败'))
    }
  } catch (error) {
    console.error('提交表单错误:', error)
    ElMessage.error('操作失败')
  }
}

// 删除按钮操作
const handleDelete = (row) => {
  ElMessageBox.confirm('是否确认删除该标签?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await delDatasetVersionTag(datasetId.value, versionId.value, row.id)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        getList()
      } else {
        ElMessage.error(response.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除标签错误:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 重置表单
const reset = () => {
  form.id = undefined
  form.name = undefined
  form.type = undefined
  form.description = undefined
  
  nextTick(() => {
    if (tagForm.value) {
      tagForm.value.resetFields()
    }
  })
}

// 获取标签类型显示文本
const getTagTypeText = (type) => {
  const found = tagTypes.find(item => item.value === type)
  return found ? found.label : type
}

// 获取标签类型样式
const getTagType = (type) => {
  const typeMap = {
    'CATEGORY': '',
    'PROPERTY': 'success',
    'STATUS': 'warning',
    'CUSTOM': 'info'
  }
  return typeMap[type] || ''
}

// 分页大小改变
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

// 页码改变
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

// 初始化
onMounted(() => {
  if (!datasetId.value || !versionId.value) {
    ElMessage.error('缺少必要的参数')
    router.push('/dataset/list')
    return
  }
  getList()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.delete-btn {
  color: #F56C6C;
}
.delete-btn:hover {
  color: #f78989;
}
</style> 