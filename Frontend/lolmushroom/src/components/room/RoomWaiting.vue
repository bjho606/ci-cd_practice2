<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useContentsStore } from '@/stores/contents'
import { useUserStore } from '@/stores/User'
import { useChatStore } from '@/stores/chatStore'
import { useSessionStore } from '@/stores/session'
import webSocketAPI from '@/api/webSocket'
import RoomNameInput from '@/components/room/RoomNameInput.vue'

const contentsStore = useContentsStore()
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
 * * 2. Player는 Session Contents 진행에 대한 Event를 구독한다. => 위치를 PlayerView로 넘어가자!
 * IMP : Event에 의해서 맞는 Routing이 이루어져야 한다.
 * TODO : Routing!!
 */
const onContentsReceived = (event) => {
  contentsStore.setContentsInfo(event)
}

/**
 * TODO 0. 모든 사람들은 총 몇명에 현재 몇명이 들어와있는 지 알아야 한다.
 * TODO 1. 팀장은 Room의 이름을 바꿀 수 있음
 * TODO 2. 팀장에 대한 View를 만들어야 함. -> 준비 완료 ( Contents 종료 )( -> 진행자에게 시그널로 가야 한다. )
 * IMP 진행자의 Signal을 await하고 있다가, 다음 Component로 이동할 수 있어야 한다. => Routing이 이루어져야 한다.
 */

onMounted(() => {
  webSocketAPI.connect({
    sessionId: sessionStore.subSessionId,
    onMessageReceived: onSubSessionMessageReceived,
    onEventReceived: onContentsReceived,
    subscriptions: ['chat', 'contents']
  })
})
</script>

<template>
  <div class="main">
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
