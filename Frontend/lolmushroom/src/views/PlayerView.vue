<script setup>
import { ref, onMounted } from 'vue'
import { useChatStore } from '@/stores/chatStore'
import { useUserStore } from '@/stores/User'
import { useRoomStore } from '@/stores/roomStore'
import { useSessionStore } from '@/stores/session'
import { useRoute } from 'vue-router'
import ChatScreen from '@/components/common/ChatScreen.vue'
import AudioPlayer from '@/components/common/AudioPlayer.vue'
import NicknameModal from '@/components/common/NicknameModal.vue'
import sessionAPI from '@/api/session'
import webSocketAPI from '@/api/webSocket'

const route = useRoute()
const chatStore = useChatStore()
const userStore = useUserStore()
const roomStore = useRoomStore()
const sessionStore = useSessionStore()
const showNicknameModal = ref(false)
const cameraIcon = ref('custom:cameraOn')
const micIcon = ref('custom:micOn')

// Functions to toggle the icons
const toggleCameraIcon = () => {
  cameraIcon.value = cameraIcon.value === 'custom:cameraOn' ? 'custom:cameraOff' : 'custom:cameraOn'
}
const toggleMicIcon = () => {
  micIcon.value = micIcon.value === 'custom:micOn' ? 'custom:micOff' : 'custom:micOn'
}

/**
 * IMP 1. MainSession에 대한 Connection을 생성한다.
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
      console.log(response.data.result)
    }
  } catch (error) {
    console.error('Error Getting Session Connection', error)
  }
}

/**
 * IMP 2. Player가 Main Session Message에 대한 메시지를 구독한다.
 * ! webSocketAPI.connect()의 CallBack에 들어갈 함수 ( Pinia의 MainSessionMessage에 저장 )
 */
const onMainSessionMessageReceived = (message) => {
  chatStore.addMainSessionMessage(message)
}
const onProgressEventReceived = (message) => {
  roomStore.setSessionData(message) // 방 정보를 업데이트
}

/**
 * IMP 3. Player가 Main Session에 입장했을 때, Flow를 관리하는 함수
 * * getSessionConection() API를 호출한다.
 * IMP 3.1. Main Session과 Connection을 만들어 준다.
 * IMP 3.2. Connection을 바탕으로 Global Chatting에 대한 Socket 연결을 진행한다.
 * IMP 3.3  MeshRoom의 Progress에 대한 Socket 연결을 진행한다.
 * TODO 제대로 동작을 해야 한다.
 */
const userFlowHandler = async () => {
  const sessionId = route.params.sessionId
  sessionStore.setSessionId(sessionId)
  console.log(sessionStore.sessionId)
  console.log(
    `URL의 Query String을 통해 SessionID를 알아냈습니다! => ${sessionId}. 이제 Main Connection을 만들게요:)`
  )
  await getSessionConnection(sessionId, { userName: userStore.userNickname })
  webSocketAPI.connect({
    sessionId: sessionStore.sessionId,
    onMessageReceived: onMainSessionMessageReceived,
    onEventReceived: onProgressEventReceived,
    subscriptions: ['chat', 'progress']
  })
}

/**
 * IMP 4. PlayerView에 들어왔을 때, 요구되는 API 호출
 * * 1. WebSocket 연결
 * TODO IF -> NickName을 설정하지 않았다면, 주기적으로 안내를 보내야 한다.
 * TODO -> 조금 불안한 점은 Group Socket도 함께 연결해줘야 할 것 같기도 하다..
 */
onMounted(async () => {
  if (userStore.userNickname === '닉네임을 설정해주세요') {
    // showNicknameModal.value = true
    await userFlowHandler()
  } else {
    webSocketAPI.connect({
      sessionId: sessionStore.sessionId,
      onMessageReceived: onMainSessionMessageReceived,
      onEventReceived: onProgressEventReceived,
      subscriptions: ['chat', 'progress']
    })
  }
})
</script>

<template>
  <ChatScreen class="bottom-left-chat" />
  <v-container class="top-left">
    <v-icon :icon="cameraIcon" class="top-left-icon" @click="toggleCameraIcon"></v-icon>
    <v-icon :icon="micIcon" class="top-left-icon" @click="toggleMicIcon"></v-icon>
    <AudioPlayer class="top-left-icon" />
  </v-container>
  <v-container fluid class="contents-container">
    <RouterView />
  </v-container>
  <!-- NicknameModal -->
  <NicknameModal
    :show="showNicknameModal"
    @update:show="showNicknameModal = $event"
    @nickname-saved="userFlowHandler"
  />
</template>

<style scoped>
.contents-container {
  display: flex; /* Use Flexbox for layout */
  justify-content: center; /* Center horizontally */
  align-items: center; /* Center vertically */
  background-color: rgba(224, 224, 224, 0.6);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 그림자 효과 */
  position: absolute; /* Position absolutely within parent */
  overflow: hidden;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
}
.bottom-left-chat {
  position: absolute;
  left: -12%;
  bottom: 2%;
  width: 36px; /* Adjust icon size */
  height: 36px; /* Adjust icon size */
}
.top-left {
  position: absolute;
  display: flex;
  left: -13%;
  top: 1%;
}
.top-left-icon {
  width: 34px; /* Adjust icon size */
  height: 34px; /* Adjust icon size */
  margin-left: 1.5%;
}
</style>
