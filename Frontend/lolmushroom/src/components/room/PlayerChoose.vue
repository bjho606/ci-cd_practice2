<script setup>
import { onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { useRoomStore } from '@/stores/roomStore'
import { useUserStore } from '@/stores/User'
import { useSessionStore } from '@/stores/session'
import { useRoute, useRouter } from 'vue-router'
import NicknameModal from '@/components/common/NicknameModal.vue'
import RoomNameInput from '@/components/room/RoomNameInput.vue'
import sessionAPI from '@/api/session'

const router = useRouter()
const route = useRoute()
const roomStore = useRoomStore()
const userStore = useUserStore()
const sessionStore = useSessionStore()
const { rooms } = storeToRefs(roomStore)

const showNicknameModal = ref(false)
const showRoomNameInput = ref(false)
const activeButtonIndex = ref(0)

// Create session connection
const getSessionConnection = async (sessionId, userName) => {
  try {
    const response = await sessionAPI.getSessionConnection(sessionId, userName)
    if (response.data.isSuccess) {
      console.log('Connection을 성공적으로 만들어 냈습니다. Connection Token은 다음과 같습니다:)')
      console.log(response.data.result)
    }
  } catch (error) {
    console.error('Error Getting Session Connection', error)
  }
}

// Get session information
const getSessionInfo = async (sessionId) => {
  try {
    const response = await sessionAPI.getSessionInfo(sessionId)
    if (response.data.isSuccess) {
      console.log('현재 Session의 정보를 가져왔습니다:) 현재 Session 상태는 다음과 같습니다:)')
      const groups = response.data.result.groups
      console.log(response.data.result)
      roomStore.setRooms(groups)
    }
  } catch (error) {
    console.error('Error Getting Session Information', error)
  }
}

// Create sub-session
const createSubSessionHandler = async (sessionId) => {
  try {
    const response = await sessionAPI.createSubSession(sessionId)
    if (response.data.isSuccess) {
      console.log('하부 Session의 생성이 완료되었습니다:) 현재 Session의 상태를 가져올게요:)')
      await getSessionInfo(sessionId)
    }
  } catch (error) {
    console.error('Error Creating SubSession', error)
  }
}

// Room click handler
const handleRoomClick = async (index) => {
  const room = rooms.value[index]
  if (!room.buttonClicked) {
    activeButtonIndex.value += 1
    await createSubSessionHandler(sessionStore.sessionId)
    // roomStore.setButtonState(index, true)
  } else {
    console.log('하부 세션으로 입장을 하시는 군요! 하부 세션에 대한 연결을 해드릴게요:)')
    await getSessionConnection(room.sessionId, { userName: userStore.userNickname })
    router.push({
      name: 'roomwaiting',
      params: { sessionId: sessionStore.sessionId, subSessionId: room.sessionId }
    })
    sessionStore.setSubSessionId(room.sessionId)
    // roomStore.updateRoomOccupants(index)
  }
}

// User flow handler
const userFlowHandler = async () => {
  const sessionId = route.params.sessionId
  sessionStore.setSessionId(sessionId)
  console.log(
    `URL의 Query String을 통해 SessionID를 알아냈습니다! => ${sessionId}. 이제 Main Connection을 만들게요:)`
  )
  await getSessionConnection(sessionId, { userName: userStore.userNickname })
  await getSessionInfo(sessionId)
}

// Component mount handler
onMounted(async () => {
  if (userStore.userNickname === '닉네임을 설정해주세요') {
    // showNicknameModal.value = true
    await userFlowHandler()
  } else {
    await userFlowHandler()
  }
})
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
    <!-- Nickname Modal -->
    <NicknameModal
      :show="showNicknameModal"
      @update:show="showNicknameModal = $event"
      @nickname-saved="userFlowHandler"
    />
    <!-- Room Name Input Dialog -->
    <v-dialog v-model="showRoomNameInput" max-width="1000px">
      <RoomNameInput />
    </v-dialog>
  </div>
</template>

<style scoped>
.mt {
  margin-top: 30px;
}
</style>
