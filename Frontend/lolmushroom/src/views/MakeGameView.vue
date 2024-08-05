<!-- eslint-disable no-unused-vars -->
<script setup>
import { ref } from 'vue'
import SettingHeader from '@/components/setting/SettingHeader.vue'
import SettingContents from '@/components/setting/SettingContents.vue'
import SettingFooter from '@/components/setting/SettingFooter.vue'
import '@/assets/fonts.css'
const step = ref(1)
const nextStep = () => {
  if (step.value < 3) step.value++
}
const prevStep = () => {
  if (step.value > 1) step.value--
}
</script>

<template>
  <SettingHeader title="새로운 세션을 만들어 보세요"> </SettingHeader>
  <SettingContents :step="step" />
  <SettingFooter :nextStep="nextStep" :prevStep="prevStep" />
</template>

<style scoped></style>
