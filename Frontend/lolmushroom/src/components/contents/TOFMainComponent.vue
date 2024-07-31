<script setup>
  import { ref } from 'vue';
  import { useUserStore } from '@/stores/User';
  import ButtonComponent from '@/components/common/ButtonComponent.vue';
  import ProgressBar from '@/components/common/ProgressBar.vue';
  import OpenViduComponent from '@/components/contents/openvidu/OpenViduComponent.vue';
  import CountDownComponent from '@/components/common/CountDownComponent.vue';
  import ResultOverlayComponent from '@/components/common/ResultOverlayComponent.vue';
  import TOFResultComponent from '@/components/contents/TOFResultComponent.vue'

  const store = useUserStore()

  // 서버에서 세션에 참여한 유저 정보 가져와 수정해야 함
  const targetUser = ref('나이스한 강아지')
  const allStatements = store.userTOFstatements.states

  // 이것도 다른 유저의 진술 가져와서 렌더링하게 로직 수정해야 함
  const statements = [allStatements.trueStates[0], allStatements.trueStates[1], allStatements.trueStates[2], allStatements.falseState]
  const selectedAnswer = ref(null);
  const isSubmitAnswer = ref(false);
  const areSubmitAnswer = ref(true)
  const isTimeUp = ref('')
  // 모든 사람이 완료했다는 결과를 받으면 값 변경
  const isAllReady = ref()

  const userCards = [
    {
      name: '나이스한 강아지',
      src: '../../public/favicon.ico',
    },
    {
      name: '재빠른 날다람쥐',
      src: '../../public/favicon.ico',
    },
    {
      name: '영민한 뱅갈호랑이',
      src: '../../public/favicon.ico',
    },
    {
      name: '얍삽한 치타',
      src: '../../public/favicon.ico',
    },
    {
      name: '여유로운 오징어',
      src: '../../public/favicon.ico',
    },
    {
      name: '거대한 사막여우',
      src: '../../public/favicon.ico',
    },
  ];

  // 랜덤으로 배열을 만드는 로직 추가로 필요할 듯
  const firstHalf = userCards.slice(0, Math.ceil(userCards.length / 2));
  const secondHalf = userCards.slice(Math.ceil(userCards.length / 2));

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
</script>

<template>
  <!-- 진실과 거짓을 가려내는 토론 화면 컴포넌트 -->
  <v-container v-if="isTimeUp">

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
          @update:myUserName="targetUserUpdate"
        />
        <ResultOverlayComponent />
        <!-- 답안 제출 전 -->
        <v-card
          class="mt-5"
          v-if="!isSubmitAnswer"
        >
          <v-card-title class='question-font'>
          <pre>{{ targetUser }}에 대한 설명이다.
  다음 중 틀린 설명은?</pre>
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
        >
          <ProgressBar />
        </v-card>

        <v-card
          class="mt-5"
          v-else
        >
          <TOFResultComponent />
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
      time="10"
      text1="뒤 시작합니다."
      text2="잠시만 기다려주시기 바랍니다."
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
</style>
