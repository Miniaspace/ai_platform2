<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>目录管理</span>
          <el-button type="primary" size="small" @click="handleCreate">新建目录</el-button>
        </div>
      </template>

      <el-table :data="directoryList" style="width: 100%">
        <el-table-column prop="name" label="目录名称" min-width="180" />
        <el-table-column prop="path" label="路径" min-width="250" />
        <el-table-column prop="fileCount" label="文件数量" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="createBy" label="创建者" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              size="mini"
              type="text"
              @click="handleEdit(scope.row)"
            >编辑</el-button>
            <el-button
              size="mini"
              type="text"
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
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="500px"
      append-to-body
    >
      <el-form ref="directoryForm" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="目录名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入目录名称" />
        </el-form-item>
        <el-form-item label="上级目录" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="directoryTree"
            :props="{ label: 'name', value: 'id' }"
            placeholder="请选择上级目录"
            clearable
          />
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

<script>
export default {
  name: 'DirectoryManagement',
  data() {
    return {
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 目录列表数据
      directoryList: [],
      // 目录树数据
      directoryTree: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined
      },
      // 弹出层标题
      dialog: {
        title: '',
        visible: false
      },
      // 表单参数
      form: {
        id: undefined,
        name: undefined,
        parentId: undefined
      },
      // 表单校验
      rules: {
        name: [
          { required: true, message: '目录名称不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getDirectoryTree()
  },
  methods: {
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleString()
    },
    /** 查询目录列表 */
    async getList() {
      this.loading = true
      try {
        const response = await this.$http.get('/data/directory/list', {
          params: this.queryParams
        })
        this.directoryList = response.data.rows
        this.total = response.data.total
      } catch (error) {
        this.$message.error('获取目录列表失败')
      }
      this.loading = false
    },
    /** 查询目录树 */
    async getDirectoryTree() {
      try {
        const response = await this.$http.get('/data/directory/tree')
        this.directoryTree = response.data
      } catch (error) {
        this.$message.error('获取目录树失败')
      }
    },
    /** 取消按钮 */
    cancel() {
      this.dialog.visible = false
      this.reset()
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        name: undefined,
        parentId: undefined
      }
    },
    /** 新增按钮操作 */
    handleCreate() {
      this.reset()
      this.dialog.title = '添加目录'
      this.dialog.visible = true
    },
    /** 修改按钮操作 */
    handleEdit(row) {
      this.reset()
      this.dialog.title = '修改目录'
      this.form = { ...row }
      this.dialog.visible = true
    },
    /** 提交按钮 */
    async submitForm() {
      this.$refs.directoryForm.validate(async (valid) => {
        if (valid) {
          try {
            if (this.form.id) {
              await this.$http.put('/data/directory', this.form)
              this.$message.success('修改成功')
            } else {
              await this.$http.post('/data/directory', this.form)
              this.$message.success('新增成功')
            }
            this.dialog.visible = false
            this.getList()
            this.getDirectoryTree()
          } catch (error) {
            this.$message.error(this.form.id ? '修改失败' : '新增失败')
          }
        }
      })
    },
    /** 删除按钮操作 */
    async handleDelete(row) {
      try {
        await this.$confirm('是否确认删除该目录?', '警告', {
          type: 'warning'
        })
        await this.$http.delete('/data/directory/' + row.id)
        this.$message.success('删除成功')
        this.getList()
        this.getDirectoryTree()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    // 分页大小改变
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.getList()
    },
    // 页码改变
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
</style> 