<script setup>
import { computed } from 'vue'
import { useContentsStore } from '@/stores/contentsStore'
import HeaderComponent from './_1HeaderComponent.vue'
import ListItem from './_1ListItem.vue'

const contentsStore = useContentsStore()
const groupedContents = computed(() => contentsStore.getGroupedContents)

const pickContents = (item) => {
  contentsStore.setPickedContents(item)
}

const addContents = (item) => {
  contentsStore.addContent(item)
}
</script>

<template>
  <div class="contents-list">
    <HeaderComponent />
    <div v-for="(category, index) in groupedContents" :key="index" class="category-section">
      <div class="category-title">{{ category.category }}</div>
      <div class="category-items">
        <ListItem
          v-for="item in category.items"
          :key="item._id"
          :item="item"
          @pick="pickContents"
          @add="addContents"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.contents-list {
  width: 100%;
  max-width: 700px;
  margin: 0 auto;
  padding-left: 20px;
  padding-top: 20px;
  display: flex;
  flex-direction: column;
  align-items: flex-start; /* Align items to the start (left) */
}

.category-section {
  width: 97%;
  margin-top: 20px;
  display: flex;
  flex-direction: column;
}

.category-title {
  font-size: 1.2em;
  font-weight: bold;
  margin-bottom: 10px;
}
</style>
