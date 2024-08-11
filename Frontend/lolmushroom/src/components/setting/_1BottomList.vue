<script setup>
import { computed, ref, watch } from 'vue'
import { useContentsStore } from '@/stores/contentsStore'
import { VueDraggableNext } from 'vue-draggable-next'
import SelectedCard from './_1SelectedCard.vue'

const contentsStore = useContentsStore()
const selectedContents = computed(() => contentsStore.getSelectedContents)
const selectedContentsTest = ref([...selectedContents.value]) // 초기값을 설정

watch(
  selectedContents,
  (newVal) => {
    selectedContentsTest.value = [...newVal] // selectedContents가 변경될 때마다 업데이트
  },
  { deep: true }
) // deep: true 옵션 추가

const removeContent = (item) => {
  contentsStore.removeSelectedContent(item._id)
}

// 드래그가 완료된 후 호출될 이벤트 핸들러 추가 (선택사항)
const onMove = (evt) => {
  // 드래그 중 발생하는 로직 추가 가능
}
</script>

<template>
  <div class="bottom-list">
    <VueDraggableNext
      v-model="selectedContentsTest"
      item-key="_id"
      tag="transition-group"
      class="draggable-container"
      @move="onMove"
    >
      <div v-for="(each, index) in selectedContentsTest" :key="each._id" class="draggable-item">
        <SelectedCard :item="each" :order="index + 1" @remove="removeContent(each)" />
      </div>
    </VueDraggableNext>
  </div>
</template>

<style scoped>
.bottom-list {
  display: flex;
  width: 100%;
  padding: 10px;
  background-color: #f0f0f0;
  overflow-x: auto;
}

.draggable-container {
  display: flex;
  gap: 10px;
}

.draggable-list {
  display: flex;
  gap: 20px;
}

.draggable-item {
  flex: 0 0 auto;
  cursor: move;
}

/* 애니메이션 정의 */
.list-enter-active,
.list-leave-active {
  transition: all 0.5s ease;
}

.list-enter,
.list-leave-to {
  transform: scale(0.9);
  opacity: 0;
}
</style>
