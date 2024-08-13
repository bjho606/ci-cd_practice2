<script setup>
import { ref } from 'vue'

const props = defineProps({
  item: Object,
  isItemSelected: Boolean
})

const emit = defineEmits(['pick', 'add'])

const isDialogVisible = ref(false)
const onPickClick = () => {
  emit('pick', props.item)
}
const onAddClick = () => {
  if (!props.item.isActive) {
    isDialogVisible.value = true
  } else {
    emit('add', props.item)
  }
}
</script>

<template>
  <div class="item-content" @click="onPickClick">
    <v-icon class="item-icon">{{ item.icon }}</v-icon>
    <span class="item-text">
      {{ item.contentTitle }}
    </span>
    <span v-if="isItemSelected" class="selected-text">선택 완료</span>
    <v-icon
      class="item-add"
      :class="{ disabled: !item.isActive || isItemSelected }"
      @click.stop="onAddClick"
    >
      mdi-plus
    </v-icon>
  </div>

  <!-- Dialog for inactive item -->
  <v-dialog v-model="isDialogVisible" max-width="350">
    <v-card>
      <v-card-title class="headline justify-center">
        <v-icon left color="green darken-2">mdi-leaf</v-icon>
        잠시만요!
      </v-card-title>
      <v-card-text class="text-center">
        🌱 이 컨텐츠는 아직 준비 중이에요. <br />조금만 기다려 주세요!
      </v-card-text>
      <v-card-actions class="justify-center">
        <v-btn color="green darken-2" text @click="isDialogVisible = false"> 확인 </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped>
.item-content {
  display: flex;
  align-items: center;
  background-color: #b3d879;
  border-radius: 20px;
  padding: 10px 20px;
  margin-bottom: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s;
}

.item-content:hover {
  background-color: #a4ca63;
  transform: scale(1.05);
}

.item-icon {
  margin-right: 10px;
}

.item-text {
  flex-grow: 1;
}

.selected-text {
  font-weight: bold;
  margin-right: 10px;
}

.item-add {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background-color: white;
  border-radius: 50%;
  cursor: pointer;
  color: #247719;
  font-size: 19px;
  transition: background-color 0.3s;
}

.item-add:hover {
  background-color: #247719;
  color: white;
}

.item-add.disabled {
  background-color: #e0e0e0;
  color: #a0a0a0;
  cursor: not-allowed;
}

.item-add.disabled:hover {
  background-color: #e0e0e0;
  color: #a0a0a0;
}
</style>
