<script setup>
import {ref, computed} from 'vue'
import { useMushroomStore } from '@/stores/mushroomStore'
import HeaderComponent from '@/components/room/playerWating/watingHeader.vue'

const mushroomStore = useMushroomStore()

// const allMushrooms = computed(() => mushroomStore.getAllMushrooms)
const allMushrooms = ref([
  { sessionId: '1', groupName: 'Group A', size: 30 },
  { sessionId: '2', groupName: 'Group B', size: 50 },
  { sessionId: '3', groupName: 'Group C', size: 40 }
])
console.log(allMushrooms.value)

const winnerGroup = computed(() => {
  return allMushrooms.value.reduce((max, group) => (group.size > max.size ? group : max), allMushrooms.value[0])
})

</script>

<template>
    <div class="results-container">

      <HeaderComponent description="게임 종료 결과를 확인하세요" /> <!-- HeaderComponent 사용 -->
      
      <div class="result">
        <h1>게임 결과</h1>
      </div>
      <div v-if="winnerGroup" class="winner-group">
        <div>
          <h2>우승 팀: {{ winnerGroup.groupName }}</h2>
        </div>
        <div>
          <p>크기: {{ winnerGroup.size }}</p>
        </div>
      </div>
  
      <div class="mushroom-results">
        <div>
          <h2>모든 그룹의 상태</h2>
        </div>
        <ul class="list">
          <li v-for="group in allMushrooms" :key="group.sessionId">
            <p>그룹 이름: {{ group.groupName }}</p>
            <p>버섯 크기: {{ group.size }}</p>
          </li>
        </ul>
      </div>
    </div>

  </template>

<style>
.results-container{
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.winner-group{
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.result{
  text-align: center;
}

.mushroom-results{
  text-align: center;
  align-content: center;
}

.list{
  display: flex;
  gap: 5px;
  list-style: none;
  padding: 0;
  justify-content: center;
}



</style>