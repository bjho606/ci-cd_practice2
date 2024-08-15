<script setup>
import axios from 'axios'
import { ref, onMounted, onBeforeUnmount, reactive, watch } from 'vue'
import { OpenVidu } from 'openvidu-browser'
import { useUserStore } from '@/stores/userStore'
import { useSessionStore } from '@/stores/sessionStore'
import { useTOFStore } from '@/stores/tofStore'
import UserVideo from './UserVideo.vue'

axios.defaults.headers.post['Content-Type'] = 'application/json'

const { VITE_OPENVIDU_URL } = import.meta.env
const { VITE_OPENVIDU_SECRET } = import.meta.env

const mic = ref(true)
const video = ref(true)

const userStore = useUserStore()
const sessionStore = useSessionStore()
const store = useTOFStore()

const state = reactive({
  // OpenVidu 관련 상태 관리
  OV: null,
  session: sessionStore.subSessionId,
  mainStreamManager: null,
  publisher: null,
  subscribers: []
})

// Join form 관리
const mySessionId = ref('SessionA') // 방 이름을 session에

const joinSession = async () => {
  // --- 1) OpenVidu 객체 얻기 ---
  state.OV = new OpenVidu(VITE_OPENVIDU_URL, VITE_OPENVIDU_SECRET)

  // 통신 과정에서 많은 log를 남기게 되는데 필요하지 않은 log를 띄우지 않게 하는 모드
  state.OV.enableProdMode()

  // --- 2) 세션 초기화 ---
  state.session = state.OV.initSession()

  // --- 3) 세션에서 이벤트가 발생할 때 작업 ---

  // 스트림이 받아질 때..
  state.session.on('streamCreated', ({ stream }) => {
    console.log('Stream created:', stream) // 스트림 생성 로그
    const subscriber = state.session.subscribe(stream, 'video-container')
    console.log('Subscriber added:', subscriber) // 구독자 추가 로그
    state.subscribers.push(subscriber)
  })

  // 스트림이 파괴될 때...
  state.session.on('streamDestroyed', ({ stream }) => {
    console.log('Stream destroyed:', stream) // 스트림 파괴 로그
    const index = state.subscribers.indexOf(stream.streamManager, 0)
    if (index >= 0) {
      state.subscribers.splice(index, 1)
      console.log('Subscriber removed at index:', index) // 구독자 제거 로그
    }
  })

  // 모든 비동기 에러에 대해...
  state.session.on('exception', ({ exception }) => {
    console.warn(exception)
  })

  // --- 4) 유효한 토큰으로 세션에 연결함 ---
  // const token = await getToken(sessionStore.subSessionId)
  const token = userStore.userOvToken
  state.session
    .connect(token, { clientData: userStore.userName })
    .then(() => {
      console.log('Session connected successfully')

      // --- 5) 속성과 함께 카메라 정의 ---
      const pub = state.OV.initPublisher('undefinded', {
        audioSource: undefined, // The source of audio. If undefined default microphone
        videoSource: undefined, // The source of video. If undefined default webcam
        publishAudio: true, // Whether you want to start publishing with your audio unmuted or not
        publishVideo: true, // Whether you want to start publishing with your video enabled or not
        resolution: '320x240', // The resolution of your video
        frameRate: 30, // The frame rate of your video
        insertMode: 'APPEND', // How the video is inserted in the target element 'video-container'
        mirror: false // Whether to mirror your local video or not
      })

      // 웹캠을 보여주고 퍼블리셔를 저장하는 메인 비디오를 설정
      state.mainStreamManager = pub
      state.publisher = pub

      // --- 6) 스트림 발행 ---
      state.session.publish(state.publisher)
    })
    .catch((error) => {
      console.log('There was an error connecting to the session:', error.code, error.message)
    })
}

const leaveSession = () => {
  // --- 7) Leave the session by calling 'disconnect' method over the Session object ---
  if (state.session) state.session.disconnect()

  // Empty all properties...
  state.session = null
  state.mainStreamManager = null
  state.publisher = null
  state.subscribers = []
  state.OV = null

  // Remove beforeunload listener
  window.removeEventListener('beforeunload', leaveSession)
}

const updateMainVideoStreamManager = (stream) => {
  if (state.mainStreamManager === stream) return
  state.mainStreamManager = stream

  //
  state.subscribers.forEach((subscriber) => {
    if (subscriber === state.mainStreamManager) {
      subscriber.publishVideo(true)
      subscriber.publishAudio(true)
    } else {
      subscriber.publishVideo(false)
      subscriber.publishAudeo(false)
    }
  })

  if (state.publisher === state.mainStreamManager) {
    state.publisher.publishVideo(true)
    state.publisher.publishAudeo(true)
  } else {
    state.publisher.publishVideo(false)
    state.publisher.publishAudeo(false)
  }
}

watch(
  () => store.targetUserToken,
  (newToken) => {
    const targetStream = state.subscribers.find((sub) => {
      sub.stream.connection.data === `${newToken}`
    })

    if (targetStream) {
      updateMainVideoStreamManager(targetStream)
    }
  }
)

const toggleMic = () => {
  mic.value = !mic.value
  state.publisher.publishAudio(mic.value)

  state.subscribers.forEach((subscriber) => {
    subscriber.subscribeToAudio(mic.value)
  })
}

const toggleVideo = () => {
  video.value = !video.value
  // 여기에 내 비디오만 변화하도록 수정한다.
  state.publisher.publishVideo(video.value)

  state.subscribers.forEach((subscriber) => {
    subscriber.subscribeToVideo(video.value)
  })
}

// 컴포넌트가 마운트될 때 실행
onMounted(() => {
  window.addEventListener('beforeunload', leaveSession)
  joinSession()
})

// 컴포넌트가 언마운트될 때 실행
onBeforeUnmount(() => {
  leaveSession()
})
</script>

<template>
  <div id="session">
    <div id="main-video" class="col-md-6">
      <UserVideo :stream-manager="state.mainStreamManager" />
      <div class="d-flex">
        <div v-if="store.targetUserToken === userStore.userOvToken">
          <v-icon v-show="video" icon="mdi-video" size="x-large" @click="toggleVideo()" />
          <v-icon v-show="!video" icon="mdi-video-off" size="x-large" @click="toggleVideo()" />
        </div>
        <v-icon v-show="mic === true" icon="mdi-microphone" size="x-large" @click="toggleMic()" />
        <v-icon
          v-show="mic === false"
          icon="mdi-microphone-off"
          size="x-large"
          @click="toggleMic()"
        />
      </div>
    </div>
    {{ store.targetUserToken }}
    <div id="video-container" class="col-md-6" style="display: none">
      <!-- <div id="video-container" class="col-md-6"> -->
      <UserVideo :stream-manager="state.publisher" />
      <UserVideo
        v-for="sub in state.subscribers"
        :key="sub.stream.connection.connectionId"
        :stream-manager="sub"
      />
    </div>
  </div>
</template>
