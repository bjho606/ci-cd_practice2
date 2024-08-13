<script setup>
import {ref, onMounted, computed} from 'vue';
import { useRoomStore } from '@/stores/roomStore';
import { useSessionStore } from '@/stores/sessionStore';
import { useUserStore } from '@/stores/userStore';
import { useRouter } from 'vue-router';
import sessionAPI from '@/api/session';

const router = useRouter();
const roomStore = useRoomStore();
const sessionStore = useSessionStore();
const userStore = useUserStore();
const rooms = computed(() => roomStore.getActiveRooms)


// 세션 연결 

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

/**
 * IMP  SubSession을 '생성'한다 -> '생성'과 '입장'은 구분되어 있음.
 * REQ
 * @param sessionId
 *
 * RES sessionId(하위 세션 ID)
 * * sessionId : 하위 세션 ID를 Result로 가져오지만, '입장'하는 것이 아니므로 쓸 일이 없다.
 * IMP CreateSubSessionHandler()를 통해 Sub Session을 만들어내면, Main Session의 정보가 Server에서 갱신된다.
 * IMP 이러한 특성을 이용해, 2번 getSessionInfo()를 통해 Main Session의 활성화 상태를 갱신한다.
 */ 
const createSubSessionHandler = async (sessionId) => {
  try {
    const response = await sessionAPI.createSubSession(sessionId)
    if (response.data.isSuccess) {
      console.log('하부 Session의 생성이 완료되었습니다:)')
    }
  } catch (error) {
    console.error('Error Creating SubSession', error)
  }
}


// IMPL1 준비 버튼 클릭하면 준비 되기

const sendReadyMessage = async (sessionId) => {
  try {
    const response = await sessionAPI.getSubSessionReady(sessionId)
    if (response.data.isSuccess) {
      console.log('우리 Team은 모두 준비 완료입니다:)')
    }
  } catch (error) {
    console.error('Error Ready Message to Manager')
  }
}

const toggleReady = (index) =>{
    if (userStore.getIsTeamLeader){        
        roomStore.rooms[index].isReady = !roomStore.rooms[index].isReady;
        sendReadyMessage(sessionStore.subSessionId)
    }
} 



// IMPL2 참가하기 버튼 누르면 밑으로 닉네임 이동
const joinSubsession = async (index) =>{
    const room =  rooms.value[index]

    // 이미 방을 들어 갈때
    if (sessionStore.subSessionId && sessionStore.subSessionId !== room.sessionId) {
    await handleLeaveSession();
    }

    // 닉네임이 없을 시 
    if (!room.users.includes(userStore.userNickname)) { 
        // 만약 room에 사람이 없으면 true
        const isFirstUserInGroup = room.occupants === 0
        await getSessionConnection(room.sessionId, { userName: userStore.userNickname })

        if (isFirstUserInGroup) userStore.setTeamLeader(true);
        else userStore.setTeamLeader(false);

        sessionStore.setSubSessionId(room.sessionId)
        room.users.push(userStore.userNickname);
    } 
}


// IMPL3 그룹 새로 만들기 클릭하면 새로운 그룹 생성
const addGroup = async () =>{
    const newIndex = rooms.value.length;   
    // roomStore.setCurrentContentsState
    await createSubSessionHandler(sessionStore.sessionId)

    roomStore.setButtonClicked(newIndex);
    sessionStore.setSessionId(sessionStore.sessionId)
}


// IMPL 4 이름 버튼 누르면 그룹명 바꾸기 
const changeRoomName = async () => {
    const newRoomName = await sessionAPI.changeSubSessionName(sessionStore.subSessionId);
    console.log("새로운 방 이름 " + newRoomName);
}


