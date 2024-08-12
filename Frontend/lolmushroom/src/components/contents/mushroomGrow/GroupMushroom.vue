<script setup>
import { computed } from 'vue'
import { useMushroomStore } from '@/stores/mushroomStore'

const props = defineProps({
  group: {
    type: Object,
    required: true
  }
})

const mushroomStore = useMushroomStore()
const mushroomImage = computed(() => mushroomStore.getMushroomImage(props.group.sessionId))
const mushroomSize = computed(() => `${props.group.size / 4}vw`) // MainMushroom과 비율을 고려하여 크기 조정

const onChangeClick = () => {
  mushroomStore.onChangeClick(props.group.sessionId)
}
</script>

<template>
  <v-card class="group-mushroom-card" @click="onChangeClick">
    <div class="mushroom-container">
      <v-img
        :src="mushroomImage"
        :style="{ width: mushroomSize, height: mushroomSize }"
        class="mushroom-image"
      />
    </div>
    <v-card-title class="text-center">{{ group.groupName }}</v-card-title>
    <v-card-text class="text-center">Size: {{ group.size }}vw</v-card-text>
  </v-card>
</template>

<style scoped>
.group-mushroom-card {
  cursor: pointer;
  max-width: 200px;
  width: 100%;
  margin: auto;
  transition: transform 0.3s;
}

.group-mushroom-card:hover {
  transform: scale(1.05);
}

.mushroom-container {
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

.mushroom-image {
  object-fit: cover;
  transition:
    width 0.5s,
    height 0.5s; /* 크기 변화 애니메이션 */
}
</style>
