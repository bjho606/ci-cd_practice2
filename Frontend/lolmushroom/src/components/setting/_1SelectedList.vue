<script setup>
import { computed, ref, watch } from 'vue'
import { useContentsStore } from '@/stores/contentsStore'
import { VueDraggableNext } from 'vue-draggable-next'
import SelectedCard from './_1SelectedCard.vue'
import SessionStartButton from './_1SessionStartButton.vue'

const contentsStore = useContentsStore()
const selectedContents = computed(() => contentsStore.getSelectedContents)
const localSelectedContents = ref(selectedContents.value)

watch(
  selectedContents,
  (newVal) => {
    localSelectedContents.value = newVal
  },
  { deep: true }
)

const removeContent = (item) => {
  contentsStore.removeContent(item)
}
const onEnd = () => {
  contentsStore.setSelectedContents(localSelectedContents.value)
}
</script>

<template>
  <div class="bottom-list">
    <VueDraggableNext
      v-model="localSelectedContents"
      item-key="_id"
      class="draggable-container"
      @end="onEnd"
    >
      <div
        v-for="(each, index) in localSelectedContents"
        :key="each._id"
        class="draggable-item-container"
      >
        <SelectedCard :item="each" :order="index + 1" @remove="removeContent" />
        <div v-if="index < localSelectedContents.length - 1" class="arrow-container">→</div>
      </div>
    </VueDraggableNext>
    <SessionStartButton v-if="localSelectedContents.length > 0" />
  </div>
</template>

<style scoped>
.bottom-list {
  display: flex;
  justify-content: space-between;
  gap: 40px;
  width: 100%;
  padding: 40px;
}

.draggable-container {
  display: flex;
}

.draggable-item-container {
  display: flex;
  align-items: center;
}

.arrow-container {
  display: flex;
  font-size: 24px;
  color: #888;
  margin-left: 10px;
  margin-right: 10px;
}

.draggable-item {
  flex: 0 0 auto;
  cursor: move;
}

.session-button {
  background-color: #4caf50;
  color: white;
  cursor: pointer;
  border-radius: 5px;
}
</style>
