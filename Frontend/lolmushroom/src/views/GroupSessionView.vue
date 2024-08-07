<!-- eslint-disable no-unused-vars -->
<script setup>
import { onMounted } from 'vue'
import { useSessionStore } from '@/stores/sessionStore'
import { useChatStore } from '@/stores/chatStore'
import webSocketAPI from '@/api/webSocket'

const chatStore = useChatStore()
const sessionStore = useSessionStore()

/**
 * * 1. Player가 Sub Session Message에 대한 메시지를 구독한다.
 * ! webSocketAPI.connect()의 CallBack에 들어갈 함수 ( Pinia의 MainSessionMessage에 저장 )
 */
const onSubSessionMessageReceived = (message) => {
  chatStore.addSubSessionMessage(message)
}

onMounted(() => {
  webSocketAPI.connect({
    sessionId: sessionStore.subSessionId,
    onMessageReceived: onSubSessionMessageReceived,
    subscriptions: ['chat']
  })
})
</script>

<template>
  <router-view></router-view>
</template>

<style scoped></style>
