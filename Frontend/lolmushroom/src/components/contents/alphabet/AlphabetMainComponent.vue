<script setup>
import { onMounted, ref, watch } from 'vue';

const category_name = ref('동물');
const userInput = ref('ㅎ ㄹ ㅇ');

</script>

<template>
  <div class="header">
      공통 컴포넌트인 헤더 넣어야됨
  </div>
<div class="container">
  <div class="statusContainer">
    <div class="info">
      <div class="info-category">카테고리</div>
      <div class="info-text">{{ category_name }}</div>
    </div>
  </div>
  <div class="playContainer">
    <div class="userInput">
      <div class="emojiField"></div><div :userInput class="initialBox">{{ userInput }}</div>
    </div>
    <div class="userInput">
      <div class="emojiField"></div><input v-html="userInput" class="inputText" placeholder="카테고리에 관한 입력을 해주세요 !">
    </div>
    <button class="submit">
          정답 맞추기
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


</style>
