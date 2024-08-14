<!-- UserGroup.vue -->
<script setup>
const props = defineProps({
  users: Array,
  groupName: String
})
</script>

<template>
  <div v-if="users.length > 0" class="user-list">
    <v-btn v-for="user in users" :key="user" class="user-btn" min-width="180">
      {{ groupName }}
      <div>
        {{ user }}
      </div>
    </v-btn>
  </div>
</template>

<style scoped>
.user-list {
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  margin-top: 20px;
}

.user-btn {
  background-color: #ceffbc;
  width: 100%;
  font-size: 12px;
  padding: 5px 10px;
  margin-bottom: 10px;
}
</style>
