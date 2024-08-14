<script setup>
import { computed } from 'vue'
import { useSessionStore } from '@/stores/sessionStore'
import QrcodeVue from 'qrcode.vue'

const sessionStore = useSessionStore()
const sessionURL = computed(() => sessionStore.getSessionUrl)

const copyToClipboard = () => {
  navigator.clipboard
    .writeText(sessionURL.value)
    .then(() => {
      alert('🔗 링크 복사 완료! 이제 붙여넣기만 하면 마법이 일어납니다! ✨')
    })
    .catch(() => {
      alert('😅 어이쿠! 복사에 실패했어요. 다시 시도해 주세요.')
    })
}

const closeWindow = () => {
  window.close()
}
</script>

<template>
  <div class="code-page">
    <!-- Pink Top Half -->
    <div class="pink-section">
      <div class="qrcode-container">
        <h3 class="title">QR코드를 공유하세요</h3>
        <qrcode-vue :value="sessionURL" :size="150" level="H" />
        <div class="text-over-line">
          <p class="subtext">방링크를 공유할 수 있어요</p>
        </div>
        <div class="link-container">
          <input v-model="sessionURL" class="link-input" readonly />
          <button class="copy-button" @click="copyToClipboard">
            <v-icon>mdi-content-copy</v-icon>
          </button>
        </div>
        <div class="white-section">
          <button class="close-button" @click="closeWindow">
            코드 창 닫기
            <v-icon class="close-icon">mdi-close</v-icon>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.code-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #fff2f7;
}

.pink-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.qrcode-container {
  background-color: #ffffff;
  width: 40%;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.title {
  font-size: 1.2em;
  margin-bottom: 15px;
}

.text-over-line {
  position: relative;
  margin-bottom: 15px;
}

.text-over-line:before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  width: 100%;
  height: 1px;
  background-color: #ccc;
  z-index: 1;
}

.subtext {
  font-size: 0.9em;
  color: #666;
  background-color: #fff;
  padding: 0 10px;
  position: relative;
  z-index: 2;
  display: inline-block;
}

.link-container {
  display: flex;
  align-items: center;
  margin-top: 10px;
}

.link-input {
  flex: 1;
  border: 1px solid #ccc;
  border-radius: 4px;
  padding: 5px;
}

.copy-button {
  background-color: transparent;
  border: none;
  cursor: pointer;
  margin-left: 5px;
}

.white-section {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.close-button {
  background-color: #8bff89;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1em;
  width: 80%;
  position: relative;
}

.close-icon {
  position: absolute;
  right: 10px;
}
</style>
