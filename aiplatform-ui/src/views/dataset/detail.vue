<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>数据集详情</span>
          <div class="right-button">
            <el-button type="primary" @click="handleEdit">编辑</el-button>
            <el-button type="danger" @click="handleDelete">删除</el-button>
          </div>
        </div>
      </template>
      
      <el-descriptions class="margin-top" :column="3" border>
        <el-descriptions-item>
          <template #label>数据集名称</template>
          {{ datasetInfo.name }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>数据集类型</template>
          {{ datasetInfo.type }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>标注类型</template>
          {{ datasetInfo.labelType }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>创建者</template>
          {{ datasetInfo.createBy }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>创建时间</template>
          {{ datasetInfo.createTime }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>状态</template>
          {{ datasetInfo.status }}
        </el-descriptions-item>
        <el-descriptions-item :span="3">
          <template #label>描述</template>
          {{ datasetInfo.description }}
        </el-descriptions-item>
      </el-descriptions>

      <el-tabs v-model="activeTab" class="demo-tabs">
        <el-tab-pane label="版本管理" name="versions">
          <dataset-versions :dataset-id="datasetId" />
        </el-tab-pane>
        <el-tab-pane label="标签管理" name="tags">
          <dataset-tags :dataset-id="datasetId" />
        </el-tab-pane>
        <el-tab-pane label="数据分析" name="analytics">
          <dataset-analytics :dataset-id="datasetId" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editDialog" title="编辑数据集" width="500px" append-to-body>
      <el-form ref="editFormRef" :model="editForm" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入数据集名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="editForm.description" type="textarea" placeholder="请输入数据集描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitEdit">确 定</el-button>
          <el-button @click="editDialog = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import DatasetVersions from './components/DatasetVersions.vue'
import DatasetTags from './components/DatasetTags.vue'
import DatasetAnalytics from './components/DatasetAnalytics.vue'
import { getDataset, updateDataset, delDataset } from '@/api/dataset'

const route = useRoute()
const router = useRouter()
const datasetId = ref(route.params.id)
const activeTab = ref('versions')
const editDialog = ref(false)
const datasetInfo = ref({})

// 编辑表单
const editForm = ref({
  name: '',
  description: ''
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入数据集名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 500, message: '长度不能超过500个字符', trigger: 'blur' }
  ]
}

// 获取数据集详情
const getDatasetInfo = async () => {
  try {
    const response = await getDataset(datasetId.value)
    if (response.code === 200) {
      datasetInfo.value = response.data || {}
    } else {
      ElMessage.error(response.msg || '获取数据集详情失败')
    }
  } catch (error) {
    console.error('获取数据集详情失败:', error)
    ElMessage.error('获取数据集详情失败')
  }
}

// 处理编辑按钮点击
const handleEdit = () => {
  editForm.value = {
    name: datasetInfo.value.name,
    description: datasetInfo.value.description
  }
  editDialog.value = true
}

// 提交编辑
const submitEdit = async () => {
  try {
    await updateDataset(datasetId.value, editForm.value)
    ElMessage.success('修改成功')
    editDialog.value = false
    getDatasetInfo()
  } catch (error) {
    console.error('修改失败:', error)
  }
}

// 处理删除按钮点击
const handleDelete = () => {
  ElMessageBox.confirm('确认要删除该数据集吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await delDataset(datasetId.value)
      ElMessage.success('删除成功')
      router.push('/dataset/index')
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

onMounted(() => {
  getDatasetInfo()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.right-button {
  display: flex;
  gap: 10px;
}

.demo-tabs {
  margin-top: 20px;
}
</style> 