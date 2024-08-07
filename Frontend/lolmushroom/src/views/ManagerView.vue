<script setup>
import { onMounted } from 'vue';
import webSocketAPI from '@/api/webSocket';
import contents from '@/api/contents';
import { useRoomStore } from '@/stores/roomStore';
import { useSessionStore } from '@/stores/sessionStore';

const sessionStore = useSessionStore();
const roomStore = useRoomStore();
const onSessionEventReceived = (message) => {
  roomStore.setSessionData(message) // 방 정보를 업데이트
}

// 모든 그룹 finish가 되면 next 호충
const onProgressEventRecived = (progress) =>{
  // 모든 조가 게임이 종료되면
  if (progress.finishGroupCount === progress.totalGroupCount){
      // 게임 순서가 끝까지 안 갔다면
      if(progress.contentsSequence < progress.totalContentsCount){
          contents.callNextContents(false)
      }
  }
}

onMounted(() => {
    webSocketAPI.connect({
      sessionId: sessionStore.sessionId,
      onEventReceived: (message) => console.log('Event received:', message),
      onProgressReceived: onProgressEventRecived,
      onMessageReceived: onSessionEventReceived,
      subscriptions: ['session', 'progress']
    })
})
</script>

<template>
  <RouterView />
</template>

<style scoped></style>
