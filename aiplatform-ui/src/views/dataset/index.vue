<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>数据集列表</span>
          <el-button
            type="primary"
            @click="handleAdd"
          >新建数据集</el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="数据集名称" prop="datasetName">
          <el-input
            v-model="queryParams.datasetName"
            placeholder="请输入数据集名称"
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

      <!-- 数据集列表 -->
      <el-table
        v-loading="loading"
        :data="datasetList"
        style="width: 100%"
        border
        row-key="datasetId"
        highlight-current-row
        @sort-change="handleSortChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column
          label="数据集名称"
          prop="datasetName"
          min-width="200"
          show-overflow-tooltip
        >
          <template #default="scope">
            <span>{{ scope.row.datasetName }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="描述"
          prop="description"
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column
          label="数据集类型"
          prop="datasetType"
          width="100"
          align="center"
        >
          <template #default="scope">
            {{ ['图像', '文本', '表格', '音频', '视频'][Number(scope.row.datasetType)] || '未知' }}
          </template>
        </el-table-column>
        <el-table-column
          label="标注类型"
          prop="labelType"
          width="100"
          align="center"
        >
          <template #default="scope">
            {{ ['分类', '检测', '分割'][Number(scope.row.labelType)] || '未知' }}
          </template>
        </el-table-column>
        <el-table-column
          label="状态"
          prop="status"
          width="100"
          align="center"
        >
          <template #default="scope">
            {{ ['未标注', '标注中', '已标注'][Number(scope.row.status)] || '未知' }}
          </template>
        </el-table-column>
        <el-table-column
          label="创建者"
          prop="createBy"
          width="120"
          show-overflow-tooltip
        />
        <el-table-column
          label="创建时间"
          prop="createTime"
          width="180"
          show-overflow-tooltip
        >
          <template #default="scope">
            {{ scope.row.createTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          width="200"
          align="center"
          fixed="right"
        >
          <template #default="scope">
            <el-button
              link
              type="primary"
              @click="handleDetail(scope.row)"
            >查看详情</el-button>
            <el-button
              link
              type="primary"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              link
              type="danger"
              @click="handleDelete(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="500px"
      append-to-body
    >
      <el-form
        ref="datasetForm"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="名称" prop="datasetName">
          <el-input v-model="form.datasetName" placeholder="请输入数据集名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入数据集描述"
            :rows="4"
          />
        </el-form-item>
        <el-form-item label="类型" prop="datasetType">
          <el-select v-model="form.datasetType" placeholder="请选择数据集类型">
            <el-option label="图像" value="0" />
            <el-option label="文本" value="1" />
            <el-option label="表格" value="2" />
            <el-option label="音频" value="3" />
            <el-option label="视频" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="标注类型" prop="labelType">
          <el-select v-model="form.labelType" placeholder="请选择标注类型">
            <el-option label="分类" value="0" />
            <el-option label="检测" value="1" />
            <el-option label="分割" value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialog.visible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { listDataset, getDataset, addDataset, updateDataset, delDataset } from '@/api/dataset/index'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

// 数据定义
const loading = ref(true)
const total = ref(0)
const datasetList = ref([])
const dateRange = ref([])
const queryForm = ref(null)
const datasetForm = ref(null)

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  datasetName: undefined,
  orderByColumn: undefined,
  isAsc: undefined
})

// 表单参数
const form = reactive({
  id: undefined,
  datasetName: undefined,
  description: undefined,
  datasetType: '0',
  labelType: '0',
  status: '0'
})

// 对话框
const dialog = reactive({
  title: '',
  visible: false
})

// 表单校验规则
const rules = {
  datasetName: [
    { required: true, message: '数据集名称不能为空', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ]
}

// 查询数据集列表
const getList = async () => {
  loading.value = true
  try {
    // 处理查询参数
    const params = {
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
      datasetName: queryParams.datasetName,
      orderByColumn: queryParams.orderByColumn,
      isAsc: queryParams.isAsc
    }
    
    // 添加时间范围
    if (dateRange.value && dateRange.value.length === 2) {
      params.beginTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }
    
    const response = await listDataset(params)
    if (response.code === 200) {
      datasetList.value = response.rows || []
      total.value = response.total || 0
    } else {
      ElMessage.error(response.msg || '获取数据集列表失败')
    }
  } catch (error) {
    console.error('获取数据集列表错误:', error)
    ElMessage.error('获取数据集列表失败')
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
  queryParams.pageNum = 1
  queryParams.pageSize = 10
  queryParams.datasetName = undefined
  queryParams.orderByColumn = undefined
  queryParams.isAsc = undefined
  dateRange.value = []
  
  // 重置表单
  if (queryForm.value) {
    queryForm.value.resetFields()
  }
  
  // 重新查询
  getList()
}

// 新增按钮操作
const handleAdd = () => {
  reset()
  dialog.title = '新增数据集'
  dialog.visible = true
}

// 修改按钮操作
const handleUpdate = async (row) => {
  try {
    const response = await getDataset(row.datasetId)
    if (response.code === 200) {
      reset()
      const data = response.data
      form.id = data.datasetId
      form.datasetName = data.datasetName
      form.description = data.description || ''
      form.datasetType = String(data.datasetType || '0')
      form.labelType = String(data.labelType || '0')
      form.status = String(data.status || '0')
      dialog.title = '修改数据集'
      dialog.visible = true
    } else {
      ElMessage.error(response.msg || '获取数据集详情失败')
    }
  } catch (error) {
    console.error('获取数据集详情错误:', error)
    ElMessage.error('获取数据集详情失败')
  }
}

// 提交表单
const submitForm = async () => {
  if (!datasetForm.value) return
  
  try {
    // 表单验证
    await datasetForm.value.validate()
    
    // 准备提交数据
    const data = {
      datasetName: form.datasetName,
      description: form.description,
      datasetType: form.datasetType,
      labelType: form.labelType,
      status: form.status
    }
    
    // 调用API
    const response = form.id
      ? await updateDataset(form.id, data)
      : await addDataset(data)
    
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
  ElMessageBox.confirm('是否确认删除该数据集?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await delDataset(row.datasetId)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        getList()
      } else {
        ElMessage.error(response.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除数据集错误:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 查看详情操作
const handleDetail = (row) => {
  router.push({
    path: `/dataset/detail/${row.datasetId}`
  })
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

// 表格排序
const handleSortChange = ({ prop, order }) => {
  queryParams.orderByColumn = prop
  queryParams.isAsc = order === 'ascending' ? 'asc' : 'desc'
  getList()
}

// 重置表单
const reset = () => {
  form.id = undefined
  form.datasetName = undefined
  form.description = undefined
  form.datasetType = '0'
  form.labelType = '0'
  form.status = '0'
  
  // 重置表单验证
  nextTick(() => {
    if (datasetForm.value) {
      datasetForm.value.resetFields()
    }
  })
}

// 初始化
onMounted(() => {
  getList()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.link-type {
  color: #409EFF;
  text-decoration: none;
}
.link-type:hover {
  color: #66b1ff;
}
.delete-btn {
  color: #F56C6C;
}
.delete-btn:hover {
  color: #f78989;
}
</style> 