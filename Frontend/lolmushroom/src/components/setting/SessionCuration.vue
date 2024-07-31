<!-- eslint-disable no-unused-vars -->
<script setup>
import { ref, watch } from 'vue'
import { VueDraggableNext } from 'vue-draggable-next'
import ContentsCard from '@/components/setting/ContentsCard.vue'

const props = defineProps({
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

const addSelection = (content) => {
  const index = localSelectedContents.value.findIndex((c) => c._id === content._id)
  if (index === -1) localSelectedContents.value.push(content)
}

const removeSelection = (content) => {
  const index = localSelectedContents.value.findIndex((c) => c._id === content._id)
  if (index !== -1) localSelectedContents.value.splice(index, 1)
}

const log = (event) => {
  console.log(event)
}
</script>

<template>
  <v-sheet class="mx-auto" elevation="8" max-width="1500">
    <!-- 상단 Contents Group -->
    <v-slide-group
      v-model="localSelectedContents"
      class="pa-4"
      selected-class="bg-success"
      show-arrows
    >
      <v-slide-group-item v-for="each in contents" :key="each.id" v-slot="{ selectedClass }">
        <v-card
          :class="['ma-4', selectedClass, 'content-card']"
          color="grey-lighten-1"
          @click="addSelection(each)"
        >
          <ContentsCard :contents="each" />
        </v-card>
      </v-slide-group-item>
    </v-slide-group>

    <!-- 하단 슬라이드 그룹 -->
    <v-slide-group class="pa-4" v-model="localSelectedContents" show-arrows>
      <VueDraggableNext v-model="localSelectedContents" @change="log" item-key="id" class="d-flex">
        <v-slide-group-item v-for="each in localSelectedContents" :key="each.id">
          <v-card
            :class="['ma-4', 'content-card']"
            color="grey-lighten-1"
            @click="removeSelection(each)"
          >
            <ContentsCard :contents="each" />
          </v-card>
        </v-slide-group-item>
      </VueDraggableNext>
    </v-slide-group>
  </v-sheet>
</template>

<style scoped>
.v-card {
  overflow: hidden;
}
.content-card {
  width: 100%; /* Adjust to fit the container */
  height: 80%; /* Adjust to fit the container */
  max-width: 180px; /* Adjust based on your layout */
  max-height: 180px; /* Adjust based on your layout */
  text-align: center;
}
</style>
