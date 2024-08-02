<script setup>
  import { ref, reactive, onBeforeUnmount, onMounted, watch, onUpdated } from 'vue';
  import { OpenVidu } from 'openvidu-browser';
  import { useSessionStore } from '@/stores/session';
  import { useUserStore } from '@/stores/User';
  import axios from 'axios';
  import UserVideo from './UserVideo.vue';
  

  axios.defaults.headers.post['Content-Type'] = 'application/json';

  const APPLICATION_SERVER_URL = process.env.NODE_ENV === 'production' ? '' : 'http://localhost:5713/';

  const store = useSessionStore()

  const props = defineProps({
    userName: Array,
    socketConnected: Boolean,
  });

  const emit = defineEmits(['update:nickName', 'publish']);

  const state = reactive({
    OV: undefined,
    session: undefined,
    mainStreamManager: undefined,
    publisher: undefined,
    subscribers: [],
    joinedPlayer: 0,
  })

  // const OV = ref(undefined);
  // const session = ref(undefined);
  // const mainStreamManager = ref(undefined);
  // const publisher = ref(undefined);
  // const subscribers = ref([]);

  const myGroupId = ref(store.subSessionId);  
  const myUserName = ref(useUserStore().userNickname)

  // API 요청을 통해 발표자 정보 내려 받기
  const nickNames = props.userName
  let iii = 0;
  const nickName = ref(nickNames[iii]);

  // const userRef = ref(null)

  const changeNickName = () => {
    iii = (iii + 1) % nickNames.length;
    nickName.value = nickNames[iii];
    };
  
  const joinSession = async () => {
    
    // --- 1) OpenVidu 객체 얻기 ---
    state.OV = new OpenVidu();

    // socket 통신 과정에서 많은 log를 남기게 되는데 필요하지 않은 log를 띄우지 않게 하는 모드
    state.OV.enableProdMode()

    state.OV.setAdvancedConfiguration({
      noStreamPlayingEventExceptionTimeout: 10000, 
    });

    // 발언감지, 간격은 50밀리초, 데시벨의 임계값 75
    // state.OV.setAdvancedConfiguration({
    //   publisherSpeakingEventsOptions: {
    //     interval: 50,
    //     threshold: -75,
    //   },
    // });

    // --- 2) 세션 초기화 ---
    state.session = state.OV.initSession();

    // --- 3) 세션 내 이벤트 처리 ---
    state.session.on('streamCreated', ({ stream }) => {
      // const subscriber = session.value.subscribe(stream, 'video-container');
      console.log('Stream created:', stream);
      const subscriber = state.session.subscribe(stream, 'video-container')
      state.subscribers.push(subscriber)
      // console.log('Subscriber:', subscriber);
      if (subscriber.videos.length !== 0) { state.joinedPlayer++ }
    });

    state.session.on('streamDestroyed', ({ stream }) => {
      const index = state.subscribers.indexOf(stream.streamManager, 0);
      if (index >= 0) {
        const subscriber = state.subscribers[index]
        if (subscriber.videos.length !== 0) { state.joinedPlayer++ }
        state.subscribers.splice(index, 1);
      }
    });

    state.session.on('exception', ({ exception }) => {
      console.warn(exception);
    });

    // 발언 시 
  //   session.value.on('publisherStartSpeaking', (event) => {
  //   for (let i = 0; i < userRef.value.children.length; i++) {
  //     if (
  //       JSON.parse(event.connection.data).clientData ===
  //       userRef.value.children[i].innerText
  //     ) {
  //       userRef.value.children[i].style.borderStyle = 'solid';
  //       userRef.value.children[i].style.borderColor = '#1773EA';
  //     }
  //   }
  // });


    // --- 4) 유효한 사용자 토큰으로 세션 연결 ---
    try {
      const token = await getToken(myGroupId.value);
      await state.session.connect(token, { clientData: myUserName.value })
      // await session.value.connect(token, { clientData: props.userName });

      // --- 5) 원하는 속성으로 카메라 스트림 초기화 ---
      const publisher = state.OV.initPublisher(undefined, {
        audioSource: undefined,
        videoSource: undefined,
        publishAudio: true,
        publishVideo: true,
        resolution: '250x250',
        frameRate: 30,
        insertMode: 'APPEND',
        mirror: false,
      });
      state.session.publish(publisher)

      // 메인 비디오를 자신의 웹캠으로 설정하고 Publisher 저장
      // 이거를 이제 순번에 맞게 설정하면 된다.
      state.mainStreamManager = publisher;
      state.publisher = publisher
      // console.log('publisher', publisher)
      // store.state.root.publisher = publisher
      state.joinedPlayerNumbers++
      console.log(state.subscribers)
      
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

  const updateMainVideoStreamManager = (stream) => {
    // 메인 비디오 교체 -> 이거를 매턴 마다 돌려가면서 사용해야지
    if (state.mainStreamManager !== stream) {
      state.mainStreamManager = stream;
    }
  };

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

  watch(nickName, (newVal) => {
    emit('update:nickName', newVal);
  });

  watch(state.subscribers, () => {
    emit('publish', true);
  })

</script>

<template>
  <div id="session" v-if="state.session">
    <div id="session-header">
      <h1 id="session-title">{{ myGroupId }}</h1>
      <input class="btn btn-large btn-danger" type="button" id="buttonLeaveSession" @click="leaveSession" value="세션 나가기" />
    </div>
    <!-- 메인 송출 비디오 -->
    <div id="main-video" class="col-md-6">
      <UserVideo :stream-manager="state.mainStreamManager"/>
    </div>
    <v-btn @click="changeNickName">
      발표 종료
    </v-btn>
    <!-- 주변 비디오 이거 렌더링만 안 하고 뒀다가 메인이랑 엿 바꿔먹는 데 사용할까?-->
    <div id="video-container" class="col-md-6">
      <!-- <UserVideo :stream-manager="state.publisher" @click="updateMainVideoStreamManager(state.publisher)"/>
      <UserVideo
        v-for="sub in state.subscribers"
        :key="sub.stream.connection.connectionId"
        :stream-manager="sub"
        @click="updateMainVideoStreamManager(sub)"
      /> -->
    </div>
  </div>
</template>

<style scoped>
</style>
