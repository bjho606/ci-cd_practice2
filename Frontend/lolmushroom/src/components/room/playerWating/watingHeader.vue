<script setup>
import SmileFace from '@/assets/image/Smillyface.svg'
import { useRoomStore } from '@/stores/roomStore'
import { defineProps } from 'vue'

const roomStore = useRoomStore()
// description 으로 props 수신
const props = defineProps({
  description: {
    type: String
  }
})
</script>
<template>
  <header class="d-flex flex-row align-center justify-space-between box-layout">
    <div class="box first">
      <div class="content-wrapper">
        <v-img :src="SmileFace" class="smile-image"></v-img>
        <span>우리들의 오리엔테이션 세션</span>
      </div>
    </div>
    <div class="box second">현재 인원 {{ roomStore.getTotalUserCount }} /60</div>
    <div class="box third" v-html="description"></div>
  </header>
</template>

<style scoped>
.box-layout {
  padding: 10px;
}
.box {
  flex: 1;
  text-align: center;
  padding: 20px;
  border-radius: 5px;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100px;
}
.first {
  background-color: #90ff77;
}
.second {
  background-color: #ceffbc;
}
.third {
  background-color: #e7ffde;
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
