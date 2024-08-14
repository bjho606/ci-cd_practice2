<script setup>
import { onMounted, computed, watch} from 'vue'
import { useBallStore } from '@/stores/ballStore'
import { useSessionStore } from '@/stores/sessionStore'
import { useRoomStore } from '@/stores/roomStore'
import { useUserStore } from '@/stores/userStore'
import { useContentsStore } from '@/stores/contentsStore'
import { useRouter } from 'vue-router'
import contentsAPI from '@/api/contents'
import WaitingHeader from '@/components/room/playerWaiting/WaitingHeader.vue'
import BallMain from './BallMain.vue'
import BallGroup from './BallGroup.vue'

const router = useRouter();
const roomStore = useRoomStore();
const ballStore = useBallStore();
const sessionStore = useSessionStore();
const userStore = useUserStore();
const contentsStore = useContentsStore();

const firstDescription = '공 키우기'
const secondDescription = '우리 그룹의 공을 최대한 크게 만드세요!'
const thirdDescription = 'Tip: 클릭 대신 스페이스바를 누를 수 있답니다 !'

/**
 * IMP Session Socket을 통해 활성화된 Group을 받아오고 Ball 개수 결정
 */
const activeGroups = computed(() => {
  return roomStore.getActiveRooms.map((room) => ({
    sessionId: room.sessionId,
    groupName: room.groupName
  }))
})

/**
 * 팀 리더가 컨텐츠 종료 시 종료 Signal을 진행자에게 보낸다.
 *
 */
const finishContents = async(sessionId) => {
  try{
    const response = await contentsAPI.finishContents(sessionId)
    if (response.data.isSuccess) {
      console.log('우리 Team은 Contents를 종료했어요!')
      console.log(response.data)
    }
  }catch(error){
    console.error('Error Finishing Contents', error)
  }
}

watch(
  () => contentsStore.currentGroupState,
  (newState) => {
    const group = newState.find((group) => group.sessionId === sessionStore.subSessionId)
    if (group && group.isFinish) {
      router.push({
        name: 'BallGrowResult',
        params: { sessionId: sessionStore.sessionId, subSessionId: sessionStore.subSessionId }
      })
    }
  },
  { deep: true } // Ensure the watcher detects nested changes within the array
)




// socket 연결
onMounted(() => {
  ballStore.initSocketConnection(
    sessionStore.sessionId,
    sessionStore.subSessionId,
    activeGroups.value
  )
})
</script>

<template>
  <div class="main-container">
    <WaitingHeader
      :first-description="firstDescription"
      :second-description="secondDescription"
      :third-description="thirdDescription"
    />
    <div class="content-container">
      <div class="left-container">
        <BallMain />
      </div>
      <div class="right-container">
        <BallGroup />
      </div>
    </div>
  </div>
</template>

<style scoped>
.main-container {
  background-color: #e7ffde;
  align-items: center;
  height: 100%; /* 화면 전체 높이를 차지 */
}

.content-container {
  display: flex;
  width: 100%;
  height: 88%;
}

.left-container {
  flex: 2;
  display: flex;
  justify-content: center; /* 수평 가운데 정렬 */
  align-items: center; /* 수직 가운데 정렬 */
}

.right-container {
  flex: 1;
  display: flex;
}
</style>
