<script setup>
import { computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useContentsStore } from '@/stores/contentsStore'
import { useSessionStore } from '@/stores/sessionStore'
import { useMushroomStore } from '@/stores/mushroomStore'
import { useRoomStore } from '@/stores/roomStore'
import webSocketAPI from '@/api/webSocket'
import Mushroom from './eachroom/eachMushRoom.vue'
import Swal from 'sweetalert2'
import contentsAPI from '@/api/contents'

const router = useRouter()
const roomStore = useRoomStore()
const contentsStore = useContentsStore()
const sessionStore = useSessionStore()
const mushroomStore = useMushroomStore()
const currentContents = computed(() => contentsStore.getCurrentContentsId)
console.log(currentContents)

const rooms = computed(() => roomStore.getRooms)
const mushrooms = computed(() => mushroomStore.getAllMushrooms)
const groups = computed(() => {
  return roomStore.getActiveRooms.map((room) => ({
    sessionId: room.sessionId,
    groupName: room.groupName
  }))
})

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

/**
 * TODO : 진행자는 현재 Contents의 상황을 Watch해야 한다.
 * TODO : 진행자는 현재 Contents의 ID에 따라, SubScribe를 다르게 하고, 예전 Subscription을 지운다.
 * TODO : 진행자는 현재 Contents의 정보를 하위 Component에 넘겨주면 된다.
 * TODO : 하위 Component는 v-if에 따라 보여주는 정보가 다르게 되고, 받는 정보가 달라진다.
 */
watch(currentContents, (newContentsId, oldContentsId) => {
  console.log('changed?')
  if (oldContentsId) {
    webSocketAPI.unsubscribeGame(oldContentsId)
  }
  if (newContentsId) {
    switch (newContentsId) {
      case '3':
        mushroomStore.initSocketConnection(
          sessionStore.sessionId,
          sessionStore.subSessionId,
          groups.value
        )
        break
      default:
        console.warn(`Unknown subscription type: ${newContentsId}`)
    }
  }
})

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
    }
  })
}

/**
 * ! Deprecated
 * ! 진행자는 multiRoom으로 가서, 현재 게임 진행상황을 관찰한다.
 */
const goToMultiRoom = () => {
  const sessionId = sessionStore.sessionId
  router.push({ name: 'multiroom', params: { sessionId: sessionId } })
}
</script>

<template>
  <div v-if="!currentContents">
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
  <div v-if="currentContents === '3'">
    <div class="grid-container">
      <!-- UI 수정 -->
      <Mushroom v-for="mushroom in mushrooms" :key="mushroom.sessionId" :group="mushroom" />
    </div>
  </div>
  <div class="d-flex justify-center mt">
    <v-btn color="deep-orange" @click="goToMultiRoom"> 돌아가기 </v-btn>
  </div>
  <div class="d-flex justify-center mt">
    <v-btn color="pink" @click="startGame"> 게임 시작 </v-btn>
  </div>
</template>

<style scoped>
div {
  text-align: center;
  font-weight: bold;
}

.rooms {
  background-color: white;
}

.mt {
  margin-top: 30px;
}
.grid-container {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px; /* 아이템 사이의 간격을 조절합니다 */
}
.grid-item {
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
}
</style>
