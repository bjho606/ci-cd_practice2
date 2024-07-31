<!-- eslint-disable no-unused-vars -->
<!-- 공통 버튼 컴포넌트 -->
<script setup>
import { defineProps } from 'vue'

const props = defineProps({
  text: {
    type: String,
    required: true
  },
  color: {
    type: String,
    default: '#3ABF38' // Default color if not provided
  },
  size: {
    type: String,
    default: 'medium' // Default size if not provided
  },
  variant: {
    type: String,
    default: 'tonal' // Default variant if not provided
  }
})
</script>

<template>
  <v-btn :variant="variant" :color="color" :size="size" rounded="lg">
    {{ text }}
  </v-btn>
</template>

<style scoped></style>
