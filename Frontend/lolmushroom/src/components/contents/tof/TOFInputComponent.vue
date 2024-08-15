<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useContentsStore } from '@/stores/contentsStore'
import { useUserStore } from '@/stores/userStore'
import { useTOFStore } from '@/stores/tofStore'
import { useSessionStore } from '@/stores/sessionStore'
import contentsAPI from '@/api/contents'
import sessionAPI from '@/api/session'
import webSocketAPI from '@/api/webSocket'
import ContentsLoading from '@/components/contents/ContentsLoading.vue'
import OtherUserWaitingComponent from '@/components/common/OtherUserWaitingComponent.vue'
import WaitingHeader from '@/components/room/playerWaiting/WaitingHeader.vue'


const router = useRouter()
const userStore = useUserStore()
const store = useTOFStore()
const sessionStore = useSessionStore()
const contentsStore = useContentsStore()
const showAlter = ref()
const showLoading = ref(false)
const contentsInfo = contentsStore.contents[0]

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
  console.log(statesObject)
  await contentsAPI.createStatements(sessionStore.subSessionId, statesObject)
  webSocketAPI.sendSubmitData(
    `/publish/game/tf/question/${sessionStore.sessionId}/${sessionStore.subSessionId}`,
    true
  )
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
    showAlter.value = true
    return
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
    sessionId: sessionStore.sessionId,
    subSessionId: sessionStore.subSessionId,
    contentsName: 'tf',
    onEventReceived: onSubmitEvent,
    subscriptions: ['question']
  })

  const hasShownLoading = localStorage.getItem('hasShownLoading')
  if (!hasShownLoading) {
    showLoading.value = true
    localStorage.setItem('hasShownLoading', 'true')

    setTimeout(() => {
      showLoading.value = false
    }, 5000) // 5초 동안 모달을 표시
  }
})
</script>

<template>
  <v-dialog v-model="showLoading" persistent max-width="1200">
    <ContentsLoading :contentsInfo="contentsInfo" :time="'5'" :countText="'초 후에 시작합니다!'" />
  </v-dialog>
  <!-- 진술을 제출했을 때 -->
  <v-container fluid v-if="!isSubmit" style="width: 70%" class="my-5">
    <WaitingHeader
      first-description="진실 혹은 거짓"
      second-description="진실 3개와 거짓 1개로 나를 소개해 보세요!"
      third-description="카메라가 켜지면 자신을 소개해 보세요."
    />
    <div class="progress-linear-container">
      <v-progress-linear
        bg-color="#FFFFFF"
        color="#24A319"
        height="30"
        max="4"
        min="0"
        :model-value="statementsCount"
        rounded
        style="border: #000000 2px solid; width: 50%"
      >
        <strong style="color: #000000">{{ statementsCount }} / 4</strong>
      </v-progress-linear>
    </div>

    <v-alert
      title="진실? 혹은 거짓!"
      text="입력 창을 모두 채워주세요."
      type="warning"
      v-if="showAlter"
      class="warning-alert"
    />
    <v-form ref="form" fast-fail @submit.prevent class="mt-5">
      <v-container>
        <v-row>
          <v-col cols="12" md="1">
            <v-chip variant="elevated" color="#00FF00"> 진실 </v-chip>
          </v-col>
          <v-col cols="12" md="5">
            <v-text-field
              v-model="statements.firstTrue"
              color="blue"
              placeholder="나의 정보 중 진실을 알려주세요."
              variant="solo"
              @keyup.enter="
                submitStatements(
                  statements.firstTrue,
                  statements.secondTrue,
                  statements.thirdTrue,
                  statements.firstFalse
                )
              "
            />
            <template v-slot:prepend-inner>
              <img
                src="../../../../src/assets/image/smile_face.svg"
                alt="custom icon"
                class="input-icon"
              />
            </template>
          </v-col>

          <v-col cols="12" md="5">
            <v-text-field
              v-model="statements.secondTrue"
              placeholder="나의 정보 중 진실을 알려주세요."
              variant="solo"
              @keyup.enter="
                submitStatements(
                  statements.firstTrue,
                  statements.secondTrue,
                  statements.thirdTrue,
                  statements.firstFalse
                )
              "
            />
          </v-col>

          <v-col cols="5" offset="1">
            <v-text-field
              v-model="statements.thirdTrue"
              placeholder="나의 정보 중 진실을 알려주세요."
              variant="solo"
              @keyup.enter="
                submitStatements(
                  statements.firstTrue,
                  statements.secondTrue,
                  statements.thirdTrue,
                  statements.firstFalse
                )
              "
            />
          </v-col>
        </v-row>

        <v-row>
          <v-col cols="12" md="1">
            <v-chip variant="elevated" color="#247719"> 거짓 </v-chip>
          </v-col>
          <v-col cols="5">
            <v-text-field
              v-model="statements.firstFalse"
              placeholder="나의 정보 중 거짓을 알려주세요."
              variant="solo"
              @keyup.enter="
                submitStatements(
                  statements.firstTrue,
                  statements.secondTrue,
                  statements.thirdTrue,
                  statements.firstFalse
                )
              "
            />
          </v-col>
        </v-row>

        <div style="text-align: center">
          <v-btn
            class="mt-2"
            text="제출하기"
            size="large"
            width="500"
            color="#43A047"
            rounded
            @click="
              submitStatements(
                statements.firstTrue,
                statements.secondTrue,
                statements.thirdTrue,
                statements.firstFalse
              )
            "
          />
        </div>
      </v-container>
    </v-form>
  </v-container>

  <!-- 진술을 제출했다면 -->
  <v-container v-else class="waiting-container">
    <OtherUserWaitingComponent :current="store.submitUserCount" :total="store.totalUserCount" />
  </v-container>
</template>

<style scoped>
.waiting-container {
  text-align: center;
  background: #247719;
  opacity: 0.7;
  margin-top: 10%;
}

v-text-field {
  border: 1px solid black;
}

.input-icon {
  width: 24px;
  height: 24px;
}

.progress-linear-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.warning-alert {
  display: flex;
  justify-content: center;
}

*{
  background: #E7FFDE;
}
</style>
