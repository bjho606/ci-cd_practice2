import { defineStore } from 'pinia'

/**
 * IMP : SessionStore 정의
 * * sessionId ( Main Session )과 subSessionId을 저장하고 있음
 * * 이 정보는 Session Storage에 저장되고 있음.
 */
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
    getSubSessionId: (state) => state.subSessionId
  },
  persist: {
    key: 'session-info-store',
    storage: sessionStorage, // 세션 스토리지에 저장
    paths: ['sessionId', 'subSessionId'] // 저장할 상태의 경로
  }
})
