<script setup>
import { ref, computed } from 'vue'
import { useMushroomStore } from '@/stores/mushroomStore'
import StatusBar from './StatusBar.vue'

const store = useMushroomStore()

const currentMushroomImage = computed(() => store.currentMushroomImage)
const currentMushroomSize = computed(() => store.currentMushroomSize)
const currentGroup = computed(() => store.currentGroup)
const clickEffect = computed(() => store.clickEffect)
const clickPosition = computed(() => store.clickPosition)

const onMushroomClick = (event) => {
  store.onMushroomClick(event)
}

const onReturnClick = () => {
  store.onReturnClick()
}
</script>

<template>
  <v-card class="solo-mushroom-card" @click="onMushroomClick">
    <!-- <StatusBar title="Main Mushroom" status="Growing" /> -->
    <v-img :src="currentMushroomImage" aspect-ratio="1" class="mushroom-image" />
    <v-card-title class="text-center">Current Group: {{ currentGroup }}</v-card-title>
    <v-card-text class="text-center"> Size: {{ currentMushroomSize }}vw </v-card-text>
    <div
      v-if="clickEffect"
      class="click-effect"
      :style="{ top: clickPosition.y + 'px', left: clickPosition.x + 'px' }"
    >
      {{ currentGroup === 'User Group' ? '❤️' : '💥' }}
    </div>
    <v-card-actions>
      <v-btn v-if="currentGroup !== 'User Group'" @click="onReturnClick" class="mx-auto">
        돌아가기
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<style scoped>
.solo-mushroom-card {
  cursor: pointer;
  max-width: 300px;
  width: 100%;
  margin: auto;
  transition: transform 0.3s;
  position: relative;
}

.solo-mushroom-card:hover {
  transform: scale(1.05);
}

.mushroom-image {
  object-fit: cover;
}

.click-effect {
  position: absolute;
  font-size: 48px;
  pointer-events: none;
  transform: translate(-50%, -50%);
}
</style>
