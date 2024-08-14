<script setup>
import CountDownComponent from './CountDownComponent.vue'
import ContentsInfo from './ContentsInfo.vue'

const props = defineProps({
  contentsInfo: {
    type: Object,
    required: true
  },
  time: {
    // CountDown Number
    type: String,
    required: true
  },
  countText: {
    // CountDown Text
    type: String,
    required: true
  }
})
const title = props.contentsInfo.contentTitle
const buttonText = props.contentsInfo.category
const instructionsText = props.contentsInfo.description
const contentImage = props.contentsInfo.imgUrl
</script>

<template>
  <ContentsInfo
    :title="title"
    :buttonText="buttonText"
    :contentImage="contentImage"
    :instructionsText="instructionsText"
  />
  <CountDownComponent :time="time" :text="countText" />
</template>
