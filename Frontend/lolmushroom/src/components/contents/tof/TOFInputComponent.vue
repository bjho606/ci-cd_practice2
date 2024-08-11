<script setup>
  import { ref, computed, reactive, onMounted } from 'vue'
  import { useRouter } from 'vue-router';
  import { useUserStore } from '@/stores/userStore';
  import { useTOFStore } from '@/stores/tofStore'
  import { useSessionStore } from '@/stores/sessionStore';
  import otherUserWaitingComponent from '@/components/common/OtherUserWaitingComponent.vue';
  import contentsAPI from '@/api/contents'
  import sessionAPI from '@/api/session'
  import webSocketAPI from '@/api/webSocket';
  import ButtonComponent from '@/components/common/ButtonComponent.vue';
  
  const router = useRouter()
  const userStore = useUserStore()
  const store = useTOFStore()
  const sessionStore = useSessionStore()
  const showAlter = ref()

  const statements = reactive({
    firstTrue: '',
    secondTrue: '',
    thirdTrue: '',
    firstFalse: ''
  })

  const isSubmit = ref()

  // 입력 개수 출력
  const writtenCounter = (t1, t2, t3, f1) => {
    let result = 0
    if (t1.length > 0) {
      result++
    }
    if (t2.length > 0) {
      result++
    }
    if (t3.length > 0) {
      result++
    }
    if (f1.length > 0) {
      result++
    }
    return result
  }

  // store에 TOF 진술을 추가하는 코드
  const addStatements = async (t1, t2, t3, f1) => {
    const statesObject = {
      ovToken: userStore.userOvToken,
      truths: [t1, t2, t3],
      false1: f1
    }
    await contentsAPI.createStatements(sessionStore.subSessionId, statesObject)
    webSocketAPI.sendSubmitData(`/publish/game/tf/question/${sessionStore.subSessionId}`, true)
  }


  // 몇 개 입력했는지 계산하는 코드
  const statementsCount = computed(() => {
    return writtenCounter(
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

  // subscribe하면 받는다.
  const onSubmitEvent = (_data, _sessionId) => {
    console.log('이벤트 수신')
    store.submitUserIncrease()
    goToTOFMain()
  }

  // 모든 사람이 준비되었을 때 TOF main으로 이동하는 함수
  const goToTOFMain = () => {
    if (store.submitUserCount === store.totalUserCount) {
      store.submitUserCount = 0
      router.push({ name: 'TOFContent' })
    }
  }

  // TOF 제출하기 버튼을 눌렀을 때 동작
  const submitStatements = (t1, t2, t3, f1) => {
    if (statementsCount.value === 4) {
      addStatements(t1, t2, t3, f1)
      .then(() => {
        isSubmit.value = true
        goToTOFMain()
      })
      .catch((error) => {
        console.log('답변 제출 중 에러가 발생하였습니다.', error)
      })
    } else {
      showAlter.value = true; return
    }
  }

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
    store.totalUserCount = await getUserCount()
    webSocketAPI.connect({
          sessionId: sessionStore.subSessionId,
          contentsName: 'tf',
          onEventReceived: onSubmitEvent,
          subscriptions: ['question']
        })
  })
</script>

<template>
  <!-- 진술을 제출했을 때 -->
  <v-container fluid v-if="!isSubmit">
    <div class="text-h4 font-weight-medium">진실! 혹은 거짓?</div>
    
    <div class="text-body-1 text-medium-emphasis my-5">
      입력하신 정보를 바탕으로 퀴즈를 생성합니다. ༼ つ ◕_◕ ༽つ
    </div>
    <!-- <v-sheet
    class="d-flex align-center px-4 py-8 mx-auto"
    color="#3ABF38"
    max-width="250"
    rounded="lg"
    > -->
      <v-progress-linear
      bg-color="#FFFFFF"
      color="#24A319"
      height="30"
      max="4"
      min="0"
      :model-value="statementsCount"
      rounded
      style="border: #000000 2px solid"
      >
          <strong style="color: #000000;">{{ statementsCount }} / 4</strong>
      </v-progress-linear>
    <!-- <div class="ms-4 text-h6">{{ answerCount }} / 4</div> -->
  <!-- </v-sheet> -->
  
  <br>
  
  <v-sheet class="mx-auto" width="300">
      <v-alert title="진실? 혹은 거짓!" text="입력 창을 모두 채워주세요." type="warning" v-if="showAlter"/>
      <v-form ref="form" fast-fail @submit.prevent>
        <v-text-field
          v-model="statements.firstTrue"
          label="첫 번째 사실"
          :rules="[NotNullRules]"
          color="blue"
        />

        <v-text-field
          v-model="statements.secondTrue"
          label="두 번째 사실"
          :rules="[NotNullRules]"
          color="blue"
        />

        <v-text-field
          v-model="statements.thirdTrue"
          label="세 번째 사실"
          :rules="[NotNullRules]"
          color="blue"
        />

        <v-text-field
          v-model="statements.firstFalse"
          label="첫 번째 거짓"
          :rules="[NotNullRules]"
          color="black"
        />

        <ButtonComponent
          text="제출하기"
          size="large"
          color="#43A047"
          @click="submitStatements()"
        />
        <!-- <v-btn
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
          "/>제출하기
        </v-btn> -->
      </v-form>
    </v-sheet>
  </v-container>

  <!-- 진술을 제출했다면 -->
  <v-container v-else class="waiting-container">
    <v-progress-linear
      bg-color="#FFFFFF"
      color="#24A319"
      height="30"
      :max="store.totalUserCount"
      min="0"
      :model-value="store.submitUserCount"
      rounded
      style="border: #000000 2px solid"
      >
          <strong style="color: #000000;">{{ store.submitUserCount  }} / {{ store.totalUserCount }}</strong>
      </v-progress-linear>
    <!-- <otherUserWaitingComponent
      :current=store.submitUserCount
      :total="store.totalUserCount"
    /> -->
  </v-container>
</template>

<style scoped>
.waiting-container {
  background: #247719;
  opacity: 0.7;
}
</style>
