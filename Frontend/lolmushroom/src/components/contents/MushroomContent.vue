<script setup>
import { ref, computed, watch } from 'vue'
import bigNormal from '@/assets/mushroomGrow/BigNormal.svg'
import midNormal from '@/assets/mushroomGrow/MidNormal.svg'
import smallNormal from '@/assets/mushroomGrow/SmallNormal.svg'

// Constants
const initialSize = 20 // vw 단위로 변경
const midSize = 30 // vw 단위로 변경
const finalSize = 40 // vw 단위로 변경
const maxSize = 50 // vw 단위로 변경
const previewSize = 10 // 오른쪽 영역의 미리보기 크기 (vw 단위)

// Group Data
const groups = ref([
  { name: 'Group2', size: 40 },
  { name: 'Group3', size: 40 },
  { name: 'Group4', size: 40 },
  { name: 'Group5', size: 40 },
  { name: 'Group6', size: 40 },
  { name: 'Group7', size: 40 },
  { name: 'Group8', size: 40 },
  { name: 'Group9', size: 50 },
  { name: 'Group10', size: 40 }
])

// User Group Data
const userGroupMushroomSize = ref(initialSize)
const userGroupMushroomImage = computed(() => getMushroomImage(userGroupMushroomSize.value))

const currentGroup = ref('User Group')
const currentMushroomSize = ref(userGroupMushroomSize.value)
const currentMushroomImage = computed(() => {
  if (currentGroup.value === 'User Group') {
    return userGroupMushroomImage.value
  } else {
    const group = groups.value.find((g) => g.name === currentGroup.value)
    return group ? getMushroomImage(group.size) : smallNormal
  }
})

// Update currentMushroomSize when userGroupMushroomSize changes
watch(userGroupMushroomSize, () => {
  if (currentGroup.value === 'User Group') {
    currentMushroomSize.value = userGroupMushroomSize.value
  }
})

// Determine mushroom image based on size
function getMushroomImage(size) {
  if (size < midSize) return smallNormal
  if (size < finalSize) return midNormal
  return bigNormal
}

// Handle mushroom click
function onMushroomClick(event) {
  const rect = event.target.getBoundingClientRect()
  clickPosition.value = {
    x: event.clientX - rect.left,
    y: event.clientY - rect.top
  }
  clickEffect.value = true
  setTimeout(() => (clickEffect.value = false), 100)

  if (currentGroup.value === 'User Group') {
    if (userGroupMushroomSize.value < maxSize) {
      userGroupMushroomSize.value += 0.05
      sendClickDataToServer('grow')
    }
  } else {
    const group = groups.value.find((g) => g.name === currentGroup.value)
    if (group && group.size > initialSize) {
      group.size -= 0.05
      currentMushroomSize.value = group.size
      sendClickDataToServer('shrink')
    }
  }
}

// Handle group mushroom click
function onGroupMushroomClick(group) {
  currentGroup.value = group.name
  currentMushroomSize.value = group.size
}

// Handle return button click
function onReturnClick() {
  currentGroup.value = 'User Group'
  currentMushroomSize.value = userGroupMushroomSize.value
}

// Send click data to the server
async function sendClickDataToServer(action) {
  console.log(`버섯 클릭 데이터 전송됨: ${action}`)
}

// Click effect state
const clickEffect = ref(false)
const clickPosition = ref({ x: 0, y: 0 })
</script>

<template>
  <v-container fluid class="main-container">
    <!-- Left Container -->
    <v-container class="left-container">
      <div class="current-group-info">
        <div>Current Group: {{ currentGroup }}</div>
        <div>Size: {{ currentMushroomSize }}vw</div>
      </div>
      <div class="mushroom-container" @click="onMushroomClick">
        <v-img
          :src="currentMushroomImage"
          :style="{
            width: currentMushroomSize + 'vw',
            height: currentMushroomSize + 'vw',
            transition: 'width 0.5s, height 0.5s'
          }"
        />
        <div
          v-if="clickEffect"
          class="click-effect"
          :style="{ top: clickPosition.y + 'px', left: clickPosition.x + 'px' }"
        >
          {{ currentGroup === 'User Group' ? '❤️' : '💥' }}
        </div>
      </div>
      <v-btn v-if="currentGroup !== 'User Group'" @click="onReturnClick">돌아가기</v-btn>
    </v-container>

    <!-- Right Container -->
    <v-container class="right-container">
      <div class="grid-container">
        <div
          v-for="group in groups"
          :key="group.name"
          class="grid-item"
          @click="onGroupMushroomClick(group)"
        >
          <v-img
            :src="getMushroomImage(group.size)"
            :style="{
              width: previewSize + 'vw',
              height: previewSize + 'vw',
              transition: 'width 0.5s, height 0.5s'
            }"
            class="group-mushroom"
          >
            <div>{{ group.name }}</div>
          </v-img>
        </div>
      </div>
    </v-container>
  </v-container>
</template>

<style scoped>
.main-container {
  display: flex;
  height: 100vh;
}

.left-container,
.right-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.current-group-info {
  margin-bottom: 16px;
  text-align: center;
}

.mushroom-container {
  display: flex;
  position: relative;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.click-effect {
  position: absolute;
  font-size: 48px;
  pointer-events: none;
  transform: translate(-50%, -50%);
}

.grid-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  width: 100%;
  height: 100%;
  align-items: center;
  justify-items: center;
  overflow-y: auto; /* Add vertical scroll */
}

.grid-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 1px solid #ccc;
  width: 100%;
  height: 100%;
}

.group-mushroom {
  cursor: pointer;
}
</style>
