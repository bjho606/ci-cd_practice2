<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useContentsStore } from '@/stores/contentsStore'
import { useRoomStore } from '@/stores/roomStore'
import { useSessionStore } from '@/stores/sessionStore'
import contentsAPI from '@/api/contents'

// 상태 관리 사용
const router = useRouter()
const contentsStore = useContentsStore()
const roomStore = useRoomStore()
const sessionStore = useSessionStore()

const rooms = computed(() => {
  return roomStore.getRooms.map((room, index) => {
    const groupState = contentsStore.currentGroupState[index]
    return {
      ...room,
      contentStatus: groupState && (groupState.isFinish ? '게임 종료' : '게임 중')
    }
  })
})

/**
 * IMP 1. 진행자에 의해 Session Contents가 진행되고, 이제 다음 Contents로 넘어가는 호출
 * * Contents API의 callNextContetns를 호출한다.
 */
const callNextContents = async (isStart) => {
  try {
    const response = await contentsAPI.callNextContents(isStart)
    if (response.data.isSuccess) {
      console.log(response.data)
    }
  } catch (error) {
    console.error('Error Call Next Contents', error)
  }
}
// RoomWatching으로 라우팅
const goToRoomWatching = () => {
  const sessionId = sessionStore.sessionId
  router.push({ name: 'roomwatching', params: { sessionId: sessionId } })
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
          <b>{{ room.groupName || `그룹${index + 1}` }}</b>
          <div class="mr">{{ room.occupants }}/{{ room.capacity }}</div>
          <div class="mr">
            {{ room.contentStatus }}
          </div>
          <div class="mr">
            <v-icon icon="$next"></v-icon>
          </div>
        </v-btn>
      </v-col>
    </v-row>
  </div>
  <div class="d-flex justify-center mt">
    <v-btn color="primary" @click="goToRoomWatching"> 전체보기 </v-btn>
  </div>
  <div class="d-flex justify-center mt">
    <v-btn color="success" @click="callNextContents(false)"> 다음 게임으로 </v-btn>
  </div>
</template>

<style scoped>
.mt {
  margin-top: 30px;
}
.mr {
  font-weight: bold;
  margin-left: 200px;
}
</style>
