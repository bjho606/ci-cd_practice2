<script setup>
import { defineEmits } from 'vue'

const emit = defineEmits(['start-game'])

const startGame = () => {
  emit('start-game')
}
</script>

<template>
  <button class="start-session-btn" @click="startGame">
    <span class="start-session-text">컨텐츠 시작하기</span>
    <v-icon class="start-session-icon" icon="mdi-chevron-right" />
  </button>
</template>

<style scoped>
.start-session-btn {
  width: 100%;
  height: 100%;
  background-color: #1f4f16;
  color: #ffffff;
  padding: 20px;
  border: none;
  border-radius: 10px;
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative; /* This allows us to position the icon independently */
}

.start-session-text {
  flex-grow: 1;
  text-align: center;
}

.start-session-icon {
  position: absolute;
  right: 20px; /* Position the icon to the far right */
  font-size: 2rem;
  color: #ffffff;
}
</style>
