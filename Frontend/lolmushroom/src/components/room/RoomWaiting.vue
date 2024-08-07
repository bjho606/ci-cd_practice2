<script setup>
import { onMounted, computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useContentsStore } from '@/stores/contentsStore'
import { useRoomStore } from '@/stores/roomStore'
import { useUserStore } from '@/stores/userStore'
import { useChatStore } from '@/stores/chatStore'
import { useSessionStore } from '@/stores/sessionStore'
import webSocketAPI from '@/api/webSocket'
import sessionAPI from '@/api/session'
import RoomNameInput from '@/components/room/RoomNameInput.vue'

const contentsStore = useContentsStore()
const roomStore = useRoomStore()
const userStore = useUserStore()
const chatStore = useChatStore()
const sessionStore = useSessionStore()
const router = useRouter()

/**
 * * 1. Player가 Sub Session Message에 대한 메시지를 구독한다.
 * ! webSocketAPI.connect()의 CallBack에 들어갈 함수 ( Pinia의 MainSessionMessage에 저장 )
 */
const onSubSessionMessageReceived = (message) => {
  chatStore.addSubSessionMessage(message)
}

/**
 * TODO 0. 모든 사람들은 총 몇명에 현재 몇명이 들어와있는 지 알아야 한다.
 * * 2. Player는 Group의 현재 정보를 실시간으로 알 수 있다.
 */
const groupInfo = computed(() => roomStore.getGroupInfoBySessionId(sessionStore.subSessionId))
console.log(groupInfo.value)

/**
 * TODO 1. 팀장은 Room의 이름을 바꿀 수 있음
 * * 3. Team Leader는 Room의 이름을 바꿀 수 있다.
 * * 3.1. Dialog를 통해 Room Name을 바꾼다.
 */
const isTeamLeader = userStore.isTeamLeader
const showRoomNameInput = ref(false)
const openDialog = () => {
  showRoomNameInput.value = true
}
const closeDialog = () => {
  showRoomNameInput.value = false
}

/**
 * TODO 2. 팀장은 진행자에게 '준비 완료' Signal을 보낸다.
 * IMP : 팀장은 Waiting이 아닌 Contents를 진행 중일 때, '완료'를 누르면, Room Waiting으로 돌아온다. ( In Every Contents )
 */
const sendReadyMessage = async (sessionId) => {
  try {
    const response = await sessionAPI.getSubSessionReady(sessionId)
    if (response.data.isSuccess) {
      console.log('우리 Team은 모두 준비 완료입니다:)')
    }
  } catch (error) {
    console.error('Error Ready Message to Manager')
  }
}

/**
 * TODO 3. 모든 Player는 진행자의 Signal을 구독하고 있다가, 다음 Component로 이동할 수 있어야 함.
 * ! Watch를 통해, currentContentsId만 Watch하면, reload가 어려울 수 있음 -> 전체를 watch할까?
 * IMP : 다음 Component로 Routing이 이루어져야 한다.
 */

