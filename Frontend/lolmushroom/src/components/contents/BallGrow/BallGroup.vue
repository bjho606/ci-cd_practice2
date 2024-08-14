<script setup>
import { computed } from 'vue'
import { useBallStore } from '@/stores/ballStore'
import BallContent from './BallContent.vue'

const ballStore = useBallStore()
const totalBalls = computed(() => ballStore.getTotalBalls)

const rows = computed(() => {
  return totalBalls.value.reduce((acc, item, index) => {
    if (index % 2 === 0) acc.push([])
    acc[acc.length - 1].push(item)
    return acc
  }, [])
})
</script>

<template>
  <div class="ball-group">
    <div v-for="(row, index) in rows" :key="index" class="row">
      <div v-for="ball in row" :key="ball.sessionId" class="ball-item">
        <BallContent :groupId="ball.sessionId" :isMain="false" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.ball-group {
  max-height: 700px;
  overflow-y: auto; /* 내용이 넘칠 때 스크롤바가 나타나도록 설정 */
}

.row {
  display: flex;
  margin-bottom: 20px;
}

.ball-item {
  margin-right: 30px;
}

.row:last-child .ball-item:last-child {
  margin-right: 0;
}
</style>
