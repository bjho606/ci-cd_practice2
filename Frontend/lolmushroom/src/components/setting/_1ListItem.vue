<script setup>
const props = defineProps({
  item: Object
})

const emit = defineEmits(['pick', 'add'])
const onPickClick = () => {
  emit('pick', props.item)
}

const onAddClick = () => {
  emit('add', props.item)
}
</script>

<template>
  <div class="item-content" @click="onPickClick">
    <v-icon class="item-icon">{{ item.icon }}</v-icon>
    <span class="item-text">{{ item.contentTitle }}</span>
    <v-icon class="item-add" @click.stop="onAddClick">mdi-plus</v-icon>
  </div>
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

.item-add {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background-color: white; /* 초록색 배경 */
  border-radius: 50%;
  cursor: pointer;
  color: #247719;
  font-size: 19px;
  transition: background-color 0.3s;
}

.item-add:hover {
  background-color: #247719; /* hover 시 조금 더 어두운 초록색 */
  color: white; /* hover 시 조금 더 어두운 초록색 */
}
</style>
