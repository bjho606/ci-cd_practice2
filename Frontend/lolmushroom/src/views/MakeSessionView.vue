<!-- makegame.vue -->
<script setup></script>

<template>
  <RouterView />
</template>

<style scoped>
.setting-container {
  display: flex; /* Use Flexbox for layout */
  flex-direction: column;
  justify-content: center; /* Center horizontally */
  align-items: center; /* Center vertically */
  position: absolute; /* Position absolutely within parent */
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
}
</style>
