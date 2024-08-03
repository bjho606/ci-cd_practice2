<script setup>
import { computed } from 'vue'
import { useMushroomStore } from '@/stores/mushroomStore'

const props = defineProps({
  group: {
    type: Object,
    required: true
  },
  previewSize: {
    type: Number,
    default: 10
  }
})

const mushroomStore = useMushroomStore()
const mushroomImage = computed(() => mushroomStore.getMushroomImage(props.group.size))

const changeClick = () => {
  mushroomStore.onChangeClick(props.id)
}
</script>

<template>
  <v-card class="group-mushroom-card" @click="changeClick">
    <v-img :src="mushroomImage" aspect-ratio="1" class="mushroom-image" />
    <v-card-title class="text-center">{{ group.name }}</v-card-title>
    <v-card-text class="text-center"> Size: {{ group.size }}vw </v-card-text>
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

.mushroom-image {
  object-fit: cover;
}
</style>
