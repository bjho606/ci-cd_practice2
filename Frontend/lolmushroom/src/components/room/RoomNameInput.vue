<script setup>
import { ref, watch } from 'vue'
import { useRoomStore } from '@/stores/roomStore'
import { useSessionStore } from '@/stores/session'
import sessionAPI from '@/api/session'

const message = ref('')
const roomStore = useRoomStore()
const sessionStore = useSessionStore()
const isButtonDisabled = ref(true)

/**
 * IMP 1. TeamLeader가 GroupName을 변경한다.
 */

const registerRoomName = () => {
  if (message.value.length > 0) {
    sessionAPI.changeSubSessionName(sessionStore.subSessionId, { groupName: message.value })
  }
}

// 입력 방 길이 유효성 검사
watch(message, (newMessage) => {
  isButtonDisabled.value = newMessage.length < 1
})
</script>

<template>
  <div class="rootNameSpace">
    <h1>당신은 팀장입니다!!</h1>
    <div>팀 이름을 정하세요:)</div>
    <p>팀 이름 : {{ message }}</p>
    <input v-model="message" placeholder="ex: 원팀!" />
    <v-btn @click="registerRoomName" :disabled="isButtonDisabled" class="btn">등록</v-btn>
  </div>
</template>
<style scoped>
.rootNameSpace {
  background-color: white;
  text-align: center;
}
.btn {
  margin-left: 10px;
}
</style>
