<script setup>
import { computed } from 'vue'
import { useBallStore } from '@/stores/ballStore'

const props = defineProps({
  ball: Object
})
const ballStore = useBallStore()
const ballImage = computed(() => ballStore.getBallImage(props.ball.sessionId))
const ballSize = computed(() => props.ball.size)
console.log(ballImage.value)
</script>

<template>
  <div>
    <v-img :src="ballImage" :style="{ width: ballSize, height: ballSize }"> </v-img>
  </div>
</template>

<style scoped></style>
