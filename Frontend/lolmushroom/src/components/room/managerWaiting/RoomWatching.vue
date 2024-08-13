<script setup>
import { ref, computed, watch } from 'vue'
import { useSessionStore } from '@/stores/sessionStore'
import { useRoomStore } from '@/stores/roomStore'
import { useContentsStore } from '@/stores/contentsStore'
import { useTOFStore } from '@/stores/tofStore'
import { useMushroomStore } from '@/stores/mushroomStore'
import Swal from 'sweetalert2'
import webSocketAPI from '@/api/webSocket'
import contentsAPI from '@/api/contents'

import ManagerHeader from './ManagerHeader.vue'
import WatchWaiting from '@/components/room/eachroom/adminWatchWaiting.vue'
import Mushroom from '@/components/room/eachroom/eachMushRoom.vue'
import ManagerFooter from './ManagerFooter.vue'
import FooterStart from './FooterStart.vue'
import FooterShare from './FooterShare.vue'

const roomStore = useRoomStore()
const contentsStore = useContentsStore()
const sessionStore = useSessionStore()
const TOFStore = useTOFStore()
const mushroomStore = useMushroomStore()
const showShareFooter = ref(true)

const currentContents = computed(() => contentsStore.getCurrentContentsId)
const mushrooms = computed(() => mushroomStore.getAllMushrooms)
const groups = computed(() => {
  return roomStore.getActiveRooms.map((room) => ({
    sessionId: room.sessionId,
    groupName: room.groupName,
    occupants: room.occupants
  }))
})

/**
 * IMP 1. 진행자가 게임 시작을 누르면, Session Contents가 시작하는 첫 단계이므로, isStart : true
 * IMP 1.1. Game이 시작된 이후에는, NextContents를 호출한다.
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
 * IMP 2. 진행자는 현재 Contents의 상황을 Watch하면서, ContentsId에 따라 Rendering이 달라진다.
 * IMP 2.1 하위 Component는 v-if에 따라 보여주는 정보가 다르게 되고, 받는 정보가 달라진다.
 */
const socketMapping = contentsStore.socketMapping
watch(currentContents, (newContentsId, oldContentsId) => {
  if (oldContentsId) {
    webSocketAPI.unsubscribeGame(socketMapping[oldContentsId])
  }
  if (newContentsId) {
    switch (newContentsId) {
      case '1':
        TOFStore.initSocketConnection(sessionStore.sessionId, groups.value)
        break
      case '7':
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
</script>

<template>
  <div class="room-waiting">
    <!-- 진행자 대기화면 Header -->
    <ManagerHeader />
    <WatchWaiting v-if="!currentContents" />

    <!-- 3. 공 키우기 화면 -->
    <div v-if="currentContents === '3'">
      <div class="grid-container">
        <Mushroom v-for="mushroom in mushrooms" :key="mushroom.sessionId" :group="mushroom" />
      </div>
    </div>

    <!-- [진행자 대기화면 Footer] -->
    <ManagerFooter>
      <template v-slot:start>
        <FooterStart @start-game="startGame" />
      </template>
      <template v-slot:share v-if="showShareFooter">
        <FooterShare />
      </template>
    </ManagerFooter>
  </div>
</template>

<style scoped>
div {
  text-align: center;
  font-weight: bold;
}
</style>
