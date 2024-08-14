<script setup>
  import { useTOFStore } from '@/stores/tofStore'
  import { ref, computed } from 'vue'

  const props = defineProps({
    targetNickName: String,
    selectedAnswer: Number,
    answer: Number,
  })

  const store = useTOFStore()
  const expanded = ref({})

  const correctRate = computed(() => {
    return store.submitUserCount !== 0 ? store.chosenArray[props.answer].length / store.submitUserCount : 0;
  })

  const toggleList = (choice) => {
    expanded.value[choice] = !expanded.value[choice]
  }
</script>

<template>
  <!-- 투표 결과 컴포넌트 -->
  <!-- 카드 컨테이너 -->
  <v-container class="tof-result-div">
    <v-row>
      <v-col v-for="i in 4" :key="i" cols="6">
        <v-card
        class="mx-5 card-border"
          :class="{
            'correct-card': props.answer === i,
            'selected-card': props.answer !== i && selectedAnswer === i
          }"
        > 
          <div>{{ store.statements[i-1] }}</div>
          <div>{{ store.chosenArray[i].length }}명</div>
        </v-card>
      </v-col>
    </v-row>

    <div class="selectResult-div">
      <div class="correctRate-div">정답률: {{ correctRate * 100 }}%</div>
    </div>
  </v-container>

  <!-- <v-container>
    <v-row>
      <v-col v-for="i in 4" :key="i" cols="6">
        <v-card
          class="mx-5 card-border"
          prepend-avatar="../../../../src/assets/image/thinking_face.svg"
          :title="`${store.statements[i-1]}-${store.chosenArray[i].length}명`"
        />
      </v-col>
    </v-row>
    <v-card v-for="i in 4" :key="i">
      <v-card-title>
        <v-icon :icon="`mdi-numeric-${i}-circle-outline`" v-show="answer!==i"></v-icon>
        <v-icon :icon="`mdi-numeric-${i}-circle-outline`" color="red" v-show="answer===i"></v-icon>
        {{ store.statements[i-1] }} - {{ store.chosenArray[i].length }}명
        <span @click="toggleList(i)">
          {{ expanded[i] ? '▲' : '▼' }}
        </span>
      </v-card-title>
      <v-expand-transition>
        <v-card-text v-if="expanded[i]">
          <ul>
            <li v-for="nickname in store.chosenArray[i]" :key="nickname">
              {{ nickname }}
            </li>
          </ul>
        </v-card-text>
      </v-expand-transition>
      <v-divider class="border-opacity-55"></v-divider>
    </v-card>
  </v-container> -->

</template>

<style scoped>
ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

li {
  padding: 5px 0;
}

* {
  font-size: small;
}

.card-border {
  display: flex;
  flex-direction: row;
  
}

.tof-result-div {
  display: flex;
  flex-direction: column;
}

.selectResult-div {
  padding: 12px;
  margin: 12px;
}

.correctRate-div {
  font-size: 25px;
  font-weight: bold;
}

.correct-card {
  background-color: green;
}

.selected-card {
  background-color: red;
}

</style>
