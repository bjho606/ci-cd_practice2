<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/User'
import ProgressBar from '@/components/common/ProgressBar.vue'
import RoomWaiting from '@/components/room/RoomWaiting.vue'

const router = useRouter()
const store = useUserStore()
const firstTrueAnswer = ref('')
const secondTrueAnswer = ref('')
const thirdTrueAnswer = ref('')
const firstFalseAnswer = ref('')
const isSubmitAnswer = ref('')

// 입력 개수 출력
const AnswerCountFunction = (a1, a2, a3, a4) => {
  let result = 0
  if (a1.length > 0) {
    result++
  }
  if (a2.length > 0) {
    result++
  }
  if (a3.length > 0) {
    result++
  }
  if (a4.length > 0) {
    result++
  }
  return result
}

// 몇 개 입력했는지 계산하는 코드
const answerCount = computed(() => {
  return AnswerCountFunction(
    firstTrueAnswer.value,
    secondTrueAnswer.value,
    thirdTrueAnswer.value,
    firstFalseAnswer.value
  )
})

// 텍스트가 입력되지 않으면 자동으로 발동
const NotNullRules = (value) => {
  if (value.length > 0) {
    return true
  } else {
    return '입력해 주시기 바랍니다.'
  }
}

// TOF 제출하기 버튼을 눌렀을 때 동작
const tofAnswerSubmit = (a1, a2, a3, a4) => {
  addTOFAnswers(a1, a2, a3, a4)
  // isSubmitAnswer.value = !isSubmitAnswer.value
  // 이거는 이제 그룹의 모든 구성원이 완료하면 실행하는 코드
  router.push({ name: 'TOFContent' });
}

// store에 TOF 진술을 추가하는 코드
const addTOFAnswers = (a1, a2, a3, a4) => {
  store.userTOFstatements = {
    nickName: store.userNickname,
    states: {
      trueStates: [a1, a2, a3],
      falseState: a4
    }
  }
}

// 모든 사람이 준비되었을 때 TOF main으로 이동하는 함수
const readyAllGuys = () => {
  router.push('TOFContent')
}
</script>

<template>
  <v-container fluid style="background-color: #ffffff" v-if="!isSubmitAnswer">
    <div class="text-h4 font-weight-medium">진실! 혹은 거짓?</div>

    <div class="text-body-1 text-medium-emphasis my-5">
      입력하신 정보를 바탕으로 퀴즈를 생성합니다. ༼ つ ◕_◕ ༽つ
    </div>
    <v-sheet
      class="d-flex align-center px-4 py-8 mx-auto"
      color="#3ABF38"
      max-width="250"
      rounded="lg"
    >
      <v-progress-linear
        bg-color="black"
        color="#000000"
        height="12"
        max="4"
        min="0"
        :model-value="answerCount"
        rounded
      ></v-progress-linear>

      <div class="ms-4 text-h6">{{ answerCount }}/4</div>
    </v-sheet>

    <br />

    <v-sheet class="mx-auto" width="300">
      <v-form ref="form" fast-fail @submit.prevent>
        <v-text-field
          v-model="firstTrueAnswer"
          label="첫 번째 사실"
          :rules="[NotNullRules]"
          color="blue"
        ></v-text-field>

        <v-text-field
          v-model="secondTrueAnswer"
          label="두 번째 사실"
          :rules="[NotNullRules]"
          color="blue"
        ></v-text-field>

        <v-text-field
          v-model="thirdTrueAnswer"
          label="세 번째 사실"
          :rules="[NotNullRules]"
          color="blue"
        ></v-text-field>

        <v-text-field
          v-model="firstFalseAnswer"
          label="첫 번째 거짓"
          :rules="[NotNullRules]"
          color="black"
        ></v-text-field>

        <v-btn
          class="mt-2"
          variant="tonal"
          rounded="lg"
          type="submit"
          block
          color="#3ABF38"
          @click="
            tofAnswerSubmit(firstTrueAnswer, secondTrueAnswer, thirdTrueAnswer, firstFalseAnswer)
          "
          >제출
        </v-btn>
      </v-form>
    </v-sheet>
  </v-container>

  <v-container v-else>
    <ProgressBar />
    <!-- <RoomWaiting /> -->
  </v-container>
</template>

<style scoped></style>
