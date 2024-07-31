<!-- eslint-disable no-unused-vars -->
<script setup>
import './assets/fonts.css'
import AudioPlayer from './components/common/AudioPlayer.vue'
import GlobalHeader from '@/components/common/GlobalHeader.vue'
import GlobalFooter from '@/components/common/GlobalFooter.vue'
import MakeSessionView from '@/views/MakeSessionView.vue'
import GroupSessionView from '@/views/GroupSessionView.vue'
import GroupFightSessionView from './views/GroupFightSessionView.vue'
import CustomRoom from '@/components/room/CustomRoom.vue'
import MultiRoom from '@/components/room/MultiRoom.vue'
import RoomWatching from '@/components/room/RoomWatching.vue'
</script>

<template>
  <v-app id="v-app">
    <!-- Navigation Drawer는 그룹 컨텐츠 / 그룹 대항전 컨텐츠 여부에 따라 달라져야 함-->
    <v-navigation-drawer :width="10" permanent></v-navigation-drawer>
    <v-navigation-drawer :width="10" location="right" permanent></v-navigation-drawer>
    <GlobalHeader />
    <v-main class="d-flex align-center justify-center background">
      <v-container fluid class="container">
        <!-- <GroupSessionView /> -->
        <!-- <MakeSessionView /> -->
        <!-- <GroupFightSessionView /> -->
        <!-- <AudioPlayer /> -->
        <RouterView></RouterView>
      </v-container>
      <!-- <ChatScreen class="chat"/> -->
      <div class="mushroomGreen mushroom-1"></div>
      <div class="mushroomOrange mushroom-2"></div>
      <div class="mushroomRed mushroom-3"></div>
    </v-main>
    <!-- <GlobalFooter /> -->
  </v-app>
</template>

<style scoped>
.background {
  background-image: url('@/assets/origbig.png');
  background-size: cover; /* Cover the entire background */
  background-position: center; /* Center the background image */
  background-attachment: fixed; /* Fix the background image */
  background-repeat: no-repeat; /* Ensure the background does not repeat */
  min-height: 100vh; /* Ensure the background covers the viewport height */
  position: relative;
}

.container {
  max-width: calc(100% - 20vw); /* => 그룹 세션 / 그룹 대항전 여부에 따라 달라져야함 */
  height: 100%; /* Ensure the container takes full height */
  position: relative;
  z-index: 2;
}
.mushroomGreen {
  width: 50px;
  height: 43px;
  background-image: url('@/assets/mushroom1.svg'); /* Replace with the actual path to mushroom image */
  background-size: contain;
  background-repeat: no-repeat;
  position: absolute;
  bottom: 0;
}
.mushroomOrange {
  width: 50px;
  height: 43px;
  background-image: url('@/assets/mushroom2.svg'); /* Replace with the actual path to mushroom image */
  background-size: contain;
  background-repeat: no-repeat;
  position: absolute;
  bottom: 0;
}
.mushroomRed {
  width: 50px;
  height: 43px;
  background-image: url('@/assets/mushroom3.svg'); /* Replace with the actual path to mushroom image */
  background-size: contain;
  background-repeat: no-repeat;
  position: absolute;
  bottom: 0;
}
.mushroom-1 {
  animation: moveMushroom1 5s linear infinite;
}

.mushroom-2 {
  animation: moveMushroom2 6s linear infinite;
}

.mushroom-3 {
  animation: moveMushroom3 4s linear infinite;
}

@keyframes moveMushroom1 {
  0% {
    left: 0;
  }
  50% {
    left: 80%;
  }
  100% {
    left: 0;
  }
}

@keyframes moveMushroom2 {
  0% {
    left: 10%;
  }
  50% {
    left: 90%;
  }
  100% {
    left: 10%;
  }
}

@keyframes moveMushroom3 {
  0% {
    left: 20%;
  }
  50% {
    left: 70%;
  }
  100% {
    left: 20%;
  }
}

/* .chat {
  position: absolute;
  bottom: 0;
  left: 0;
} */
</style>
