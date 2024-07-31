<script setup>
import { ref } from 'vue'
import ChatScreen from '@/components/common/ChatScreen.vue'
const cameraIcon = ref('custom:cameraOn')
const micIcon = ref('custom:micOn')

// Functions to toggle the icons
const toggleCameraIcon = () => {
  cameraIcon.value = cameraIcon.value === 'custom:cameraOn' ? 'custom:cameraOff' : 'custom:cameraOn'
}

const toggleMicIcon = () => {
  micIcon.value = micIcon.value === 'custom:micOn' ? 'custom:micOff' : 'custom:micOn'
}
</script>

<template>
  <ChatScreen class="bottom-left-chat" />
  <v-container class="top-left">
    <v-icon :icon="cameraIcon" class="top-left-icon" @click="toggleCameraIcon"></v-icon>
    <v-icon :icon="micIcon" class="top-left-icon" @click="toggleMicIcon"></v-icon>
  </v-container>
  <v-container fluid class="contents-container">
    <RouterView />
  </v-container>
</template>

<style scoped>
.contents-container {
  display: flex; /* Use Flexbox for layout */
  justify-content: center; /* Center horizontally */
  align-items: center; /* Center vertically */
  background-color: rgba(224, 224, 224, 0.6); /* 원하는 배경색으로 변경 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 그림자 효과 */
  position: absolute; /* Position absolutely within parent */
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
}
.bottom-left-chat {
  position: absolute;
  left: -12%;
  bottom: 2%;
  width: 36px; /* Adjust icon size */
  height: 36px; /* Adjust icon size */
}
.top-left {
  position: absolute;
  display: flex;
  left: -13.5%;
  top: 1%;
}
.top-left-icon {
  width: 34px; /* Adjust icon size */
  height: 34px; /* Adjust icon size */
  margin-left: 1.5%;
}
</style>
