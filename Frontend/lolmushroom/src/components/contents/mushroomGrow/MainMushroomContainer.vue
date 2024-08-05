<script setup>
import { ref, computed } from 'vue'
import { useSessionStore } from '@/stores/session'
import { useMushroomStore } from '@/stores/mushroomStore'
import StatusBar from './StatusBar.vue'

const sessionStore = useSessionStore()
const mushroomStore = useMushroomStore()
const currentGroup = computed(() => mushroomStore.getCurrentGroup)
const currentGroupName = computed(() => mushroomStore.getMushroomName(currentGroup.value))
const currentGroupSize = computed(() => mushroomStore.getMushroomSize(currentGroup.value))
const currentGroupImage = computed(() => mushroomStore.getMushroomImage(currentGroupSize.value))

/**
 * IMP 1. User의 Main Mushroom을 Click에 대한 EventHandler
 * * 1. Click에 대한 Effect를 정의
 * * 2. Click 정보를 Publish하는 mushroomStore의 onMushroomClick()
 */
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
/**
 * TODO StatusBar
 * TODO onReturnClick
 */
</script>

<template>
  <v-card class="solo-mushroom-card">
    <!-- 상단에 현재 그룹 정보를 표시 -->
    <v-card-title>{{ currentGroupName }}</v-card-title>
    <v-card-text>Size: {{ currentGroupSize }}</v-card-text>

    <!-- 버섯 이미지 클릭 -->
    <div class="mushroom-container" @click="onMushroomClick">
      <v-img
        :src="currentGroupImage"
        :style="{ width: currentGroupSize + 'vw', height: currentGroupSize + 'vw' }"
        class="mushroom-image"
      />
      <!-- 클릭 효과 -->
      <div
        v-if="clickEffect"
        class="click-effect"
        :style="{ top: clickPosition.y + 'px', left: clickPosition.x + 'px' }"
      >
        {{ currentGroup === mushroomStore.userGroup ? '❤️' : '💥' }}
      </div>
    </div>

    <!-- 돌아가기 버튼 -->
    <v-card-actions>
      <v-btn
        v-if="currentGroup !== mushroomStore.userGroup"
        @click="mushroomStore.onReturnClick"
        class="mx-auto"
      >
        돌아가기
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<style scoped>
.solo-mushroom-card {
  height: 100%;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: auto;
  padding: 20px;
  position: relative;
}

.mushroom-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  width: 100%;
  height: auto;
  margin: 20px 0;
}

.mushroom-image {
  cursor: pointer;
}

.click-effect {
  position: absolute;
  font-size: 48px;
  pointer-events: none;
  transform: translate(-50%, -50%);
}
</style>
