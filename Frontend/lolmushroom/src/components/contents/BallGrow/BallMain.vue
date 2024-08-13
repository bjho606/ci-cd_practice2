<script setup>
import { computed } from 'vue'
import { useSessionStore } from '@/stores/sessionStore'
import { useBallStore } from '@/stores/ballStore'

const sessionStore = useSessionStore()
const ballStore = useBallStore()
const currentBall = computed(() => ballStore.getCurrentGroup)
const currentGroupName = computed(() => ballStore.getGroupName)
const currentGroupImage = computed(() => ballStore.getBallImage(currentBall.value))
</script>

<template>
  <div></div>
</template>

<style scoped></style>
