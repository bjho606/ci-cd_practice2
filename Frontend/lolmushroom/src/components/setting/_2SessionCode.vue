<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useSessionStore } from '@/stores/sessionStore'
import QrcodeVue from 'qrcode.vue'

const sessionStore = useSessionStore()
const sessionURL = computed(() => sessionStore.getSessionUrl)

// 클립보드에 링크 복사하기
const copyToClipboard = () => {
  navigator.clipboard.writeText(sessionURL.value)
  alert('링크가 복사되었습니다!')
}

// 라우터 사용하여 창 닫기 버튼 클릭 시 페이지 이동
const router = useRouter()
const closeWindow = () => {
  router.push('/') // 홈 또는 원하는 페이지로 이동
}
</script>

<template>
  <v-container fluid class="code-page">
    <!-- Pink Top Half -->
    <v-row class="pink-section">
      <v-col class="text-center">
        <qrcode-vue :value="sessionURL" :size="300" level="H" />
        <h3 class="title">QR코드를 공유하세요</h3>
      </v-col>
    </v-row>

    <!-- White Bottom Half -->
    <v-row class="white-section">
      <v-col class="text-center">
        <p class="subtext">방링크도 공유할 수 있어요</p>
        <v-text-field
          v-model="roomLink"
          label="방링크"
          readonly
          append-icon="mdi-content-copy"
          @click:append="copyToClipboard"
          dense
        ></v-text-field>

        <v-btn color="green" @click="closeWindow" block small> 코드 창 닫기 </v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.code-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.pink-section {
  background-color: #fff2f7;
  height: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.white-section {
  background-color: #ffffff;
  height: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

.title {
  font-size: 1em; /* 제목의 크기를 작게 설정 */
}

.subtext {
  font-size: 0.8em; /* 서브 텍스트의 크기를 작게 설정 */
  margin-bottom: 5px;
}

.v-text-field {
  max-width: 200px; /* 텍스트 필드의 크기를 작게 설정 */
  margin-bottom: 5px;
}

.v-btn {
  width: 100%;
  max-width: 200px; /* 버튼의 크기를 작게 설정 */
  margin-top: 5px;
}
</style>
