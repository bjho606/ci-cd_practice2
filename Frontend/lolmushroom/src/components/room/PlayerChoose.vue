<script setup>
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRoomStore } from '@/stores/roomStore'
import { useUserStore } from '@/stores/User'
import { useSessionStore } from '@/stores/session'
import { useRouter } from 'vue-router'
import sessionAPI from '@/api/session'

const router = useRouter()
const roomStore = useRoomStore()
const userStore = useUserStore()
const sessionStore = useSessionStore()
const { rooms, activeButtonIndex } = storeToRefs(roomStore)

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
      console.log('하부 Session의 생성이 완료되었습니다:) 현재 Session의 상태를 가져올게요:)')
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
    // roomStore.setButtonState(index, true)
  } else {
    console.log('하부 세션으로 입장을 하시는 군요! 하부 세션에 대한 연결을 해드릴게요:)')
    const isFirstUserInGroup = room.occupants === 0
    await getSessionConnection(room.sessionId, { userName: userStore.userNickname })
    if (isFirstUserInGroup) userStore.setTeamLeader(true)
    sessionStore.setSubSessionId(room.sessionId)
    // 세션 정보 저장
    
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
  <div>
    <v-row>
      <v-col v-for="(room, index) in rooms" :key="index" cols="12" md="6">
        <template v-if="!room.buttonClicked">
          <v-btn
            :disabled="index > activeButtonIndex"
            @click="handleRoomClick(index)"
            size="x-large"
            width="300"
            height="100"
            rounded="lg"
            block
            color="#e9ecef"
            class="mb-2"
            style="opacity: 0.7"
          >
            <div class="mr">
              <v-icon icon="$plus"></v-icon>
            </div>
          </v-btn>
        </template>
        <template v-else>
          <v-btn
            @click="handleRoomClick(index)"
            size="x-large"
            width="300"
            height="100"
            rounded="lg"
            block
            color="#e9ecef"
            class="mb-2"
          >
            <b>{{ room.groupName || `그룹${index + 1}` }}</b>
            <div class="mr">{{ room.occupants }}/{{ room.capacity }}</div>
            <div class="mr">
              <v-icon icon="$next"></v-icon>
            </div>
          </v-btn>
        </template>
      </v-col>
    </v-row>
  </div>
</template>

<style scoped>
.mt {
  margin-top: 30px;
}
</style>
