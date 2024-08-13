<script setup>
const emits = defineEmits(['createGroup'])
const handleCreateGroupClick = () => {
  emits('createGroup')
}
</script>

<template>
  <v-btn
    min-width="150px"
    varient="elevated"
    :style="{ backgroundColor: '#CEFFBC' }"
    class="add-group-button"
    @click="handleCreateGroupClick"
  >
    그룹 새로 만들기
  </v-btn>
</template>

<style scoped>
.add-group-button {
  margin-top: 45px;
}
</style>
