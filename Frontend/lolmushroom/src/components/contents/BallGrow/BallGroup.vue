<script setup>
import { computed } from 'vue'
import { useBallStore } from '@/stores/ballStore'
import BallContent from './BallContent.vue'

const ballStore = useBallStore()
const totalBalls = computed(() => ballStore.getTotalBalls)
console.log(totalBalls.value)
</script>

<template>
  <div class="ball-group">
    <BallContent v-for="ball in totalBalls" :key="ball.sessionId" :ball="ball" />
  </div>
</template>

<style scoped></style>
