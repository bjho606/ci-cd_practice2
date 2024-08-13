<script setup>
import { computed } from 'vue'
import { useRoomStore } from '@/stores/roomStore'
import waitingHeader from './WaitingHeader.vue'
import PlayerWaitng from './PlayerWaitng.vue'

// onMounted 될 시 닉네임 만들기 창 열리게 작동
const roomStore = useRoomStore()

// 재사용성을 높이는 헤더 소개
const text1 = '우리들의 오리엔테이션 세션'
const text2 = computed(() => `현재 인원 ${roomStore.getTotalUserCount}/60`)
const text3 =
  '원하는 팀을 구성하고 컨텐츠를 함께 하세요 ! <br /> 먼저 들어간 사람이 그룹장이 됩니다!'
</script>

<template>
  <div class="background">
    <!-- 대기화면 소개 글 추가-->
    <waitingHeader
      :first-description="text1"
      :second-description="text2"
      :third-description="text3"
    />
    <!-- <PlayerWaitingGround /> -->
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
