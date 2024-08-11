<script setup>
  import { onMounted, ref, watch } from 'vue';
  import { useUserStore } from '@/stores/userStore';
  import { useSessionStore } from '@/stores/sessionStore';
  import { useTOFStore } from '@/stores/tofStore'
  import { useRouter } from 'vue-router'
  import sessionAPI from '@/api/session';
  import contentsAPI from '@/api/contents';
  import webSocketAPI from '@/api/webSocket';
  import ButtonComponent from '@/components/common/ButtonComponent.vue';
  import ProgressBar from '@/components/common/OtherUserWaitingComponent.vue';
  import OpenViduComponent from '@/components/contents/tof/openvidu/OpenViduComponent.vue';
  import CountDownComponent from '@/components/common/CountDownComponent.vue';
  import TOFResultComponent from '@/components/contents/tof/TOFResultComponent.vue'
  import TOFSideUserComponent from '@/components/contents/tof/TOFSideUserComponent.vue';
  // import ResultOverlayComponent from '@/components/common/ResultOverlayComponent.vue';

  const store = useTOFStore()
  const router = useRouter()
  const userStore = useUserStore()
  const sessionStore = useSessionStore()
  const counting = ref(true)
  const index = ref(0)
  const sessionUserNameList = await getSubSessionInfo(sessionStore.sessionId, sessionStore.subSessionId);

  const allStatements = ref('')
  allStatements.value = await getStatements()
  console.log(allStatements.value)
  store.targetUserToken = ref(allStatements.value[index.value].ovToken)
  store.statements = allStatements.value[index.value].statements
  const answer = ref(allStatements.value[index.value]['falseIndex'] + 1)

  const userChoice = []
  const userCards = []
  const firstHalf = ref([]);
  const secondHalf = ref([]);

  sessionUserNameList.result.username.forEach(name => {
      userChoice.push(name);
      // 여기에 ovtoken을 꼽아서 발표 진행 때 highrighted 효과 부여할 수 있게 해야 한다.
      userCards.push({
        name,
        src: 'public/favicon.ico',
      });
      // 랜덤으로 배열 순서를 조작하는 로직 추가 필요 
      firstHalf.value = userCards.slice(0, Math.ceil(userCards.length / 2))
      secondHalf.value = userCards.slice(Math.ceil(userCards.length / 2))
    })

  // 이것도 다른 유저의 진술 가져와서 렌더링하게 로직 수정해야 함
  const selectedAnswer = ref();
  const isSubmitAnswer = ref();
  const areSubmitAnswer = ref()
  const isTimeUp = ref()
  
  // 세션에 참가한 유저 정보를 요청하는 함수
  async function getSubSessionInfo(sessionId, subSessionId) {
    const response = await sessionAPI.getSubSessionInfo(sessionId, subSessionId);
    return response.data;
  }

  // 세션의 참가한 전체 유저의 모든 진술을 가져오는 함수
  async function getStatements() {
    const response = await contentsAPI.getStatements(sessionStore.subSessionId)
    return response.data.result.allTFInfos
  }

  // 답변을 제출했다면 제출 현황을 확인
  const submitAnswer = (selected) => {
    const data = {
      ovToken: userStore.userOvToken,
      chosen: selected,
    }
    isSubmitAnswer.value = true
    webSocketAPI.sendAnswerData(`/publish/game/tf/answer/${sessionStore.subSessionId}`, data)
  }

  // 10초의 시간이 지나면 카메라와 질문이 공개됨
  const timeUp = (bool) => {
    isTimeUp.value = bool
    counting.value = !bool
  }

  // 선택한 버튼 시각적으로 활성화하는 함수
  const buttonActivate = (key) => {
  selectedAnswer.value = key;
  };

  const targetUserUpdate = () => {
  // 현재 finish 소켓 사용 불가
  //   if (index.value < store.totalUserCount - 1) {
      webSocketAPI.sendNextData(`/publish/game/tf/next/${sessionStore.subSessionId}`, true)
  //   } else {
  //     const data = {
  //       sessionId: sessionStore.subSessionId,
  //     }
  //     webSocketAPI.sendFinishData('/publish/game/tf/finish', data)
  //   }
  }
  
  // subscribe하면 받는다.
  const onAnswerReceived = (event) => {
    const { ovToken, chosen, userName } = event
    store.submitUserIncrease()
    store.addChosenData(userName, chosen)
    if (store.submitUserCount === store.totalUserCount - 1) {
      areSubmitAnswer.value = true
    }
  }

  const onNextReceived = (_event) => {
    if (index.value < store.totalUserCount - 1) {
      selectedAnswer.value = null;
      isSubmitAnswer.value = false;
      areSubmitAnswer.value = false;
      store.submitUserCount = 0
      index.value++
      timeUp('')
    } else {
      router.push({
        name: "roomwaiting",
        params: 
        {
          sessionId: sessionStore.sessionId,
          subSessionId: sessionStore.subSessionId,
        }
      })
    }
  }

  const onEndReceived = (event) => {
    console.log('완료')
    // const { sessionId } = event
    // router.push({ name: 'roomwaiting', params: { sessionId } });
  }


  // index 값이 증가할 때마다 관련 값 갱신
  watch(index, (newIndex, oldIndex) => {
    if (newIndex < store.totalUserCount) {
      store.targetUserToken = allStatements.value[newIndex]['ovToken'];
      store.statements = allStatements.value[newIndex].statements
      store.chosenArray = {
        "1": [],
        "2": [],
        "3": [],
        "4": [],
      }
    }
  });

  onMounted(async () => {
    console.log('연결 좀...')
    webSocketAPI.connect({
          sessionId: sessionStore.subSessionId,
          contentsName: 'tf',
          onEventReceived: onAnswerReceived,
          onNextReceived: onNextReceived,
          onEndReceived: onEndReceived,
          subscriptions: ['answer', 'next', 'end']
        })
  })
