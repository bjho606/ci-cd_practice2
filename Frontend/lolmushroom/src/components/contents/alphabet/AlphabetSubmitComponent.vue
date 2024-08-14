<script setup>
  import { onMounted, ref, watch } from 'vue';
  import { useRouter } from 'vue-router' 
  import { useSessionStore } from '@/stores/sessionStore'
  import { useAlphabetStore } from '@/stores/alphabetStore'
  import { useUserStore } from '@/stores/userStore';
  import sessionAPI from '@/api/session'
  import contentsAPI from '@/api/contents';

  const router = useRouter()
  const store = useAlphabetStore()
  const sessionStore = useSessionStore()
  const userStore = useUserStore()
  const quizWord = ref('');
  const showAlter = ref('')

  // 세션에 참가한 유저 정보를 요청하는 함수
  const response = await sessionAPI.getSubSessionInfo(sessionStore.sessionId, sessionStore.subSessionId)
  store.setTotalUser(response['data']['result']['currentUserCount'])
  console.log('총 인원수', store.totalUserCount)

  const categoryName = await contentsAPI.getCategory(sessionStore.subSessionId)
  console.log('내 퀴즈 카테고리는: ', categoryName)

  // 퀴즈를 제출하는 함수
  const submitQuizWord = async () => {
    if (quizWord.value === '') {
      showAlter.value = true; return
    } else {
      showAlter.value = false
      const object = {
        ovToken: userStore.ovToken,
        categoryName: categoryName['data']['result']['categoryName'],
        quizWord: quizWord.value,
      }
      const response = await contentsAPI.createQuizWord(sessionStore.sessionId, sessionStore.subSessionId, object)
      console.log(response)
      if (response['data']['result']['created'] === true) {
      store.submitUserIncrease()
      quizWord.value = ''
      // 제출 인원과 총 인원 수 계산 하는 함수 추가 -> 구독자들도 화랑님한테 물어봐서 로직 추가
      if (store.submitUserCount === store.totalUserCount) {
        router.push('alphabetContent')
      }
      }
    }
  }


  
</script>

<template>
  <!-- <div class="header">
      공통 컴포넌트인 헤더 넣어야됨
  </div> -->
<div class="container">
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
      <strong style="color: #000000;">{{ store.submitUserCount }} / {{ store.totalUserCount }}</strong>
      </v-progress-linear>
    </div>

    <div class="info">
      <div class="info-category">카테고리</div>
      <div class="info-text">{{ categoryName['data']['result']['categoryName'] }}</div>
    </div>
  </div>
  <div class="playContainer">
    <div>
      <v-alert title="초성 게임!" text="입력 창을 모두 채워주세요." type="warning" v-if="showAlter" class="warning-alert"/>
    </div>
    <div class="userInput">
      <div class="emojiField"></div><input v-html="quizWord" class="inputText" placeholder="카테고리에 관한 입력을 해주세요 !" v-model="quizWord" @keyup.enter="submitQuizWord()"/>
    </div>
    <button class="submit" @click="submitQuizWord()">
          제출하기
    </button>
  </div>
</div>
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
}

.info{
  display: flex;
  width: 600px;
  height: 60px;
  border-radius: 5px;
  margin-top: 20px;
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
  width: 682px;
  height: 94px;
  background-color: #1F4F16;
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
