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
  <v-container fluid class="main-container">
    <!-- * Top Side : 현재 Game의 진행에 대한 정보를 나타내는 Status Bar -->
    <div class="top-container d-flex align-center justify-space-between">
      <StatusBar class="status-bar" title="Game Info" status="In Progress" />
      <v-btn
        v-if="userStore.isTeamLeader"
        color="red"
        @click="finishContents(sessionStore.subSessionId)"
      >
        Finish
      </v-btn>
    </div>

    <!-- * Bottom Size : Left -> MainMushroom , Right -> GroupMushrooms -->
    <v-row class="bottom-container">
      <v-col cols="12" md="8">
        <MainMushroomContainer />
      </v-col>
      <v-col cols="12" md="4">
        <GroupMushroomContainer />
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.main-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  gap: 16px;
}

.top-container {
  margin-top: 2%;
  border-bottom-left-radius: 8px;
  border-bottom-right-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.bottom-container {
  flex-grow: 1; /* Bottom Container가 남은 공간을 차지하도록 */
  overflow: hidden; /* 내부 요소가 커져도 넘치지 않도록 */
  display: flex;
  flex-direction: row;
  margin-bottom: 3.5%;
}
</style>
