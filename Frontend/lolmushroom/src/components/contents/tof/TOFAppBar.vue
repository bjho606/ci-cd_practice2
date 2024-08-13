<script setup>
  const props = defineProps({
    title: String,
    subtitle: String,
    text: String,
  })
</script>

<template>
  <!-- TOF 전용 헤더 -->
  <header class="d-flex flex-row align-center justify-space-between box-layout">
    <div class="box first elevation-8">
      <div class="content-wrapper">
        <v-img src="../../../../src/assets/image/Smillyface.svg" class="smile-image"/>
        <strong>{{ props.title }}</strong>
      </div>
    </div>
    <div class="box second elevation-8">
      <p>{{ props.subtitle }}</p>
    </div>
    <div class="box third elevation-8">
      {{ props.text }}
    </div>
  </header>
</template>

<style scoped>
  .box-layout {
      padding: 10px;
    }
  .box {
    flex: 1;
    text-align: center;
    font-weight: bold;
    padding: 20px;
    border-radius: 5px;
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 70px; 
  }
  .first {
    background-color: #90FF77;
  }
  .second {
    background-color: #CEFFBC;
  }
  .third {
    background-color: #E7FFDE;
  }
  .content-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .smile-image {
    width: 30px;
    height: 30px;
    margin-right: 10px;
  }
</style>