<script setup>
import { ref, reactive, onBeforeUnmount, onMounted} from 'vue'
import { OpenVidu } from 'openvidu-browser'
import axios from 'axios'

axios.defaults.headers.post['Content-Type'] = 'application/json'

const { VITE_API_BASE_URL } = import.meta.env;


const state = reactive({
  OV: undefined,
  session: undefined,
  mainStreamManager: undefined,
  publisher: undefined,
  subscribers: [],
  // 추가적으로 정의하고 싶은 것들
  joinedPlayer: 0,
  userTokens: []
})

const myGroupId = ref('')
const myUserName = ref('김경호')
// div 객체와 연결한 ref객체
const videoContainer = ref('')


const joinSession = async () => {
  // --- 1) OpenVidu 객체 얻기 ---
  state.OV = new OpenVidu()
  // socket 통신 과정에서 많은 log를 남기게 되는데 필요하지 않은 log를 띄우지 않게 하는 모드
  state.OV.enableProdMode()

  // 지연 시간 10초?로 설정
  state.OV.setAdvancedConfiguration({
    noStreamPlayingEventExceptionTimeout: 10000
  })

  // --- 2) 세션 초기화 ---
  state.session = state.OV.initSession()
  
  // --- 3) 세션 내 이벤트 처리 ---
  // streamcreated라는 이벤트가 터지면 이하의 코드를 실행한다.
  // streamcreated라는 것은 뭐냐?
  // 다른 놈이 세션에 연결되면 자동으로 발동한다.
  state.session.on('streamCreated', ({ stream }) => {
    const subscriber = state.session.subscribe(stream, 'video-container')
    state.subscribers.push(subscriber)
    // 구독자 토큰 추가
    state.userTokens.push(subscriber['stream']['connection']['connectionId'])
    state.joinedPlayer++
  })

  // 세션에서 누가 탈락하면 실행하는 함수
  state.session.on('streamDestroyed', ({ stream }) => {
    const pElements = videoContainer.value.querySelectorAll('p')
    pElements.forEach((elements) => {
      if (elements.id === stream.connection.connectionId) {
        elements.remove()
      }
    })

  })
  
  state.session.on('exception', ({ exception }) => {
    console.warn(exception)
  })

  // --- 4) 유효한 사용자 토큰으로 세션 연결 ---
  try {
    const token = await getToken()
    await state.session.connect(token, { clientData: myUserName.value })

    // 퍼블리셔를 생성하고 video-container 아래에 비디오를 추가
    const publisher = state.OV.initPublisher('video-container', {
      audioSource: undefined,
      videoSource: undefined,
      publishAudio: true,
      publishVideo: true,
      resolution: '250x250',
      frameRate: 30,
      insertMode: 'APPEND',
      mirror: false
    })
    state.session.publish(publisher)

    // 메인 비디오를 자신의 웹캠으로 설정하고 Publisher 저장
    state.mainStreamManager = publisher
    state.publisher = publisher

    // 본인(발행자) ovtoken 추가
    state.userTokens.push(state.publisher['session']['connection']['connectionId'])
    // 유저 정보 하단에 추가
    state.joinedPlayer++

      // --- 6) 스트림 발행 ---
    } catch (error) {
      console.log('세션 연결 중 오류가 발생했습니다:', error.code, error.message);
    }
    
    window.addEventListener('beforeunload', leaveSession);
  };

  const leaveSession = () => {
    // --- 7) 세션 나가기 ---
    if (state.session) state.session.disconnect();

    // 모든 속성 비우기
    state.session = undefined;
    state.mainStreamManager = undefined;
    state.publisher = undefined;
    state.subscribers = [];
    state.OV = undefined;

    // beforeunload 리스너 제거
    window.removeEventListener('beforeunload', leaveSession);
  };

  // 토큰을 발급받는 함수
  const getToken = async () => {
    const sessionId = await createSession()
    return await createToken(sessionId)
  }

  // 상위 세션 생성하는 함수
  const createSession = async () => {
    console.log(VITE_API_BASE_URL + '/api/v1/sessions')
    const response = await axios.post(
      VITE_API_BASE_URL + '/api/v1/sessions',
      {
        headers: { 
          'Content-Type': 'application/json',
        },
        contentsOrder: [
          1,2,3,6,2,5
        ]
      }
    )
    console.log('세션 ID 생성 완료')
    return response.data.result.sessionId
  }

  // 토큰 발급하는 함수
  const createToken = async (sessionId) => {
    // 세션 이름 설정
    myGroupId.value = sessionId

    // 세션 연결하는 API 호출
    const response = await axios.post(
      VITE_API_BASE_URL + '/api/v1/sessions/' + sessionId + '/connections',
      {
        headers: { 'Content-Type': 'application/json' }
      },
      {
        'userName': myUserName.value
      },
    )
    return response.data.result.ovToken
  }

  onBeforeUnmount(() => {
    leaveSession()
  })
  
  // 마운트하면 바로 실행
  onMounted(() => {
    joinSession()
  })
</script>

<template>
  <div id="session" v-if="state.session">
    <div id="session-header">
      <h1 id="session-title">{{ myGroupId }}</h1>
    </div>
    <input class="btn btn-large btn-danger" type="button" id="buttonLeaveSession" @click="leaveSession" value="세션 나가기" />
    <div id="video-container" class="col-md-6" ref="videoContainer" />
  </div>
</template>

<style scoped></style>
