<template>
  <vue-countdown :time="`${props.time}` * 1000" v-slot="{ seconds }" @end="endCountDown()">
    <h1 style="font-size: 80px;">{{ seconds }}초 {{ props.text1 }}</h1>
    <h1 style="font-size: 80px;">{{ props.text2 }}</h1>
    
  </vue-countdown>
</template>

<script setup>

const emit = defineEmits(['endCountDown'])

const props = defineProps({
  time: {
    type: String,
    required: true
  },
  text1: {
    type: String,
    required: false
  },
  text2: {
    type: String,
    required: false
  },
})

const endCountDown = () => {
  emit('endCountDown', true)
}
</script>
