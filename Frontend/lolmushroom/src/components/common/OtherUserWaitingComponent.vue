<script setup>
  const props = defineProps({
    total: {
      type: Number,
      required: true
    },
    current: {
      type: Number,
      default: 0,
    },
  })
</script>


<template>
  <!-- 컨텐츠 진행 중 다른 사용자를 기다리는 컴포넌트 -->
  <img src="../../assets/joker.gif" alt="joker" style="display: block;">
  <v-progress-linear
  color="black"
  indeterminate
  :height="12"
  />
  <h2 style="color: black">다른 사람을 잠시 기다려 주시기 바랍니다.</h2>
  <h2>{{ props.current }} / {{ props.total }}</h2>
</template>


<style scoped>

</style>