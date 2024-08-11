<script setup>
import { useRoomStore } from '@/stores/roomStore';
import { defineProps } from 'vue';

const roomStore = useRoomStore();
// description 으로 props 수신
const props = defineProps({
  description : {
    type: String
  }
})

</script >
<template>
    <v-container class="d-flex flex-row align-center justify-space-between box-layout">
      <div class="box first">
        <div class="content-wrapper">
          <v-img src="src/assets/image/Smillyface.svg" class="smile-image"></v-img>
          <span>우리들의 오리엔테이션 세션</span>
        </div>
      </div>
      <div class="box second">현재 인원  {{roomStore.getTotalUserCount }} /60</div>
      <div class="box third" v-html="description"></div>
    </v-container>
  </template>
  
  <style scoped>
  .box-layout {
    padding: 10px;
  }
  .box {
    flex: 1;
    text-align: center;
    font-weight: bold;
    padding: 20px;
    border-radius: 5px;
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 70px; 
  }
  .first {
    background-color: #90FF77;
  }
  .second {
    background-color: #CEFFBC;
  }
  .third {
    background-color: #E7FFDE;
  }
  .content-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .smile-image {
    width: 30px;
    height: 30px;
    margin-right: 10px;
  }
  </style>