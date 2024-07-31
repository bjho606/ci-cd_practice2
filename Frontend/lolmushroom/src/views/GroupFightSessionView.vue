<!-- eslint-disable no-unused-vars -->
<script setup>
import { ref } from 'vue'
import ChatScreen from '@/components/common/ChatScreen.vue'
import MushroomGrowContent from '@/components/contents/MushroomGrowContent.vue'
import MushroomContent from '@/components/contents/MushroomContent.vue'
</script>

<template>
  <ChatScreen class="bottom-left" />
  <v-container fluid class="contents-container">
    <!-- <MushroomGrowContent /> -->
    <MushroomContent />
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
  overflow: hidden;
}
.bottom-left {
  position: absolute;
  bottom: 20px;
  left: -5%;
  width: 36px; /* Adjust icon size */
  height: 36px; /* Adjust icon size */
}
</style>
