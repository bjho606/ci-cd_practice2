<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/userStore'
import { useSessionStore } from '@/stores/sessionStore'
import ProgressBar from '@/components/common/ProgressBar.vue'
import RoomWating from '@/components/room/RoomWaiting.vue'
import contentsAPI from '@/api/contents'
import sessionAPI from '@/api/session'

const router = useRouter()
const store = useUserStore()
const sessionStore = useSessionStore()

const statements = reactive({
  firstTrue: '',
  secondTrue: '',
  thirdTrue: '',
  firstFalse: ''
})
const isSubmitAnswer = ref()
const totalUserCount = ref()

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
    statements.firstTrue,
    statements.secondTrue,
    statements.thirdTrue,
    statements.firstFalse
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

// 모든 사람이 준비되었을 때 TOF main으로 이동하는 함수
const readyAllGuys = () => {
  router.push({ name: 'TOFContent' })
}

// TOF 제출하기 버튼을 눌렀을 때 동작
const tofAnswerSubmit = (a1, a2, a3, a4) => {
  addTOFAnswers(a1, a2, a3, a4)
  isSubmitAnswer.value = true
  // 실제로는 소켓통신과 watch를 통해 readyallguys를 실행
  readyAllGuys()
}

// store에 TOF 진술을 추가하는 코드
const addTOFAnswers = async (a1, a2, a3, a4) => {
  const statesObject = {
    ovToken: store.userOvToken,
    truths: [a1, a2, a3],
    false1: a4
  }
  await contentsAPI.createStatements(sessionStore.subSessionId, statesObject)
}

// 세션에 참가한 유저 정보를 요청하는 함수
// const getSubSessionInfo = async (sessionId, subSessionId) => {
//     const response = await sessionAPI.getSubSessionInfo(sessionId, subSessionId);
//     currentUserCount.value = response.data.result.username.length
//   }

// 현재 하위 세션에 존재하는 전체 유저의 수를 반환하는 함수
async function getUserCount() {
  const response = await sessionAPI.getSubSessionInfo(
    sessionStore.sessionId,
    sessionStore.subSessionId
  )
  return response['data']['result']['currentUserCount']
}

// 컴포넌트가 마운트될 때 getUserCount 함수를 호출하여 totalUserCount를 설정
onMounted(async () => {
  totalUserCount.value = await getUserCount()
})
</script>

<template>
  <!-- 진술을 제출했을 때 -->
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
          v-model="statements.firstTrue"
          label="첫 번째 사실"
          :rules="[NotNullRules]"
          color="blue"
        ></v-text-field>

        <v-text-field
          v-model="statements.secondTrue"
          label="두 번째 사실"
          :rules="[NotNullRules]"
          color="blue"
        ></v-text-field>

        <v-text-field
          v-model="statements.thirdTrue"
          label="세 번째 사실"
          :rules="[NotNullRules]"
          color="blue"
        ></v-text-field>

        <v-text-field
          v-model="statements.firstFalse"
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
            tofAnswerSubmit(
              statements.firstTrue,
              statements.secondTrue,
              statements.thirdTrue,
              statements.firstFalse
            )
          "
          >제출
        </v-btn>
      </v-form>
    </v-sheet>
  </v-container>

  <!-- 진술을 제출했다면 -->
  <v-container v-else>
    <ProgressBar current="1" :total="totalUserCount" />
    <!-- <RoomWating /> -->
  </v-container>
</template>

<style scoped></style>
