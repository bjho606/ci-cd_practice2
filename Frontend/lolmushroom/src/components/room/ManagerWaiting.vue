<script setup>
import { storeToRefs } from 'pinia'
import { useRoomStore } from '@/stores/roomStore'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'

// 상태 관리 사용
const router = useRouter()
const roomStore = useRoomStore()
const { rooms } = storeToRefs(roomStore)

// RoomWatching으로 라우팅
const goToMultiRoom = () => {
  router.push('multiRoom')
}

// 게임 시작 알림
const startGame = () => {
  Swal.fire({
    title: '게임을 시작하시겠습니까?',
    text: '다시 돌아갈 수 없어요!',
    icon: 'question',
    color: 'black',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: '시작',
    cancelButtonText: '취소'
  }).then((result) => {
    if (result.isConfirmed) {
      goToMultiRoom()
    }
  })
}

/**
 * TODO : MainSession Connection
 * TODO : SessionInfo에 대한 Socket
 * TODO : Contents 진행에 대한 Socket
 * TODO : 준비 완료 불 다 들어오면 ->  게임 시작을 누를 수 있도록 도와준다.
 *
 * IMP TODO : Contents/start , next , reload API를 호출 => WS이 되면, 이거 Message만 보고싶어.. => 돌아감의 마지노선..
 * IMP 진행자에게 그에 맞는 UI 버튼 같은 것을 추가해줘야 한다. ( 각 팀의 완료 여부를 볼 수 있는 UI 추가 필수 !! )
 */
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
          <div class="mr">{{ room.occupants }}/{{ room.capacity }}</div>
          <div class="mr">
            <v-icon icon="$next"></v-icon>
          </div>
        </v-btn>
      </v-col>
    </v-row>
  </div>
  <div class="d-flex justify-center mt">
    <v-btn color="pink" @click="startGame"> 게임 시작 </v-btn>
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
