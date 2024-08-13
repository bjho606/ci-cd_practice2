<script setup>
import { useRouter } from 'vue-router'
import { useSessionStore } from '@/stores/sessionStore'


const router = useRouter()
const sessionStore = useSessionStore()
const sessioncodeUrl = router.resolve({
    name: 'sessioncode',
    query: {
      sessionUrl: sessionStore.getSessionUrl
    }
  }).href

const shareCode = () => {
  window.open(sessioncodeUrl, '_blank', 'width=1200,height=800')
}


</script>

<template>
  <div class="share-session">
    <v-btn class="share-session-btn" icon  @click="shareCode()">
      <v-icon class="share-icon" icon="mdi-share" />
      공유
    </v-btn>
    <div class="share-info-text">방 코드 공유로 편리한 오리엔테이션을 만드세요</div>
  </div>
</template>

<style scoped>
.share-session {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.share-session-btn {
  display: flex;
  align-items: center;
  background-color: #e7ffde;
  color: #000000;
  width: 120px;
  height: 60%;
  padding: 10px 20px;
  margin-bottom: 10px;
  border: none;
  border-radius: 50px;
  font-size: 1.2rem;
}

.share-icon {
  font-size: 1.5rem; /* Adjust size as needed */
  margin-right: 8px;
  color: #000000;
}

.share-info-text {
  font-size: 1rem;
  text-align: center;
}
</style>
