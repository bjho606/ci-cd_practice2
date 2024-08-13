<!-- eslint-disable no-unused-vars -->
<script setup>
import { onMounted } from 'vue'
import { useChatStore } from '@/stores/chatStore'
import { useSessionStore } from '@/stores/sessionStore'
import webSocketAPI from '@/api/webSocket'

const chatStore = useChatStore()
const sessionStore = useSessionStore()

/**
 * IMP 1. Player가 SubSession Chatting Socket을 구독한다.
 * TODO : GroupSessionView에 있는 SubSession Chatting Socket을 Player View로 옮겨야 할 수도 있음
 * ! webSocketAPI.connect()의 CallBack에 들어갈 함수 ( Pinia의 SubSessionMessage에 저장 )
 */
const onSubSessionMessageReceived = (message) => {
  chatStore.addSubSessionMessage(message)
}
/**
 * IMP 2. Player가 GroupSessionView에 들어왔을 때, 진행해야 하는 함수
 */
onMounted(() => {
  webSocketAPI.connect({
    sessionId: sessionStore.subSessionId,
    onMessageReceived: onSubSessionMessageReceived,
    subscriptions: ['chat']
  })
})
</script>

<template>
  <RouterView />
</template>

<style scoped></style>
