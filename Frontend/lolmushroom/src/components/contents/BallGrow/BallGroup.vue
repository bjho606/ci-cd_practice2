<script setup>
import { computed, ref  } from 'vue'
import { useBallStore } from '@/stores/ballStore'
import BallContent from './BallContent.vue'
import { useSessionStore } from '@/stores/sessionStore';

const ballStore = useBallStore();
const sessionStore = useSessionStore();


// const totalBalls = computed(() => ballStore.getTotalBalls)
//console.log(totalBalls.value)
const subSessionId = computed(() => sessionStore.getSessionId)


// 기존 배열을 두개씩 끊어서 저장하는 rows배열
// const rows = computed(() => {
//   return props.totalBalls.reduce((acc, item, index) => {
//     if (index % 2 === 0) acc.push([]);
//     acc[acc.length - 1].push(item);
//     return acc;
//   }, []);
// });

// 더미 데이터
const dummyBalls = ref([
  { sessionId: '1', size: '100' },
  { sessionId: '2', size: '200' },
  { sessionId: '3', size: '300' },
  { sessionId: '4', size: '500' },
  { sessionId: '5', size: '400' },
  { sessionId: '6', size: '200' },
  { sessionId: '7', size: '400' },
  { sessionId: '8', size: '300' },
  { sessionId: '9', size: '400' },
  { sessionId: '10', size: '200' },
  // 필요한 만큼 더미 객체를 추가하세요.
]);
const rows = computed(() => {
  return dummyBalls.value.reduce((acc, item, index) => {
    if (index % 2 === 0) acc.push([]);
    acc[acc.length - 1].push(item);
    return acc;
  }, []);
});

</script>

<template>
  <div class="ball-group">
    <!-- 원래 코드 -->
    <!-- <BallContent v-for="ball in totalBalls" :key="ball.sessionId" :ball="ball" :isGreen="true"/> --
    <!-- 두줄씩 출력하기 위한 rows배열(2*n) -->
    <div v-for="(row, rowIndex) in rows" :key="rowIndex" class="row">
      <!-- TODO: isGreen ==> ball.sessionId == userSubSessionId 를 반환하면 됨, 즉 내 세션이면 초록색으로 렌더링 한다는 뜻 -->
        <div v-for="ball in row" :key="ball.sessionId" class="ball-item">
          <BallContent :ball="ball" :isGreen="ball.sessionId === subSessionId" />
        </div>
    </div>
  </div>
</template>
<style scoped>

.ball-group{
  max-height: 700px;
  overflow-y: auto;     /* 내용이 넘칠 때 스크롤바가 나타나도록 설정 */
}

.row { 
  display: flex;
  margin-bottom: 20px;
}

.ball-item {
  margin-right: 30px;
}

.row:last-child .ball-item:last-child {
  margin-right: 0; 
}
</style>
