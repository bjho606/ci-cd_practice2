<template>
  <!-- 카드 컴포넌트 -->
  <v-card
    class="mx-auto"
    max-width="400"
  >
    <v-img
      class="align-end text-white"
      height="200"
      src="https://cdn.vuetifyjs.com/images/cards/docks.jpg"
      cover
    >
      <v-card-subtitle class="pt-4 content-feature">
        <v-chip variant="elevated">자기소개</v-chip>
      </v-card-subtitle>
      
      <!-- 추가 및 제거 버튼 -->
      <!-- <div class="actions-container"> -->
        <v-card-actions v-show="isReady">
          <v-btn class="ready-button" color='black' icon="mdi-minus"></v-btn>
        </v-card-actions>
        <v-card-actions v-show="!isReady">
          <v-btn class="ready-button" color='black' icon="mdi-plus"></v-btn>
        </v-card-actions>
      <!-- </div> -->
    </v-img>
    
    <v-card-title>진실? 거짓?</v-card-title>

    <v-card-text>
      <div>나는 동료를 얼마나 잘 알고 있을까요?</div>
      <div>동료의 4가지 진술 중에서 거짓을 찾아내보시오.</div>
    </v-card-text>
  </v-card>
</template>

<script setup>
  const isReady = true
</script>

<style scoped>
  /* .actions-container {
    display: flex;
    justify-content: center;
    height: 100%;
  } */

  .v-card-actions {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 200px;
  }

  .ready-button {
    display: flex;
    justify-content: center;
    align-self: center;
  }

  .v-card-subtitle {
    position: absolute;
    top: 0;
    right: 0;
  }
</style>