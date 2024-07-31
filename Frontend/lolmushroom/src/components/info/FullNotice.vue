<script setup>
import { defineEmits } from 'vue'
const emit = defineEmits(['close'])
const closeNotice = () => {
  emit('close')
}
</script>

<template>
  <v-card>
    <v-card-title>
      <h2 class="text-center font-weight-black"> 공지 </h2>
      <h6>총 <span class="count-n">2</span>건의 공지사항이 있습니다.</h6>
    </v-card-title>
    <v-divider :thickness="4" color="purple"></v-divider>
    <v-card-text>
      <a>2024-07-23</a> 저희 서비스는 개발중입니다!
    </v-card-text>
    <v-card-text>
      <a>2024-07-26</a> 저희 서비스는 알파테스트 중입니다!
    </v-card-text>
    <v-card-actions>
      <v-btn @click="closeNotice">닫기</v-btn>
    </v-card-actions>
  </v-card>
</template>

<style scoped>
.count-n{
  color:red
}
a{
		color: white;
		text-decoration: none;
		background-color: #f44366;
		display: inline-block; 
		width: 100px;
		text-align: center;
		margin: 3px;
	} 
</style>