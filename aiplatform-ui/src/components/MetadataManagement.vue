<template>
  <div>
    <h1>元数据管理</h1>
    <button @click="showCreateModal">创建元数据</button>
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>名称</th>
          <th>类型</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="metadata in metadataList" :key="metadata.metadataId">
          <td>{{ metadata.metadataId }}</td>
          <td>{{ metadata.name }}</td>
          <td>{{ metadata.type }}</td>
          <td>
            <button @click="showUpdateModal(metadata)">更新</button>
            <button @click="deleteMetadata(metadata.metadataId)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
    <CreateMetadataModal v-if="isCreateModalVisible" @close="isCreateModalVisible = false" @created="fetchMetadata" />
    <UpdateMetadataModal v-if="isUpdateModalVisible" :metadata="selectedMetadata" @close="isUpdateModalVisible = false" @updated="fetchMetadata" />
  </div>
</template>

<script>
import axios from 'axios';
import CreateMetadataModal from './CreateMetadataModal.vue';
import UpdateMetadataModal from './UpdateMetadataModal.vue';

export default {
  components: { CreateMetadataModal, UpdateMetadataModal },
  data() {
    return {
      metadataList: [],
      isCreateModalVisible: false,
      isUpdateModalVisible: false,
      selectedMetadata: null
    };
  },
  methods: {
    fetchMetadata() {
      axios.get('/api/metadata').then(response => {
        this.metadataList = response.data;
      }).catch(error => {
        console.error('获取元数据失败:', error);
      });
    },
    showCreateModal() {
      this.isCreateModalVisible = true;
    },
    showUpdateModal(metadata) {
      this.selectedMetadata = metadata;
      this.isUpdateModalVisible = true;
    },
    deleteMetadata(metadataId) {
      axios.delete(`/api/metadata/${metadataId}`).then(() => {
        this.fetchMetadata();
      }).catch(error => {
        console.error('删除元数据失败:', error);
      });
    }
  },
  mounted() {
    this.fetchMetadata();
  }
};
</script> 