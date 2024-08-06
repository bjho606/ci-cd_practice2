<script setup>
import { storeToRefs } from 'pinia'
import { useRoomStore } from '@/stores/roomStore'
import { useSessionStore } from '@/stores/sessionStore'
import { useRouter } from 'vue-router'

const router = useRouter()
const roomStore = useRoomStore()
const sessionStore = useSessionStore()
const { rooms, roomCount } = storeToRefs(roomStore)

// go to multi room
const goToMultiRoom = () => {
  const sessionId = sessionStore.sessionId
  router.push({ name: 'multiroom', params: { sessionId: sessionId } })
}
</script>

<template>
  <div class="grid-container">
    <div v-for="(room, index) in rooms" :key="index" class="grid-item card">
      <span>{{ index + 1 }} 번방</span>
    </div>
  </div>
  <div class="d-flex justify-center mt">
    <v-btn color="deep-orange" @click="goToMultiRoom"> 돌아가기 </v-btn>
  </div>
</template>

<style scoped>
.mt {
  margin-top: 30px;
}
.grid-container {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px; /* 아이템 사이의 간격을 조절합니다 */
}
.grid-item {
  background-color: teal;
  padding-top: 100%; /* 1:1 비율 유지 */
  position: relative;
}
.grid-item > * {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
.card {
  width: 100%;
  background-color: teal;
}
</style>
