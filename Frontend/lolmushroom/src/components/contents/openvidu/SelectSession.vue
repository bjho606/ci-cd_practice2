<template>
  <div id="join-dialog" class="jumbotron vertical-center">
    <h1>비디오 세션에 참여하기</h1>
    <div class="form-group">
      <p>
        <label>참가자</label>
        <input v-model="myUserName" class="form-control" type="text" required />
      </p>
      <p>
        <label>세션</label>
        <input v-model="mySessionId" class="form-control" type="text" required />
      </p>
      <p class="text-center">
        <button class="btn btn-lg btn-success" @click="joinSession()">
          참여!
        </button>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, defineEmits } from 'vue';

const mySessionId = ref('SessionA');
// 변경 요청이 오면 유저 닉네임을 수정해야 함
const myUserName = ref('Participant' + Math.floor(Math.random() * 100));

const emit = defineEmits(['join']);

const joinSession = () => {
  emit('join', { sessionId: mySessionId.value, userName: myUserName.value });
};
</script>

<style scoped>
/* 스타일을 필요에 따라 추가하세요 */
</style>
