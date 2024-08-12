<script setup>
import { onMounted, computed, watch } from 'vue'
import { useMushroomStore } from '@/stores/mushroomStore'
import { useContentsStore } from '@/stores/contentsStore'
import { useUserStore } from '@/stores/userStore'
import { useSessionStore } from '@/stores/sessionStore'
import { useRoomStore } from '@/stores/roomStore'
import { useRouter } from 'vue-router'
import contentsAPI from '@/api/contents'
import watingHeader from '@/components/room/playerWating/watingHeader.vue'
import MainMushroomContainer from './MainMushroomContainer.vue'
import GroupMushroomContainer from './GroupMushroomContainer.vue'

const roomStore = useRoomStore()
const userStore = useUserStore()
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
    <watingHeader />
    <!-- * Top Side : 현재 Game의 진행에 대한 정보를 나타내는 Status Bar -->
    <button
      v-if="userStore.isTeamLeader"
      class="finish-button"
      @click="finishContents(sessionStore.subSessionId)"
    >
      Finish
    </button>

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
}

.finish-button {
  background-color: red;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.bottom-container {
  height: 80%;
  display: flex;
  flex-direction: row;
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
