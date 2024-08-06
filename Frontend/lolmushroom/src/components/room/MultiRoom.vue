<script setup>
import { storeToRefs } from 'pinia'
import { useRoomStore } from '@/stores/roomStore'
import { useSessionStore } from '@/stores/sessionStore'
import { useRouter } from 'vue-router'

// 상태 관리 사용
const router = useRouter()
const roomStore = useRoomStore()
const sessionStore = useSessionStore()
const { rooms } = storeToRefs(roomStore)

// RoomWatching으로 라우팅
const goToRoomWatching = () => {
  const sessionId = sessionStore.sessionId
  router.push({ name: 'roomwatching', params: { sessionId: sessionId } })
}
</script>

<template>
  <div>
    <v-row>
      <v-col v-for="(room, index) in rooms" :key="index" cols="12" md="6">
        <v-btn
          size="x-large"
          width="300"
          height="100"
          rounded="lg"
          block
          color="#e9ecef"
          class="mb-2"
        >
          <b>{{ `그룹${index + 1}` }}</b>
          <div class="mr">{{ room.occupants }}/{{ room.capacity }}</div>
          <div class="mr">
            <v-icon icon="$next"></v-icon>
          </div>
        </v-btn>
      </v-col>
    </v-row>
  </div>
  <div class="d-flex justify-center mt">
    <v-btn color="primary" @click="goToRoomWatching"> 전체보기 </v-btn>
  </div>
</template>

<style scoped>
.mt {
  margin-top: 30px;
}
.mr {
  font-weight: bold;
  margin-left: 200px;
}
</style>
