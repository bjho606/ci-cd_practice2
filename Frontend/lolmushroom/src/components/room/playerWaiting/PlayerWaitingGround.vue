<script setup>
import { computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useRoomStore } from '@/stores/roomStore'
import { useSessionStore } from '@/stores/sessionStore'
import { useUserStore } from '@/stores/userStore'
import { useContentsStore } from '@/stores/contentsStore'
import sessionAPI from '@/api/session'

const router = useRouter()
const roomStore = useRoomStore()
const sessionStore = useSessionStore()
const userStore = useUserStore()
const contentsStore = useContentsStore()
const rooms = computed(() => roomStore.getRooms)
const activeButtonIndex = computed(() => roomStore.activeButtonIndex)
const subSessionInfo = computed(() => roomStore.getGroupInfoBySessionId(sessionStore.subSessionId))

watch(subSessionInfo, (newValue) => {
  if (newValue && newValue.teamLeaderId === userStore.userId) {
    userStore.setTeamLeader(true)
  } else {
    userStore.setTeamLeader(false)
  }
})

// SubSession 연결
const getSessionConnection = async (sessionId, userName) => {
  try {
    const response = await sessionAPI.getSessionConnection(sessionId, userName)
    if (response.data.isSuccess) {
      console.log('Connection을 성공적으로 만들어 냈습니다. Connection Token은 다음과 같습니다:)')
    }
  } catch (error) {
    console.error('Error Getting Session Connection', error)
  }
}

/**
 * IMP  SubSession을 '생성'한다 -> '생성'과 '입장'은 구분되어 있음.
 * REQ
 * @param sessionId
 *
 * RES sessionId(하위 세션 ID)
 * * sessionId : 하위 세션 ID를 Result로 가져오지만, '입장'하는 것이 아니므로 쓸 일이 없다.
 * IMP CreateSubSessionHandler()를 통해 Sub Session을 만들어내면, Main Session의 정보가 Server에서 갱신된다.
 * IMP 이러한 특성을 이용해, 2번 getSessionInfo()를 통해 Main Session의 활성화 상태를 갱신한다.
 */
const createSubSessionHandler = async (sessionId) => {
  try {
    const response = await sessionAPI.createSubSession(sessionId)
    if (response.data.isSuccess) {
      console.log('하부 Session의 생성이 완료되었습니다:)')
    }
  } catch (error) {
    console.error('Error Creating SubSession', error)
  }
}

// IMPL1 준비 버튼 클릭하면 준비 되기

const sendReadyMessage = async (index) => {
  const room = rooms.value[index]
  try {
    const response = await sessionAPI.getSubSessionReady(room.sessionId)
    if (response.data.isSuccess) {
      console.log('우리 Team은 모두 준비 완료입니다:)')
    }
  } catch (error) {
    console.error('Error Ready Message to Manager')
  }
}

const handleRoomClick = async (index) => {
  const room = rooms.value[index]

  if (room.sessionId === sessionStore.subSessionId) {
    // User is already in this session, so leave it
    await handleLeaveSession(index)
    sessionStore.setSubSessionId(null)
  } else {
    if (!room.isActive) {
      await createSubSessionHandler(sessionStore.sessionId)
      sessionStore.setSessionId(sessionStore.sessionId)
    } else {
      console.log('하부 세션으로 입장을 하시는 군요! 하부 세션에 대한 연결을 해드릴게요:)')
      await getSessionConnection(room.sessionId, { userName: userStore.userName })
      sessionStore.setSubSessionId(room.sessionId)
    }
  }
}

const changeRoomName = async (index) => {
  const room = rooms.value[index]
  if (room.sessionId === sessionStore.subSessionId && userStore.isTeamLeader) {
    await sessionAPI.changeSubSessionName(room.sessionId)
  }
}

const handleLeaveSession = async () => {
  try {
    await sessionAPI.getSubSessionQuit(sessionStore.subSessionId)
    userStore.setTeamLeader(false)
    sessionStore.setSubSessionId(null)
    console.log('Session에서 성공적으로 나왔습니다.')
  } catch (error) {
    console.error('Error Leaving Session', error)
  }
}

