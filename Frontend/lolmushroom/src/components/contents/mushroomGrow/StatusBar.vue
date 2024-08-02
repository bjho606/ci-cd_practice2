<template>
  <v-sheet class="status-bar" elevation="2">
    <v-row align="center" justify="space-between">
      <v-col cols="auto">
        <v-icon>mdi-information</v-icon>
        <span>{{ title }}</span>
      </v-col>
      <v-col cols="auto">
        <span>{{ status }}</span>
      </v-col>
    </v-row>
  </v-sheet>
</template>

<script setup>
const props = defineProps({
  title: {
    type: String,
    required: true
  },
  status: {
    type: String,
    required: true
  }
})
</script>

<style scoped>
.status-bar {
  width: 100%;
  padding: 8px 16px;
  background-color: #fafafa;
  border-bottom: 1px solid #e0e0e0;
}
</style>
