<script setup>
import { computed, ref } from 'vue'
import BlueBallComponent from '@/components/contents/BallGrow/BlueBallComponent.vue'
import GreenBallComponent from '@/components/contents/BallGrow/GreenBallComponent.vue'

const props = defineProps({
  ball: Object,
  isMain: Boolean,
  isGreen: Boolean
})

const ballSize = computed(() => props.ball.size)
const _isMain = computed(() => props.isMain)
const groupName = ref("그룹이름!!!");

const _isGreen = computed(() => props.isGreen)

const containerClass = computed(() => (_isMain.value ? 'main-container' : 'sub-container'))
const ballClass = computed(() => (_isMain.value ? 'main-ball' : 'group-ball'))
</script>

<template>
  <div :class="containerClass">
    <div class="ball-name">{{ groupName }}</div>
    <div class="ball-container">
      <GreenBallComponent v-if="_isGreen" :class="ballClass" :health="ballSize" :isMain="_isMain"/>
      <BlueBallComponent v-else :class="ballClass" :health="ballSize" :isMain="_isMain"/>
    </div>
  </div>
</template>

<style scoped>
.main-container,
.sub-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #FFF2F7;
  border: 1px solid black;
  border-radius: 20px;
}

.main-container {
  width: 600px;
  height: 400px;
}

.sub-container {
  width: 300px;
  height: 200px;
}

.ball-name {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 5px;
  font-size: x-large;
}

.ball-container {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
