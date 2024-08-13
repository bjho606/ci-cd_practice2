<script setup>
import { computed } from 'vue'
import { useBallStore } from '@/stores/ballStore'
import BallContent from './BallContent.vue'

const ballStore = useBallStore()
const totalBalls = computed(() => ballStore.getTotalBalls)
console.log(totalBalls.value)


// 기존 배열을 두개씩 끊어서 저장하는 rows배열
const rows = computed(() => {
  return props.totalBalls.reduce((acc, item, index) => {
    if (index % 2 === 0) acc.push([]);
    acc[acc.length - 1].push(item);
    return acc;
  }, []);
});

</script>

<template>
  <div class="ball-group">
    <!-- 원래 코드 -->
    <!-- <BallContent v-for="ball in totalBalls" :key="ball.sessionId" :ball="ball" :isGreen="true"/> -->

    <!-- 두줄씩 출력하기 위한 rows배열(2*n) -->
    <div v-for="(row, rowIndex) in rows" :key="rowIndex" class="row">
      <!-- TODO: isGreen ==> ball.sessionId == userSubSessionId 를 반환하면 됨, 즉 내 세션이면 초록색으로 렌더링 한다는 뜻 -->
      <BallContent v-for="ball in row" :key="ball.sessionId" :ball="ball" :isGreen="true" />
    </div>
  </div>
</template>

<style scoped></style>
