<script setup>
import { onMounted } from 'vue'
import { useRoomStore } from '@/stores/roomStore'
import { useSessionStore } from '@/stores/sessionStore'
import { useContentsStore } from '@/stores/contentsStore'
import sessionAPI from '@/api/session'
import webSocketAPI from '@/api/webSocket'

const sessionStore = useSessionStore()
const contentsStore = useContentsStore()
const roomStore = useRoomStore()

/**
 * TODO : MainSession Connection
 * TODO : SessionInfo에 대한 Socket
 * TODO : Contents 진행에 대한 Socket
 * TODO : 준비 완료 불 다 들어오면 ->  게임 시작을 누를 수 있도록 도와준다.
 *
 * /

/**
 * IMP 2. ManagerView에 처음 들어왔을 때, MainConnection이 연결되어 있지 않은 상태
 * * 이와 같은 이유로, 처음 들어오면 Socket을 통한 Group 정보가 아닌, API를 통한 그룹 정보를 호출해야 한다.
 * * 근데 참여자들이 들어오는 것 자체가 Trigger가 되므로, 굳이 사용하지 않아도 되는 함수일 수도 있다. 
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
 * IMP 3. Manager가 Server에 대해 필요한 Socket을 연결한다.
 * * 1. Session 정보에 대한 Socket
 * * 2. Contents 진행에 대한 Socket
 * @param message
 */
const onSessionEventReceived = (message) => {
  roomStore.setSessionData(message) // 방 정보를 업데이트
}
const onProgressEventReceived = (message) => {
  contentsStore.setCurrentContentsState(message)
}
const managerFlowHandler = async () => {
  await getSessionInfo(sessionStore.sessionId)
  webSocketAPI.connect({
    sessionId: sessionStore.sessionId,
    onEventReceived: onSessionEventReceived,
    onProgressReceived: onProgressEventReceived,
    subscriptions: ['session', 'progress']
  })
}

onMounted(() => {
  managerFlowHandler()
})
</script>

<template>
  <RouterView />
</template>

<style scoped></style>
