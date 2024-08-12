<script setup>
import { computed } from 'vue'
import { useContentsStore } from '@/stores/contentsStore'

const contentsStore = useContentsStore()
const content = computed(() => contentsStore.getPickedContents)
</script>

<template>
  <div v-if="content" class="contents-card-shadow contents-card">
    <!-- 이미지 섹션 -->
    <div class="image-section">
      <v-img :src="content.imgUrl" max-width="300px" max-height="300px"></v-img>
    </div>

    <!-- 제목 섹션 -->
    <div class="title-section">
      <h2>{{ content.contentTitle }}</h2>
    </div>

    <!-- Green Section (설명 섹션) -->
    <div class="green-section">
      {{ content.description }}
    </div>
  </div>
  <div v-else>
    <p>No content selected.</p>
  </div>
</template>

<style scoped>
.contents-card {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.contents-card-shadow {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.image-section {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.title-section {
  flex: 0.1;
  display: flex;
  align-items: center;
  justify-content: start;
}

.green-section {
  flex: 0.5;
  background-color: #e7ffde;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
