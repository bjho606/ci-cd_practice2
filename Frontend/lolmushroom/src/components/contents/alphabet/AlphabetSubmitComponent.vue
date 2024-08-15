<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useContentsStore } from '@/stores/contentsStore'
import { useSessionStore } from '@/stores/sessionStore'
import { useAlphabetStore } from '@/stores/alphabetStore'
import { useUserStore } from '@/stores/userStore'
import ContentsLoading from '../ContentsLoading.vue'
import sessionAPI from '@/api/session'
import contentsAPI from '@/api/contents'
import webSocketAPI from '@/api/webSocket'
import WaitingHeader from '@/components/room/playerWaiting/WaitingHeader.vue'


const router = useRouter()
const store = useAlphabetStore()
const sessionStore = useSessionStore()
const contentsStore = useContentsStore()
const contentsInfo = contentsStore.contents[3]
const showLoading = ref(false)
const userStore = useUserStore()
const quizWord = ref('')
const isDisabled = ref(false)
const showAlert = reactive({
  blank: false,
  korean: false
})
// const showAlter = ref('')

// 세션에 참가한 유저 정보를 요청하는 함수
const response = await sessionAPI.getSubSessionInfo(
  sessionStore.sessionId,
  sessionStore.subSessionId
)
store.setTotalUser(response['data']['result']['currentUserCount'])

const categoryName = await contentsAPI.getCategory(sessionStore.subSessionId)

const onWordReceived = () => {
  store.submitUserIncrease()
  if (store.submitUserCount === store.totalUserCount) {
    store.submitUserClear()
    router.push('alphabetContent')
  }
}

// 영문이나 숫자가 들어가 있으면 거른다.
const validateQuizWord = (word) => {
  const regex = /^[가-힣]+$/
  if (regex.test(word)) {
    return false
  } else {
    return true
  }
}

// 퀴즈를 제출하는 함수
const submitQuizWord = async () => {
  if (quizWord.value === '') {
    showAlert.blank = true
    showAlert.korean = true
    return
  } else if (validateQuizWord(quizWord.value)) {
    quizWord.value = ''
    showAlert.blank = false
    showAlert.korean = true
    return
  } else {
    showAlert.value = false
    const object = {
      ovToken: userStore.userOvToken,
      categoryName: categoryName['data']['result']['categoryName'],
      quizWord: quizWord.value.trim()
    }
    const response = await contentsAPI.createQuizWord(
      sessionStore.sessionId,
      sessionStore.subSessionId,
      object
    )
    if (response['data']['result']['created'] === true) {
      quizWord.value = ''
      isDisabled.value = true
    }
  }
}

onMounted(async () => {
  console.log('초성 게임 연결 중..')
  // 세션 연결
  webSocketAPI.connect({
    subSessionId: sessionStore.subSessionId,
    onEventReceived: onWordReceived,
    subscriptions: ['word']
  })
  const hasShownLoading = localStorage.getItem('hasShownLoading')
  console.log('누구한테나 떠라', hasShownLoading)
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
  <!-- <div class="header">
      공통 컴포넌트인 헤더 넣어야됨
  </div> -->
  <v-dialog v-model="showLoading" persistent max-width="1200">
    <ContentsLoading :contentsInfo="contentsInfo" :time="'5'" :countText="'초 후에 시작합니다!'" />
  </v-dialog>
  <div class="container">
    <WaitingHeader
      first-description="초성 맞추기"
      second-description="초성을 토대로 단어를 맞춰보세요!"
      third-description="순서는 랜덤이랍니다!"
    />
    <div class="statusContainer">
      <div class="progress-bar">
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
          <strong style="color: #000000"
            >{{ store.submitUserCount }} / {{ store.totalUserCount }}</strong
          >
        </v-progress-linear>
      </div>

      <div class="info">
        <div class="info-category">카테고리</div>
        <div class="info-text">{{ categoryName['data']['result']['categoryName'] }}</div>
      </div>
    </div>
    <div class="playContainer">
      <div>
        <v-alert
          title="초성 게임!"
          text="입력 창을 모두 채워주세요."
          type="warning"
          v-if="showAlert.blank"
          class="warning-alert"
        />
        <v-alert
          title="초성 게임!"
          text="온전한 한국어 단어로 적어주세요."
          type="warning"
          v-if="showAlert.korean"
          class="warning-alert"
        />
      </div>
      <div class="userInput" :style="{ backgroundColor: isDisabled ? '#d3d3d3' : '#fff' }">
        <div class="emojiField"></div>
        <input
          v-html="quizWord"
          class="inputText"
          :placeholder="isDisabled ? '작성 완료' : '카테고리에 관한 입력을 해주세요!'"
          v-model="quizWord"
          @keyup.enter="submitQuizWord()"
          :disabled="isDisabled"
        />
      </div>
      <button class="submit" @click="submitQuizWord()">제출하기</button>
    </div>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  background-color: #e7ffde;
  height: 100%;
}
.header {
  margin: 20px auto auto;
  width: 1856px;
  height: 94px;
  background-color: blueviolet;
}

.statusContainer {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.progress-bar {
  width: 1000px;
  height: 69px;
}

.info {
  display: flex;
  width: 600px;
  height: 60px;
  border-radius: 5px;
  margin-top: 20px;
}
.info-category {
  margin: 0;
  height: 60px;
  width: 600px;
  border-radius: 10px 0 0 10px;
  background-color: #ceffbc;
  display: flex;
  justify-content: center;
  align-items: center;
}
.info-text {
  margin: 0;
  height: 60px;
  width: 600px;
  border-radius: 0 10px 10px 0;
  background-color: #00ff00;
  display: flex;
  justify-content: center;
  align-items: center;
}
.playContainer {
  margin-top: 10px;
  height: 650px;
  display: flex;
  flex-direction: column;
  align-items: center; /* 수직으로 가운데 정렬 */
  /* flex: 1; */
  justify-content: space-around; /* 수평으로 가운데 정렬 */
}

.userInput {
  width: 571px;
  height: 134px;
  border-radius: 20px;
  background-color: #fff;
  border: 2px solid black;
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 3px;
}
.emojiField {
  width: 60px;
  height: 60px;
  /* background-color: #1F4F16; */
  background-image: url('../../../../src/assets/image/smile_face.svg');
}
.inputText {
  flex: 1;
  height: 60px;
  outline: none; /* 포커스 시 나타나는 기본 테두리 제거 */
  border: none; /* 기본 테두리 제거 */
  background-color: transparent; /* 배경색 투명하게 설정 */
  font-size: 16px; /* 원하는 폰트 크기로 조정 */
  padding: 10px; /* 내부 여백 추가 */
}

/* 선택적: WebKit 브라우저(Chrome, Safari 등)의 자동 채우기 스타일 제거 */
.inputText:-webkit-autofill,
.inputText:-webkit-autofill:hover,
.inputText:-webkit-autofill:focus,
.inputText:-webkit-autofill:active {
  transition: background-color 5000s ease-in-out 0s;
  -webkit-text-fill-color: inherit !important;
}

.submit {
  width: 682px;
  height: 94px;
  background-color: #1f4f16;
  border-radius: 20px;
  color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
}

.warning-alert {
  width: 100%;
}
</style>
