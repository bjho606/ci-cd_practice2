import { defineStore } from 'pinia'

// userStore 정의
export const useUserStore = defineStore('user', {
  state: () => ({
    userNickname: '닉네임을 설정해주세요',
    userTOFstatements: {},    
  }),
  actions: {
    setNickname(nickname) {
      this.userNickname = nickname
    }
  },
  persist: {
    key: 'user-store',
    storage: sessionStorage,
    paths: ['userNickname', 'userTOFstatements']
  }
})
