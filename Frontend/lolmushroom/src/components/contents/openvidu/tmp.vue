<template>
  <div class="callmy-left-top-container">
    <div class="callmy-all-video-list">
      <UserVideo
        :isPlayer="false"
        :stream-manager="publisher"
        :startDetection="false"
        @click="updateMainVideoStreamManager(publisher)"
      />
      <UserVideo
        v-for="sub in subscribers"
        :isPlayer="false"
        :key="sub.stream.connection.connectionId"
        :startDetection="false"
        :stream-manager="sub"
        @click="updateMainVideoStreamManager(sub)"
      />
    </div>
  </div>
  <div class="callmy-left-bottom-container">
    <div v-if="gameStart && roundStart" class="callmy-round-info-container">
      <div class="callmy-round-info">
        <span class="callmy-title-round">{{ callmyManager.round }} Round!</span>
        {{ callmyManager.nowPlayUsers.length ? callmyManager.nowPlayUsers[0].username : '' }}
        <span class="callmy-title-vs">VS</span>
        {{ callmyManager.nowPlayUsers.length ? callmyManager.nowPlayUsers[1].username : '' }}
      </div>
    </div>
    <div class="callmy-now-play-video-container">
      <div v-if="gameStart && roundStart" class="callmy-now-play-video-list">
        <UserVideo
          :isPlayer="true"
          :startDetection="startDetection"
          :stream-manager="publisher"
        />
        <UserVideo
          v-for="sub in subscribers"
          :key="sub.stream.connection.connectionId"
          :isPlayer="true"
          :startDetection="startDetection"
          :stream-manager="sub"
        />
      </div>
      <div v-else-if="!gameStart">
        <h1>아직 게임 시작 전입니다. {{ joinedPlayerNumbers }} / {{ playerNumbers }}</h1>
      </div>
      <div v-else>
        <h1>잠시 후, 다음 라운드가 시작됩니다.</h1>
      </div>
    </div>
    <div :class="{'callmy-sub-container': true, 'callmy-sub-container-none': !(speaker && answer)}">
      <div class="callmy-sub">
        <div>
          <span class="callmy-speaker">{{ speaker }}</span>
          <span class="callmy-answer">{{ answer }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
@import url('./callmy-webcam.css');
</style>

<script setup>
import { ref, reactive, onBeforeUnmount, watch, computed } from 'vue';
import axios from 'axios';
import { OpenVidu } from 'openvidu-browser';
import { useStore } from 'vuex';
import UserVideo from './components/UserVideo.vue';

axios.defaults.headers.post['Content-Type'] = 'application/json';
axios.defaults.headers.post['Access-Control-Allow-Origin'] = '*';

const OPENVIDU_SERVER_URL = `https://${location.hostname}:8443`;
const OPENVIDU_SERVER_SECRET = 'NARANG_VIDU';

const props = defineProps({
  roomId: String,
  socketConnected: Boolean,
  gameStart: Boolean,
  roundStart: Boolean,
  playerNumbers: Number,
  startDetection: Boolean,
  speaker: String,
  answer: String,
});

const emit = defineEmits(['joinCallMyRoom']);

const store = useStore();

const state = reactive({
  OV: undefined,
  session: undefined,
  mainStreamManager: undefined,
  publisher: undefined,
  subscribers: [],
  joinedPlayerNumbers: 0,
});

const mySessionId = computed(() => props.roomId);
const myUserName = computed(() => store.getters['root/username']);
const callmyManager = computed(() => store.state.root.callmyManager);

const joinSession = async () => {
  state.OV = new OpenVidu();
  state.session = state.OV.initSession();

  state.session.on('streamCreated', ({ stream }) => {
    const subscriber = state.session.subscribe(stream);
    state.subscribers.push(subscriber);
    if (subscriber.videos.length !== 0) state.joinedPlayerNumbers++;
  });

  state.session.on('streamDestroyed', ({ stream }) => {
    const index = state.subscribers.indexOf(stream.streamManager, 0);
    if (index >= 0) {
      const subscriber = state.subscribers[index];
      if (subscriber.videos.length !== 0) state.joinedPlayerNumbers--;
      state.subscribers.splice(index, 1);
    }
  });

  state.session.on('exception', ({ exception }) => {
    console.warn(exception);
  });

  try {
    const token = await getToken(mySessionId.value);
    await state.session.connect(token, { clientData: myUserName.value });

    const publisher = state.OV.initPublisher(undefined, {
      audioSource: undefined,
      videoSource: undefined,
      publishAudio: true,
      publishVideo: true,
      resolution: '600x320',
      frameRate: 30,
      insertMode: 'APPEND',
      mirror: true,
    });

    state.mainStreamManager = publisher;
    state.publisher = publisher;
    store.state.root.publisher = publisher;
    state.joinedPlayerNumbers++;
    state.session.publish(publisher);
  } catch (error) {
    console.log('There was an error connecting to the session:', error.code, error.message);
  }

  window.addEventListener('beforeunload', leaveSession);
};

const leaveSession = () => {
  if (state.session) state.session.disconnect();

  state.session = undefined;
  state.mainStreamManager = undefined;
  state.publisher = undefined;
  state.subscribers = [];
  state.OV = undefined;

  window.removeEventListener('beforeunload', leaveSession);
};

const updateMainVideoStreamManager = (stream) => {
  if (state.mainStreamManager !== stream) {
    state.mainStreamManager = stream;
  }
};

const getToken = async (mySessionId) => {
  const sessionId = await createSession(mySessionId);
  return createToken(sessionId);
};

const createSession = async (sessionId) => {
  const data = JSON.stringify({ customSessionId: sessionId });

  try {
    const res = await axios.post(`${OPENVIDU_SERVER_URL}/openvidu/api/sessions`, data, {
      auth: {
        username: 'OPENVIDUAPP',
        password: OPENVIDU_SERVER_SECRET,
      },
    });
    return res.data.id;
  } catch (error) {
    if (error.response.status === 409) {
      return sessionId;
    } else {
      console.warn(`No connection to OpenVidu Server. This may be a certificate error at ${OPENVIDU_SERVER_URL}`);
      if (window.confirm(`No connection to OpenVidu Server. This may be a certificate error at ${OPENVIDU_SERVER_URL}\n\nClick OK to navigate and accept it. If no certificate warning is shown, then check that your OpenVidu Server is up and running at "${OPENVIDU_SERVER_URL}"`)) {
        location.assign(`${OPENVIDU_SERVER_URL}/accept-certificate`);
      }
      throw error;
    }
  }
};

const createToken = async (sessionId) => {
  try {
    const res = await axios.post(`${OPENVIDU_SERVER_URL}/openvidu/api/sessions/${sessionId}/connection`, {}, {
      auth: {
        username: 'OPENVIDUAPP',
        password: OPENVIDU_SERVER_SECRET,
      },
    });
    return res.data.token;
  } catch (error) {
    throw error;
  }
};

watch(() => state.joinedPlayerNumbers, (newVal) => {
  if (newVal === props.playerNumbers) emit('joinCallMyRoom');
});

watch(() => props.socketConnected, (newVal) => {
  if (newVal) {
    joinSession();
  }
});

onBeforeUnmount(() => {
  leaveSession();
});

</script>
