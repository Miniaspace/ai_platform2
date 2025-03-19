<template>
  <div class="modal">
    <h2>创建元数据</h2>
    <form @submit.prevent="createMetadata">
      <div>
        <label for="name">名称:</label>
        <input type="text" v-model="metadata.name" required />
      </div>
      <div>
        <label for="type">类型:</label>
        <input type="text" v-model="metadata.type" required />
      </div>
      <button type="submit">创建</button>
      <button @click="$emit('close')">取消</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      metadata: {
        name: '',
        type: '',
      },
    };
  },
  methods: {
    createMetadata() {
      axios.post('/api/metadata', this.metadata).then(() => {
        this.$emit('created');
        this.metadata.name = '';
        this.metadata.type = '';
      });
    },
  },
};
</script>

<style scoped>
.modal {
  /* Add your modal styles here */
}
</style>
