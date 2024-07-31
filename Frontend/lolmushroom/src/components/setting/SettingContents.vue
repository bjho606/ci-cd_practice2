<!-- SettingContents.vue -->
<script setup>
import { ref, watch } from 'vue'
import SessionStartCard from '@/components/setting/SessionStart.vue'
import SessionCuration from '@/components/setting/SessionCuration.vue'
import SessionCode from '@/components/setting/SessionCode.vue'
const props = defineProps({
  step: {
    type: Number,
    required: true
  },
  nextStep: {
    type: Function,
    required: true
  },
  contents: {
    type: Array,
    required: true
  },
  selectedContents: {
    type: Array,
    required: true
  }
})

const localSelectedContents = ref(props.selectedContents)
const emit = defineEmits(['update-selected-contents'])
watch(localSelectedContents, (newVal) => {
  emit('update-selected-contents', newVal)
})
</script>

<template>
  <v-container class="gray-wrapper">
    <template v-if="step === 1">
      <SessionStartCard :next-step="nextStep" />
    </template>
    <template v-else-if="step === 2">
      <SessionCuration
        :contents="contents"
        :selected-contents="localSelectedContents"
        @update-selected-contents="localSelectedContents = $event"
      />
    </template>
    <template v-else-if="step === 3">
      <SessionCode />
    </template>
  </v-container>
</template>

<style scoped>
.gray-wrapper {
  background-color: #e0e0e0;
}
</style>
