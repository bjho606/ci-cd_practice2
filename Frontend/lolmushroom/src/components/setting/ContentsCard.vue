<!-- eslint-disable no-unused-vars -->
<script setup>
const props = defineProps(['contents'])
</script>

<template>
  <v-card class="mx-auto" max-width="180">
    <v-img height="130px" width="130%" :src="contents.imgUrl"></v-img>
    <v-card-title class="title"> {{ contents.contentTitle }} </v-card-title>
  </v-card>
</template>

<style scoped>
.title {
  font-size: 12px;
  font-weight: bold;
}
</style>
