<script setup>
import { onMounted, ref } from 'vue';
import watingHeader from './watingHeader.vue';
import setNickname from './setNickname.vue';

// onMounted 될 시 닉네임 만들기 창 열리게 작동
const showNickname = ref(false);
// 재사용성을 높이는 헤더 소개 
const text = '원하는 팀을 구성하고 컨텐츠를 함께 하세요!'

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
    <v-btn 
    min-width="150"
    variant="elevated"
    :style="{ backgroundColor: '#00FF00', color: '#000' }"
    class="StartButton"
    >
    그룹 새로 만들기
    </v-btn>
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
