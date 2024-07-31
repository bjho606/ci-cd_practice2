<!-- eslint-disable no-unused-vars -->
<script setup>
import GamePadImage from '@/assets/ps-gamepad.svg'

const props = defineProps({
  nextStep: {
    type: Function,
    required: true
  }
})

</script>

<template>
  <v-card class="mx-auto custom-card" max-width="344" hover>
    <v-card-title class="custom-title" @click="nextStep">세션 만들기</v-card-title>
    <v-img class="custom-image" :src="GamePadImage" aspect-ratio="1" @click="nextStep"></v-img>
    <v-card-text class="custom-text"> 우리 모임만의 컨텐츠를 큐레이션하고 즐기세요! </v-card-text>
  </v-card>
</template>

<style scoped>
.custom-card {
  text-align: center;
  border: 1px solid #ccc; /* Light grey border */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* Subtle shadow */
}

.custom-title {
  /*background-color: #2e7d32;  Dark green background */
  background-image: linear-gradient(
    68.6deg,
    rgba(79, 183, 131, 1) 14.4%,
    rgba(254, 235, 151, 1) 92.7%
  );
  color: #fff; /* White text */
  padding: 10px;
  font-size: 18px;
}

.custom-image {
  height: 200px;
  object-fit: contain;
  margin: 10px 0;
}

.custom-text {
  font-size: 14px;
  color: #666; /* Grey text */
  padding: 0 10px 10px 10px;
}
</style>
