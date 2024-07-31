<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useSessionStore } from '@/stores/session'
import avatarImg from '@/assets/origbig.png'
import webSocketAPI from '@/api/webSocket'

const menu = ref(false)
const sessionStore = useSessionStore()
const text = ref('')
const myMessages = ref([])

const sendMessage = () => {
  if (text.value.trim() === '') return
  const chatMessage = {
    type: 'ALL',
    userSid: 1,
    senderName: 'User1',
    sessionSid: sessionStore.subSessionId,
    content: text.value,
    timestamp: new Date().toISOString()
  }
  webSocketAPI.sendMessage('/publish/chat/message', chatMessage)
  text.value = ''
}

const onMessageReceived = (message) => {
  console.log('onMessageReceived:', message) // 추가된 로그
  myMessages.value.push(message)
}

const connectToWebSocket = () => {
  webSocketAPI.connect(sessionStore.subSessionId, onMessageReceived)
}

onMounted(() => {
  connectToWebSocket()
})

onBeforeUnmount(() => {
  webSocketAPI.disconnect()
})
</script>

<template>
  <div class="text-center">
    <v-menu v-model="menu" :close-on-content-click="false" location="top">
      <template v-slot:activator="{ props }">
        <v-badge :offset-y="7" :offset-x="7" :content="23">
          <v-icon icon="custom:chatButton" v-bind="props" class="icon-size"> </v-icon>
        </v-badge>
      </template>

      <v-card min-width="300" min-height="300">
        <v-list>
          <v-list-item
            prepend-avatar="https://cdn.vuetifyjs.com/images/john.jpg"
            subtitle="이규석, 김경호, 김윤홍, 송인범, 변재호, 오화랑"
            title="1번 그룹의 재잘재잘"
          >
            <template v-slot:append>
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

        <v-list>
          <v-list-item v-for="(message, index) in myMessages" :key="index">
            <v-banner :avatar="avatarImg" color="black" :text="message.content" :stacked="false">
              <template v-slot:actions>
                <v-btn>추후 구현 必</v-btn>
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
</style>
