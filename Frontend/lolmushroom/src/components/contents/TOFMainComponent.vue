<script setup>
  import { onMounted, ref, watch } from 'vue';
  import { useUserStore } from '@/stores/User';
  import { useSessionStore } from '@/stores/session';
  import { useRoomStore } from '@/stores/roomStore'
  import sessionAPI from '@/api/session';
  import contentsAPI from '@/api/contents';
  import ButtonComponent from '@/components/common/ButtonComponent.vue';
  import ProgressBar from '@/components/common/ProgressBar.vue';
  import OpenViduComponent from '@/components/contents/openvidu/OpenViduComponent.vue';
  import CountDownComponent from '@/components/common/CountDownComponent.vue';
  import TOFResultComponent from '@/components/contents/TOFResultComponent.vue'
  import TOFSideUserComponent from '@/components/contents/TOFSideUserComponent.vue';
  import ResultOverlayComponent from '@/components/common/ResultOverlayComponent.vue';
  import RoomWaiting from '@/components/room/RoomWaiting.vue';

  const store = useUserStore()
  const sessionStore = useSessionStore()

  const userNames = []
  const userCards = []
  const firstHalf = ref([]);
  const secondHalf = ref([]);

  // 서버에서 세션에 참여한 유저 정보 가져와 수정해야 함
  const targetUser = ref(userNames[0])
  const allStatements = ref('')
  const totalUserCount = ref()

  // 이것도 다른 유저의 진술 가져와서 렌더링하게 로직 수정해야 함
  const selectedAnswer = ref(null);
  const isSubmitAnswer = ref(false);
  const areSubmitAnswer = ref(false)

  const isTimeUp = ref('')
  
  // 세션에 참가한 유저 정보를 요청하는 함수
  async function getSubSessionInfo(sessionId, subSessionId) {
    const response = await sessionAPI.getSubSessionInfo(sessionId, subSessionId);
    return response.data;
  }

  // 세션의 참가한 전체 유저의 모든 진술을 가져오는 함수
  async function getStatements() {
    const response = await contentsAPI.getStatements(store.subSessionId)
    return response.data
  }


  // 구조 분해 할당으로 배열을 섞는 함수
  const arrayShuffle = (array) => {
    for (let i = array.length - 1; i > 0; i--) {
      // 무작위로 index 값 생성 (0 이상 i 미만)
      let j = Math.floor(Math.random() * (i + 1));
      [array[i], array[j]] = [array[j], array[i]];
    }
    return array
  }

  // 선택한 버튼 시각적으로 활성화하는 함수
  const buttonActivate = (key) => {
    selectedAnswer.value = key;
  };

  // 답변을 제출했다면 제출 현황을 확인
  const submitAnswer = (i) => {
    // 여기서 선택 결과를 보냄
    isSubmitAnswer.value = true
  }

  // 10초의 시간이 지나면 카메라와 질문이 공개됨
  const timeUp = (bool) => {
    isTimeUp.value = bool
  }
  
  // 타겟 유저가 바뀌면(송출되는 화면이 갱신되면) 해당 유저에게 테두리를 씌움
  // 타겟 유저의 진술로 전환함
  const targetUserUpdate = () => {
    console.log('추가해야 함')
  };

  // 유저 정보를 가져오는 코드
  const fetchUserData = async () => {
    const sessionUserNameList = await getSubSessionInfo(sessionStore.sessionId, sessionStore.subSessionId);
    totalUserCount.value = sessionUserNameList['data']['result']['currentUserCount'];
    sessionUserNameList.result.username.forEach(name => {
      userNames.push(name);
      userCards.push({
        name,
        src: '../../../public/favicon.ico',
      });
    })
    targetUser.value = userNames[0]

    // 랜덤으로 배열 순서를 조작하는 로직 추가 필요 
    firstHalf.value = userCards.slice(0, Math.ceil(userCards.length / 2));
    secondHalf.value = userCards.slice(Math.ceil(userCards.length / 2));
  }
  
  // 사람이 추가되면 유저 카드를 갱신
  // watch(() => store.userTOFstatements.value, (newVal) => {
  //   if (newVal) {
  //     fetchUserData();
  //   }
  // });
  
  onMounted (async () => {
    fetchUserData()
    allStatements.value = await getStatements()
    console.log(allStatements.value)
  })
