<script setup>
import { onMounted, ref } from 'vue';
import watingHeader from './watingHeader.vue';
import setNickname from './setNickname.vue';
import PlayerWatingGround from './PlayerWatingGround.vue';

// onMounted 될 시 닉네임 만들기 창 열리게 작동
const showNickname = ref(false);
// 재사용성을 높이는 헤더 소개 
const text = "원하는 팀을 구성하고 컨텐츠를 함께 하세요 ! <br /> 먼저 들어간 사람이 그룹장이 됩니다!"

onMounted(()=>{
    showNickname.value = true;
})

// 창 닫기 모달
const closeModal = () =>{
    showNickname.value = false;
}
</script>

<template>
<div class="background">
    <!-- 대기화면 소개 글 추가-->
    <watingHeader  :description = "text"/>
    <PlayerWatingGround />
    <!-- 닉네임 만들기 모달 창 -->
    <v-dialog v-model="showNickname" persistent max-width = "500">
    <template v-slot:activator="{ props }"></template>
    <set-nickname @close="closeModal" />
    </v-dialog>
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
