<script setup>
import { ref, computed } from 'vue'
import { useSessionStore } from '@/stores/sessionStore'
import { useMushroomStore } from '@/stores/mushroomStore'

const sessionStore = useSessionStore()
const mushroomStore = useMushroomStore()
const currentGroup = computed(() => mushroomStore.getCurrentGroup)
const currentGroupName = computed(() => mushroomStore.getMushroomName(currentGroup.value))
const currentGroupSize = computed(() => mushroomStore.getMushroomSize(currentGroup.value))
const currentGroupImage = computed(() => mushroomStore.getMushroomImage(currentGroupSize.value))

const clickEffect = ref(false)
const clickPosition = ref({ x: 0, y: 0 })
const onMushroomClick = (event) => {
  const rect = event.target.getBoundingClientRect()
  clickPosition.value = {
    x: event.clientX - rect.left,
    y: event.clientY - rect.top
  }
  clickEffect.value = true
  setTimeout(() => (clickEffect.value = false), 200)
  mushroomStore.onMushroomClick(sessionStore.sessionId, currentGroup.value)
}
</script>

<template>
  <div class="game-container">
    <!-- 맨 위: 타이머 -->
    <div class="timer-container">
      <p>남은 시간: 00:59</p> <!-- 타이머 자리 -->
    </div>

    <!-- 가운데: 메인 버섯 카드 -->
    <div class="solo-mushroom-card">
      <!-- 버섯 이미지 클릭 -->
      <div class="mushroom-container" @click="onMushroomClick">
        <!-- 이미지 넣어야 할 자리 -->
        <!-- 클릭 효과 -->
        <div
          v-if="clickEffect"
          class="click-effect"
          :style="{ top: clickPosition.y + 'px', left: clickPosition.x + 'px' }"
        >
          {{ currentGroup === mushroomStore.userGroup ? '❤️' : '💥' }}
        </div>
      </div>
    </div>

    <!-- 맨 아래: 돌아가기 버튼 -->
    <div class="card-actions">
      <p>내 그룹으로 돌아가기</p>
      <button
        v-if="currentGroup !== mushroomStore.userGroup"
        @click="mushroomStore.onReturnClick"
        class="return-button"
      >
        그룹으로 돌아가기
      </button>
    </div>
  </div>
</template>

<style scoped>
.game-container {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  height: 100vh; /* 화면 전체 높이 사용 */
  width: 100%;
  padding: 16px;
  box-sizing: border-box; /* 패딩 포함 크기 계산 */
}

.timer-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 20%;
  height: 10%; /* 화면 높이의 10% 할당 */
  background-color: #90FF77;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 16px;
}

.solo-mushroom-card {
  width: 90%;
  flex-grow: 1; /* 나머지 공간을 모두 차지하도록 */
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  background-color: white;
  margin-bottom: 16px;
  padding: 16px;
  box-sizing: border-box;
}

.mushroom-container {
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  width: 100%;
  height: 100%;
}

.click-effect {
  position: absolute;
  font-size: 48px;
  pointer-events: none;
  transform: translate(-50%, -50%);
}

.card-actions {
  display: flex;
  justify-content: center;
  width: 40%;
  height: 10%; /* 화면 높이의 10% 할당 */
  background-color: #24A319;
}

.return-button {
  background-color: #1976d2;
  color: #24A319;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  text-align: center;
}
</style>
