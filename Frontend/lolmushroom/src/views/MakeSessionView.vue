<!-- makegame.vue -->
<script setup>
import { ref, watch, onMounted } from 'vue'
import { useContentsStore } from '@/stores/contentsStore'
import SettingHeader from '@/components/setting/SettingHeader.vue'
import SettingContents from '@/components/setting/SettingContents.vue'
import SettingFooter from '@/components/setting/SettingFooter.vue'
import '@/assets/fonts.css'

// Setting Step
const step = ref(1)
const headerTitle = ref('Meshroom에 오신 걸 환영합니다')
const updateTitle = () => {
  switch (step.value) {
    case 1:
      headerTitle.value = 'Meshroom에 오신 걸 환영합니다'
      break
    case 2:
      headerTitle.value = '우리에게 딱 맞는 컨텐츠를 클릭하세요'
      break
    case 3:
      headerTitle.value = '방 링크를 공유하세요!'
      break
    default:
      headerTitle.value = 'Meshroom에 오신 걸 환영합니다'
  }
}
watch(step, updateTitle)

const nextStep = () => {
  if (step.value < 3) step.value++
}
const prevStep = () => {
  if (step.value == 2) step.value--
}

const contentsStore = useContentsStore()
const selectedContents = ref([])
const updateSelectedContents = (newSelectedContents) => {
  selectedContents.value = newSelectedContents
}

onMounted(() => {})
</script>

<template>
  <v-container fluid class="setting-container">
    <SettingHeader :title="headerTitle" :step="step"></SettingHeader>
    <SettingContents
      :step="step"
      :nextStep="nextStep"
      :contents="contentsStore.contents"
      :selectedContents="selectedContents"
      @update-selected-contents="updateSelectedContents"
    />
    <SettingFooter
      :nextStep="nextStep"
      :prevStep="prevStep"
      :step="step"
      :selectedContents="selectedContents"
    />
  </v-container>
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
