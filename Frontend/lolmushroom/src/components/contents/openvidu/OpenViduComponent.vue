<script setup>
  import { ref, onBeforeUnmount, onMounted, watch } from 'vue';
  import { OpenVidu } from 'openvidu-browser';
  import UserVideo from './UserVideo.vue';
  import axios from 'axios';

  axios.defaults.headers.post['Content-Type'] = 'application/json';

  const APPLICATION_SERVER_URL = process.env.NODE_ENV === 'production' ? '' : 'http://localhost:5713/';

  const props = defineProps({
    sessionId: String,
    userName: String,
  });

  const emit = defineEmits(['update:myUserName']);

  const OV = ref(undefined);
  const session = ref(undefined);
  const mainStreamManager = ref(undefined);
  const publisher = ref(undefined);
  const subscribers = ref([]);
  // 세션 이름은 어디서 관리하지? 
  const mySessionId = ref('SessionA');
  
  // API 요청을 통해 발표자 정보 내려 받기
  const nickNames = ['나이스한 강아지', '재빠른 날다람쥐', '영민한 뱅갈호랑이', '얍삽한 치타', '여유로운 오징어', '거대한 사막여우']
  let iii = 0
  const myUserName = ref(nickNames[iii]);
  const changeNickName = () => {
    iii ++
    myUserName.value = nickNames[iii]
  }
  
  // const myUserName = ref('Participant' + Math.floor(Math.random() * 100));

  const joinSession = async () => {
    // --- 1) OpenVidu 객체 얻기 ---
    OV.value = new OpenVidu();

    // --- 2) 세션 초기화 ---
    session.value = OV.value.initSession();

    // --- 3) 세션 내 이벤트 처리 ---
    session.value.on('streamCreated', ({ stream }) => {
      const subscriber = session.value.subscribe(stream);
      subscribers.value.push(subscriber);
    });

    session.value.on('streamDestroyed', ({ stream }) => {
      const index = subscribers.value.indexOf(stream.streamManager, 0);
      if (index >= 0) {
        subscribers.value.splice(index, 1);
      }
    });

    session.value.on('exception', ({ exception }) => {
      console.warn(exception);
    });

    // --- 4) 유효한 사용자 토큰으로 세션 연결 ---
    try {
      const token = await getToken(props.sessionId);
      await session.value.connect(token, { clientData: myUserName.value });
      // await session.value.connect(token, { clientData: props.userName });

      // --- 5) 원하는 속성으로 카메라 스트림 초기화 ---
      publisher.value = OV.value.initPublisher(undefined, {
        audioSource: undefined,
        videoSource: undefined,
        publishAudio: true,
        publishVideo: true,
        resolution: '250x250',
        frameRate: 30,
        insertMode: 'APPEND',
        mirror: false,
      });

      // 메인 비디오를 자신의 웹캠으로 설정하고 Publisher 저장
      mainStreamManager.value = publisher.value;

      // --- 6) 스트림 발행 ---
      session.value.publish(publisher.value);
    } catch (error) {
      console.log('세션 연결 중 오류가 발생했습니다:', error.code, error.message);
    }

    window.addEventListener('beforeunload', leaveSession);
  };

  const leaveSession = () => {
    // --- 7) 세션 나가기 ---
    if (session.value) session.value.disconnect();

    // 모든 속성 비우기
    session.value = undefined;
    mainStreamManager.value = undefined;
    publisher.value = undefined;
    subscribers.value = [];
    OV.value = undefined;

    // beforeunload 리스너 제거
    window.removeEventListener('beforeunload', leaveSession);
  };

  // const updateMainVideoStreamManager = (stream) => {
  //   // 메인 비디오 교체 -> 이거를 매턴 마다 돌려가면서 사용해야지
  //   if (mainStreamManager.value !== stream) {
  //     mainStreamManager.value = stream;
  //   }
  // };

  const getToken = async (mySessionId) => {
    const sessionId = await createSession(mySessionId);
    return await createToken(sessionId);
  };

  const createSession = async (sessionId) => {
    const response = await axios.post(APPLICATION_SERVER_URL + 'api/sessions', { customSessionId: sessionId }, {
      headers: { 'Content-Type': 'application/json' },
    });
    return response.data;
  };

  const createToken = async (sessionId) => {
    const response = await axios.post(APPLICATION_SERVER_URL + 'api/sessions/' + sessionId + '/connections', {}, {
      headers: { 'Content-Type': 'application/json' },
    });
    return response.data;
  };

  onBeforeUnmount(() => {
    leaveSession();
  });

  onMounted(() => {
    joinSession();
  });

  watch(myUserName, (newVal) => {
    emit('update:myUserName', newVal);
  });

</script>

<template>
  <div id="session">
    <div id="session-header">
      <h1 id="session-title">{{ mySessionId }}</h1>
      <!-- <input class="btn btn-large btn-danger" type="button" id="buttonLeaveSession" @click="leaveSession" value="세션 나가기" /> -->
    </div>
    <!-- 메인 송출 비디오 -->
    <div id="main-video" class="col-md-6">
      <user-video :stream-manager="mainStreamManager" />
    </div>
    <v-btn @click="changeNickName">
      닉네임 변경
    </v-btn>
    <!-- 주변 비디오 이거 렌더링만 안 하고 뒀다가 메인이랑 엿 바꿔먹는 데 사용할까?-->
    <!-- <div id="video-container" class="col-md-6">
      <user-video :stream-manager="publisher" @click="updateMainVideoStreamManager(publisher)" />
      <user-video v-for="sub in subscribers" :key="sub.stream.connection.connectionId" :stream-manager="sub" @click="updateMainVideoStreamManager(sub)" />
    </div> -->
  </div>
</template>

<style scoped>
</style>
