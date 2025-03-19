<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="元数据名称" prop="metadataName">
        <el-input
          v-model="queryParams.metadataName"
          placeholder="请输入元数据名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="元数据类型" prop="metadataType">
        <el-select v-model="queryParams.metadataType" placeholder="元数据类型" clearable style="width: 200px">
          <el-option
            v-for="dict in metadataTypeOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['data:metadata:add']"
        >新增元数据</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Upload"
          @click="handleImport"
          v-hasPermi="['data:metadata:import']"
        >导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['data:metadata:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="Download"
          @click="handleTemplate"
          v-hasPermi="['data:metadata:template']"
        >模板下载</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['data:metadata:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="metadataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="元数据名称" align="left" prop="metadataName" :show-overflow-tooltip="true" />
      <el-table-column label="元数据类型" align="center" prop="metadataType">
        <template #default="scope">
          <dict-tag :options="metadataTypeOptions" :value="scope.row.metadataType"/>
        </template>
      </el-table-column>
      <el-table-column label="描述" align="left" prop="description" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180"/>
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
            type="text"
            icon="Edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['data:metadata:edit']"
          >修改</el-button>
          <el-button
            type="text"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['data:metadata:remove']"
          >删除</el-button>
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

    <!-- 添加或修改元数据对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="metadataRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="元数据名称" prop="metadataName">
          <el-input v-model="form.metadataName" placeholder="请输入元数据名称" />
        </el-form-item>
        <el-form-item label="元数据类型" prop="metadataType">
          <el-select v-model="form.metadataType" placeholder="请选择元数据类型">
            <el-option
              v-for="dict in metadataTypeOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 元数据导入对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="400px" append-to-body>
      <el-upload
        ref="uploadRef"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">只能上传xlsx/xls文件，且不超过10MB</div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">确 定</el-button>
          <el-button @click="upload.open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Metadata">
import { listMetadata, getMetadata, delMetadata, addMetadata, updateMetadata, getMetadataTemplate } from "@/api/data/metadata";
import { getToken } from "@/utils/auth";

const { proxy } = getCurrentInstance();

const metadataList = ref([]);
const metadataTypeOptions = ref([]);
const showSearch = ref(true);
const loading = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const open = ref(false);

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  metadataName: undefined,
  metadataType: undefined
});

// 表单参数
const form = ref({
  metadataId: undefined,
  metadataName: undefined,
  metadataType: undefined,
  description: undefined
});

// 表单校验
const rules = ref({
  metadataName: [
    { required: true, message: "元数据名称不能为空", trigger: "blur" }
  ],
  metadataType: [
    { required: true, message: "元数据类型不能为空", trigger: "change" }
  ]
});

// 文件上传参数
const upload = reactive({
  open: false,
  title: '',
  isUploading: false,
  url: process.env.VUE_APP_BASE_API + "/data/metadata/importData",
  headers: { Authorization: "Bearer " + getToken() }
});

/** 查询元数据列表 */
function getList() {
  loading.value = true;
  listMetadata(queryParams.value).then(response => {
    metadataList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 表单重置 */
function reset() {
  form.value = {
    metadataId: undefined,
    metadataName: undefined,
    metadataType: undefined,
    description: undefined
  };
  proxy.resetForm("metadataRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加元数据";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const metadataId = row.metadataId;
  getMetadata(metadataId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改元数据";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["metadataRef"].validate(valid => {
    if (valid) {
      if (form.value.metadataId != undefined) {
        updateMetadata(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addMetadata(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const metadataIds = row.metadataId || ids.value;
  proxy.$modal.confirm('是否确认删除元数据编号为"' + metadataIds + '"的数据项？').then(function() {
    return delMetadata(metadataIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 导入按钮操作 */
function handleImport() {
  upload.title = "元数据导入";
  upload.open = true;
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('/data/metadata/export', {
    ...queryParams.value
  }, `metadata_${new Date().getTime()}.xlsx`);
}

/** 模板下载操作 */
function handleTemplate() {
  getMetadataTemplate().then(response => {
    proxy.download('/data/metadata/template', {}, `metadata_template.xlsx`);
  });
}

/** 文件上传中处理 */
const handleFileUploadProgress = (event, file, fileList) => {
  upload.isUploading = true;
};

/** 文件上传成功处理 */
const handleFileSuccess = (response, file, fileList) => {
  upload.open = false;
  upload.isUploading = false;
  proxy.$refs["uploadRef"].clearFiles();
  proxy.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true });
  getList();
};

/** 提交上传文件 */
function submitFileForm() {
  proxy.$refs["uploadRef"].submit();
}

onMounted(() => {
  getList();
  proxy.getDicts("sys_metadata_type").then(response => {
    metadataTypeOptions.value = response.data;
  });
});
</script> 