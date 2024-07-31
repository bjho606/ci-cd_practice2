<script setup>
import { computed, defineComponent } from 'vue';
import OvVideo from './OvVideo.vue';

// props 선언
const props = defineProps({
  streamManager: Object,
});

// 컴포넌트 등록
defineComponent({
  components: {
    OvVideo,
  },
});

const getConnectionData = () => {
  const { connection } = props.streamManager.stream;
  return JSON.parse(connection.data);
};

const clientData = computed(() => {
  const { clientData } = getConnectionData();
  return clientData;
});
</script>

<template>
  <div v-if="streamManager">
    <ov-video :stream-manager="streamManager" />
    <div><p>{{ clientData }}</p></div>
  </div>
</template>
