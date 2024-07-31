<script setup></script>

<template>
  <div class="main">
    <v-container class="main-space">
      <div class="animated-text">
        <span>게</span><span>임</span><span>을</span><span> </span><span>기</span><span>다</span>
        <span>리</span><span>는</span><span> </span><span>중</span><span>입</span><span>니</span
        ><span>다</span>
      </div>
      <v-img height="70%" src="https://i.ibb.co/Nrr5g3n/wating-Mushroom.png"> </v-img>
      <v-container>
        <v-progress-linear indeterminate color="white" :height="15"></v-progress-linear>
      </v-container>
    </v-container>
  </div>
</template>

<style scoped>
@font-face {
  font-family: NeoDunggeunmo;
  src:
    url('@/assets/fonts/NeoDunggeunmoPro-Regular.woff') format('woff'),
    url('@/assets/fonts/NeoDunggeunmoPro-Regular.woff2') format('woff2');
}

.main-space {
  width: 40ex;
  height: 37ex;
  background-color: #795c34;
  opacity: 80%;
}

.main {
  font-family: NeoDunggeunmo;
  font-weight: 100;
  text-align: center;
  color: white;
  font-size: xx-large;
}

.animated-text {
  margin-top: 20px;
}

.animated-text span {
  display: inline-block;
  animation: jump 0.6s ease-in-out infinite;
}

@keyframes jump {
  0%,
  20%,
  50%,
  80%,
  100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-20px);
  }
  60% {
    transform: translateY(-10px);
  }
}

.animated-text span:nth-child(1) {
  animation-delay: 0s;
}
.animated-text span:nth-child(2) {
  animation-delay: 0s;
}
.animated-text span:nth-child(3) {
  animation-delay: 0.05s;
}
.animated-text span:nth-child(4) {
  animation-delay: 0.05s;
}
.animated-text span:nth-child(5) {
  animation-delay: 0.1s;
}
.animated-text span:nth-child(6) {
  animation-delay: 0.1s;
}
.animated-text span:nth-child(7) {
  animation-delay: 0.15s;
}
.animated-text span:nth-child(8) {
  animation-delay: 0.15s;
}
.animated-text span:nth-child(9) {
  animation-delay: 0.2s;
}
.animated-text span:nth-child(10) {
  animation-delay: 0.2s;
}
.animated-text span:nth-child(11) {
  animation-delay: 0.25s;
}
.animated-text span:nth-child(12) {
  animation-delay: 0.3s;
}
.animated-text span:nth-child(13) {
  animation-delay: 0.3s;
}

/* 로딩바 css */
.loader {
  width: 300px;
  margin: 80px;
  position: relative;
}

.loader .loading {
  position: relative;
  width: 100%;
  height: 10px;
  border: 1px solid brown;
  border-radius: 10px;
  animation: turn 4s linear 1.75s infinite;
}

.loader .loading:before {
  content: '';
  display: block;
  position: absolute;
  width: 0;
  height: 100%;
  background-color: white;
  box-shadow: 10px 0px 15px 0px black;
  animation: load 2s linear infinite;
}

@keyframes load {
  0% {
    width: 0%;
  }

  87.5% {
    width: 100%;
  }
}

@keyframes turn {
  0% {
    transform: rotateY(0deg);
  }

  6.25%,
  50% {
    transform: rotateY(180deg);
  }

  56.25%,
  100% {
    transform: rotateY(360deg);
  }
}

@keyframes bounce {
  0%,
  100% {
    top: 10px;
  }

  12.5% {
    top: 30px;
  }
}
</style>
