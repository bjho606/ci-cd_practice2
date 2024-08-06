import { defineStore } from 'pinia'
import { ref } from 'vue'

// userStore 정의
export const useUserStore = defineStore('user', {
  state: () => ({
    userOvToken: ref('undefined'),
    userNickname: '닉네임을 설정해주세요',
    isTeamLeader: false,
  }),
  actions: {
    setNickname(nickname) {
      this.userNickname = nickname
    },
    setTeamLeader(isTeamLeader) {
      this.isTeamLeader = isTeamLeader
    }
  },
  persist: {
    key: 'user-store',
    storage: sessionStorage,
    paths: ['userNickname', 'userOvToken', 'isTeamLeader']
  }
})
