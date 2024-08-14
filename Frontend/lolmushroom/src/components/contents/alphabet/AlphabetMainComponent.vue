<script setup>
  import { onMounted, ref, computed } from 'vue';
  import { useSessionStore } from '@/stores/sessionStore';
  import { useAlphabetStore } from '@/stores/alphabetStore';
  import contentsAPI from '@/api/contents';
  import webSocketAPI from '@/api/webSocket';
  import CountDownComponent from '@/components/common/CountDownComponent.vue'

  const store = useAlphabetStore()
  const sessionStore = useSessionStore()
  const isTimeUp = ref()
  const counting = ref(true)
  const showAlert = ref()
  const index = ref(0)
  const guessWord = ref('')
  const guessWords = ref([])
  const areSubmitAnswer = computed(() => store.submitUserCount === store.totalUserCount)

  // 카운트 다운이 종료되면 Main화면을 렌더링하는 함수
  const timeUp = (bool) => {
    isTimeUp.value = bool
    counting.value = !bool
  }

  // 모든 문제를 가져옴
  const quizWords = await contentsAPI.getQuizWords(sessionStore.subSessionId)
  const alliIniQuizInfos = quizWords['data']['result']['alliIniQuizInfos']

  // 정답을 발행
  const publishAnswer = () => {
    if (guessWord.value === '') {
      showAlert.value = true
    } else {
      const data = {
        guessWord: guessWord.value
      }
      webSocketAPI.sendAnswerData(`/publish/game/ini-quiz/guess/${sessionStore.sessionId}/${sessionStore.subSessionId}`, data)
      guessWord.value = ''
    }
  }

  // 다른 사용자의 정답을 구독
  const onAnswerReceived = (event) => {
    const { ovToken, result, submittedWord, userName } = event
    const wordData = {
      word: submittedWord,
      top: Math.random(),
      left: Math.random()
    }
    guessWords.value.push(wordData)
    
    // 만약 정답이 나오면
    if (result) {
      
    }
  }


  onMounted(async () => {
    console.log('초성 게임 연결 중..')
    // 세션 연결
    webSocketAPI.connect({
          sessionId: sessionStore.sessionId,
          subSessionId: sessionStore.subSessionId,
          onEventReceived: onAnswerReceived,
          subscriptions: ['guess']
        })
  })

</script>

<template>
  <!-- <div class="header">
      공통 컴포넌트인 헤더 넣어야됨
  </div> -->
  <div class="container" v-show="isTimeUp">
    <div class="statusContainer">
      <div class="info">
        <div class="info-category">카테고리</div>
        <div class="info-text">{{ alliIniQuizInfos[index]['categoryName'] }}</div>
      </div>
    </div>
    <div class="playContainer">
      <div class="userInput">
        <div class="emojiField"></div>
        <div div :guessWord class="initialBox">{{ alliIniQuizInfos[index]['quizWord'] }}</div>
      </div>
      <div class="userInput">
        <div class="emojiField"></div>
        <input v-html="guessWord" class="inputText" placeholder="카테고리에 관한 입력을 해주세요!" v-model="guessWord" @keyup.enter="publishAnswer()">
      </div>
      <div>
        <v-alert title="초성 게임!" text="입력 창을 모두 채워주세요." type="warning" v-if="showAlert" class="warning-alert"/>
      </div>
      <button class="submit" @click="publishAnswer()">
            정답 맞추기
      </button>
      <!-- 침여자의 답변 렌더링 -->
      <div v-for="wordData in guessWords" :key="wordData.word"
           class="moving-word"
           :style="{ '--random-top': wordData.top, '--random-left': wordData.left }">
        <h1>{{ wordData.word }}</h1>
      </div>
    </div>
  </div>

  <v-container class="countdown-container" v-show="!isTimeUp">
    <div class="countdown-timer">
      <CountDownComponent v-if="counting" time="2.5" text="집중해서 초성을 맞춰보세요!" @end-count-down="timeUp(true)"/>
    </div>
    
  </v-container>

  <v-container v-show="areSubmitAnswer">
    <p>1</p>
  </v-container>
</template>

<style scoped>
.container{
  display: flex;
  flex-direction: column;
  background-color: #E7FFDE;
  height: 100%;
}
.header{
  margin: 20px auto auto ;
  width: 1856px;
  height: 94px;
  background-color: blueviolet;
}

.statusContainer{
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.progress-bar{
  width: 1000px;
  height: 69px;
  background-color: yellow;
}

.info{
  display: flex;
  width: 600px;
  height: 60px;
  border-radius: 5px;
  margin-top: 80px;
}
.info-category{
  margin: 0;
  height: 60px;
  width: 600px;
  border-radius: 10px 0 0 10px;
  background-color: #CEFFBC;
  display: flex;
  justify-content: center;
  align-items: center;

}
.info-text{
  margin: 0;
  height: 60px;
  width: 600px;
  border-radius: 0 10px 10px 0;
  background-color: #00FF00;
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

.userInput{
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
.emojiField{
  width: 60px;
  height: 60px;
  background-color: #1F4F16;
}
.initialBox {
  flex: 1;
  height: 120px;
  outline: none; /* 포커스 시 나타나는 기본 테두리 제거 */
  border: none; /* 기본 테두리 제거 */
  background-color: transparent; /* 배경색 투명하게 설정 */
  font-size: 64px; /* 원하는 폰트 크기로 조정 */
  font-weight: bold;
  padding: 10px; /* 내부 여백 추가 */
  text-align: center;
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

.submit{
  font-size: 32px;
  width: 292px;
  height: 94px;
  background-color: #24A319;
  border-radius: 20px;
  color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
}

.warning-alert {
  width: 100%;
}

/* css 애니메이션 정의 */
.moving-word {
  position: absolute;
  animation: moveWord 5s infinite alternate ease-in-out;
}

@keyframes moveWord {
  0% {
    top: 10%;
    left: 10%;
  }
  100% {
    top: calc(90% * var(--random-top));
    left: calc(90% * var(--random-left));
  }
}


</style>
