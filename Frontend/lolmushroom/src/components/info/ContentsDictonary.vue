<!-- eslint-disable no-unused-vars -->
<script setup>
const props = defineProps({
  contents: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['close'])
const closeDictionary = () => {
  emit('close')
}
</script>

<template>
  <v-card>
    <v-card-title>
      <h2 class="text-center font-weight-black">🍄 컨텐츠 도감 🍄</h2>
      <h6>📕 아이스 브레이킹 컨텐츠를 확인하고 습득하세요!</h6>
    </v-card-title>
    <v-divider :thickness="4" color="blue"></v-divider>

    <v-card-text>
      <v-container>
        <v-row>
          <v-col v-for="content in contents" :key="content._id" cols="12" md="4">
            <v-card>
              <v-img :src="content.imgUrl" height="200px" cover></v-img>
              <v-card-title class="indigo--text">{{ content.contentTitle }}</v-card-title>
              <br />
              <v-card-text>
                <p
                  v-for="(sentence, index) in content.description.split('.')"
                  :key="index"
                  class="description-sentence"
                >
                  {{ sentence.trim() }}
                </p>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </v-card-text>

    <v-card-actions>
      <v-btn @click="closeDictionary">닫기</v-btn>
    </v-card-actions>
  </v-card>
</template>

<style scoped>
.indigo--text {
  text-align: center;
  color: black !important;
  background-image: linear-gradient(
    109.6deg,
    rgba(3, 195, 187, 1) 11.2%,
    rgba(180, 196, 255, 1) 91.1%
  );
}
</style>
