<script setup>
import { ref, computed, watch } from 'vue'
import bigNormal from '@/assets/mushroomGrow/BigNormal.svg'
import midNormal from '@/assets/mushroomGrow/MidNormal.svg'
import smallNormal from '@/assets/mushroomGrow/SmallNormal.svg'

// Constants
const initialSize = 10 // vw 단위로 변경
const midSize = 25 // vw 단위로 변경
const finalSize = 45 // vw 단위로 변경
const maxSize = 50 // vw 단위로 변경
const previewSize = 10 // 오른쪽 영역의 미리보기 크기 (vw 단위)

// Group Data
const groups = ref([
  { name: 'Group2', size: 16 },
  { name: 'Group3', size: 17 },
  { name: 'Group4', size: 18 },
  { name: 'Group5', size: 19 },
  { name: 'Group6', size: 20 },
  { name: 'Group7', size: 42 },
  { name: 'Group8', size: 42 },
  { name: 'Group9', size: 43 },
  { name: 'Group10', size: 43 },
  { name: 'Group11', size: 43 },
  { name: 'Group12', size: 43 }
])

const pairedGroups = ref(
  groups.value.reduce((result, value, index, array) => {
    if (index % 2 === 0) result.push(array.slice(index, index + 2))
    return result
  }, [])
)

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
      userGroupMushroomSize.value += 5
      sendClickDataToServer('grow')
    }
  } else {
    const group = groups.value.find((g) => g.name === currentGroup.value)
    if (group && group.size > initialSize) {
      group.size -= 1
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
      <v-virtual-scroll :items="pairedGroups">
        <template v-slot:default="{ item }">
          <v-list-item class="list-item">
            <template v-slot:prepend>
              <v-img
                v-if="item[0]"
                :src="getMushroomImage(item[0].size)"
                :style="{
                  width: previewSize + 'vw',
                  height: previewSize + 'vw',
                  transition: 'width 0.5s, height 0.5s'
                }"
                class="group-mushroom"
                @click="() => onGroupMushroomClick(item[0])"
              >
                <div>{{ item[0].name }}</div>
              </v-img>
            </template>
            <template v-slot:append>
              <v-img
                v-if="item[1]"
                :src="getMushroomImage(item[1].size)"
                :style="{
                  width: previewSize + 'vw',
                  height: previewSize + 'vw',
                  transition: 'width 0.5s, height 0.5s'
                }"
                class="group-mushroom"
                @click="() => onGroupMushroomClick(item[1])"
              >
                <div>{{ item[1].name }}</div>
              </v-img>
            </template>
          </v-list-item>
        </template>
      </v-virtual-scroll>
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
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.mushroom-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
}

.click-effect {
  position: absolute;
  font-size: 48px;
  pointer-events: none;
  transform: translate(-50%, -50%);
}

.group-mushroom {
  cursor: pointer;
}

.right-container {
  align-items: center; /* 가운데 정렬 */
}
</style>
