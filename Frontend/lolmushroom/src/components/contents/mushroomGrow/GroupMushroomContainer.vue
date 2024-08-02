<template>
  <v-container class="right-container">
    <StatusBar title="Main Mushroom" status="Growing" />
    <v-container class="grid-container">
      <GroupMushroom
        v-for="group in groups"
        :key="group.name"
        :group="group"
        :previewSize="previewSize"
      />
    </v-container>
  </v-container>
</template>

<script setup>
import StatusBar from '@/components/contents/mushroomGrow/StatusBar.vue'
import GroupMushroom from '@/components/contents/mushroomGrow/GroupMushroom.vue'
import { useMushroomStore } from '@/stores/mushroomStore'

const store = useMushroomStore()
const groups = store.groups
const previewSize = store.previewSize
</script>

<style scoped>
.right-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.grid-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr); /* 2개의 열로 구성 */
  gap: 16px;
  width: 100%;
  height: 100%;
  align-items: center;
  justify-items: center;
  overflow-y: auto;
  padding: 16px;
}
</style>
