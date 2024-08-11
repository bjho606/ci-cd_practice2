``<script setup>
  import axios from 'axios'
  import { ref, onMounted, onBeforeUnmount, reactive, watch } from 'vue'
  import { OpenVidu } from 'openvidu-browser'
  import { useUserStore } from '@/stores/userStore'
  import { useSessionStore } from '@/stores/sessionStore'
  import { useTOFStore } from '@/stores/tofStore'
  import UserVideo from './UserVideo.vue'

  axios.defaults.headers.post['Content-Type'] = 'application/json'

  // const { VITE_API_BASE_URL } = import.meta.env;
  const { VITE_OPENVIDU_URL } = import.meta.env;
  const { VITE_OPENVIDU_SECRET } = import.meta.env;


  const userStore = useUserStore()
  const sessionStore = useSessionStore()
  const store = useTOFStore()

  const state = reactive({
    // OpenVidu 관련 상태 관리
    OV: null,
    session: sessionStore.SessionId,
    mainStreamManager: null,
    publisher: null,
    subscribers: [],
  })

  // Join form 관리
  const mySessionId = ref('SessionA')   // 방 이름을 session에

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
      const subscriber = state.session.subscribe(stream)
      state.subscribers.push(subscriber)
    })

    // 스트림이 파괴될 때...
    state.session.on('streamDestroyed', ({ stream }) => {
      const index = state.subscribers.indexOf(stream.streamManager, 0)
      if (index >= 0) {
        state.subscribers.splice(index, 1)
      }
    })

    // 모든 비동기 에러에 대해...
    state.session.on('exception', ({ exception }) => {
      console.warn(exception)
    })

    // --- 4) 유효한 토큰으로 세션에 연결함 ---
    // const token = await getToken(sessionStore.subSessionId)
    const token = userStore.userOvToken
    state.session.connect(token, { clientData: userStore.userNickname })
      .then(() => {
        // --- 5) Get your own camera stream with the desired properties ---
        const pub = state.OV.initPublisher(undefined, {
          audioSource: undefined, // The source of audio. If undefined default microphone
          videoSource: undefined, // The source of video. If undefined default webcam
          publishAudio: true, // Whether you want to start publishing with your audio unmuted or not
          publishVideo: true, // Whether you want to start publishing with your video enabled or not
          resolution: '320x240', // The resolution of your video
          frameRate: 30, // The frame rate of your video
          insertMode: 'APPEND', // How the video is inserted in the target element 'video-container'
          mirror: false, // Whether to mirror your local video or not
        })

        // 웹캠을 보여주고 퍼블리셔를 저장하는 메인 비디오를 설정하고 Set the main video in the page to display our webcam and store our Publisher
        state.mainStreamManager = pub
        state.publisher = pub
        
        // --- 6) Publish your stream ---
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
  }

  watch(() => store.targetUserToken, (newToken) => {
    const targetStream = state.subscribers.find(
      sub => {
        sub.stream.connection.data === `"${newToken}"`
      }
    );
    
    if (targetStream) {
      updateMainVideoStreamManager(targetStream);
    }
  });

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
    <div id="session-header">
      <h1 id="session-title">{{ mySessionId }}</h1>
      <!-- <input class="btn btn-large btn-danger" type="button" id="buttonLeaveSession" @click="leaveSession"
        value="Leave session" /> -->
    </div>
    <div id="main-video" class="col-md-6">
      <UserVideo :stream-manager="state.mainStreamManager" />
    </div>
    <div id="video-container" class="col-md-6" style="display: none;">
      <UserVideo :stream-manager="state.publisher"/>
      <UserVideo v-for="sub in state.subscribers" :key="sub.stream.connection.connectionId" :stream-manager="sub"/>
    </div>
  </div>
</template>