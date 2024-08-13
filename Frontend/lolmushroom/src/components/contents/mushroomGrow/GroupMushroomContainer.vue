<script setup>
import { computed } from 'vue'
import { useMushroomStore } from '@/stores/mushroomStore'
import GroupMushroom from './GroupMushroom.vue'

const mushroomStore = useMushroomStore()

// Get the other mushrooms directly
const otherMushrooms = computed(() => mushroomStore.getOtherMushrooms)
</script>

<template>
  <div class="group-mushroom">
    <div class="mushroom-grid">
      <GroupMushroom
        v-for="mushroom in otherMushrooms"
        :key="mushroom.sessionId"
        :group="mushroom"
        class="mushroom-grid-item"
      />
    </div>
  </div>
</template>

<style scoped>
.group-mushroom {
  height: 100%;
  background-color: #E7FFDE;
  padding: 16px;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 추가적으로 모든 오버플로우를 숨기기 */
}

.mushroom-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr); /* 항상 2개의 열로 유지 */
  gap: 10px;
  max-height: 100%; /* 부모의 높이에 맞게 최대 높이 설정 */
  overflow-y: auto; /* 스크롤을 가능하게 설정 */
  flex-grow: 1; /* 그리드가 가능한 최대 높이를 차지하게 설정 */
}

.mushroom-grid-item {
  width: 100%;
  height: 200px; /* 고정된 높이 설정 */
  overflow: hidden;
  position: relative;
  background-color: #f0f0f0;
  border-radius: 8px;
  margin: 2px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.1);
}
</style>
