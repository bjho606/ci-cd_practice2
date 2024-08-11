<script setup></script>

<template>
  <div class="custom-button">
    <div class="left-part">컨텐츠 리스트</div>
    <div class="right-part">컨텐츠를 클릭해서 나만의 큐레이션을 구성하여 밑에서 확인하세요 !</div>
  </div>
</template>

<style scoped>
.custom-button {
  display: inline-flex;
  align-items: center;
  border-radius: 20px;
  background-color: #90ff77;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.left-part {
  background-color: #247719;
  color: white;
  padding: 10px 20px;
  border-radius: 20px; /* Round only the left side */
}

.right-part {
  color: black;
  padding: 10px 20px;
}
</style>
