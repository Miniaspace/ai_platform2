<template>
  <div class="upload-container">
    <h2>文件上传</h2>
    <input type="file" multiple @change="handleFileChange" />
    <div v-if="uploadProgress > 0">
      <p>上传进度: {{ uploadProgress }}%</p>
      <progress :value="uploadProgress" max="100"></progress>
    </div>
    <button @click="uploadFiles">开始上传</button>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      files: [],
      uploadProgress: 0,
    };
  },
  methods: {
    handleFileChange(event) {
      this.files = Array.from(event.target.files);
    },
    uploadFiles() {
      const formData = new FormData();
      this.files.forEach(file => {
        formData.append('files', file);
      });
      axios.post('/api/files/upload', formData, {
        onUploadProgress: progressEvent => {
          this.uploadProgress = Math.round((progressEvent.loaded * 100) / progressEvent.total);
        }
      }).then(() => {
        this.uploadProgress = 0;
        this.$emit('uploaded');
      });
    },
  },
};
</script>

<style scoped>
.upload-container {
  border: 1px dashed #ccc;
  padding: 20px;
  margin: 20px 0;
}
</style> 