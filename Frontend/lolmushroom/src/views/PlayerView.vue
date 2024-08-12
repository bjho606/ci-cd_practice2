<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

import { useContentsStore } from '@/stores/contentsStore'
import { useChatStore } from '@/stores/chatStore'
import { useUserStore } from '@/stores/userStore'
import { useRoomStore } from '@/stores/roomStore'
import { useSessionStore } from '@/stores/sessionStore'
import sessionAPI from '@/api/session'
import webSocketAPI from '@/api/webSocket'

import NicknameModal from '@/components/common/NicknameModal.vue'
import ChatScreen from '@/components/common/ChatScreen.vue'
import AudioPlayer from '@/components/common/AudioPlayer.vue'

const route = useRoute()
const contentsStore = useContentsStore()
const chatStore = useChatStore()
const userStore = useUserStore()
const roomStore = useRoomStore()
const sessionStore = useSessionStore()

const showNicknameModal = ref(false)
const cameraIcon = ref('custom:cameraOn')
const micIcon = ref('custom:micOn')

/**
 * * Camera, Mic Icon을 Toggle할 수 있는 CallBack()
 */
const toggleCameraIcon = () => {
  cameraIcon.value = cameraIcon.value === 'custom:cameraOn' ? 'custom:cameraOff' : 'custom:cameraOn'
}
const toggleMicIcon = () => {
  micIcon.value = micIcon.value === 'custom:micOn' ? 'custom:micOff' : 'custom:micOn'
}

/**
 * IMP 1. MainSession에 대한 Connection을 생성하는 CallBack()
 * REQ
 * @param sessionId
 * @param userName
 *
 * RES openvide_token, user_token
 * * 1.1 : Connection Token은 OpenVidu에서 주는 Token
 * TODO -> OpenVidu Token가 저장되는 지 확인해야 한다.
 * * 1.2 : Server와 인증에 사용되는 User JWT Token은 Cookie에 저장됨
 */
const getSessionConnection = async (sessionId, userName) => {
  try {
    const response = await sessionAPI.getSessionConnection(sessionId, userName)
    if (response.data.isSuccess) {
      console.log('Connection을 성공적으로 만들어 냈습니다. Connection Token은 다음과 같습니다:)')
      console.log(response.data)
      userStore.setUserId(response.data.result.userId)
      userStore.userOvToken = response.data.result['ovToken'].slice(-16)
    }
  } catch (error) {
    console.error('Error Getting Session Connection', error)
  }
}

/**
 * IMP 2. Player가 상호작용하는 데 필요한 모든 Socket을 연결하는 CallBack()
 * IMP webSocket에 정의한 함수 Interface에 구현체 함수를 만드는 과정이라고 생각하면 된다.
 * * onMainSessionMessageReceived : MainSession Message에 대한 Socket
 * * onSessionEventReceived : Session Info에 대한 변경 Event에 대한 Socket
 * * onProgressEventReceived : Contents의 진행 상황 변경 Event에 대한 Socket
 * TODO : 추가적으로 더 많은 Socket에 연결해야 할 수도 있다. ( Group Chat, Other Game에 대한 Socket )
 */
const onMainSessionMessageReceived = (message) => {
  chatStore.addMainSessionMessage(message)
}
const onSessionEventReceived = (message) => {
  roomStore.setSessionData(message) // 방 정보를 업데이트
}
const onProgressEventReceived = (message) => {
  contentsStore.setCurrentContentsState(message)
}

/**
 * IMP 3. PlayerView에 처음 들어왔을 때, Session의 상태를 가져오는 getSessionInfo API 호출
 * ! MainConnection과 연결되고 Socket 연결이 되기 때문에, API를 통해 Group 정보를 호출해야 함.
 */
const getSessionInfo = async (sessionId) => {
  try {
    const response = await sessionAPI.getSessionInfo(sessionId)
    if (response.data.isSuccess) {
      roomStore.setSessionData(response.data.result)
    }
  } catch (error) {
    console.error('Error Getting Session Information', error)
  }
}

/**
 * IMP 4. Player가 Main Session에 입장했을 때, 동작해야 하는 Flow를 관리하는 함수
 * * 4.1. getSessionConnection() API 호출 : Main Session과 Connection을 만들어 준다.
 * * 4.2. Connection을 바탕으로 Global Chatting에 대한 Socket 연결을 진행한다.
 * * 4.3. Connection을 바탕으로 Session Info에 대한 Socket 연결을 진행한다.
 * * 4.4  Connection을 바탕으로 MeshRoom의 Contents Progress에 대한 Socket 연결을 진행한다.
 * TODO : 추가적으로 더 많은 Socket에 연결해야 할 수도 있다. ( Group Chat, Other Game에 대한 Socket )
 */
const userFlowHandler = async () => {
  const sessionId = route.params.sessionId
  sessionStore.setSessionId(sessionId)
  console.log(
    `URL의 Query String을 통해 SessionID를 알아냈습니다! => ${sessionId}. 이제 Main Connection을 만들게요:)`
  )
  await getSessionConnection(sessionId, { userName: userStore.userNickname })
  await getSessionInfo(sessionId)
  webSocketAPI.connect({
    sessionId: sessionStore.sessionId,
    onMessageReceived: onMainSessionMessageReceived,
    onEventReceived: onSessionEventReceived,
    onProgressReceived: onProgressEventReceived,
    subscriptions: ['chat', 'session', 'progress']
  })
}

/**
 * IMP 5. Player가 PlayerView에 들어온 순간에 호출되어야 하는 CallBack()
 * * 5.1 : 닉네임 설정 X => showNicknameModal -> userFlowHandler()
 * * 5.2 : 닉네임 설정 O => webSocketAPI를 통한 Socket 재연결
 */
onMounted(async () => {
  if (userStore.userNickname === '닉네임을 설정해주세요') {
    showNicknameModal.value = true
    // await userFlowHandler()
  } else {
    webSocketAPI.connect({
      sessionId: sessionStore.sessionId,
      onMessageReceived: onMainSessionMessageReceived,
      onEventReceived: onSessionEventReceived,
      onProgressReceived: onProgressEventReceived,
      subscriptions: ['chat', 'session', 'progress']
    })
  }
})
</script>

<template>
  <!-- IMP : ChatScreen Component -->
  <!-- <ChatScreen /> -->
  <!-- IMP : Camera, Mic, Audio Player Icon -->
  <!-- <v-container>
    <v-icon :icon="cameraIcon" @click="toggleCameraIcon"></v-icon>
    <v-icon :icon="micIcon" @click="toggleMicIcon"></v-icon>
    <AudioPlayer />
  </v-container> -->
  <!-- IMP : PlayerView의 RouterView 요소들 -->
  <div class="main">
    <RouterView />
  </div>
  <!-- IMP : NickName Modal -->
  <NicknameModal
    :show="showNicknameModal"
    @update:show="showNicknameModal = $event"
    @nickname-saved="userFlowHandler"
  />
</template>

<style scoped>
.main {
  padding: 2%
}


</style>
