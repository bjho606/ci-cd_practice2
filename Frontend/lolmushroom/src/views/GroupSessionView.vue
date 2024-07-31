<!-- eslint-disable no-unused-vars -->
<script setup>
import { ref } from 'vue'
import ChatScreen from '@/components/common/ChatScreen.vue'
import TOFInputsComponent from '@/components/contents/TOFInputsComponent.vue'
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
  <v-container fluid class="contents-container">
    <ChatScreen class="bottom-left" />
    <!-- RouterView로 들어갈 예정 -->
    <TOFInputsComponent />
    <v-container class="top-right">
      <v-icon :icon="cameraIcon" class="top-right-icon" @click="toggleCameraIcon"> </v-icon>
      <v-icon
        :icon="micIcon"
        class="top-right-icon top-right-right"
        @click="toggleMicIcon"
      ></v-icon>
    </v-container>
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
.bottom-left {
  position: absolute;
  bottom: 20px;
  left: -15%;
  width: 36px; /* Adjust icon size */
  height: 36px; /* Adjust icon size */
}
.top-right {
  position: absolute;
  top: 10px;
  left: 102%;
}
.top-right-right {
  left: 10px;
}
.top-right-icon {
  width: 35px; /* Adjust icon size */
  height: 35px; /* Adjust icon size */
}
</style>
