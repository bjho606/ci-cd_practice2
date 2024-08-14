<script setup>
import { computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useContentsStore } from '@/stores/contentsStore'
import { useSessionStore } from '@/stores/sessionStore'
import { useUserStore } from '@/stores/userStore'
import { useRoomStore } from '@/stores/roomStore'
import WaitingHeader from './WaitingHeader.vue'
import PlayerWaitng from './PlayerWaitng.vue'

const router = useRouter()
const contentsStore = useContentsStore()
const sessionStore = useSessionStore()
const roomStore = useRoomStore()
const userStore = useUserStore()
const text1 = '우리들의 오리엔테이션 세션'
const text2 = computed(() => `현재 인원 ${roomStore.getTotalUserCount}/60`)
const text3 =
  '원하는 팀을 구성하고 컨텐츠를 함께 하세요 ! <br /> 먼저 들어간 사람이 그룹장이 됩니다!'

const routeMapping = contentsStore.getRouteMapping
watch(
  () => contentsStore.currentContentsId,
  (newContentsId) => {
    if (newContentsId && routeMapping[newContentsId]) {
      userStore.setIsStarted()
      router.push({
        name: routeMapping[newContentsId],
        params: {
          sessionId: sessionStore.sessionId,
          subSessionId: sessionStore.subSessionId
        }
      })
    }
  }
)
</script>

<template>
  <div class="background">
    <!-- 대기화면 소개 글 추가-->
    <WaitingHeader
      :first-description="text1"
      :second-description="text2"
      :third-description="text3"
    />
    <PlayerWaitng />
  </div>
</template>

<style scoped>
.background {
  background-color: #fff2f7;
  height: 100%;
  width: 100%;
}

.StartButton {
  margin-top: 40px;
  margin-left: 20px;
}
</style>
