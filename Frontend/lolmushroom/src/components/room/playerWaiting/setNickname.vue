<script setup>
import { ref, computed, watch } from 'vue';
import { useUserStore } from '@/stores/userStore';

// emit 설정
const props = defineProps({ show: Boolean })
const emits = defineEmits(['update:show', 'nickname-saved'])


const nickname = ref('');
const required = value => !!value || '닉네임을 입력하세요';
// 입력이 안되면 제출 버튼 금지
const isFormValid = computed(() => !!nickname.value);
const userStore = useUserStore();

// 창으로 뜨기

watch(
  () => props.show,
  (newVal) => {
    nicknameDialog.value = newVal;
  }
);

const nicknameDialog = ref(props.show)



const createNickname = () => {
  if (nickname.value.trim() !== '') {
    userStore.setNickname(nickname.value);
    emits('nickname-saved')
    closeModal();
  }
};

const closeModal = () => {
  emits('update:show', false)
}

</script>

<template>
  <v-container class="out-box">
    <v-container class="text-bold text-h4">
      닉네임 설정하기
    </v-container>
    <v-text-field
      v-model="nickname"
      label="닉네임 입력"
      variant="solo-filled"
      width="400"
      :rules="[required]"
    ></v-text-field>
    <v-btn 
      @click="createNickname"
      :disabled="!isFormValid"
      min-width="200"
      type="submit"
      variant="elevated"
      :style="{ backgroundColor: '#CEFFBC', color: '#000' }"
      class=" text-bold submit-button"
    >
      완료
    </v-btn>
  </v-container>
</template>

<style scoped>
.out-box {
  width: 800px;
  height: 450px;
  background-color: #1ed013;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  padding: 30px;
  font-weight: bold;
}

.text-bold {
  margin-bottom: 40px;
  font-weight: bold;
  font-size: 24px; 
}

.submit-button {
  border-radius: 30px;
  margin-bottom: 40px;
  min-width: 150px;
  min-height: 50px;
}
</style>
