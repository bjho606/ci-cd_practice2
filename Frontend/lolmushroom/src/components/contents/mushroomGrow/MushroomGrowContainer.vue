<script setup>
import { onMounted } from 'vue'
import { useMushroomStore } from '@/stores/mushroomStore'
import { useSessionStore } from '@/stores/session'
import sessionAPI from '@/api/session'
import StatusBar from './StatusBar.vue'
import MainMushroomContainer from './MainMushroomContainer.vue'
import GroupMushroomContainer from './GroupMushroomContainer.vue'

const mushroomStore = useMushroomStore()
const sessionStore = useSessionStore()

/**
 * * TEST GROUP DATA
 * IMP Progress Socket을 통해 정보를 가져올 것
 */
const groups = [
  { sessionId: 'group-session-101', groupName: 'Group 1' },
  { sessionId: 'group-session-102', groupName: 'Group 2' },
  { sessionId: 'group-session-103', groupName: 'Group 3' },
  { sessionId: sessionStore.subSessionId, groupName: 'User Group' }, // 현재 사용자의 그룹
  { sessionId: 'group-session-105', groupName: 'Group 5' },
  { sessionId: 'group-session-106', groupName: 'Group 6' },
  { sessionId: 'group-session-107', groupName: 'Group 7' },
  { sessionId: 'group-session-108', groupName: 'Group 8' },
  { sessionId: 'group-session-109', groupName: 'Group 9' },
  { sessionId: 'group-session-110', groupName: 'Group 10' },
  { sessionId: 'group-session-111', groupName: 'Group 11' },
  { sessionId: 'group-session-112', groupName: 'Group 12' },
  { sessionId: 'group-session-113', groupName: 'Group 13' },
  { sessionId: 'group-session-114', groupName: 'Group 14' },
  { sessionId: 'group-session-115', groupName: 'Group 15' }
]

/**
 * TODO : MushroomGrow Contents에 대한 Socket 연결
 * IMP Socket 연결하는 중...
 */
onMounted(() => {
  mushroomStore.initSocketConnection(sessionStore.sessionId, sessionStore.subSessionId, groups)
})
</script>

<!-- IMP :  MushroomGrow Contents의 하위 Container를 담아내는 Container -->
<!-- TODO : 아마 Contents 설명을 위한 Overlay가 들어가는 것이 좋을 것 같음 For 작전 타임 -->
<template>
  <v-container fluid class="main-container">
    <!-- * Top Side : 현재 Game의 진행에 대한 정보를 나타내는 Status Bar -->
    <StatusBar class="top-container" title="Game Info" status="In Progress" />

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
  margin-bottom: 3%;
}
</style>
