<script setup>
import { computed } from 'vue'
import { useMushroomStore } from '@/stores/mushroomStore'
import StatusBar from './StatusBar.vue'
import GroupMushroom from './GroupMushroom.vue'

const mushroomStore = useMushroomStore()

// Get the other mushrooms directly
const otherMushrooms = computed(() => mushroomStore.getOtherMushrooms)
</script>

<template>
  <v-card class="group-mushroom">
    <StatusBar title="Group Mushroom" status="Growing" />
    <v-container class="mushroom-grid">
      <GroupMushroom
        v-for="mushroom in otherMushrooms"
        :key="mushroom.sessionId"
        :group="mushroom"
      />
    </v-container>
  </v-card>
</template>

<style scoped>
.group-mushroom {
  height: 100%;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.mushroom-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr); /* 항상 2개의 열로 유지 */
  gap: 10px;
  max-height: calc(100vh - 150px); /* 원하는 높이로 조정 가능 */
  overflow-y: auto;
}
</style>
