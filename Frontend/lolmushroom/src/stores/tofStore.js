import { defineStore } from 'pinia'

export const useTOFStore = defineStore('tof', {
  state: () => ({
    totalUserCount: 0,
    submitUserCount: 0,
    targetUserToken: '',
    chosenArray: {
      1: [],
      2: [],
      3: [],
      4: []
    },
    statements: []
  }),

  actions: {
    submitUserIncrease() {
      this.submitUserCount++
    },
    addChosenData(user, chosen) {
      this.chosenArray[chosen].push(user)
    }
  }
})
