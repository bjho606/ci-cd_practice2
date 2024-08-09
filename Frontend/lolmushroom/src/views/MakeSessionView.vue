<!-- makegame.vue -->
<script setup>
import '@/assets/fonts.css'
import { ref, watch, onMounted } from 'vue'
import { useContentsStore } from '@/stores/contentsStore'

import SettingHeader from '@/components/setting/SettingHeader.vue'
import SettingContents from '@/components/setting/SettingContents.vue'
import SettingFooter from '@/components/setting/SettingFooter.vue'

/**
 * * @step : 현재 진행되고 있는 Meshroom Session의 단계에 대한 반응형 객체
 * * @headerTitle : SettingHeader Component에 표시될 제목에 대한 반응형 객체
 * * @contentsStore : Contents Pinia에 대한 객체 -> Contents Pinia에 대한 객체
 * IMP @selectedContents : Contents Curation 과정에 선택한 Contents 배열
 */
const step = ref(1)
const headerTitle = ref('Meshroom에 오신 걸 환영합니다')
const contentsStore = useContentsStore()
const selectedContents = ref([])

/**
 * IMP 1. SettingHeader Component에 표시되는 제목에 대한 Update CallBack()
 * * 1.1 watch를 통해 step의 변경을 감지하고, 각 step에 맞는 Title을 보여준다.
 */
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

/**
 * IMP 2. 다음 / 이전 단계로 이동하는 CallBack()
 */
const nextStep = () => {
  if (step.value < 3) step.value++
}
const prevStep = () => {
  if (step.value == 2) step.value--
}

/**
 * IMP 3. Selected Contents Array가 변경되면, 이를 반영한다.
 * @param newSelectedContents
 */
const updateSelectedContents = (newSelectedContents) => {
  selectedContents.value = newSelectedContents
}

onMounted(() => {})
</script>

<!-- IMP : Props와 Emit이 많이 일어나는 설계 -->
<!-- * :param = -> props를 보내주는 것 -->
<!-- * @param = -> emit을 받아주는 것 -->
<template>
  <v-container fluid class="setting-container">
    <!-- * Setting Header : Setting의 상단을 담당한다. -->
    <SettingHeader :title="headerTitle" :step="step"></SettingHeader>
    <!-- * Setting Contents : Setting 본문을 담당한다. -->
    <SettingContents
      :step="step"
      :nextStep="nextStep"
      :contents="contentsStore.contents"
      :selectedContents="selectedContents"
      @update-selected-contents="updateSelectedContents"
    />
    <!-- * Setting Footer : Setting의 하단을 담당한다. -->
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
