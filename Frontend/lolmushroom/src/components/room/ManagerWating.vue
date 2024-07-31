<script setup>
import { storeToRefs } from 'pinia';
import { useRoomStore } from '@/stores/roomStore';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';

// 상태 관리 사용
const router = useRouter();
const roomStore = useRoomStore();
const { rooms } = storeToRefs(roomStore);

// RoomWatching으로 라우팅
const goToMultiRoom = () => {
  router.push('multiRoom');
}

// 게임 시작 알림
const startGame = () => {
  Swal.fire({
    title: '게임을 시작하시겠습니까?',
    text: "다시 돌아갈 수 없어요!",
    icon: 'question',
    color: "black", 
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: '시작',
    cancelButtonText: '취소'
  }).then((result) => {
    if (result.isConfirmed) {
      goToMultiRoom();
    }
  });
}
</script>

<template>
  <div>
    <v-row>
      <v-col v-for="(room, index) in rooms" :key="index" cols="12" md="6">
        <v-btn
          size="x-large"
          width="300"
          height="100"
          rounded="lg"
          block
          color="#e9ecef"
          class="mb-2"
        >
          <b>{{ `그룹${index + 1}` }}</b>
          <div class="mr">
            {{ room.occupants }}/{{ room.capacity }}
          </div>
          <div class="mr">          
            <v-icon icon="$next"></v-icon>
          </div>
        </v-btn>
      </v-col>
    </v-row>
  </div>
  <div class="d-flex justify-center mt">
    <v-btn color="pink" @click="startGame">
      게임 시작
    </v-btn>
  </div>
</template>

<style scoped>
.mt {
  margin-top: 30px;
}
.mr {
  font-size: small;
  font-weight: bold;
  margin-left: 200px;
}
</style>
