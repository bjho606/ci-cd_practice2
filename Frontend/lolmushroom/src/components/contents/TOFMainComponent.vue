<script setup>
  import { onMounted, ref, watch } from 'vue';
  import { useUserStore } from '@/stores/User';
  import { useSessionStore } from '@/stores/session';
  import { useRoomStore } from '@/stores/roomStore'
  import ButtonComponent from '@/components/common/ButtonComponent.vue';
  import ProgressBar from '@/components/common/ProgressBar.vue';
  import OpenViduComponent from '@/components/contents/openvidu/OpenViduComponent.vue';
  import CountDownComponent from '@/components/common/CountDownComponent.vue';
  import ResultOverlayComponent from '@/components/common/ResultOverlayComponent.vue';
  import TOFResultComponent from '@/components/contents/TOFResultComponent.vue'
  import RoomWaiting from '@/components/room/RoomWaiting.vue';
  import sessionAPI from '@/api/session';

  const store = useUserStore()
  const sessionStore = useSessionStore()
  const roomStore = useRoomStore()

  const userNames = []
  const userCards = []
  const firstHalf = ref([]);
  const secondHalf = ref([]);

  // 서버에서 세션에 참여한 유저 정보 가져와 수정해야 함
  const targetUser = ref(userNames)
  const allStatements = store.userTOFstatements.states
  
  // 이것도 다른 유저의 진술 가져와서 렌더링하게 로직 수정해야 함
  const statements = [allStatements.trueStates[0], allStatements.trueStates[1], allStatements.trueStates[2], allStatements.falseState]
  const selectedAnswer = ref(null);
  const isSubmitAnswer = ref(false);
  const areSubmitAnswer = ref(true)
  const isTimeUp = ref('')
  const totalParticipants = ref()

  // 세션에 참가한 유저 정보를 요청하는 함수
  const getSubSessionInfo = async (sessionId, subSessionId) => {
      const response = await sessionAPI.getSubSessionInfo(sessionId, subSessionId);
      return response.data;
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
  const targetUserUpdate = (nextUser) => {
    targetUser.value = nextUser;
  };

  // 유저 정보를 가져오는 코드
  const fetchUserData = async () => {
    const sessionUserNameList = await getSubSessionInfo(sessionStore.sessionId, sessionStore.subSessionId);
    totalParticipants.value = sessionUserNameList.result.username.length;
    sessionUserNameList.result.username.forEach(element => {
      userNames.push(element);
      userCards.push({
        name: element,
        src: '../../../public/favicon.ico',
      });
    })
    targetUser.value = userNames[0]
    // 랜덤으로 배열 순서를 조작하는 로직 추가 필요 
    firstHalf.value = userCards.slice(0, Math.ceil(userCards.length / 2));
    secondHalf.value = userCards.slice(Math.ceil(userCards.length / 2));
  }
  
  // 사람이 추가되면 유저 카드를 갱신
  watch(() => store.userTOFstatements.value, (newVal) => {
    if (newVal) {
      fetchUserData();
    }
  });

  const handlePublish = () => {
    fetchUserData()
  }
</script>

<template>
  <!-- 진실과 거짓을 가려내는 토론 화면 컴포넌트 -->
  <v-container v-if="isTimeUp" class="container">

    <v-row justify="space-between">
      <v-col>
        <v-container
          v-for="(avatar, i) in firstHalf"
          :key="i"
          class="card-container"
          :class="{ highlighted: avatar.name === targetUser }"
        >
          <v-avatar
            class="my-3"  
            size="60"
          >
            <v-img
              :src="avatar.src"
              cover
            />
          </v-avatar>
          <v-list-item
            class="text-black"
            :title="avatar.name"
          >
          </v-list-item>
        </v-container>
          
      </v-col>
      <v-col md="6" style="text-align: center">
        
        <OpenViduComponent
          @update:myUserName="sessionStore.subSessionId"
          :session-id="getSubSessionInfo"
          :user-name="userNames"
          @publish="handlePublish"
        />
        <ResultOverlayComponent />
        <!-- 답안 제출 전 -->
        <v-card
          class="mt-5"
          v-if="!isSubmitAnswer"
        >
          <v-card-title class='question-font' style="overflow: flip;">
          <pre>{{ targetUser }}에 대한 설명이다. 다음 중 틀린 설명은?</pre>
          </v-card-title>
          <v-card-text
            v-for="(statement, i) in statements"
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
          class="mt-5"
          v-else-if="isSubmitAnswer && !areSubmitAnswer"
          width="500"
        >
          <ProgressBar
            :total="totalParticipants"
          />
          <!-- <RoomWaiting /> -->
        </v-card>

        <v-card
          class="mt-5"
          v-else
          width="500"
        >
        <!-- props 추가 -->
          <TOFResultComponent
/>
        </v-card>
      </v-col>
      
      <!-- 참여자 아이콘 닉네임 렌더링 -->
      <v-col>
        <v-container
          v-for="(avatar, i) in secondHalf"
          :key="i"
          class="card-container"
          :class="{ highlighted: avatar.name === targetUser }"
        >
          <v-avatar
            class="my-3"  
            size="60"
          >
            <v-img
              :src="avatar.src"
              cover
            />
          </v-avatar>
          <v-list-item
            class="text-black"
            :title="avatar.name"
          >
          </v-list-item>
        </v-container>
      </v-col>
    </v-row>
  </v-container>

  <!-- 카운트 다운 컴포넌트 -->
  <v-container v-else>
    <CountDownComponent
      time="2.5"
      text="잠시 숨을 고르고 기다려주시기 바랍니다."
      @end-count-down="timeUp(true)"
    />
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
</style>
