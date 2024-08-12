<script setup>
const props = defineProps({
  item: Object,
  order: Number
})

const emit = defineEmits(['remove'])

const onRemoveClick = () => {
  emit('remove', props.item)
}
</script>

<template>
  <div class="content-card">
    <img :src="item.imgUrl" alt="Content Thumbnail" class="thumbnail" />
    <button class="delete-btn" @click="onRemoveClick">✖</button>
    <div class="content-info">
      <div>{{ item.contentTitle }}</div>
    </div>
  </div>
</template>

<style scoped>
.content-card {
  position: relative;
  width: 130px;
  background-color: #f5f5f5;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: transform 0.2s;
}

.content-card:hover {
  transform: scale(1.05);
}

.thumbnail {
  width: 90%;
  height: auto;
  border-radius: 5px;
}

.content-info {
  display: flex;
  font-size: 12px;
  margin-top: 5px;
  justify-content: center;
}

.delete-btn {
  position: absolute;
  top: -7px;
  right: -7px;
  width: 24px;
  height: 24px;
  background-color: #fff2f7;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 16px;
  color: red;
  line-height: 24px;
  text-align: center;
  padding: 0;
}
</style>
