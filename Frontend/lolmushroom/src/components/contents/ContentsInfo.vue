<script setup>
import { defineProps } from 'vue'

defineProps({
  title: {
    type: String,
    required: true
  },
  buttonText: {
    type: String,
    required: true
  },
  contentsImage: {
    type: String,
    required: true
  },
  instructionsText: {
    type: String,
    required: true
  }
})
</script>

<template>
  <div class="content-loading-container">
    <div class="header">
      <div class="emoji">😊</div>
      <div class="title">{{ title }}</div>
    </div>
    <div class="content-wrapper">
      <div class="content-image">{{ contentsImage }}</div>
      <div class="instructions">
        <button class="btn">{{ buttonText }}</button>
        <div class="instructions-text">
          <h3>플레이 방법</h3>
          <p>
            {{ instructionsText }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 전체 컨테이너 설정 */
.content-loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #e8f5e9;
  width: 100%;
  height: 100vh;
  box-sizing: border-box;
}

/* 헤더를 상단에 고정 */
.header {
  display: flex;
  align-items: center;
  background-color: #ccff90;
  padding: 10px;
  border-radius: 10px;
  width: 200px;
  max-width: 1200px;
  position: fixed;
  top: 20px;
  left: 10%;
  z-index: 1000;
  box-sizing: border-box;
}

.emoji {
  font-size: 24px;
  margin-right: 10px;
}

.title {
  font-size: 24px;
  font-weight: bold;
}

/* 콘텐츠 영역의 레이아웃 설정 */
.content-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  width: 100%;
  max-width: 1500px;
  margin-top: 80px; /* 헤더 높이만큼 여백 추가 */
  padding: 20px;
  box-sizing: border-box;
  flex-grow: 1;
}

/* 콘텐츠 이미지 설정 */
.content-image {
  width: 65%;
  height: 100%;
  max-height: 500px;
  background-color: #e0e0e0;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 18px;
  color: #555;
}

/* 오른쪽의 플레이 방법 및 협동 버튼 설정 */
.instructions {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  width: 30%;
  text-align: center;
}

.btn {
  width: 100%;
  padding: 10px;
  font-size: 18px;
  background-color: #76ff03;
  border: none;
  border-radius: 10px;
  margin-bottom: 20px;
}

.instructions-text {
  border-radius: 10px;
  color: white;
  font-size: 16px;
  display: inline-block;
  background-color: #66bb6a; /* 기본 배경색 */
}

/* h3와 p 태그의 스타일 구분 */
.instructions-text h3 {
  color: #ffffff;
  background-color: #24a319;
  padding: 10px;
  margin: 0;
  border-radius: 10px 10px 0 0;
}

.instructions-text p {
  color: #000000; /* 흰색 */
  background-color: #90ff77; /* 파란색 배경 */
  padding: 10px;
  border-radius: 0 0 10px 10px;
  margin: 0;
  line-height: 1.5;
}
</style>
