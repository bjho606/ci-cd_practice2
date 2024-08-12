<script setup>
import { onMounted, ref, computed } from 'vue';
import { useRoomStore } from '@/stores/roomStore';
import waitingHeader from './waitingHeader.vue';
import setNickname from './setNickname.vue';
import PlayerWaitingGround from './PlayerWaitingGround.vue';

// onMounted 될 시 닉네임 만들기 창 열리게 작동
// 재사용성을 높이는 헤더 소개 

const text1= "우리들의 오리엔테이션 세션"
const text2 = computed(() => `현재 인원 ${roomStore.getTotalUserCount}/60`);
const text3 = "원하는 팀을 구성하고 컨텐츠를 함께 하세요 ! <br /> 먼저 들어간 사람이 그룹장이 됩니다!"

const roomStore = useRoomStore();

</script>

<template>
<div class="background">
    <!-- 대기화면 소개 글 추가-->
    <waitingHeader :first-description = "text1" :second-description="text2" :third-description = "text3"/>
    <PlayerWaitingGround />
    <!-- 닉네임 만들기 모달 창 -->
</div>
</template>

<style scoped>
.background {
background-color: #FFF2F7;
height: 100vh; 
width: 100%; 
margin: 0; 
padding: 0;
box-sizing: border-box; 
}

.StartButton {
margin-top: 40px;
margin-left: 20px;
}
</style>