const routeMapping = contentsStore.getRouteMapping
watch(
  () => contentsStore.currentContentsId,
  (newContentsId) => {
    if (newContentsId && routeMapping[newContentsId]) {
      userStore.setIsStarted()
      router.push({
        name: routeMapping[newContentsId],
        params: {
          sessionId: sessionStore.sessionId, // Replace with actual session ID
          subSessionId: sessionStore.subSessionId // Replace with actual sub-session ID
        }
      })
    }
  }
)
</script>

<template>
  <div class="room-list-container">
    <div class="room-list">
      <v-container v-for="(room, index) in rooms" :key="room.sessionId" class="group-container">
        <div v-if="room.isActive">
          <v-card
            class="group-card"
            :class="{ 'active-session': room.sessionId === sessionStore.subSessionId }"
          >
            <v-btn
              :style="{
                backgroundColor: room.isReady ? '#000000' : '#D9D9D9',
                color: room.isReady ? '#FFFFFF' : '#000000'
              }"
              @click="sendReadyMessage(index)"
              class="ready-btn"
            >
              <div>
                {{ room.isReady ? '준비완료' : '준비' }}
                <p v-if="!userStore.getIsTeamLeader" class="warning">
                  팀장만 준비완료가 가능합니다!
                </p>
              </div>
            </v-btn>
            <v-btn class="group-name" @click="changeRoomName(index)">
              <v-img src="src/assets/image/autorenew.svg" class="icon" />
              {{ room.groupName }}
            </v-btn>
            <v-btn
              :style="{ backgroundColor: '#E7FFDE' }"
              class="join-btn"
              @click="handleRoomClick(index)"
            >
              {{ room.sessionId === sessionStore.subSessionId ? '나가기' : '참가하기' }}
            </v-btn>
            <!-- 하위 세션 밑에 USER 정보 삽입 -->
            <div v-if="room.users.length > 0" class="user-list">
              <v-btn v-for="user in room.users" :key="user" class="user-btn" min-width="180">
                {{ room.groupName }}
                <div>
                  {{ user }}
                </div>
              </v-btn>
            </div>
            <!-- 정보 삽입 END -->
          </v-card>
        </div>
        <div v-else-if="index === activeButtonIndex">
          <v-btn
            min-width="150px"
            varient="elevated"
            :style="{ backgroundColor: '#CEFFBC' }"
            class="add-group-button"
            @click="handleRoomClick(index)"
          >
            그룹 새로 만들기
          </v-btn>
        </div>
      </v-container>
    </div>
  </div>
</template>

<style scoped>
/* 글씨 가운데 */
v-container {
  text-align: center;
}

/* 말풍선 팀장만 준비 가능*/
.warning {
  display: none;
  position: absolute;
  width: 320px;
  padding: 0px;
  left: 50%;
  transform: translateX(-50%);
  margin-bottom: 5px; /* Space between button and warning */
  border-radius: 8px;
  background: #333;
  color: #fff;
  font-size: 14px;
  z-index: 10;
}

.ready-btn:hover .warning {
  display: block;
}

/* 전체 컨테이너 이동 */
.room-list-container {
  width: 100%;
  height: 90%;
  overflow-x: auto;
}

.room-list {
  flex-direction: row;
  display: flex;
  flex-wrap: nowrap;
  justify-content: flex-start;
  padding: 10px;
}

.group-container {
  flex: 0 0 auto;
  width: 250px;
  margin-bottom: 20px;
}

/* 그룹 플렉스로 설정 */
.group-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
  background-color: #fff2f7;
}

/* 전체 크기만 설정 */
.ready-btn,
.group-name,
.join-btn {
  width: 100%;
}

.ready-btn {
  min-height: 40px;
}

.group-name {
  background-color: #66ff4f;
  font-size: larger;
  font-weight: bold;
  min-height: 60px;
  margin-bottom: 20px;
}

/* 그룹 만들기 버튼 */
.add-group-button {
  margin-top: 30px;
  min-height: 80px;
}

/* 유저 정보 나열 */
.user-list {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-top: 10px;
}

.user-btn {
  background-color: #ceffbc;
  font-size: 12px;
  padding: 5px 10px;
  margin-bottom: 10px;
}

.active-session {
  border: 2px solid #4caf50; /* Green border to emphasize the active session */
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2); /* Slight shadow for better visual effect */
}

/* 다시하기 이미지 아이콘 설정 */
.icon {
  width: 20px;
  margin-right: 10px;
}
</style>
