import { defineStore } from 'pinia'
import webSocketAPI from '@/api/webSocket'
import blueBall from '@/assets/ballGrow/BlueBall.svg'
import greenBall from '@/assets/ballGrow/GreenBall.svg'
/**
 * IMP : MushroomGrow Store
 * * MushroomGrow Contents에 대한 전반적인 정보를 저장하고 있다.
 * * Mushroom에 대한 정보는 Session Storage에 저장되지 않는다.
 */
export const useMushroomStore = defineStore('mushroomStore', {
  state: () => ({
    // IMP : Mushroom Size BreakPoints
    initSize: 10,
    midSize: 20,
    finalSize: 30,
    maxSize: 40,

    // IMP : User Group Check and Total Mushroom Information
    userGroup: null,
    currentGroup: null,
    groupNameMap: new Map(),
    mushroomMap: new Map()
  }),
  actions: {
    /**
     * IMP 1. MushRoomGrow Game에 대한 Subscription을 수행함.
     * * 1.1. mushroomMap 정보에 대한 초기화를 진행함.
     * * 1.2. userGroup과 currentGroup에 대한 초기화를 진행함.
     * * 1.3. mushroomGrow에 대한 Subscription을 진행함.
     * REQ 1. Main Session ID, 2. SubSession ID ( USER ), 3. group 정보 ( progress Socket )
     * ! Progress Socekt -> groups : group Array { sessionId, groupName, maxUserCount, currentUserCount, users Array }
     * @param {*} sessionId
     * @param {*} subSessionId
     * @param {*} groups
     * RES fetchMushroomData()에서 설명
     */
    initSocketConnection(sessionId, subSessionId, groups) {
      groups.forEach((group) => {
        this.groupNameMap.set(group.sessionId, group.groupName)
        this.mushroomMap.set(group.sessionId, 20)
      })
      this.userGroup = this.currentGroup = subSessionId
      console.log('Connect 시도중')
      webSocketAPI.connect({
        sessionId: sessionId,
        contentsId: 'touch',
        onEventReceived: this.fetchMushroomData,
        subscriptions: ['game']
      })
    },

    /**
     * IMP 2. Socket으로부터 Data를 수신하여 각각의 Mushroom 정보를 Update
     * * 2.1 subSessionID와 변화된 Size를 받아낸다.
     * @param {*} event
     * RES mainSessionId ( Main Session ), sessionId ( Sub Session ), size
     */
    fetchMushroomData(event) {
      console.log(event)
      const { sessionId, size } = event
      this.mushroomMap.set(sessionId, size / 5)
    },

    /**
     * IMP 3. Socket을 통해 Click 정보를 Server에 Publish한다.
     * REQ sessionId ( SubSession ID ), actionType ( INCREASE, DECREASE )
     */
    onMushroomClick(sessionId, subSessionId) {
      const clickData = {
        mainSessionId: sessionId,
        sessionId: subSessionId,
        type: 'DECREASE'
      }
      if (this.currentGroup === this.userGroup) {
        clickData.type = 'INCREASE'
      }
      console.log(clickData)
      webSocketAPI.sendClickData(`/publish/game/touch`, clickData)
    },

    /**
     * IMP 4. Other Group의 Mushroom 을 Click하면 CurrentGroup으로 전환된다.
     * IMP 4.1 'Other Group'에서 UserGroup으로 전환할 수 있다.
     * REQ sessionId ( subSession ID )
     * @param {*} sessionId
     */
    onChangeClick(sessionId) {
      this.currentGroup = sessionId
    },
    onReturnClick() {
      this.currentGroup = this.userGroup
    }
  },
  getters: {
    getCurrentGroup: (state) => {
      return state.currentGroup
    },
    getMushroomName: (state) => (groupId) => {
      return state.groupNameMap.get(groupId)
    },
    getMushroomSize: (state) => (groupId) => {
      return state.mushroomMap.get(groupId)
    },
    getMushroomImage: (state) => (groupId) => {
      // groupId가 userGroup과 같으면 green ball 이미지를 반환
      if (groupId === state.userGroup) {
        return greenBall
      }
      // groupId가 userGroup과 다르면 blue ball 이미지를 반환
      return blueBall
    },
    getOtherMushrooms: (state) => {
      const otherMushrooms = []
      state.mushroomMap.forEach((size, sessionId) => {
        if (sessionId !== state.userGroup) {
          otherMushrooms.push({
            sessionId,
            groupName: state.groupNameMap.get(sessionId),
            size
          })
        }
      })
      return otherMushrooms
    },
    getAllMushrooms: (state) => {
      const allMushrooms = []
      state.mushroomMap.forEach((size, sessionId) => {
        allMushrooms.push({
          sessionId,
          groupName: state.groupNameMap.get(sessionId),
          size
        })
      })
      return allMushrooms
    }
  }
})
