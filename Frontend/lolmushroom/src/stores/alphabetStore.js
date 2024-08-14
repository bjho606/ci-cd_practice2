import { defineStore } from 'pinia'

// import router from '@/router'



// TOFStore 정의
export const useAlphabetStore = defineStore('alphabet', {
  state: () => ({
    totalUserCount: 0,
    submitUserCount: 0,
  }),

  actions: {
    submitUserIncrease() {
      this.submitUserCount++
    },
    setTotalUser (userCount) {
      this.totalUserCount = userCount
    },
    submitUserClear() {
      this.submitUserCount = 0
    }
  },
})