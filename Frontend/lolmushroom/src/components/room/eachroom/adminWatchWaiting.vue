<script setup>
import { computed } from 'vue'
import { useRoomStore } from '@/stores/roomStore'

// Store access
const roomStore = useRoomStore()

// Compute the rooms and empty slots
const rooms = computed(() => roomStore.getRooms)
// const emptySlots = computed(() => {
//   // Logic to calculate empty slots if needed
//   const maxSlots = 8 // Example number of max slots
//   return maxSlots - rooms.value.length
// })
</script>

<template>
  <div class="group-list">
    <div
      v-for="(room, index) in rooms"
      :key="index"
      :class="['group-card', { 'ready': room.isReady, 'preparing': !room.isReady }]"
    >
      <div class="group-header">
        <div class="group-name">{{ room.groupName || `그룹${index + 1}` }}</div>
        <div class="group-count">{{ room.occupants }} / {{ room.capacity }}</div>
      </div>
      <div class="group-members">
        <div class="user-item" v-for="(user, i) in room.users" :key="i">{{ user }}</div>
      </div>
      <div class="group-status">
        <span>{{ room.isReady ? '준비 완료' : '준비 중' }}</span>
      </div>
    </div>
    
    <!-- 빈 그룹 공간 -->
    <!-- <div class="group-card empty-card" v-for="index in emptySlots" :key="'empty-' + index">
      <div class="empty-slot">✖</div>
    </div> -->
  </div>
</template>

<style scoped>
.group-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(200px, 1fr)); /* 2개의 고정된 열 */
  grid-template-rows: repeat(5, 250px); /* 5개의 고정된 행 */
  margin-top: 20px;
  gap: 30px;
  justify-content: center;
}

.group-card {
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  border: 1px solid #00FF00;
  border-radius: 20px;
  text-align: center;
  min-width: 250px;
  min-height: 250px;
}

.group-card.ready .group-status {
  background-color: #1F4F16;
  color: #ffffff;
}

.group-card.preparing .group-status {
  background-color: #00D200;
  color: #ffffff;
}

.group-header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  min-height: 40px;
  padding: 10px 0;
}

.group-name {
  flex: 1;
  font-size: 25px;
  font-weight: bold;
}

.group-count {
  flex: 1;
  font-size: 20px;
}

.group-members {
  display: grid;
  grid-template-columns: repeat(2, 1fr); /* 5개의 열로 고정 */
  grid-template-rows: repeat(5, 1fr); /* 2개의 행으로 고정 */
  gap: 5px; /* 아이템 간의 간격 */
  height: 130px;
}

.user-item {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
}

.group-status {
  background-color: #364b41;
  color: white;
  height: 40px;
  font-size: 20px;
  border: none;
  align-content: center;
}

.empty-card {
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px dashed #00D200;
  height: 315px;
  width: 315px;
}
</style>