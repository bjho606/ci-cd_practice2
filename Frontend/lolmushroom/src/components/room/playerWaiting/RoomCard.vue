<script setup>
import { computed } from 'vue'
import { useSessionStore } from '@/stores/sessionStore'
import { useUserStore } from '@/stores/userStore'
import ReadyButton from './ReadyButton.vue'
import GroupName from './GroupName.vue'
import JoinButton from './JoinButton.vue'
import UserGroup from './UserGroup.vue'

const sessionStore = useSessionStore()
const userStore = useUserStore()
const props = defineProps({
  room: Object
})
const emits = defineEmits(['onReady', 'onChangeName', 'onJoinOrLeave'])
const handleReadyClick = () => {
  emits('onReady')
}
const handleChangeNameClick = () => {
  emits('onChangeName')
}
const handleJoinOrLeaveClick = () => {
  emits('onJoinOrLeave')
}

const isGroupTeamLeader = computed(() => {
  return sessionStore.subSessionId === props.room.sessionId && userStore.isTeamLeader
})
const isGroupMember = computed(() => {
  return sessionStore.subSessionId === props.room.sessionId
})
</script>

<template>
  <div class="group-card">
    <div :class="['group-info', { 'group-info--strong-border': isGroupMember }]">
      <ReadyButton
        :isReady="room.isReady"
        :isGroupTeamLeader="isGroupTeamLeader"
        @onClick="handleReadyClick"
      />
      <GroupName
        :groupName="room.groupName"
        :isGroupMember="isGroupMember"
        @onClick="handleChangeNameClick"
      />
      <JoinButton :isGroupMember="isGroupMember" @onClick="handleJoinOrLeaveClick" />
    </div>
    <UserGroup :users="room.users" :groupName="room.groupName" />
  </div>
</template>

<style scoped>
.group-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
  width: 100%;
  background-color: #fff2f7;
}

.group-info--strong-border {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.5);
}
</style>
