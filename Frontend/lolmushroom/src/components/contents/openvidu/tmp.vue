<template>
  <div class="live-container">
    <div id="session">
      <header class="session-header">
        <span class="live-label">
          <i class="material-icons"> live_tv </i>
          라이브 <span class="live-status" :class="{ 'live-on': onLive }">{{ onLive ? 'on' : 'off' }}</span>
        </span>
        <span class="re-access-button" @click="resetConnect">재접속하기</span>
      </header>
      <section v-if="session" class="session-body">
        <div id="main-video" class="main-video">
          <user-video v-if="onLive" :stream-manager="mainStreamManager" />
          <div v-else class="video-off">방송 준비 중입니다</div>
        </div>
        <div class="chat-box">
          <div ref="chatDisplay" class="chat-display">
            <div v-for="(chat, index) in chats" :key="index" class="chat-line">
              <div v-if="chat.userId === getUserInfo.id" class="my-comment">
                <img :src="chat.profileImage" class="user-profile" />
                <div>
                  <span class="participant-name">{{ chat.nickname }} </span><span class="chat-msg">{{ chat.msg }}</span>
                </div>
              </div>
              <div v-else class="other-comment">
                <img :src="chat.profileImage" class="user-profile" />
                <div>
                  <span class="participant-name other">{{ chat.nickname }} </span
                  ><span class="chat-msg">{{ chat.msg }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="msg-wrapper">
            <div class="msg-guide">
              <img :src="getUserInfo.profileImage" class="user-profile" />
              {{ getUserInfo.nickname }}
            </div>
            <input
              v-model="sendMsg"
              type="text"
              class="msg-input"
              placeholder="메세지를 입력해주세요"
              @keydown.enter="submitMsg"
            />
          </div>
        </div>
      </section>
      <section v-else class="session-error">
        <div>세션 연결이 어렵습니다. 잠시 후에 시도 해주세요.</div>
        <div><awesome icon="redo-alt" @click="refresh"></awesome></div>
      </section>
    </div>
  </div>
</template>



<script>
import { mapGetters, mapMutations } from 'vuex';
import { OpenVidu } from 'openvidu-browser';
import UserVideo from '@/components/live/UserVideo';
import axios from 'axios';

const OPENVIDU_SERVER_URL = 'https://k4b2021.p.ssafy.io:9000';
const OPENVIDU_SERVER_SECRET = 'MY_SECRET';

const instance = axios.create({
  baseURL: OPENVIDU_SERVER_URL,
  headers: { 'Content-Type': 'application/json' },
});
export default {
  components: {
    UserVideo,
  },
  data() {
    return {
      OV: undefined,
      session: undefined,
      mainStreamManager: undefined,
      mySessionId: '',
      myUserName: '',
      storeId: this.$route.params.storeId,
      subscribers: [],
      chats: [],
      sendMsg: '',
      onLive: false,
    };
  },
  computed: {
    ...mapGetters(['getUserInfo']),
  },
  created() {
    this.mySessionId = `Session${this.storeId}`;
    this.myUserName = `User${this.getUserInfo.id}`;
    this.joinSession();
    this.setSpinnerState(false);
  },
  beforeDestroy() {
    this.leaveSession();
  },

  methods: {
    ...mapMutations(['setSpinnerState']),
    refresh() {
      location.reload();
    },
    chat_on_scroll() {
      this.$refs.chatDisplay.scrollTop = this.$refs.chatDisplay.scrollHeight;
    },
    resetConnect() {
      this.setSpinnerState(true);
      this.leaveSession();
      setTimeout(() => {
        this.joinSession();
        this.setSpinnerState(false);
      }, 1000);
    },
    submitMsg() {
      if (this.sendMsg.trim() === '') return;
      const sendData = {
        userId: this.getUserInfo.id,
        profileImage: this.getUserInfo.profileImage,
        nickname: this.getUserInfo.nickname,
        msg: this.sendMsg,
      };
      this.sendMsg = '';
      this.session
        .signal({
          data: JSON.stringify(sendData), // Any string (optional)
          to: [], // Array of Connection objects (optional. Broadcast to everyone if empty)
          type: 'my-chat', // The type of message (optional)
        })
        .then(() => {
          console.log('Message successfully sent');
        })
        .catch(error => {
          console.error(error);
        });
    },
    joinSession() {
      // --- Get an OpenVidu object ---
      this.OV = new OpenVidu();

      // --- Init a session ---
      this.session = this.OV.initSession();

      // --- Specify the actions when events take place in the session ---

      // On every new Stream received...
      this.session.on('streamCreated', ({ stream }) => {
        this.onLive = true;
        const subscriber = this.session.subscribe(stream);
        this.subscribers.push(subscriber);
        this.mainStreamManager = this.subscribers[this.subscribers.length - 1];
      });
      this.session.on('signal:my-chat', event => {
        this.chats.push(JSON.parse(event.data));
        setTimeout(this.chat_on_scroll, 10);
      });
      // On every Stream destroyed...
      this.session.on('streamDestroyed', ({ stream }) => {
        this.onLive = false;
        const index = this.subscribers.indexOf(stream.streamManager, 0);
        if (index >= 0) {
          this.subscribers.splice(index, 1);
        }
      });

      // On every asynchronous exception...
      this.session.on('exception', ({ exception }) => {
        console.warn(exception);
      });

      // --- Connect to the session with a valid user token ---

      // 'getToken' method is simulating what your server-side should do.
      // 'token' parameter should be retrieved and returned by your own backend
      this.getToken(this.mySessionId).then(token => {
        this.session
          .connect(token, { clientData: this.myUserName })
          .then(() => {})
          .catch(error => {
            console.log('There was an error connecting to the session:', error.code, error.message);
          });
      });

      window.addEventListener('beforeunload', this.leaveSession);
    },
    leaveSession() {
      // --- Leave the session by calling 'disconnect' method over the Session object ---
      if (this.session) this.session.disconnect();

      this.session = undefined;
      this.mainStreamManager = undefined;
      this.OV = undefined;

      window.removeEventListener('beforeunload', this.leaveSession);
    },
    getToken(mySessionId) {
      return this.createSession(mySessionId).then(sessionId => this.createToken(sessionId));
    },
    createSession(sessionId) {
      return new Promise((resolve, reject) => {
        instance
          .post(
            `${OPENVIDU_SERVER_URL}/openvidu/api/sessions`,
            JSON.stringify({
              customSessionId: sessionId,
            }),
            {
              auth: {
                username: 'OPENVIDUAPP',
                password: OPENVIDU_SERVER_SECRET,
              },
            },
          )
          .then(response => response.data)
          .then(data => resolve(data.id))
          .catch(error => {
            if (error.response.status === 409) {
              resolve(sessionId);
            } else {
              console.warn(
                `No connection to OpenVidu Server. This may be a certificate error at ${OPENVIDU_SERVER_URL}`,
              );
              if (
                window.confirm(
                  `No connection to OpenVidu Server. This may be a certificate error at ${OPENVIDU_SERVER_URL}\n\nClick OK to navigate and accept it. If no certificate warning is shown, then check that your OpenVidu Server is up and running at "${OPENVIDU_SERVER_URL}"`,
                )
              ) {
                location.assign(`${OPENVIDU_SERVER_URL}/accept-certificate`);
              }
              reject(error.response);
            }
          });
      });
    },
    createToken(sessionId) {
      return new Promise((resolve, reject) => {
        instance
          .post(
            `${OPENVIDU_SERVER_URL}/openvidu/api/sessions/${sessionId}/connection`,
            {},
            {
              auth: {
                username: 'OPENVIDUAPP',
                password: OPENVIDU_SERVER_SECRET,
              },
            },
          )
          .then(response => response.data)
          .then(data => resolve(data.token))
          .catch(error => reject(error.response));
      });
    },
  },
};
</script>