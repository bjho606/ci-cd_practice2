<script setup>
  import { computed, defineComponent } from 'vue';
  import { useUserStore } from '@/stores/userStore'
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

  // const getConnectionData = () => {
  //   console.log(props.streamManager.stream, '이규석')
  //   const { connection } = props.streamManager.stream
  //   return JSON.parse(connection.data);
  // };

  // const clientData = computed(() => {
  //   const { clientData } = getConnectionData();
  //   return clientData;
  // });

</script>

<template>
  <div v-if="streamManager">
    <ov-video :stream-manager="streamManager" />
     <!-- 이거 닉네임 수정 로직 구현해야 함 -->
    <!-- <div><p>{{ clientData }}</p></div> -->
  </div>
</template>
