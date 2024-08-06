<script setup>
import { useUserStore } from '@/stores/userStore'
import { ref, computed } from 'vue'

const props = defineProps({
  targetNickName: String,
  choiceAnswerMatrix: Array,
  statements: Array
})
const store = useUserStore()
// 하드코딩한 것은 대부분 API로 받아올 것
const myNickName = store.userNickname
const targetNickName = props.targetNickName
const answer = ref('4')

// const statements = ref({
//   1: '나는 최고다',
//   2: '나는 천재다.',
//   3: '나는 잘생겼다.',
//   4: '나는 공부를 잘 한다.',
// });

// const choiceAnswerMatrix = {
//   '나이스한 강아지': {
//     1: [],
//     2: ['재빠른 날다람쥐', '영민한 뱅갈호랑이'],
//     3: ['얍삽한 치타'],
//     4: ['여유로운 오징어', '거대한 사막여우'],
//   }
// };

const statements = props.targetNickName
const choiceAnswerMatrix = props.choiceAnswerMatrix

const expanded = ref({})

const toggleList = (choice) => {
  expanded.value[choice] = !expanded.value[choice]
}

const totalParticipants = computed(() => {
  return Object.values(choiceAnswerMatrix[myNickName]).reduce(
    (total, nicknames) => total + nicknames.length,
    0
  )
})

const correctRate =
  choiceAnswerMatrix[targetNickName][answer.value].length / totalParticipants.value
</script>

<template>
  <v-container>
    <h1>총 참여자 수: {{ totalParticipants }}명</h1>
    <h2>정답률: {{ correctRate * 100 }}%</h2>
    <v-card v-for="i in 4" :key="i">
      <v-card-title>
        <v-icon :icon="`mdi-numeric-${i}-circle-outline`"></v-icon>
        {{ statements[i] }} - {{ choiceAnswerMatrix[targetNickName][i].length }}명
        <span @click="toggleList(i)">
          {{ expanded[i] ? '▲' : '▼' }}
        </span>
        <!-- <v-btn icon >
              <v-icon>{{ expanded[i] ? 'mdi-chevron-up' : 'mdi-chevron-down' }}</v-icon>
            </v-btn> -->
      </v-card-title>
      <v-expand-transition>
        <v-card-text v-if="expanded[i]">
          <ul>
            <li v-for="nickname in choiceAnswerMatrix[targetNickName][i]" :key="nickname">
              {{ nickname }}
            </li>
          </ul>
        </v-card-text>
      </v-expand-transition>
      <v-divider class="border-opacity-55"></v-divider>
    </v-card>
  </v-container>
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
</style>
