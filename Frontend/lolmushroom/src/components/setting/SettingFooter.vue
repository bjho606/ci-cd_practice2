<!-- eslint-disable no-unused-vars -->
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useSessionStore } from '@/stores/sessionStore'
import sessionAPI from '@/api/session'
import ButtonComponent from '@/components/common/ButtonComponent.vue'

const sessionStore = useSessionStore()
const router = useRouter()
const props = defineProps({
  step: {
    type: Number,
    required: true
  },
  nextStep: {
    type: Function,
    required: true
  },
  prevStep: {
    type: Function,
    required: true
  },
  selectedContents: {
    type: Array,
    required: true
  }
})

/**
 * IMP 1. MainSession에 대한 Connection을 생성한다.
 * REQ
 * @param sessionId
 * @param userName
 *
 * RES openvide_token, user_token
 */
const getSessionConnection = async (sessionId, userName) => {
  try {
    const response = await sessionAPI.getSessionConnection(sessionId, userName)
    if (response.data.isSuccess) {
      console.log('Connection을 성공적으로 만들어 냈습니다. Connection Token은 다음과 같습니다:)')
    }
  } catch (error) {
    console.error('Error Getting Session Connection', error)
  }
}

const calculateTotalDuration = () => {
  let totalMinutes = 0
  props.selectedContents.forEach((content) => {
    totalMinutes += content.duration
  })
  return `${totalMinutes}분`
}

const errorMessage = ref('')
const loading = ref(false)
const createSessionHandler = async () => {
  loading.value = true
  errorMessage.value = ''
  try {
    const order = props.selectedContents.map((content) => content._id)
    const response = await sessionAPI.createSession({ contentsOrder: order })
    if (response.data.isSuccess) {
      const { sessionId, url } = response.data.result
      sessionStore.setSessionId(sessionId) // Store sessionId in Pinia
      console.log('진행자님! SessionID가 생성되었습니다. => ', sessionId)
      props.nextStep()
    } else {
      errorMessage.value = 'Failed to create session: ' + response.data.message
    }
  } catch (error) {
    errorMessage.value = 'Error creating session: ' + error.message
  } finally {
    loading.value = false
  }
}

const goToSession = async () => {
  const sessionId = sessionStore.sessionId
  await getSessionConnection(sessionId, { userName: 'Manager' })
  router.push({ name: 'roomwatching', params: { sessionId: sessionId } })
}
</script>

<template>
  <v-container class="white-part">
    <v-container class="button-container">
      <ButtonComponent
        v-if="step == 2"
        text="돌아가기"
        color="black"
        size="large"
        @click="prevStep"
      />
      <v-spacer></v-spacer>
      <div v-if="step === 2" class="estimated-time">
        총 예상 시간: {{ calculateTotalDuration() }}
      </div>
      <ButtonComponent
        v-if="step == 2"
        :loading="loading"
        text="결정하기"
        color="green"
        size="large"
        @click="createSessionHandler"
      />
      <ButtonComponent
        v-if="step == 3"
        :loading="loading"
        text="참여자 기다리기"
        color="green"
        size="large"
        @click="goToSession"
      />
    </v-container>
  </v-container>
</template>

<style scoped>
.white-part {
  background-color: #ffffff;
  height: 70px;
}

.button-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.estimated-time {
  margin-right: 20px;
  font-weight: bold;
}
</style>
