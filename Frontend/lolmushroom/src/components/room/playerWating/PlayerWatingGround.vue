<script setup>
import {ref} from 'vue';
import watingHeader from './watingHeader.vue';
import { useRoomStore } from '@/stores/roomStore';

const roomStore = useRoomStore();
const text = "원하는 팀을 구성하고 컨텐츠를 함께 하세요 ! <br /> 먼저 들어간 사람이 그룹장이 됩니다!"


// IMPL1 준비 버튼 클릭하면 준비 되기
const toggleReady = (index) =>{
    roomStore.rooms[index].isReady = !roomStore.rooms[index].isReady;
}
// IMPL2 참가하기 버튼 누르면 밑으로 닉네임 이동


// IMPL3 그룹 새로 만들기 클릭하면 새로운 그룹 생성
const addGroup = () =>{
    if(roomStore.rooms.length < 10){
        roomStore.rooms.push({
            sessionId: `session-${roomStore.rooms.length +1 }`,
            groupName: `그룹 ${roomStore.rooms.length +1}`,
            capacity: 10,
            occupants: 1,
            users: [],
            isReady: false,
            buttonClicked: false
        })
    }
}

</script>

<template>
    <watingHeader  :description = "text" class="description-text"/>

    <v-container v-for="(group, index) in roomStore.getRooms" :key="group.sessionId" class="groupContainer" >
        <!--  준비 버튼 만들기 -->
        <v-btn :style="{ backgroundColor: isReady ? '#000000' : '#D9D9D9', color: isReady ? '#FFFFFF' : '#000000' }"
         @click="toggleReady">
      <div>{{ isReady ? '준비완료' : '준비' }}</div> 
    </v-btn>
        <v-btn>
            <div> 
                <v-img> </v-img>
                {{ group.groupName }} 
            </div>
        </v-btn>
         <v-btn :style="{ backgroundColor: '#E7FFDE'}"> 참가하기 </v-btn>
    </v-container>
    <v-btn
        min-width="150"
        varient = "elevated"
        :style ="{backgroundColor: '#CEFFBC'}"
        class="addGroupButton"
        @click = "addGroup"
    > 그룹 새로 만들기</v-btn>

</template>

<style scoped>  
/* 설명 글씨 크기 줄이기 */
.description-text{
    font-size: 12px; 
    margin-bottom: 20px;
}

.groupContainer{
    display: flex;
    justify-content: center;
    gap: 10px; 
    margin-bottom: 10px;

}

v-container{
    text-align: center;
}

</style>