</script>

<template>
  <!-- 진실과 거짓을 가려내는 토론 화면 컴포넌트 -->
  <!-- countdown이 마무리 되면 -->
  <v-container v-show="isTimeUp" class="container">
    <v-row justify="space-between">
      <v-col>
        <TOFSideUserComponent :users="firstHalf" :target="store.targetUserToken" />
      </v-col>
      
      <v-col md="6" style="text-align: center">
        <OpenViduComponent />
        <v-container v-if="store.targetUserToken === userStore.userOvToken">
          <v-btn text="발표 종료" @click.stop="targetUserUpdate()" />
        </v-container>

        <!-- <ResultOverlayComponent /> -->
        <!-- 답안 제출 전 -->
        <v-card
          v-if="!isSubmitAnswer && store.targetUserToken !== userStore.userOvToken"
          class="mt-5"
        >
          <v-card-title class='question-font' style="overflow: flip;">
          <pre>{{ allStatements[index].userName }}에 대한 설명이다. 다음 중 틀린 설명은?</pre>
          </v-card-title>
          <div
            v-for="(statement, i) in allStatements[index].statements"
            :key="i"
            @click="buttonActivate(i + 1)"
          >
            <v-card-text>
              <p>
                <v-icon v-show="selectedAnswer != i + 1" :icon="`mdi-numeric-${i + 1}-circle-outline`" />
                <v-icon v-show="selectedAnswer === i + 1" :icon="`mdi-numeric-${i + 1}-circle`" color="red"/>
                {{ statement }}
              </p>
            </v-card-text>
          </div>
          <div class="button-container" v-if="selectedAnswer" @click="submitAnswer(selectedAnswer)">
            <ButtonComponent text="선택하기" size="large"/>
          </div>
        </v-card>

        <!-- 답안 제출 완료 -->
        <v-card
          v-else-if="isSubmitAnswer && !areSubmitAnswer && store.targetUserToken !== userStore.userOvToken"
          class="mt-5"
          width="500"
        >
          <ProgressBar
            :current="store.submitUserCount" 
            :total="store.totalUserCount - 1"
          />
          <!-- <RoomWaiting /> -->
        </v-card>

        <!-- 전체 제출 완료 -->
        <v-card v-else class="mt-5" width="500">
          <TOFResultComponent
            :target-nick-name="allStatements[index].username"
            :answer="answer"
          />
        </v-card>
      </v-col>

      <!-- 참여자 아이콘 닉네임 렌더링 -->
      <v-col>
        <TOFSideUserComponent :users="secondHalf" :target="store.targetUserToken" />
      </v-col>
    </v-row>
  </v-container>

  <!-- 카운트 다운 컴포넌트 -->
  <v-container class="countdown-container" v-show="!isTimeUp">
    <div class="countdown-timer">
      <CountDownComponent
        v-if="counting"
        time="10"
        text="발표자는 누가 될까요? 두근두근."
        @end-count-down="timeUp(true)"
      />
    </div>
  </v-container>
</template>

<style scoped>
.button-container {
  display: flex;
  justify-content: flex-end;
}

.card-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.text-black {
  font-size: xx-small;
}

.container {
  max-height: 80%;
}

.countdown-container {
  background: #247719;
  opacity: 0.7;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  text-align: center;
}

.countdown-timer {
  margin-bottom: 20px;
}
</style>
