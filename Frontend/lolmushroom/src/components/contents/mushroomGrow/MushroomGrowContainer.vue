<script setup>
import { onMounted, computed, watch } from 'vue'
import { useMushroomStore } from '@/stores/mushroomStore'
import { useContentsStore } from '@/stores/contentsStore'
import { useUserStore } from '@/stores/userStore'
import { useSessionStore } from '@/stores/sessionStore'
import { useRoomStore } from '@/stores/roomStore'
import { useRouter } from 'vue-router'
import contentsAPI from '@/api/contents'
import StatusBar from './StatusBar.vue'
import MainMushroomContainer from './MainMushroomContainer.vue'
import GroupMushroomContainer from './GroupMushroomContainer.vue'

const roomStore = useRoomStore()
const userStore = useUserStore() // 팀장으로 임명한다.
const contentsStore = useContentsStore()
const mushroomStore = useMushroomStore()
const sessionStore = useSessionStore()
const router = useRouter()

/**
 * * TEST GROUP DATA
 * IMP Progress Socket을 통해 정보를 가져올 것
 */

const groups = computed(() => {
  return roomStore.getActiveRooms.map((room) => ({
    sessionId: room.sessionId,
    groupName: room.groupName
  }))
})

/**
 * TODO : Team Leader는 Contents가 종료되면, 종료 Signal을 진행자에게 보낸다.
 */
const finishContents = async (sessionId) => {
  try {
    const response = await contentsAPI.finishContents(sessionId)
    if (response.data.isSuccess) {
      console.log('우리 Team은 Contents를 종료했어요!')
      console.log(response.data)
    }
  } catch (error) {
    console.error('Error Finishing Contents', error)
  }
}

watch(
  () => contentsStore.currentGroupState,
  (newState) => {
    const group = newState.find((group) => group.sessionId === sessionStore.subSessionId)

    if (group && group.isFinish) {
      router.push({
        name: 'roomwaiting',
        params: { sessionId: sessionStore.sessionId, subSessionId: sessionStore.subSessionId }
      })
    }
  },
  { deep: true } // Ensure the watcher detects nested changes within the array
)

/**
 * TODO : MushroomGrow Contents에 대한 Socket 연결
 * IMP Socket 연결하는 중...
 */
onMounted(() => {
  mushroomStore.initSocketConnection(
    sessionStore.sessionId,
    sessionStore.subSessionId,
    groups.value
  )
})
</script>

<!-- IMP :  MushroomGrow Contents의 하위 Container를 담아내는 Container -->
<!-- TODO : 아마 Contents 설명을 위한 Overlay가 들어가는 것이 좋을 것 같음 For 작전 타임 -->
<template>
  <div class="main-container">
    <!-- * Top Side : 현재 Game의 진행에 대한 정보를 나타내는 Status Bar -->
    <div class="top-container">
      <div class="section section-1">
        <div class="content">
          <h1>공 키우기</h1>
        </div>
      </div>
      <div class="section section-2">
        <div class="content">
          <p>우리 그룹의 공을 최대한 크게 만들어요!</p>
        </div>
      </div>
      <div class="section section-3">
        <div class="tip-section">
          <div class="content">
            <p> TIP: 클릭 대신 스페이스바를 누르면 더 빠를 수도 있습니다~!</p>
          </div>
          <button
            v-if="userStore.isTeamLeader"
            class="finish-button"
            @click="finishContents(sessionStore.subSessionId)"
          >
            Finish
          </button>
        </div>
      </div>
    </div>

    <!-- * Bottom Size : Left -> MainMushroom , Right -> GroupMushrooms -->
    <div class="bottom-container">
      <div class="main-mushroom-container">
        <MainMushroomContainer />
      </div>
      <div class="group-mushroom-container">
        <GroupMushroomContainer />
      </div>
    </div>
  </div>
</template>


<style scoped>
.main-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  gap: 16px;
}

.top-container {
  display: flex; /* Flexbox 레이아웃 적용 */
  position: relative;
  margin: 2% 2%;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border-radius: 20px;
  height: 40%;
  overflow: hidden;
}

.section {
  position: absolute;
  height: 100%;
}

.section-1 {
  border-radius: 20px;
  width: 20%;
  background-color: #90FF77;
  text-align: center;
  left: 0;
  z-index: 3;
  align-content: center;
}

.section-2 {
  width: 40%;
  background-color: #CEFFBC;
  left: 15%;
  z-index: 2;
  border-radius: 20px;
  text-align: center;
  align-content: center;
}

.section-3 {
  width: 63%;
  left: 35%;
  z-index: 1;
  text-align: center;
  align-content: center;
}

.tip-section {
  text-align: right; /* Tip 섹션의 텍스트를 오른쪽 정렬 */
}

.finish-button {
  background-color: red;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 8px;
}

.bottom-container {
  flex-grow: 1;
  overflow: hidden;
  display: flex;
  flex-direction: row;
  margin-bottom: 3.5%;
}

.main-mushroom-container {
  flex: 0 0 60%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%; /* 부모 컴포넌트가 가득 채우도록 설정 */
}

.group-mushroom-container {
  flex: 0 0 40%;
  margin-right: 5%;
}
</style>

