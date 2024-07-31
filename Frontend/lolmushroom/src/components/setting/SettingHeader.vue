<!-- eslint-disable no-unused-vars -->
<script setup>
import ButtonComponent from '@/components/common/ButtonComponent.vue'
import NicknameModal from '@/components/common/NicknameModal.vue'
import { ref } from 'vue'

const NicknameContents = ref(false)
const makeNickname = ref('닉네임 생성하기')

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  step: {
    type: Number,
    required: true
  }
})

const openNickname = () => {
  NicknameContents.value = true
}
const closeNickname = () => {
  NicknameContents.value = false
}
</script>

<template>
  <v-container class="white-part">
    <h1 class="container-title">{{ title }}</h1>
    <ButtonComponent
      v-if="step === 1"
      :text="makeNickname"
      :color="'#DBAC03'"
      @click="openNickname"
    />
  </v-container>
  <v-dialog v-model="NicknameContents" max-width="1600px">
    <NicknameModal
      :show="NicknameContents"
      @update:show="NicknameContents = $event"
      @close="closeNickname"
    />
  </v-dialog>
</template>

<style scoped>
.white-part {
  background-color: #ffffff;
  height: 70px;
  display: flex;
  justify-content: space-between;
}
.container-title {
  font-size: 25px;
  font-weight: bold;
}
</style>
