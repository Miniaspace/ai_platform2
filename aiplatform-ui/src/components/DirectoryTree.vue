<template>
  <div class="directory-tree">
    <el-tree
      :data="treeData"
      :props="defaultProps"
      @node-click="handleNodeClick"
      :default-expand-all="false"
      node-key="id"
      :highlight-current="true"
      :expand-on-click-node="false"
    >
      <template #default="{ node, data }">
        <span class="custom-tree-node">
          <span>
            <i :class="getNodeIcon(data)" class="mr-1"></i>
            {{ node.label }}
          </span>
          <span class="operations" v-if="data.type === 'directory'">
            <el-button
              type="text"
              size="small"
              @click.stop="() => handleAdd(data)"
            >
              <i class="el-icon-plus"></i>
            </el-button>
            <el-button
              type="text"
              size="small"
              @click.stop="() => handleDelete(node, data)"
            >
              <i class="el-icon-delete"></i>
            </el-button>
          </span>
        </span>
      </template>
    </el-tree>
  </div>
</template>

<script>
export default {
  name: 'DirectoryTree',
  data() {
    return {
      treeData: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      }
    }
  },
  methods: {
    handleNodeClick(data) {
      this.$emit('node-click', data)
    },
    getNodeIcon(data) {
      return data.type === 'directory' ? 'el-icon-folder' : 'el-icon-document'
    },
    handleAdd(data) {
      this.$emit('add', data)
    },
    handleDelete(node, data) {
      this.$emit('delete', { node, data })
    },
    loadData(data) {
      this.treeData = data
    }
  }
}
</script>

<style scoped>
.directory-tree {
  margin: 10px;
}
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
.operations {
  display: none;
}
.custom-tree-node:hover .operations {
  display: inline-block;
}
</style> 