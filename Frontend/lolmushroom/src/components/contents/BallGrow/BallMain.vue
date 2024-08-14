<script setup>
import { onMounted, ref, computed} from 'vue'
import { useSessionStore } from '@/stores/sessionStore'
import { useBallStore } from '@/stores/ballStore'
import BallContent from './BallContent.vue';

const sessionStore = useSessionStore()
const ballStore = useBallStore()
const currentBall = computed(() => ballStore.getCurrentGroup)
const currentGroupName = computed(() => ballStore.getGroupName)
const currentGroupImage = computed(() => ballStore.getBallImage(currentBall.value))

// 남은 시간
const RemainTime = ref(0)

// 지금 클릭 한 컴포넌트
const nowClick = ref(false)

// 오는 세션ID랑 같은 세션ID의 팀이름을 찾는다.
const nowTeamName = ref("안녕");

// 클릭 이펙트 기능
const clickEffects = ref([])

const addClickEffect = (event) => {
  const effect = {
    id: Date.now(),
    x: event.clientX,
    y: event.clientY
  }
  clickEffects.value.push(effect)
  setTimeout(() => {
    clickEffects.value = clickEffects.value.filter(e => e.id !== effect.id)
  }, 500)
}


// 돌아가기 버튼
const backToMyBall = () =>{
  nowClick.value= true
}

// IMPL 
// 공 사이즈, 남은 시간 소켓으로 통신해서 주기적으로 받기


</script>



<template>
  <div class="center-wrapper" @click="addClickEffect">
    <v-btn class="remainTime">남은 시간: {{ RemainTime }}초</v-btn>
    <!-- <div class="myBallContainer"> -->
      <!-- <div :style="{ fontSize: 'xx-large' }">{{ nowTeamName }}</div> -->
      <!-- 색깔 공 넣기 -->
      <!-- <div class="initBallState"> -->
        <!-- Test Size 50으로 지정-->
        <BallContent v-if="nowClick" :ball="70" :is-main="true" :is-green="true"  />
        <BallContent v-else :ball="50" :is-main="true" :is-green="false" />
      <!-- </div> -->
    <!-- </div> -->
    <v-btn v-if="!nowClick" class="backToMyGroup" @click="backToMyBall">내 그룹으로 돌아가기</v-btn>
    <div v-for="effect in clickEffects" :key="effect.id"
         class="click-effect"
         :style="{ left: `${effect.x - 50}px`, top: `${effect.y - 50}px` }">
         <v-img src="src/assets/image/spark.png"></v-img>
    </div>
  </div>
</template>

<style>
/* 전역 스타일로 이동 */
.click-effect {
  position: fixed;
  width: 100px;
  height: 100px;
  background-color: rgba(224, 107, 34, 0.5);
  border-radius: 50%;
  pointer-events: none;
  transform: scale(0);
  animation: click-animation 0.5s forwards;
  z-index: 100000;
}

@keyframes click-animation {
  to {
    transform: scale(1);
    opacity: 0;
  }
}
</style>

<style scoped>
.center-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center; 
  justify-content: center; 
  height: 100vh; 
  box-sizing: border-box;
  padding: 20px; 
}

.remainTime {
  background-color: #90FF77;
  width: 300px;
  height: 60px !important;
  margin-bottom: 20px; 
  border-radius: 20px; 
  border: 1px black solid;
}

.myBallContainer {
  background-color: #FFF2F7;
  width: 600px;
  height: 400px;
  margin-bottom: 20px; /* Add spacing between container and button */
}

.backToMyGroup {
  margin-top: 30px;
  background-color: #24A319;
  color: white;
  width: 490px;
  height: 100px !important;
  border-radius: 20px
}

/* 공 중앙으로 이동 */
.initBallState {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 40vh;
}


</style>