</script>

<template>
  <!-- 진실과 거짓을 가려내는 토론 화면 컴포넌트 -->
   <!-- countdown이 마무리 되면 -->
  <v-container v-if="isTimeUp" class="container">
    <v-row justify="space-between">

      <v-col>
        <TOFSideUserComponent
          :users="firstHalf"
          :target="targetUser"
        />
      </v-col>
      <v-col md="6" style="text-align: center">
        <OpenViduComponent
          @update:myUserName="sessionStore.subSessionId"
          :session-id="getSubSessionInfo"
          :user-name="userNames"
        />
        <!-- 이 버튼을 누르면 기존 발표자의 발표 종료 -->
         <!-- 처음 들어오면 바로 랜덤으로 발표자 순서를 정해놓아야 하는데  -->
          <!-- 그냥 username순으로 할까? -->
         <!-- 이 버튼은 내가 발표자로 활성화 되어 있을 때만 적용 -->
          <!-- 1. 다음 발표자 캠을 mainmanager로 변경 -->
           <!-- 2. ovtoken을 통해 사용자 진술 가져오기 -> 렌더링 -->
          <!-- 3. 발표자 테두리 변경 -->
           <!-- 사용자 지정은 index로 하고, 발표자 테두리만 ovtoken으로 변경 -->
        <v-container v-if="targetUser !== store.ovToken">
          <CountDownComponent
            time="30"
            @end-count-down="targetUserUpdate()"
          />
          <ButtonComponent
            text="발표 종료"
            @click="targetUserUpdate()"
          />
        </v-container>

        <!-- <ResultOverlayComponent /> -->
        <!-- 답안 제출 전 -->
        <v-card
          v-if="!isSubmitAnswer"
          class="mt-5"
        >
          <v-card-title class='question-font' style="overflow: flip;">
          <pre>{{ targetUser }}에 대한 설명이다. 다음 중 틀린 설명은?</pre>
          </v-card-title>
          <v-card-text
            v-for="(statement, i) in allStatements"
            :key="i"
            @click="buttonActivate(i + 1)"
          > 
            <v-icon v-show="selectedAnswer != i + 1" :icon="`mdi-numeric-${i + 1}-circle-outline`" />
            <v-icon v-show="selectedAnswer === i + 1" :icon="`mdi-numeric-${i + 1}-circle`" color="red"/>
            {{ statement }}
          </v-card-text>
          <div class="button-container" v-if="selectedAnswer" @click="submitAnswer(selectedAnswer)">
            <ButtonComponent text="선택하기" size="large"/>
          </div>
        </v-card>
        
        <!-- 답안 제출 완료 -->
        <v-card
          v-else-if="isSubmitAnswer && !areSubmitAnswer"
          class="mt-5"
          width="500"
        >
          <ProgressBar
            :total="totalUserCount"
          />
          <!-- <RoomWaiting /> -->
        </v-card>

        <!-- 전체 제출 완료 -->
        <v-card
        v-else
          class="mt-5"
          width="500"
        >
        <!-- props 추가 -->
          <TOFResultComponent
            target-nick-name=""
            :statements="allStatements"
            choice-answer-matrix=""
          />
        </v-card>
      </v-col>
      
      <!-- 참여자 아이콘 닉네임 렌더링 -->
      <v-col>
        <TOFSideUserComponent
          :users="secondHalf"
          :target="targetUser"
        />
      </v-col>
    </v-row>
  </v-container>

  <!-- 카운트 다운 컴포넌트 -->
  <v-container class="countdown-container" v-else >
    <div class="countdown-timer">
      <CountDownComponent
      time="2.5"
      text="잠시 숨을 고르고 기다려주시기 바랍니다."
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

.highlighted {
  border: 2px solid red;
}

.text-black {
  font-size: xx-small;
}

.container {
  max-height: 80%;
}

.countdown-container {
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
