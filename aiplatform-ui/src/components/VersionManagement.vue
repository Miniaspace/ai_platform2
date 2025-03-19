<template>
  <div class="version-management-container">
    <h2>版本管理</h2>
    <div class="version-list">
      <div v-for="version in versions" :key="version.versionId" class="version-item">
        <span>{{ version.versionNumber }}</span>
        <button @click="compareVersion(version.versionId)">对比</button>
      </div>
    </div>
    <button @click="fetchVersions">加载版本</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      versions: [],
    };
  },
  methods: {
    fetchVersions() {
      // Fetch versions from the API
      // Example: axios.get('/api/versions').then(response => { this.versions = response.data; });
    },
    compareVersion(versionId) {
      // Handle version comparison
      console.log('Comparing version:', versionId);
    },
  },
  mounted() {
    this.fetchVersions();
  },
};
</script>

<style scoped>
.version-management-container {
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
  margin: 20px 0;
}
.version-item {
  margin: 5px 0;
}
</style> 