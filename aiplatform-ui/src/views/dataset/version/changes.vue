<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
      <el-form-item label="变更类型" prop="changeType">
        <el-select v-model="queryParams.changeType" placeholder="变更类型" clearable>
          <el-option
            v-for="dict in dict.type.dataset_version_change_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="变更时间">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 变更记录列表 -->
    <el-table v-loading="loading" :data="changeList">
      <el-table-column label="变更类型" align="center" prop="changeType">
        <template #default="scope">
          <dict-tag :options="dict.type.dataset_version_change_type" :value="scope.row.changeType"/>
        </template>
      </el-table-column>
      <el-table-column label="变更描述" align="center" prop="changeDesc" :show-overflow-tooltip="true" />
      <el-table-column label="文件名称" align="center" prop="fileName" :show-overflow-tooltip="true" />
      <el-table-column label="变更前" align="center" prop="beforeChange" :show-overflow-tooltip="true">
        <template #default="scope">
          <el-button
            v-if="scope.row.oldFileId"
            type="text"
            @click="handlePreview(scope.row.oldFileId)"
          >{{ scope.row.beforeChange }}</el-button>
          <span v-else>{{ scope.row.beforeChange }}</span>
        </template>
      </el-table-column>
      <el-table-column label="变更后" align="center" prop="afterChange" :show-overflow-tooltip="true">
        <template #default="scope">
          <el-button
            v-if="scope.row.fileId"
            type="text"
            @click="handlePreview(scope.row.fileId)"
          >{{ scope.row.afterChange }}</el-button>
          <span v-else>{{ scope.row.afterChange }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作人员" align="center" prop="createBy" />
      <el-table-column label="变更时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="120" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
            type="text"
            icon="View"
            @click="handleDetail(scope.row)"
            v-hasPermi="['dataset:version:query']"
          >详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 变更详情对话框 -->
    <el-dialog title="变更详情" v-model="detailOpen" width="800px" append-to-body>
      <div class="change-detail" v-loading="detailLoading">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="变更类型">
            <dict-tag :options="dict.type.dataset_version_change_type" :value="detailForm.changeType"/>
          </el-descriptions-item>
          <el-descriptions-item label="变更时间">
            {{ parseTime(detailForm.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="操作人员">
            {{ detailForm.createBy }}
          </el-descriptions-item>
          <el-descriptions-item label="文件名称">
            {{ detailForm.fileName }}
          </el-descriptions-item>
          <el-descriptions-item label="变更描述" :span="2">
            {{ detailForm.changeDesc }}
          </el-descriptions-item>
          <el-descriptions-item label="变更前" :span="2">
            <div v-if="detailForm.oldFileId" class="change-content">
              <el-button type="text" @click="handlePreview(detailForm.oldFileId)">
                查看原文件
              </el-button>
            </div>
            <div v-else class="change-content">{{ detailForm.beforeChange }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="变更后" :span="2">
            <div v-if="detailForm.fileId" class="change-content">
              <el-button type="text" @click="handlePreview(detailForm.fileId)">
                查看新文件
              </el-button>
            </div>
            <div v-else class="change-content">{{ detailForm.afterChange }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listVersionChanges, getVersionChange } from '@/api/dataset/version-change'
import { getFilePreviewUrl } from '@/api/dataset/version-file'

const { proxy } = getCurrentInstance()

// 遮罩层
const loading = ref(false)
const detailLoading = ref(false)
// 显示搜索条件
const showSearch = ref(true)
// 总条数
const total = ref(0)
// 变更记录表格数据
const changeList = ref([])
// 是否显示详情弹出层
const detailOpen = ref(false)
// 日期范围
const dateRange = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  versionId: undefined,
  changeType: undefined,
  beginTime: undefined,
  endTime: undefined
})

// 详情表单
const detailForm = reactive({
  changeId: undefined,
  versionId: undefined,
  changeType: undefined,
  changeDesc: undefined,
  fileName: undefined,
  fileId: undefined,
  oldFileId: undefined,
  beforeChange: undefined,
  afterChange: undefined,
  createBy: undefined,
  createTime: undefined
})

/** 查询变更记录列表 */
function getList() {
  loading.value = true
  if (dateRange.value && dateRange.value.length === 2) {
    queryParams.beginTime = dateRange.value[0]
    queryParams.endTime = dateRange.value[1]
  }
  listVersionChanges(queryParams).then(response => {
    changeList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = []
  proxy.resetForm("queryForm")
  handleQuery()
}

/** 查看详情按钮操作 */
function handleDetail(row) {
  detailLoading.value = true
  detailOpen.value = true
  getVersionChange(row.changeId).then(response => {
    Object.assign(detailForm, response.data)
    detailLoading.value = false
  })
}

/** 预览文件 */
function handlePreview(fileId) {
  getFilePreviewUrl(fileId).then(response => {
    const previewUrl = response.data
    window.open(previewUrl)
  })
}

onMounted(() => {
  // 从路由参数中获取版本ID
  const route = useRoute()
  queryParams.versionId = route.params.versionId
  getList()
})
</script>

<style scoped>
.change-detail {
  padding: 20px;
}
.change-content {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style> 