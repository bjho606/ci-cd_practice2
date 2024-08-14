<script setup>
import { ref, computed } from 'vue'
import { useBallStore } from '@/stores/ballStore'
import { useSessionStore } from '@/stores/sessionStore';
import BallContent from './BallContent.vue'

const ballStore = useBallStore();
const sessionStore = useSessionStore();
const currentGroup = computed(() => ballStore.getCurrentGroup)
const currentGroupName = computed(()=> ballStore.getCurrentGroupName(currentGroup.value))
const currentBallsize = computed(()=>ballStore.getBallSize(currentGroup.value))

const isMine = computed(() => ballStore.getIsMyBall(currentGroup))

/**
 * TODO Time에 대한 Value를 관리해야 한다.
 */
const RemainTime = ref(0)

const nowClick = ref(false)
const clickEffects = ref([])
const addClickEffect = (event) => {
  const effect = {
    id: Date.now(),
    x: event.clientX,
    y: event.clientY
  }
  clickEffects.value.push(effect)
  setTimeout(() => {
    clickEffects.value = clickEffects.value.filter((e) => e.id !== effect.id)
  }, 500)
}

// ballClick을 할 시 ballStore로 데이터를 전송한다.
const onBallClick = () =>{
  ballStore.onBallClick(sessionStore.sessionId, currentGroup.value)
}
// 다시 자신의 공으로 돌아온다.
const backToMyBall = () => {
  nowClick.value = true
}
</script>

<template>
  <div class="center-wrapper" @click="addClickEffect">
    <v-btn class="remainTime">남은 시간: {{ RemainTime }}초</v-btn>
    <BallContent :isMain="true" :groupId="currentGroup" @click="onBallClick" />
    <v-btn v-if="!isMine" class="backToMyGroup" @click="backToMyBall">내 그룹으로 돌아가기</v-btn>
    <div
      v-for="effect in clickEffects"
      :key="effect.id"
      class="click-effect"
      :style="{ left: `${effect.x - 50}px`, top: `${effect.y - 50}px` }"
    >
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
  background-color: #90ff77;
  width: 300px;
  height: 60px !important;
  margin-bottom: 20px;
  border-radius: 20px;
  border: 1px black solid;
}

.myBallContainer {
  background-color: #fff2f7;
  width: 600px;
  height: 400px;
  margin-bottom: 20px; /* Add spacing between container and button */
}

.backToMyGroup {
  margin-top: 30px;
  background-color: #24a319;
  color: white;
  width: 490px;
  height: 100px !important;
  border-radius: 20px;
}

/* 공 중앙으로 이동 */
.initBallState {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 40vh;
}
</style>
