import { defineStore } from 'pinia'
import webSocketAPI from '@/api/webSocket'
import blueBall from '@/assets/ballGrow/blueBall.svg'
import greenBall from '@/assets/ballGrow/greenBall.svg'

export const useBallStore = defineStore('ballStore', {
  state: () => ({
    // IMP : User Group Check & Total Group Info
    userGroup: null,
    currentGroup: null,
    groupNameMap: new Map(),
    ballMap: new Map()
  }),
  actions: {
    /**
     * IMP 1. BallGrow Game에 대한 Subscription 진행
     * * 1.1 ballMap에 대한 초기화
     * * 1.2 userGroup과 currentGroup 초기화
     * * 1.3 ballGrow Contents에 대한 Subscription 진행
     */
    initSocketConnection(sessionId, subSessionId, groups) {
      groups.forEach((group) => {
        this.groupNameMap.set(group.sessionId, group.groupName)
        this.ballMap.set(group.sessionId, 20)
      })
      this.userGroup = this.currentGroup = subSessionId
      webSocketAPI.connect({
        sessionId: sessionId,
        contentsId: 'touch',
        onEventReceived: this.fetchBallData,
        subscriptions: ['game']
      })
    },
    fetchBallData(event) {
      const { sessionId, size } = event
      this.ballMap.set(sessionId, size)
    },
    onBallClick(sessionId, subSessionId) {
      const clickData = {
        mainSessionId: sessionId,
        sessionId,
        subSessionId,
        type: 'DECREASE'
      }
      if (this.currentGroup === this.userGroup) {
        clickData.type = 'INCREASE'
      }
    },
    onChangeClick(sessionId) {
      this.currentGroup = sessionId
    },
    onReturnClick() {
      this.currentGroup = this.userGroup
    }
  },
  getters: {
    getCurrentGroup: (state) => state.currentGroup,
    getGroupName: (state) => (groupId) => state.groupNameMap.get(groupId),
    getBallSize: (state) => (groupId) => state.ballMap.get(groupId),
    getBallImage: (state) => (groupId) => {
      if (groupId === state.userGroup) return greenBall
      return blueBall
    },
    getTotalBalls: (state) => {
      const totalBall = []
      state.ballMap.forEach((size, sessionId) => {
        totalBall.push({
          sessionId,
          groupName: state.groupNameMap.get(sessionId),
          size
        })
      })
      return totalBall
    }
  }
})
