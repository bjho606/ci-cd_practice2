import { defineStore } from 'pinia'

// import router from '@/router'



// TOFStore 정의
export const useAlphabetStore = defineStore('alphabet', {
  state: () => ({
    totalUserCount: 0,
    submitUserCount: 0,
    targetUserToken: '',
    chosenArray: {
      "1": [],
      "2": [],
      "3": [],
      "4": [],
    },
    statements: []
  }),

  actions: {
    submitUserIncrease() {
      this.submitUserCount++
    },
    setTotalUser (userCount) {
      this.totalUserCount = userCount
    },
    addChosenData(user, chosen) {
      this.chosenArray[chosen].push(user)
    }
  },
})