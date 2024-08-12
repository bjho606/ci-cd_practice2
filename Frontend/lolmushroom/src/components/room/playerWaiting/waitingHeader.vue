<script setup>
import { useRoomStore } from '@/stores/roomStore';
import { defineProps } from 'vue';

const roomStore = useRoomStore();
// description 으로 props 수신
const props = defineProps({
  firstDescription: {
    type: String,
    required: true
  },
  secondDescription : {
    type: String,
    required : true
  },
  thirdDescription : {
    type: String
  },
})

</script >
<template>
    <v-container class="d-flex flex-row align-center justify-space-between box-layout">
      <div class="box first">
        <div class="content-wrapper">
          <v-img src="src/assets/image/Smillyface.svg" class="smile-image"></v-img>
          <span v-html="firstDescription"></span>
        </div>
      </div>
      <div class="box second" v-html="secondDescription"></div>
      <div class="box third" v-html="thirdDescription"></div>
    </v-container>
  </template>
  
  <style scoped>

  .box {
    flex: 1;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 50px; 
    box-shadow: 0px 3px 0px 0px #555555

  }
  .first {
    border-radius: 5px 0px 0px 5px;
    background-color: #90FF77;
    font-size:x-large;
  }
  .second {
    background-color: #CEFFBC;
    font-size:x-large
  }
  .third {
    border-radius: 0px 5px 5px 0px;
    background-color: #E7FFDE;
    font-size: x-small
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