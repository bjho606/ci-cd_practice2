<script setup>
import { computed } from 'vue'
import { useSessionStore } from '@/stores/sessionStore'
import { useRoomStore } from '@/stores/roomStore'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import contentsAPI from '@/api/contents'

// 상태 관리 사용
const router = useRouter()
const roomStore = useRoomStore()
const sessionStore = useSessionStore()
const rooms = computed(() => roomStore.getRooms)

// RoomWatching으로 라우팅
const goToMultiRoom = () => {
  // Assuming sessionId is stored in the roomStore
  router.push({ name: 'multiroom', params: { sessionId: sessionStore.sessionId } })
}

/**
 * IMP 1. 진행자가 게임 시작을 누르면, Session Contents가 시작하는 첫 단계이므로, isStart : true
 * * Contents API의 callNextContetns를 호출한다.
 */
const callNextContents = async (isStart) => {
  try {
    const response = await contentsAPI.callNextContents(isStart)
    if (response.data.isSuccess) {
      console.log(response.data)
    }
  } catch (error) {
    console.error('Error Call Next Contents', error)
  }
}

// 게임 시작 알림
const startGame = () => {
  Swal.fire({
    title: '게임을 시작하시겠습니까?',
    text: '다시 돌아갈 수 없어요!',
    icon: 'question',
    color: 'black',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: '시작',
    cancelButtonText: '취소'
  }).then((result) => {
    if (result.isConfirmed) {
      callNextContents(true)
      goToMultiRoom()
    }
  })
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
          <b>{{ room.groupName || `그룹${index + 1}` }}</b>
          <div class="mr">
            <span v-if="room.isReady" class="ready-status"> (준비 완료) </span
            >{{ room.occupants }}/{{ room.capacity }}
          </div>
          <div class="mr">
            <v-icon icon="$next"></v-icon>
          </div>
        </v-btn>
      </v-col>
    </v-row>
  </div>
  <div class="d-flex justify-center mt">
    <v-btn color="pink" @click="startGame"> 게임 시작 </v-btn>
  </div>
</template>

<style scoped>
.mt {
  margin-top: 30px;
}
.mr {
  font-size: small;
  font-weight: bold;
  margin-left: 200px;
}
</style>
