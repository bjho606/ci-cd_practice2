import { defineStore } from 'pinia'

export const useChatStore = defineStore('chat', {
  state: () => ({
    mainSessionMessages: [],
    subSessionMessages: [],
    currentMode: 'All'
  }),
  actions: {
    addMainSessionMessage(message) {
      this.mainSessionMessages.push(message)
    },
    addSubSessionMessage(message) {
      this.subSessionMessages.push(message)
    },
    setCurrentMode(mode) {
      this.currentMode = mode
    }
  },
  persist: {
    key: 'chat-store',
    storage: sessionStorage, // 세션 스토리지에 저장
    paths: ['mainSessionMessages', 'subSessionMessages', 'currentMode'] // 저장할 상태의 경로
  }
})
