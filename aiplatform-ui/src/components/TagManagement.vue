<template>
  <div class="tag-management-container">
    <h2>标签管理</h2>
    <div class="tag-list">
      <div v-for="tag in tags" :key="tag.tagId" class="tag-item">
        <input type="checkbox" :value="tag.tagId" v-model="selectedTags" />
        <span>{{ tag.tagName }}</span>
      </div>
    </div>
    <button @click="submitTags">提交选择的标签</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      tags: [],
      selectedTags: [],
    };
  },
  methods: {
    fetchTags() {
      // Fetch tags from the API
      // Example: axios.get('/api/tags').then(response => { this.tags = response.data; });
    },
    submitTags() {
      // Handle the submission of selected tags
      console.log('Selected Tags:', this.selectedTags);
    },
  },
  mounted() {
    this.fetchTags();
  },
};
</script>

<style scoped>
.tag-management-container {
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
  margin: 20px 0;
}
.tag-item {
  margin: 5px 0;
}
</style> 