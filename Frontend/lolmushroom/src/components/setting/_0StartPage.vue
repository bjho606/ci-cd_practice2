<script setup>
import TitleComponent from './_0TitleComponent.vue'
import SessionStartCard from './_0SessionStartCard.vue'
</script>

<template>
  <v-container fluid class="start-page">
    <!-- Pink Top Half -->
    <v-row class="pink-section">
      <v-col>
        <TitleComponent />
      </v-col>
    </v-row>
    <div class="card-container">
      <SessionStartCard />
    </div>
    <!-- White Bottom Half -->
    <v-row class="white-section"> </v-row>
  </v-container>
</template>

<style scoped>
.start-page {
  height: 100vh;
}

.pink-section {
  background-color: #fff2f7;
  height: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.white-section {
  background-color: #ffffff;
  height: 50%;
}

.card-container {
  position: absolute;
  top: 47%;
  left: 50%;
  transform: translate(-50%, -50%);
}
</style>
