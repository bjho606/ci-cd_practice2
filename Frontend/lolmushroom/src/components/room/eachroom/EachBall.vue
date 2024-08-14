<script setup>
import { computed } from 'vue'
import { useBallStore } from '@/stores/ballStore'

const props = defineProps({
  room: {
    type: Object,
    required: true
  }
})

// ballStore에서 데이터를 가져옴
const ballStore = useBallStore()

// 해당 room의 rank와 score를 ballStore에서 가져오는 computed 속성
const rank = computed(() => ballStore.getRankByRoomId(props.room.id)) // 예시: 함수 이름은 실제 store에 맞게 변경
const score = computed(() => ballStore.getScoreByRoomId(props.room.id)) // 예시: 함수 이름은 실제 store에 맞게 변경
</script>

<template>
  <div class="room-info">
    <div class="rank">등수 - {{ rank }}</div>
    <div class="score">점수 - {{ score }}</div>
  </div>
</template>

<style scoped>
.room-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
  color: #000;
}

.rank,
.score {
  margin: 5px 0;
}
</style>
