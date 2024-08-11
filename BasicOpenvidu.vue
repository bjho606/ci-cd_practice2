<script setup>
  import { ref, onMounted, onBeforeUnmount } from 'vue'
  import axios from 'axios'
  import UserVideo from './components/UserVideo.vue'
  import { useUserStore } from '@/stores/userStore'


  axios.defaults.headers.post['Content-Type'] = 'application/json'

  const { VITE_OPENVIDU_URL } = import.meta.env;

  const userStore = useUserStore()

  const state = reactive({
    // OpenVidu 관련 상태 관리
    OV: null,
    session: null,
    mainStreamManager: null,
    publisher: null,
    subscribers: [],
  })

  // Join form 관리
  const mySessionId = ref('SessionA')   // 방 이름을 session에
  const myUserName = ref(userStore.userNickName)

  const joinSession = async () => {
    // --- 1) OpenVidu 객체 얻기 ---
    OV.value = new OpenVidu()

    // socket 통신 과정에서 많은 log를 남기게 되는데 필요하지 않은 log를 띄우지 않게 하는 모드
    // state.OV.enableProdMode()

    // --- 2) 세션 초기화 ---
    session.value = state.OV.initSession()

    // --- 3) 세션에서 이벤트가 발생할 때 작업 ---

    // 스트림이 받아질 때..
    session.value.on('streamCreated', ({ stream }) => {
      const subscriber = state.session.subscribe(stream)
      state.subscribers.push(subscriber)
    })

    // 스트림이 파괴될 때...
    session.value.on('streamDestroyed', ({ stream }) => {
      const index = state.subscribers.indexOf(stream.streamManager, 0)
      if (index >= 0) {
        state.subscribers.splice(index, 1)
      }
    })

    // On every asynchronous exception...
    session.value.on('exception', ({ exception }) => {
      console.warn(exception)
    })

    // --- 4) 유효한 토큰으로 세션에 연결함 ---
    const token = await getToken(userStore.userOvToken)

    state.session.connect(token, { clientData: myUserName.value })
      .then(() => {
        // --- 5) Get your own camera stream with the desired properties ---
        const pub = OV.value.initPublisher(undefined, {
          audioSource: undefined, // The source of audio. If undefined default microphone
          videoSource: undefined, // The source of video. If undefined default webcam
          publishAudio: true, // Whether you want to start publishing with your audio unmuted or not
          publishVideo: true, // Whether you want to start publishing with your video enabled or not
          resolution: '640x480', // The resolution of your video
          frameRate: 30, // The frame rate of your video
          insertMode: 'APPEND', // How the video is inserted in the target element 'video-container'
          mirror: false, // Whether to mirror your local video or not
        })

        // Set the main video in the page to display our webcam and store our Publisher
        mainStreamManager.value = pub
        publisher.value = pub

        // --- 6) Publish your stream ---
        session.value.publish(publisher.value)
      })
      .catch((error) => {
        console.log('There was an error connecting to the session:', error.code, error.message)
      })
  }

  const leaveSession = () => {
    // --- 7) Leave the session by calling 'disconnect' method over the Session object ---
    if (session.value) session.value.disconnect()

    // Empty all properties...
    session.value = null
    mainStreamManager.value = null
    publisher.value = null
    subscribers.value = []
    OV.value = null

    // Remove beforeunload listener
    window.removeEventListener('beforeunload', leaveSession)
  }

  const updateMainVideoStreamManager = (stream) => {
    if (mainStreamManager.value === stream) return
    mainStreamManager.value = stream
  }

  // API 요청을 통해 토큰을 가져오는 함수
  const getToken = async (sessionId) => {
    const createdSessionId = await createSession(sessionId)
    return await createToken(createdSessionId)
  }

  const createSession = async (sessionId) => {
    const response = await axios.post(APPLICATION_SERVER_URL + 'api/sessions', { customSessionId: sessionId }, {
      headers: { 'Content-Type': 'application/json' },
    })
    return response.data // The sessionId
  }

  const createToken = async (sessionId) => {
    const response = await axios.post(APPLICATION_SERVER_URL + 'api/sessions/' + sessionId + '/connections', {}, {
      headers: { 'Content-Type': 'application/json' },
    })
    return response.data // The token
  }

  // 컴포넌트가 마운트될 때 실행
  onMounted(() => {
    window.addEventListener('beforeunload', leaveSession)
  })

  // 컴포넌트가 언마운트될 때 실행
  onBeforeUnmount(() => {
    leaveSession()
  })
</script>

<template>
  <div id="main-container" class="container">
    <div id="join" v-if="!session.value">
      <div id="img-div">
        <img src="resources/images/openvidu_grey_bg_transp_cropped.png" />
      </div>
      <div id="join-dialog" class="jumbotron vertical-center">
        <h1>Join a video session</h1>
        <div class="form-group">
          <p>
            <label>Participant</label>
            <input v-model="myUserName" class="form-control" type="text" required />
          </p>
          <p>
            <label>Session</label>
            <input v-model="mySessionId" class="form-control" type="text" required />
          </p>
          <p class="text-center">
            <button class="btn btn-lg btn-success" @click="joinSession">
              Join!
            </button>
          </p>
        </div>
      </div>
    </div>

    <div id="session" v-if="session.value">
      <div id="session-header">
        <h1 id="session-title">{{ mySessionId }}</h1>
        <input class="btn btn-large btn-danger" type="button" id="buttonLeaveSession" @click="leaveSession"
          value="Leave session" />
      </div>
      <div id="main-video" class="col-md-6">
        <UserVideo :stream-manager="mainStreamManager.value" />
      </div>
      <div id="video-container" class="col-md-6">
        <UserVideo :stream-manager="publisher.value" @click.native="updateMainVideoStreamManager(publisher.value)" />
        <UserVideo v-for="sub in subscribers.value" :key="sub.stream.connection.connectionId" :stream-manager="sub"
          @click.native="updateMainVideoStreamManager(sub)" />
      </div>
    </div>
  </div>
</template>

