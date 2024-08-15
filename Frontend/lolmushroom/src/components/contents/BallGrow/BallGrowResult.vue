<script setup>
import { computed, onMounted , ref} from 'vue'
import BallContent from './BallContent.vue'
import { useBallStore } from '@/stores/ballStore';

const ballStore = useBallStore();

// Sample data

const allBalls = ref([
  { sessionId: '1', groupName: 'Group A', size: 30 },
  { sessionId: '2', groupName: 'Group B', size: 50 },
  { sessionId: '3', groupName: 'Group C', size: 40 }
])

// const allBalls = ballStore.getTotalBalls;

const winnerGroup = computed(() => {
  return allBalls.value.reduce((max, group) => (group.size > max.size ? group : max), allBalls.value[0])
})

function firework() {
  var duration = 100 * 100;
  var animationEnd = Date.now() + duration;
  var defaults = { startVelocity: 25, spread: 360, ticks: 50, zIndex: 0 };

  function randomInRange(min, max) {
    return Math.random() * (max - min) + min;
  }

  var interval = setInterval(function () {
    var timeLeft = animationEnd - Date.now();

    if (timeLeft <= 0) {
      return clearInterval(interval);
    }

    var particleCount = 50 * (timeLeft / duration);

    confetti(
      Object.assign({}, defaults, {
        particleCount,
        origin: { x: randomInRange(0.1, 0.3), y: Math.random() - 0.2 }
      })
    );
    confetti(
      Object.assign({}, defaults, {
        particleCount,
        origin: { x: randomInRange(0.7, 0.9), y: Math.random() - 0.2 }
      })
    );
  }, 100);
}

onMounted(() => {
  const script = document.createElement('script');
  script.src = "https://cdn.jsdelivr.net/npm/canvas-confetti@1.5.1/dist/confetti.browser.min.js";
  script.onload = () => {
    firework(); 
  };
  document.body.appendChild(script);
});
</script>



<template>
    <div class="results-container">
      <div class="result-header"><h1>우승 그룹!</h1></div>
      <BallContent :ball="winnerGroup.size" :is-main="true" :is-green="false"  />
    </div>
  </template>
  
  
  <style scoped>
  .results-container{
      background-color: #E7FFDE;
      display: flex;
      flex-direction: column;
      align-items: center; 
      justify-content: center; 
      height: 100vh; 
      box-sizing: border-box;
      padding: 20px; 
  }
  </style>
  