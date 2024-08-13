<script setup>
import { computed } from 'vue'
import { useRoomStore } from '@/stores/roomStore'

// Store access
const roomStore = useRoomStore()
const rooms = computed(() => roomStore.getRooms)
</script>

<template>
  <div class="group-list">
    <div
      v-for="(room, index) in rooms"
      :key="index"
      :class="[
        'group-card',
        { inactive: !room.isActive, ready: room.isReady, preparing: room.isActive && !room.isReady }
      ]"
    >
      <div v-if="room.isActive">
        <div class="group-header">
          <div class="group-name">{{ room.groupName || `그룹${index + 1}` }}</div>
          <div class="group-count">{{ room.occupants }} / {{ room.capacity }}</div>
        </div>
        <div class="group-members">
          <div class="user-item" v-for="(user, i) in room.users.slice(0, 6)" :key="i">
            {{ user }}
          </div>
        </div>
        <div class="group-status">
          <span>{{ room.isReady ? '준비 완료' : '준비 중' }}</span>
        </div>
      </div>
      <div v-else class="inactive-content">
        <div class="inactive-icon">✖</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.group-list {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 20px;
  gap: 30px;
}

.group-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  border: 1px solid #00ff00;
  border-radius: 15px;
  width: 250px;
  height: 250px;
  padding: 0px 10px 0px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.group-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.group-card.ready .group-status {
  background-color: #1f4f16;
  padding: 10px;
}

.group-card.preparing .group-status {
  background-color: #00d200;
  padding: 10px;
}

.group-card.inactive {
  background-color: #f8f8f8;
  border: 1px dashed #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.inactive-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  font-size: 70px;
  color: #ff0000;
}

.group-status {
  background-color: #364b41;
  color: white;
  height: 40px;
  font-size: 18px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 10px;
}

.group-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #e0e0e0;
}

.group-name {
  font-size: 20px;
  font-weight: bold;
}

.group-count {
  font-size: 16px;
}

.group-members {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 5px;
  margin-top: 10px;
  flex-grow: 1;
}

.user-item {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  background-color: #f0f0f0;
  padding: 5px;
  border-radius: 5px;
}
</style>
