<!-- eslint-disable no-unused-vars -->
<script setup>
import './assets/fonts.css'
import { onMounted } from 'vue'
import { useContentsStore } from './stores/contentsStore'
import contentsAPI from '@/api/contents'
import PlayerNicknameSetting from './components/room/playerWating/PlayerNicknameSetting.vue'

/**
 * * 1. Meshroom의 Contents 목록을 가져와 Pinia에 저장한다.
 * IMP : ContentsAPI.getContents()를 호출하는 fetchContents를 통해 Pinia에 저장
 */

const contentsStore = useContentsStore()
const fetchContents = async () => {
  try {
    const response = await contentsAPI.getContents()
    console.log(response.data)
    if (response.data.isSuccess) {
      contentsStore.setContents(response.data.result.contents)
    }
  } catch (error) {
    console.log('Error Getting Meshroom Contents', error)
  }
}

/**
 * * 2. 모든 사용자는 Meshroom이 OnMount될 때, Contents List를 내려받는다.
 */
onMounted(() => {
  fetchContents()
})
</script>

<template>
  <v-app id="v-app">
    <!-- IMP : 현재 의도는 Navigation Drawer가 Group / GroupFight 여부에 따라 달라지는 것 -->
    <v-navigation-drawer :width="20" permanent> </v-navigation-drawer>
    <v-navigation-drawer :width="20" location="right" permanent></v-navigation-drawer>
    <GlobalHeader />
    <v-main class="d-flex align-center justify-center">
      <v-container>
        <!--<RouterView></RouterView>-->
        <PlayerNicknameSetting />
      </v-container>
      <div class="mushroomGreen mushroom-1"></div>
      <div class="mushroomOrange mushroom-2"></div>
      <div class="mushroomRed mushroom-3"></div>
    </v-main>
  </v-app>
</template>

<style scoped>


@font-face {
  font-family: Line;
  src:
    url('@/assets/fonts/LINESeedKR-Bd.woff') format('woff'),
    url('@/assets/fonts/LINESeedKR-Bd.woff2') format('woff2');
}

*{
  font-family: Line;
}


/* .container {
  max-width: calc(100%);
  height: 100%;
  position: relative;
  overflow: visible;
} */

/* .background {
  background-image: url('@/assets/origbig.png');
  background-size: cover; 
  background-position: center; 
  background-attachment: fixed; 
  background-repeat: no-repeat; 
  min-height: 100vh; 
  position: relative;
} */
/* Mushroom Moving CSS
.mushroomGreen {
  width: 50px;
  height: 43px;
  background-image: url('@/assets/mushroom1.svg'); 
  background-size: contain;
  background-repeat: no-repeat;
  position: absolute;
  bottom: 0;
}
.mushroomOrange {
  width: 50px;
  height: 43px;
  background-image: url('@/assets/mushroom2.svg'); 
  background-size: contain;
  background-repeat: no-repeat;
  position: absolute;
  bottom: 0;
}
.mushroomRed {
  width: 50px;
  height: 43px;
  background-image: url('@/assets/mushroom3.svg'); 
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
} */
</style>
