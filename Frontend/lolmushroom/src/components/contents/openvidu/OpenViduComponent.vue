<script setup>
  import { ref, reactive, onBeforeUnmount, onMounted, watch, nextTick } from 'vue';
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

  const emit = defineEmits(['update:nickName']);

  const state = reactive({
    OV: undefined,
    session: undefined,
    mainStreamManager: undefined,
    publisher: undefined,
    subscribers: [],
    joinedPlayer: 0,
    userTokens: [],
  })

  const myGroupId = ref(store.subSessionId);  
  const myUserName = ref(useUserStore().userNickname)
  const videoContainer = ref('')
  // API 요청을 통해 발표자 정보 내려 받기
  const nickNames = props.userName
  let iii = 0;
  const nickName = ref(nickNames[iii]);

  // const userRef = ref(null)

  const changeNickName = () => {
    iii = (iii + 1) % nickNames.length;
    nickName.value = nickNames[iii];
    console.log(document.querySelectorAll('video'), '닉네임 변경')
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
      addUserName(stream.connection)
      const subscriber = state.session.subscribe(stream, 'video-container');
      // const subscriber = state.session.subscribe(stream);
      state.subscribers.push(subscriber)
      // 구독자 토큰 추가
      state.userTokens.push(subscriber['stream']['connection']['connectionId'])
      state.joinedPlayer++
    })


    state.session.on('streamDestroyed', ({ stream }) => {
      const pElements = videoContainer.value.querySelectorAll('p');
      pElements.forEach(elements => {
        if (elements.id === stream.connection.connectionId) {
          elements.remove()
        }
      })
      const index = state.subscribers.indexOf(stream.streamManager, 0);
      if (index >= 0) {
        const subscriber = state.subscribers[index]
        if (subscriber.videos.length !== 0) { state.joinedPlayer-- }
        state.subscribers.splice(index, 1);

      }
    })
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

      const publisher = state.OV.initPublisher('video-container', {
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

      // 본인(발행자) ovtoken 추가
      state.userTokens.push(state.publisher['session']['connection']['connectionId'])
      // 유저 정보 하단에 추가
      state.joinedPlayer++

      // --- 6) 스트림 발행 ---
    } catch (error) {
      console.log('세션 연결 중 오류가 발생했습니다:', error.code, error.message);
    }
    
    window.addEventListener('beforeunload', leaveSession);
    addUserName(state.publisher.stream.connection)
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

  // props로 어떤 값을 받으면 watch로 감시하고 있다가 이거 실행해서 특정 카메라만 display 하는 함수
  const updateMainVideoStreamManager = (id) => {
    const elements = videoContainer.value.children;
    for (let i = 0; i < elements.length; i++) {
      if (elements[i].id !== id) {
        elements[i].style.display = 'none';
      } else {
        elements[i].style.display = ''
      }
    }
  }

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

  const applyVideoStyles = () => {
    nextTick(() => {
      const videos = document.querySelectorAll('video');
      let i = 0
      videos.forEach(video => {
        video.id = state.userTokens[i]
        i ++
      });
    });
  };

  // 유저 정보를 추출하여 비디오 상단에 추가
  const addUserName = (connectionData) => {
    const pElement = document.createElement('p')
    pElement.textContent = JSON.parse(connectionData.data).clientData
    pElement.setAttribute('id', connectionData.connectionId)
    videoContainer.value.appendChild(pElement)
  }

  // `state.joinedPlayer`가 변경될 때마다 `applyVideoStyles` 호출
  watch(() => state.joinedPlayer, applyVideoStyles);
  watch(state.joinedPlayer, (newValue, oldValue) => {
    if (newValue > oldValue) {
      applyVideoStyles()
    }
  })

  onBeforeUnmount(() => {
    leaveSession();
  });

  onMounted(() => {
    joinSession();
  });

  watch(nickName, (newVal) => {
    emit('update:nickName', newVal);
  });

</script>

<template>
  <div id="session" v-if="state.session">
    <div id="session-header">
      <h1 id="session-title">{{ myGroupId }}</h1>
    </div>
    <v-btn @click="updateMainVideoStreamManager(state.userTokens[1])">카메라변경.</v-btn>
    <input class="btn btn-large btn-danger" type="button" id="buttonLeaveSession" @click="leaveSession" value="세션 나가기" />
    <div id="video-container" class="col-md-6" ref="videoContainer"></div>
  </div>
</template>

<style scoped>
#video-container > #local-video-undefined{
  display: none;
}

#remote-video-str_CAM_LZKP_con_EnkzQoI6M8 {
  background-color: aqua;
}
</style>
