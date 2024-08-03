<script setup>
import { computed } from 'vue'
import { useMushroomStore } from '@/stores/mushroomStore'
import StatusBar from './StatusBar.vue'
import GroupMushroom from './GroupMushroom.vue'

const mushroomStore = useMushroomStore()

// Group mushrooms in pairs for 2-column layout
const chunkedMushrooms = computed(() => {
  const otherMushrooms = mushroomStore.getOtherMushrooms
  const chunkSize = 2
  const chunks = []
  for (let i = 0; i < otherMushrooms.length; i += chunkSize) {
    chunks.push(otherMushrooms.slice(i, i + chunkSize))
  }
  return chunks
})
</script>

<template>
  <v-card class="main-mushroom-card">
    <StatusBar title="Main Mushroom" status="Growing" />
    <v-virtual-scroll
      :items="chunkedMushrooms"
      :height="580"
      item-height="90"
      class="custom-scroll full-height"
    >
      <template v-slot:default="{ item: mushroomPair }">
        <v-row no-gutters>
          <v-col v-for="(mushroom, index) in mushroomPair" :key="index" cols="12" sm="6">
            <GroupMushroom
              :group="{ id: mushroom.sessionId, name: mushroom.name, size: mushroom.size }"
            />
          </v-col>
        </v-row>
      </template>
    </v-virtual-scroll>
  </v-card>
</template>

<style scoped>
.main-mushroom-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.custom-scroll::-webkit-scrollbar {
  width: 4px; /* 스크롤바 너비 조정 */
}

.custom-scroll::-webkit-scrollbar-track {
  background: #f1f1f1; /* 스크롤바 트랙 배경색 */
}

.custom-scroll::-webkit-scrollbar-thumb {
  background: #888; /* 스크롤바 색상 */
}

.custom-scroll::-webkit-scrollbar-thumb:hover {
  background: #555; /* 호버 시 스크롤바 색상 */
}
</style>
