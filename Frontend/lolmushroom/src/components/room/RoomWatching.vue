<script setup>
import { computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useContentsStore } from '@/stores/contentsStore'
import { useSessionStore } from '@/stores/sessionStore'
import { useMushroomStore } from '@/stores/mushroomStore'
import { useRoomStore } from '@/stores/roomStore'
import webSocketAPI from '@/api/webSocket'
import WatchWaiting from './eachroom/adminWatchWaiting.vue'
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
  <div class="room-waiting">
    <!-- [진행자 대기화면 Header] -->
    <header class="room-waiting-header">
      <div class="room-waiting-title">
        <span>😀</span> 
        우리들의 오리엔테이션 세션
      </div>
      <div class="room-waiting-count-info">
          현재 인원  
          <span class="total-count-info">{{ currentCount }}/ 60</span>
      </div>
    </header>

    <!-- [진행자 대기화면 그룹별 현황 뷰] : 진행되는 콘텐츠에 따라 바뀌는 부분 -->
    <!-- 0. 기본 대기 화면 -->
    <WatchWaiting v-if="!currentContents"/>
    
    <!-- 3. 공 키우기 화면 -->
    <div v-if="currentContents === '3'">
      <div class="grid-container">
        <Mushroom v-for="mushroom in mushrooms" :key="mushroom.sessionId" :group="mushroom" />
      </div>
    </div>
        
    <!-- [진행자 대기화면 Footer] -->
    <div class="room-waiting-footer">
      <div></div>
      <div class="room-waiting-footer-start">
        <button class="start-session-btn" @click="startGame">컨텐츠 시작하기</button>
      </div>
      <div class="room-waiting-footer-share">
        <v-btn class="share-session-btn">공유</v-btn>
        <div class="share-info-text">방 코드 공유로 편리한 오리엔테이션을 만드세요</div>
      </div>
    </div>
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

.room-waiting {
  font-family: 'Arial', sans-serif;
  width: 100%;
  margin: 0 auto;
}

.room-waiting-header {
  min-width: 500px;
  width: 60%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-content: center;
  background-color: #1ED013;
  border-radius: 10px;
  margin-bottom: 30px;
}

.room-waiting-title {
  flex-basis: 60%;
  min-width: 200px; /* Set the minimum width to maintain layout on smaller screens */
  max-width: 60%; /* Ensures that it doesn't grow beyond 60% */
  min-height: 60px;
  background-color: #24A319;
  border-radius: 10px;
  align-content: center;
  padding: 10px;
  font-size: 22px;
  font-weight: bold;
}

.room-waiting-count-info {
  flex-direction: row;
  justify-content: center;
  align-content: center;
  border-radius: 10px;
  width: 40%;
  font-size: 3vm;
}


.room-waiting-footer{
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  margin-top: 30px;
  margin-bottom: 50px;
  text-align: center;
  height: 80px;
}

.room-waiting-footer > div {
  flex: 1;
}

.room-waiting-footer-start {
  flex: 2;
}

.start-session-btn {
  width: 100%;
  height: 100%;
  background-color: #1F4F16;
  color: #FFFFFF;
  padding: 20px 20px;
  border: none;
  border-radius: 10px;
  font-size: 1.5rem;
}

.room-waiting-footer-share {
  min-width: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.share-session-btn{
  background-color: #E7FFDE;
  color: #000000;
  width: 120px;
  height: 60%;
  padding: 10px 20px;
  margin-bottom: 10px;
  margin-right: 50px;
  border: none;
  border-radius: 50px;
  align-content: center;
  font-size: 1.2rem;
}

.share-info-text {
  max-width: 100%;
  font-size: 3vm;
}

.group-list {
  display: flex;
  flex-wrap: wrap;
  margin-top: 20px;
  gap: 20px;
}

.group-card {
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  border: 1px solid #00FF00;
  border-radius: 20px;
  width: 23%;
  text-align: center;
  width: 315px;
  height: 315px;
}

.group-card.ready .group-status {
  background-color: #1F4F16;
  color: #ffffff;
}

.group-card.preparing .group-status {
  background-color: #00D200;
  color: #ffffff;
}

.group-header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  min-height: 45px;
  margin: 10px 0;
  padding: 10px 0;
}

.group-name {
  flex: 1;
  font-size: 25px;
  font-weight: bold;
}

.group-count {
  flex: 1;
  font-size: 20px;
}

.group-members {
  list-style: none;
  height: 130px;
  padding: 10px;
  margin: 10px 0;
}

.group-status {
  background-color: #364b41;
  color: white;
  height: 45px;
  font-size: 20px;
  border: none;
  align-content: center;
}

.empty-card {
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px dashed #00D200;
  height: 315px;
  width: 315px;
}

</style>
