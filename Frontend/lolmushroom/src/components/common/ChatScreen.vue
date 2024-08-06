<script setup>
import { ref, computed } from 'vue'
import { useSessionStore } from '@/stores/sessionStore'
import { useChatStore } from '@/stores/chatStore'
import avatarImg from '@/assets/origbig.png'
import webSocketAPI from '@/api/webSocket'
import { useRoomStore } from '@/stores/roomStore'

const menu = ref(false)
const chatStore = useChatStore()
const sessionStore = useSessionStore()
const roomStore = useRoomStore()
const text = ref('')
const subSessionId = sessionStore.getSubSessionId

/**
 * * 1. 사용자에게 보이는 채팅의 내용은 currentMode에 따라 다르다.
 * IMP 1.1 'All' Mode : mainSessionMessages
 * IMP 1.2 'Group' Mode : subSessionMessages
 */
const isMainMode = computed(() => chatStore.currentMode === 'All')
const currentMessages = computed(() => {
  return isMainMode.value ? chatStore.mainSessionMessages : chatStore.subSessionMessages
})
const currentTitle = computed(() =>
  isMainMode.value ? '전체 그룹의 재잘재잘' : '우리 그룹의 재잘재잘'
)
const currentSubtitle = computed(() =>
  isMainMode.value
    ? roomStore.getTotalUserCount + '명'
    : roomStore.getGroupInfoBySessionId(subSessionId).occupants + '명'
)

/**
 * * 2. CurrentMode를 Toggle한다.
 * IMP 'All' <-> 'Group'
 */

const toggleMode = () => {
  if (chatStore.currentMode === 'All') {
    chatStore.setCurrentMode('Group')
    console.log(isMainMode.value)
  } else {
    chatStore.setCurrentMode('All')
    console.log(isMainMode.value)
  }
}

/**
 * * 3. Message를 WebSocket을 통해 Send한다.
 * IMP 'All' Mode -> 'sessionStore.sessionId'로 설정하고 Send
 * IMP 'Group' Mode -> 'sessionStore.subSessionId'로 설정하고 Send
 */

const sendMessage = () => {
  if (text.value.trim() === '') return
  const sessionId = isMainMode.value ? sessionStore.sessionId : sessionStore.subSessionId
  const chatMessage = {
    sessionId: sessionId,
    content: text.value,
    timestamp: new Date().toISOString()
  }
  webSocketAPI.sendMessage('/publish/chat/message', chatMessage)
  text.value = ''
}
</script>

<template>
  <div class="text-center">
    <v-menu v-model="menu" :close-on-content-click="false" location="top">
      <template v-slot:activator="{ props }">
        <v-badge :offset-y="7" :offset-x="7" :content="23">
          <v-icon icon="custom:chatButton" v-bind="props" class="icon-size"> </v-icon>
        </v-badge>
      </template>

      <v-card min-width="300" min-height="300" max-height="500" overflow-y: auto>
        <v-list>
          <v-list-item
            prepend-avatar="https://cdn.vuetifyjs.com/images/john.jpg"
            :subtitle="currentSubtitle"
            :title="currentTitle"
          >
            <template v-slot:append>
              <v-btn class="toggle-button" @click="toggleMode" icon>
                <div class="toggle-circle" :class="{ 'active-circle': isMainMode }"></div>
                <span class="toggle-text left" :class="{ visible: isMainMode }">모두</span>
                <span class="toggle-text" :class="{ visible: !isMainMode }">우리</span>
              </v-btn>
              <v-btn
                style="color: red"
                icon="mdi-minus-box"
                variant="text"
                @click="menu = false"
              ></v-btn>
            </template>
          </v-list-item>
        </v-list>

        <v-divider></v-divider>

        <v-list max-height="300px" style="overflow-y: auto">
          <v-list-item v-for="(message, index) in currentMessages" :key="index">
            <v-banner :avatar="avatarImg" :text="message.userName" color="black" :stacked="false">
              <template v-slot:actions>
                <div>{{ message.content }}</div>
              </template>
            </v-banner>
          </v-list-item>
        </v-list>

        <v-card-actions>
          <v-text-field
            label="메시지를 입력하세요."
            variant="underlined"
            v-model="text"
            @keyup.enter="sendMessage"
          ></v-text-field>
          <v-btn color="primary" variant="text" @click="sendMessage"> Send </v-btn>
        </v-card-actions>
      </v-card>
    </v-menu>
  </div>
</template>

<style scoped>
.icon-size {
  width: 50px;
  height: 50px;
}

.toggle-button {
  background-color: #2a3b59;
  border-radius: 20px;
  width: 90px;
  height: 30px;
  position: relative;
  display: inline-flex;
  align-items: center;
  padding: 0;
  transition: background-color 0.3s;
  overflow: hidden;
  cursor: pointer;
}

.toggle-circle {
  width: 25px;
  height: 25px;
  background-color: #52d1ff;
  border-radius: 50%;
  position: absolute;
  top: 50%;
  left: 5px;
  transform: translateY(-50%);
  transition: transform 0.3s;
}

.active-circle {
  transform: translate(56px, -50%);
}

.toggle-text {
  color: white;
  font-weight: bold;
  position: absolute;
  left: 50%;
  transition:
    opacity 0.3s,
    visibility 0.3s;
  pointer-events: none;
  opacity: 0;
  visibility: hidden;
}
.toggle-text.left {
  left: 10%;
}

.toggle-text.visible {
  opacity: 1;
  visibility: visible;
}
</style>
