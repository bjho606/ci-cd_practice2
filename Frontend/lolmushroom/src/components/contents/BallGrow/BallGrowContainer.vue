<script setup>
import { onMounted, computed } from 'vue'
import { useBallStore } from '@/stores/ballStore'
import { useSessionStore } from '@/stores/sessionStore'
import { useRoomStore } from '@/stores/roomStore'


import WaitingHeader from '@/components/room/playerWaiting/waitingHeader.vue'
import BallMain from './BallMain.vue'
import BallGroup from './BallGroup.vue'

const firstDescription = "공 키우기"
const secondDescription = "우리 그룹의 공을 최대한 크게 만드세요!"
const thirdDescription = "Tip: 클릭 대신 스페이스바를 누를 수 있답니다 !"
const roomStore = useRoomStore()
const ballStore = useBallStore()
const sessionStore = useSessionStore()

/**
 * IMP Progress Socket을 통해 활성화된 Group을 받아오고 Ball 개수 결정
 */
const activeGroups = computed(() => {
  return roomStore.getActiveRooms.map((room) => ({
    sessionId: room.sessionId,
    groupName: room.groupName
  }))
})
console.log(activeGroups.value)

onMounted(() => {
  ballStore.initSocketConnection(
    sessionStore.sessionId,
    sessionStore.subSessionId,
    activeGroups.value
  )
})
</script>

<template>
  <WaitingHeader :first-description="firstDescription" :second-description="secondDescription" :third-description="thirdDescription"/>
  <div class="main-container">
    <div class="content-container">
      <!-- <div class="left-container">
        <BallMain />
      </div>
      <div class="right-container">
        <BallGroup />
      </div> -->
    </div>
  </div>
</template>

<style scoped>
.main-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.content-container {
  display: flex;
  width: 100%;
}
.left-container {
  flex: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.right-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  overflow-y: auto;
  height: 100%;
}
</style>
