<template>
  <div ref="gameContainer" class="game-container"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { initGame, destroyGame } from '@/plugins/mushroomGrowMy'

const gameContainer = ref(null)

onMounted(() => {
  if (gameContainer.value) {
    initGame(gameContainer.value)
  }
})

onBeforeUnmount(() => {
  destroyGame()
})
</script>

<style scoped>
.game-container {
  width: 100%;
  height: 100vh;
  overflow: hidden;
}
</style>
