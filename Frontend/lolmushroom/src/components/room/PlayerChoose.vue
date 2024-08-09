<script setup>
import { onMounted, computed } from 'vue'
import { useRoomStore } from '@/stores/roomStore'
import { useUserStore } from '@/stores/userStore'
import { useSessionStore } from '@/stores/sessionStore'
import { useRouter } from 'vue-router'
import sessionAPI from '@/api/session'

const router = useRouter()
const roomStore = useRoomStore()
const userStore = useUserStore()
const sessionStore = useSessionStore()
// const { rooms, activeButtonIndex } = storeToRefs(roomStore)
const rooms = computed(() => roomStore.getRooms)
const activeButtonIndex = computed(() => roomStore.getActiveButtonIndex)

/**
 * IMP 1. SubSession에 대한 Connection을 생성한다.
 * REQ
 * @param sessionId
 * @param userName
 *
 * RES openvide_token, user_token
 * ! Connection Token은 OpenVidu에서 주는 Token
 * ! Server와 인증에 사용되는 User JWT Token은 Cookie에 저장됨
 */
const getSessionConnection = async (sessionId, userName) => {
  try {
    const response = await sessionAPI.getSessionConnection(sessionId, userName)
    if (response.data.isSuccess) {
      console.log('Connection을 성공적으로 만들어 냈습니다. Connection Token은 다음과 같습니다:)')
      console.log(response.data)
    }
  } catch (error) {
    console.error('Error Getting Session Connection', error)
  }
}

/**
 * IMP 2. SubSession을 '생성'한다 -> '생성'과 '입장'은 구분되어 있음.
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

/**
 * * 3. Room을 클릭한다 => 2가지의 User Flow로 분기된다.
 * @param index
 * IMP 3.1 Room이 비활성 상태인 경우 -> 1번 createSubSessionHandler를 통해, 새로운 Sub Session을 생성한다.
 * IMP 3.2 Room이 활성 상태인 경우 -> 해당 Sub Session으로 들어가는 Routing을 해주고, 동시에 Sub Session과 Connection을 만들어준다.
 * * 이때, 'subSessionId : room.sessionId'을 통해 Sub Session Routing을 시켜주고 있음
 * * Room 배열에 이미 subSessionId가 붙어 있기 때문에, 가능한 Process
 * ! Leader가 2개가 만들어질 수 있을까?
 */
const handleRoomClick = async (index) => {
  const room = rooms.value[index]
  if (!room.buttonClicked) {
    await createSubSessionHandler(sessionStore.sessionId)
    sessionStore.setSessionId(sessionStore.sessionId)
  } else {
    console.log('하부 세션으로 입장을 하시는 군요! 하부 세션에 대한 연결을 해드릴게요:)')
    const isFirstUserInGroup = room.occupants === 0
    await getSessionConnection(room.sessionId, { userName: userStore.userNickname })
    if (isFirstUserInGroup) userStore.setTeamLeader(true)
    else userStore.setTeamLeader(false)
    sessionStore.setSubSessionId(room.sessionId)

    router.push({
      name: 'roomwaiting',
      params: { sessionId: sessionStore.sessionId, subSessionId: room.sessionId }
    })
  }
}

/**
 * TODO -> 주기적으로 getSessionInfo()를 통해, Room에 대한 정보를 가져와야 한다.
 * ! IF -> MeshRoom Flow에 대한 Socket 연결이 진행되면 Deprecated될 동작
 */

onMounted(async () => {})
</script>

<template>
  <v-container>
    <v-slide-group show-arrows>
      <v-slide-item v-for="(room, index) in rooms" :key="index">
        <v-card class="mx-4" color="grey lighten-4" width="250" height="200">
          <v-card-title>{{ room.isReady ? '준비' : '미준비' }}</v-card-title>
          <v-card-subtitle>{{ room.groupName }}</v-card-subtitle>
          <v-card-actions>
            <v-btn @click="handleRoomClick(index)" text>입장</v-btn>
          </v-card-actions>
        </v-card>
        <v-list v-for="user in room.users" :key="user" dense>
          <v-list-item>
            <v-list-item-content>
              <v-list-item-title>{{ user }}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </v-slide-item>
      <v-slide-item>
        <v-card
          class="mx-4"
          color="grey lighten-2"
          width="250"
          height="200"
          @click="handleRoomClick(index)"
        >
          <v-card-title class="d-flex justify-center align-center">+</v-card-title>
        </v-card>
      </v-slide-item>
    </v-slide-group>
  </v-container>
</template>

<style scoped>
.mt {
  margin-top: 30px;
}
</style>