const routeMapping = contentsStore.getRouteMapping
watch(
  () => contentsStore.currentContentsId,
  (newContentsId) => {
    if (newContentsId && routeMapping[newContentsId]) {
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

// New computed property to check if the room is ready
const isRoomReady = computed(() => {
  const group = groupInfo.value
  return group && group.ready
})

onMounted(() => {
  webSocketAPI.connect({
    sessionId: sessionStore.subSessionId,
    onMessageReceived: onSubSessionMessageReceived,
    subscriptions: ['chat']
  })
})
</script>

<template>
  <div class="main">
    <v-btn v-if="isTeamLeader" @click="openDialog" color="primary">Change Room Name</v-btn>
    <v-btn v-if="isTeamLeader" @click="sendReadyMessage(sessionStore.subSessionId)" color="primary">
      Are We Ready?
    </v-btn>
    <v-dialog v-model="showRoomNameInput" max-width="1000px">
      <RoomNameInput @name-registerd="closeDialog" />
    </v-dialog>
    <v-container class="group-info" fluid>
      <v-row justify="center" align="center">
        <v-col cols="auto">
          <v-card color="white" class="pa-4" outlined>
            <v-card-title class="text-h6">Group: {{ groupInfo.groupName }}</v-card-title>
            <v-card-subtitle class="text-h5">
              {{ groupInfo.occupants }} / {{ groupInfo.capacity }} Users
            </v-card-subtitle>
            <v-card-text v-if="isRoomReady">
              <v-alert type="success" border="left" colored-border> The room is ready! </v-alert>
            </v-card-text>
            <v-card-text v-else>
              <v-alert type="warning" border="left" colored-border>
                Waiting for players to be ready...
              </v-alert>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
    <v-container class="main-space">
      <div class="animated-text">
        <span>게</span><span>임</span><span>을</span><span> </span><span>기</span><span>다</span>
        <span>리</span><span>는</span><span> </span><span>중</span><span>입</span><span>니</span
        ><span>다</span>
      </div>
      <v-img height="70%" src="https://i.ibb.co/Nrr5g3n/wating-Mushroom.png"> </v-img>
      <v-container>
        <v-progress-linear indeterminate color="white" :height="15"></v-progress-linear>
      </v-container>
    </v-container>
  </div>
</template>

<style scoped>
@font-face {
  font-family: NeoDunggeunmo;
  src:
    url('@/assets/fonts/NeoDunggeunmoPro-Regular.woff') format('woff'),
    url('@/assets/fonts/NeoDunggeunmoPro-Regular.woff2') format('woff2');
}

.main-space {
  width: 40ex;
  height: 37ex;
  background-color: #795c34;
  opacity: 80%;
}

.main {
  font-family: NeoDunggeunmo;
  font-weight: 100;
  text-align: center;
  color: white;
  font-size: xx-large;
}

.animated-text {
  margin-top: 20px;
}

.animated-text span {
  display: inline-block;
  animation: jump 0.6s ease-in-out infinite;
}

@keyframes jump {
  0%,
  20%,
  50%,
  80%,
  100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-20px);
  }
  60% {
    transform: translateY(-10px);
  }
}

.animated-text span:nth-child(1) {
  animation-delay: 0s;
}
.animated-text span:nth-child(2) {
  animation-delay: 0s;
}
.animated-text span:nth-child(3) {
  animation-delay: 0.05s;
}
.animated-text span:nth-child(4) {
  animation-delay: 0.05s;
}
.animated-text span:nth-child(5) {
  animation-delay: 0.1s;
}
.animated-text span:nth-child(6) {
  animation-delay: 0.1s;
}
.animated-text span:nth-child(7) {
  animation-delay: 0.15s;
}
.animated-text span:nth-child(8) {
  animation-delay: 0.15s;
}
.animated-text span:nth-child(9) {
  animation-delay: 0.2s;
}
.animated-text span:nth-child(10) {
  animation-delay: 0.2s;
}
.animated-text span:nth-child(11) {
  animation-delay: 0.25s;
}
.animated-text span:nth-child(12) {
  animation-delay: 0.3s;
}
.animated-text span:nth-child(13) {
  animation-delay: 0.3s;
}

/* 로딩바 css */
.loader {
  width: 300px;
  margin: 80px;
  position: relative;
}

.loader .loading {
  position: relative;
  width: 100%;
  height: 10px;
  border: 1px solid brown;
  border-radius: 10px;
  animation: turn 4s linear 1.75s infinite;
}

.loader .loading:before {
  content: '';
  display: block;
  position: absolute;
  width: 0;
  height: 100%;
  background-color: white;
  box-shadow: 10px 0px 15px 0px black;
  animation: load 2s linear infinite;
}

@keyframes load {
  0% {
    width: 0%;
  }

  87.5% {
    width: 100%;
  }
}

@keyframes turn {
  0% {
    transform: rotateY(0deg);
  }

  6.25%,
  50% {
    transform: rotateY(180deg);
  }

  56.25%,
  100% {
    transform: rotateY(360deg);
  }
}

@keyframes bounce {
  0%,
  100% {
    top: 10px;
  }

  12.5% {
    top: 30px;
  }
}
</style>
