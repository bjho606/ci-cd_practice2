<template>
  <v-container fluid class="main-container">
    <!-- Top - Left side with game info and controls -->
    <v-sheet class="top-container">
      <StatusBar title="Game Info" status="In Progress" />
    </v-sheet>

    <!-- Bottom - Two columns: Main Mushroom and Other Mushrooms -->
    <v-row class="bottom-container">
      <v-col cols="12" md="6">
        <v-card class="main-mushroom-card">
          <StatusBar title="Main Mushroom" status="Growing" />
          <v-img :src="mainMushroomImage" aspect-ratio="1" class="mushroom-image" />
          <v-card-title class="text-center">Current Level: {{ mainMushroomLevel }}</v-card-title>
          <v-card-subtitle class="text-center">Size: {{ mainMushroomSize }}vw</v-card-subtitle>
          <v-card-actions class="justify-center">
            <v-btn v-if="currentGroup !== 'User Group'" @click="onReturnClick">Return</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
      <v-col cols="12" md="6" class="right-container">
        <v-row>
          <v-col v-for="(mushroom, index) in otherMushrooms" :key="index" cols="12" sm="6">
            <MushroomCard :mushroom="mushroom" />
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref } from 'vue'
import StatusBar from './StatusBar.vue'
import MushroomCard from './MainMushroom.vue'

const mainMushroomImage = ref('path_to_main_mushroom_image')
const mainMushroomLevel = ref(1)
const mainMushroomSize = ref(30)
const timeElapsed = ref('00:00:00')
const otherMushrooms = ref([
  { image: 'path_to_mushroom_1', level: 2, size: 20 },
  { image: 'path_to_mushroom_2', level: 3, size: 25 },
  { image: 'path_to_mushroom_3', level: 4, size: 22 }
])
</script>

<style scoped>
.main-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  gap: 16px;
}

.top-container {
  flex: 0 1 auto;
  margin-top: 3%;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.bottom-container {
  flex: 1 1 auto;
  display: flex;
  overflow-y: auto;
}

.main-mushroom-card {
  height: 100%;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: auto;
}

.mushroom-image {
  width: 50%;
  margin: 20px auto;
}

.right-container {
  display: flex;

  /* gap: 16px; */
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
</style>
