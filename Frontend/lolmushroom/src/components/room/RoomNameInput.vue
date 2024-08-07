<script setup>
import { ref, watch } from 'vue'
import { useSessionStore } from '@/stores/sessionStore'
import sessionAPI from '@/api/session'

const message = ref('')
const sessionStore = useSessionStore()
const isButtonDisabled = ref(true)
const emit = defineEmits(['name-registered'])

/**
 * IMP 1. TeamLeader가 GroupName을 변경한다.
 */

const registerRoomName = async () => {
  if (message.value.length > 0) {
    try {
      const response = await sessionAPI.changeSubSessionName(sessionStore.subSessionId, {
        groupName: message.value
      })
      console.log(response)
      if (response.data.isSuccess) console.log('Team 이름을 변경했습니다!')
    } catch (error) {
      console.error('Team 이름을 변경하는데 오류가 발생했습니다!')
    }
  } else {
    alert('최소 1자 이상은 팀 이름으로 등록해야 합니다.')
  }
  emit('name-registered')
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
