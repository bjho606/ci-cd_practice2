<!-- GroupName.vue -->
<script setup>
const props = defineProps({
  groupName: String,
  isGroupMember: Boolean
})
const emit = defineEmits(['onClick'])
const handleClick = () => {
  if (!props.isGroupMember) {
    console.log('Group Member가 아니라면, 함부로 다른 Group의 이름을 바꿀 수 없어요:)')
  } else {
    emit('onClick')
  }
}
</script>

<template>
  <v-btn class="group-name" @click="handleClick">
    <v-img src="src/assets/image/autorenew.svg" class="icon" />
    {{ groupName }}
  </v-btn>
</template>

<style scoped>
.group-name {
  background-color: #fff58a64;
  font-size: larger;
  font-weight: bold;
  min-height: 60px;
  margin-bottom: 10px;
  width: 100%;
}

.icon {
  width: 20px;
  margin-right: 10px;
}
</style>
