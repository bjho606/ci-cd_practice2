<script setup>
import { computed } from 'vue'
import { useSessionStore } from '@/stores/sessionStore'
import QrcodeVue from 'qrcode.vue'
import ButtonComponent from '@/components/common/ButtonComponent.vue'

const { VITE_BASE_URL } = import.meta.env

const sessionStore = useSessionStore()
const sessionURL = computed(() => {
  return `${VITE_BASE_URL}/${sessionStore.sessionId}`
})

const copyToClipboard = () => {
  navigator.clipboard.writeText(sessionURL.value)
  alert('URL이 복사되었습니다.')
}
</script>

<template>
  <v-container>
    <v-row>
      <v-col cols="6" align-self="center">
        <qrcode-vue :value="sessionURL" :size="300" level="H" />
      </v-col>
      <v-col cols="6">
        <v-text-field
          v-model="sessionURL"
          label="참여 코드"
          prepend-inner-icon="mdi-link"
          readonly
        ></v-text-field>
        <ButtonComponent text="복사하기" color="blue" size="large" @click="copyToClipboard" />
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped></style>
