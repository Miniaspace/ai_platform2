<template>
  <div class="preview-container">
    <h2>文件预览</h2>
    <div v-if="file">
      <div v-if="isImage(file)">
        <img :src="file.url" alt="Image Preview" class="image-preview" />
      </div>
      <div v-else-if="isVideo(file)">
        <video controls class="video-preview">
          <source :src="file.url" type="video/mp4" />
          您的浏览器不支持视频标签。
        </video>
      </div>
      <div v-else-if="isDocument(file)">
        <p>文档预览功能尚未实现，您可以下载文档进行查看。</p>
        <a :href="file.url" download>下载文档</a>
      </div>
      <div v-else>
        <p>不支持的文件类型。</p>
      </div>
    </div>
    <div v-else>
      <p>请选择一个文件进行预览。</p>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    file: {
      type: Object,
      required: false,
    },
  },
  methods: {
    isImage(file) {
      return file && file.type.startsWith('image/');
    },
    isVideo(file) {
      return file && file.type.startsWith('video/');
    },
    isDocument(file) {
      return file && file.type.startsWith('application/');
    },
  },
};
</script>

<style scoped>
.preview-container {
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
  margin: 20px 0;
}
.image-preview {
  max-width: 100%;
  height: auto;
}
.video-preview {
  width: 100%;
  height: auto;
}
</style> 