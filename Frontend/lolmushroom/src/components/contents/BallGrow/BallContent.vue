<script setup>
import { computed } from 'vue'
import BlueBallComponent from '@/components/contents/BallGrow/BlueBallComponent.vue'
import GreenBallComponent from '@/components/contents/BallGrow/GreenBallComponent.vue'
// import { useBallStore } from '@/stores/ballStore'

const props = defineProps({
  ball: Object,
  isMain: Boolean,
  isGreen: Boolean
})
// const ballStore = useBallStore()
// const ballImage = computed(() => ballStore.getBallImage(props.ball.sessionId))
const ballSize = computed(() => props.ball.size)
const _isMain = computed(() => props.isMain)
const groupName = ref("그룹이름!!!");

// 우리 그룹이면 초록색으로 랜더링 해야댐
// TODO::vif로 isGreen이 true이면 초록색 랜더링 해야함
const _isGreen = computed(() => props.isGreen)
// console.log(ballImage.value)
</script>

<template>
  <div class="main-cotainer">
    <div class="ball-name">{{ groupName }}</div>
    <div class="ball-status">{{ ballSize }}</div>
    <div class="ball-container">
      <!-- v-if="_isGreen" <GreenBallComponent class="main-ball" :health="ballSize" :isMain="_isMain"/>-->
      <!-- ball 싸이즈와 메인인지 여부를 주면 볼크기를 랜더링 해준다. -->
      <BlueBallComponent class="main-ball" :health="ballSize" :isMain="_isMain"/>
    </div>
  </div>
</template>

<style scoped>
.main-cotainer{
  position: relative; /* b 태그 위치를 고정하기 위한 상대적 포지션 */
  width: 600px;
  height: 400px;
}
.ball-name{
  position: absolute;
    top: 10px;
    left: 10px;
    padding: 5px; /* 필요에 따라 패딩 조절 */
    background-color: rgba(255, 255, 255, 0.8);
}
.ball-status{
  position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%); /* 상태 표시를 가운데에 정확히 위치시키기 위해 사용 */
    pointer-events: none;
    z-index: 1;
}
.ball-container{
  
  border: 1px solid black;
  border-radius: 20px;

  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