// IMPL 5 Player가 방을 나간다.
const handleLeaveSession = async() =>{
    try{
        await sessionAPI.getSubSessionQuit(sessionStore.subSessionId);
        const currentRoom = rooms.value.find(room => room.sessionId === sessionStore.subSessionId);
        if(currentRoom){
            const userIndex = currentRoom.users.indexOf(userStore.userNickname);
            // user닉네임이 방에 있을 때
            if(userIndex !== -1){
                currentRoom.users.splice(userIndex, 1);
            }
    }
    sessionStore.setSubSessionId(null);
    }catch (error){
        console.error('Error Leaving Session', error);
    }
};

</script>

<template>
    <div class="room-list-container">
        <div class="room-list">
        <v-container v-for="(group, index) in rooms.filter(room => room.isActive)" :key="group.sessionId" class="group-container">
        <v-card class="group-card">
            <v-btn
            :style="{ backgroundColor: group.isReady ? '#000000' : '#D9D9D9', color: group.isReady ? '#FFFFFF' : '#000000' }"
            @click="toggleReady(index)"
            class="ready-btn"
            >
            <div>
                {{ group.isReady ? '준비완료' : '준비' }}
              <p v-if=" !userStore.getIsTeamLeader" class="warning">팀장만 준비완료가 가능합니다!</p>
            </div>
            </v-btn>
            <v-btn class="group-name" @click="changeRoomName">
            <v-img src="src/assets/image/autorenew.svg" class="icon" />
            {{ group.groupName }}
            </v-btn>
            <v-btn :style="{ backgroundColor: '#E7FFDE'}" class="join-btn"  @click ="joinSubsession(index)">
            참가하기
            </v-btn>
            <!-- 하위 세션 밑에 USER 정보 삽입 -->
            <div v-if="group.users.length > 0" class="user-list">
                <v-btn v-for="user in group.users" :key="user" class="user-btn" min-width="180">
                    {{ group.groupName}}
                    <div>
                        {{ user }}
                    </div>
                </v-btn>
            </div>
            <!-- 정보 삽입 END -->
        </v-card>
        </v-container>
        <v-btn
        min-width="150px"
        varient = "elevated"
        :style ="{backgroundColor: '#CEFFBC'}"
        class="add-group-button"
        @click = "addGroup"
        > 
        그룹 새로 만들기
    </v-btn>
    </div>
</div>


</template>

<style scoped>  

/* 글씨 가운데 */
v-container{
    text-align: center;
}

/* 말풍선 팀장만 준비 가능*/
.warning {
  display: none;
  position: absolute;
  width: 320px;
  padding: 0px;
  left: 50%;
  transform: translateX(-50%);
  margin-bottom: 5px; /* Space between button and warning */
  border-radius: 8px;
  background: #333;
  color: #fff;
  font-size: 14px;
  z-index: 10;
}

.ready-btn:hover .warning {
  display: block;
}

/* 전체 컨테이너 이동 */
.room-list-container {
width: 100%;
height: 90%;
overflow-x: auto;
}


.room-list {
flex-direction: row;
display: flex;
flex-wrap: nowrap;
justify-content: flex-start;
padding: 10px;
}


.group-container {
flex: 0 0 auto;
width: 250px;
margin-bottom: 20px;
}


/* 그룹 플렉스로 설정 */
.group-card {
display: flex;
flex-direction: column;
align-items: center;
padding: 20px;
height: 100%;
background-color: #FFF2F7;
}

/* 전체 크기만 설정 */
.ready-btn, .group-name, .join-btn {
width: 100%;

}

.ready-btn{
    min-height: 40px;
}

.group-name{
    background-color: #66FF4F;
    font-size: larger;
    font-weight: bold;
    min-height: 60px;
    margin-bottom: 20px;
}

/* 그룹 만들기 버튼 */
.add-group-button{
    margin-top: 30px;
    min-height: 80px;
}

/* 유저 정보 나열 */
.user-list{
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    margin-top: 10px;
}

.user-btn{
    background-color: #CEFFBC;
    font-size : 12px;
    padding : 5px 10px;
    margin-bottom: 10px;
}


/* 다시하기 이미지 아이콘 설정 */
.icon{
    width: 20px;
    margin-right: 10px;
}
</style>

