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

// 상태 관리 & Routing 사용
const router = useRouter()
const route = useRoute()
const roomStore = useRoomStore()
const userStore = useUserStore()
const sessionStore = useSessionStore()
const { rooms } = storeToRefs(roomStore)

const showNicknameModal = ref(false)
const showRoomNameInput = ref(false)
const allButtonCount = 10
const activeButtonIndex = ref(0)

// 1. 세션 연결 생성 => 여기서 받아오는 OpenVidu Token은 SessionStore에 저장 가능성 존재
const getSessionConnection = async (sessionId, userName) => {
  try {
    const response = await sessionAPI.getSessionConnection(sessionId, userName)
    if (response.data.isSuccess) {
      console.log(response.data.result)
    }
  } catch (error) {
    console.error('Error Getting Session Connection', error)
  }
}

// 2. 세션 정보 가져오기 => Session Store & Room Store에 저장 가능성 존재
const getSessionInfo = async (sessionId) => {
  try {
    const response = await sessionAPI.getSessionInfo(sessionId)
    if (response.data.isSuccess) {
      console.log(response.data.result)
    }
  } catch (error) {
    console.error('Error Getting Session Information', error)
  }
}

// 3. 하위 세션 생성 => 하위 세션은 생성과 입장이 나뉘어져 있음
// 하위 세션을 생성하는 Post를 날려서, Server에 하위세션 정보를 저장해야 한다.
const createSubSessionHandler = async (sessionId) => {
  try {
    const response = await sessionAPI.createSubSession(sessionId)
    if (response.data.isSuccess) {
      sessionStore.setSubSessionId(response.data.result.sessionId)
    }
  } catch (error) {
    console.error('Error Creating SubSession', error)
  }
}

// 방 클릭 핸들러
const handleRoomClick = async (index) => {
  if (rooms.value[index].occupants === 0) {
    activeButtonIndex.value += 1
    showRoomNameInput.value = true

    if (index <= allButtonCount) {
      //roomStore.updateRoomOccupants(index);
      await createSubSessionHandler(sessionStore.sessionId)
      roomStore.setButtonState(index, true)
    }
  } else {
    sessionStore.setSubSessionId(index + 1)
    router.push({
      name: 'roomwaiting',
      params: { sessionId: sessionStore.sessionId, num: sessionStore.subSessionId }
    })
    roomStore.updateRoomOccupants(index)
  }
}

// 사용자 플로우 핸들러
const userFlowHandler = async () => {
  const sessionId = route.params.sessionId
  sessionStore.setSessionId(sessionId)
  await getSessionConnection(sessionId, { userName: userStore.userNickname })
  await getSessionInfo(sessionId)
}

// 컴포넌트가 마운트될 때 처리
onMounted(async () => {
  if (userStore.userNickname === '닉네임을 설정해주세요') {
    showNicknameModal.value = true
    await userFlowHandler()
  } else {
    // 현재는 바로 userFlowHandler가 나오게 처리
    // await userFlowHandler()
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
            <b>{{ `그룹${index + 1}` }}</b>
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
