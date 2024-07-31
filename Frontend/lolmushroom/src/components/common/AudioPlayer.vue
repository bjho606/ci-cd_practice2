<script setup>
import { ref } from 'vue'
import MeshroomBgm from '@/assets/MeshroomBgm.mp3'

const audio = ref(MeshroomBgm)
const isPlaying = ref(false)
const bgmIcon = ref('custom:bgmOn')

const toggleSound = () => {
  if (audio.value) {
    if (isPlaying.value) {
      bgmIcon.value = 'custom:bgmOff'
      audio.value.pause()
    } else {
      audio.value.play()
      bgmIcon.value = 'custom:bgmOn'
    }
    isPlaying.value = !isPlaying.value
  }
}
</script>

<template>
  <div>
    <audio :src="MeshroomBgm" preload="auto" loop ref="audio" id="audio"></audio>
    <v-icon :icon="bgmIcon" @click="toggleSound"> </v-icon>
  </div>
</template>

<style scoped></style>
