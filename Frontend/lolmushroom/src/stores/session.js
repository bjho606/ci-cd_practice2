import { defineStore } from 'pinia'

export const useSessionStore = defineStore('sessionInfo', {
  state: () => ({
    sessionId: null,
    subSessionId: null
  }),
  actions: {
    setSessionId(sessionId) {
      this.sessionId = sessionId
    },
    setSubSessionId(subSessionId) {
      this.subSessionId = subSessionId
    }
  },
  getters: {
    getSubSessionId: (state) => state.subSessionId,
  },
  persist: {
    key: 'session-info-store',
    storage: sessionStorage, // 세션 스토리지에 저장
    paths: ['sessionId', 'subSessionId'] // 저장할 상태의 경로
  }
})
