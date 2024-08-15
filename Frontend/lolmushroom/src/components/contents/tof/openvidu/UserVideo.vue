<script setup>
  import { computed } from 'vue';
  import OvVideo from './OvVideo.vue';

  // props 선언
  const props = defineProps({
    streamManager: Object,
  });

  const getConnectionData = () => {
    console.log('Connection Data:', props.streamManager.stream.connection.data);
    const { connection } = props.streamManager.stream
    return connection.data;
  };

  const clientData = computed(() => {
    const { clientData } = getConnectionData();
    return clientData;
  });

</script>

<template>
  <div v-if="props.streamManager" class="d-flex" style="justify-content: center;">
    <ov-video :stream-manager="props.streamManager"/>
    <div><p>{{ clientData }}</p></div>
  </div>
</template>

