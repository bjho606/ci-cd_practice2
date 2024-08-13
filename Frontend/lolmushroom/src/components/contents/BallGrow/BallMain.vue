<script setup>
import { computed, ref } from 'vue'
import { useSessionStore } from '@/stores/sessionStore'
import { useBallStore } from '@/stores/ballStore'
import BlueBallComponent from './BlueBallComponent.vue';
import GreenBallComponent from './GreenBallComponent.vue';

const sessionStore = useSessionStore()
const ballStore = useBallStore()
const currentBall = computed(() => ballStore.getCurrentGroup)
const currentGroupName = computed(() => ballStore.getGroupName)
const currentGroupImage = computed(() => ballStore.getBallImage(currentBall.value))

// 남은 시간
const RemainTime = ref(0)
</script>

<template>
  <div class="center-wrapper">
    <v-btn class="remainTime">남은 시간: {{ RemainTime }}초</v-btn>
    <div class="myBallContainer">
      <!-- 색깔 공 넣기 -->
      <div class="initBallState">
        <GreenBallComponent />
      </div>
    </div>
    <v-btn class="backToMyGroup">내 그룹으로 돌아가기</v-btn>
  </div>
</template>

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
  width: 260px;
  margin-bottom: 20px; /* Add spacing between buttons and container */
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
  width: 200px;
}


/* 공 중앙으로 이동 */
.initBallState {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50vh;
}

</style>
