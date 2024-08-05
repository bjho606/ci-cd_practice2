import { defineStore } from 'pinia'
import webSocketAPI from '@/api/webSocket'
import bigNormal from '@/assets/mushroomGrow/BigNormal.svg'
import midNormal from '@/assets/mushroomGrow/MidNormal.svg'
import smallNormal from '@/assets/mushroomGrow/SmallNormal.svg'

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
      // groups는 초기 데이터를 가져와 Map에 설정
      groups.forEach((group) => {
        this.groupNameMap.set(group.sessionId, group.groupName)
        this.mushroomMap.set(group.sessionId, 20) // 초기 사이즈 설정
      })
      this.userGroup = this.currentGroup = subSessionId // UserGroup 설정

      webSocketAPI.connect({
        sessionId: sessionId,
        contentsName: 'touch',
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
    // 소켓으로부터 데이터를 수신하여 버섯 정보를 업데이트
    fetchMushroomData(event) {
      console.log(event)
      const { sessionId, size } = event
      this.mushroomMap.set(sessionId, size)
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
     * REQ sessionId ( subSession ID )
     * @param {*} sessionId
     */
    onChangeClick(sessionId) {
      this.currentGroup = sessionId
    },
    onReturnClick() {
      this.currentGroup = this.userGroup
    },

    /**
     * ! Initialize mushroom data locally for testing purposes
     */
    initLocalData(subSessionId, groups) {
      groups.forEach((group) => {
        this.groupNameMap.set(group.sessionId, group.groupName)
        this.mushroomMap.set(group.sessionId, 30) // Initial size set to 20
      })
      this.userGroup = this.currentGroup = subSessionId // Set the first group as the user's group
    },
    updateMushroomSize(sessionId, size) {
      this.mushroomMap.set(sessionId, size)
    },
    onMushroomClickLocal(sessionId) {
      const currentSize = this.mushroomMap.get(sessionId)
      if (this.currentGroup === this.userGroup) {
        this.mushroomMap.set(sessionId, currentSize + 1) // Increase size
      } else {
        this.mushroomMap.set(sessionId, currentSize - 1) // Decrease size
      }
    }
  },
  getters: {
    // Getter Of Current Group Id
    getCurrentGroup: (state) => {
      return state.currentGroup
    },
    // Specific Group Id에 따른 Group Name
    getMushroomName: (state) => (groupId) => {
      return state.groupNameMap.get(groupId)
    },

    // Specific Group Id에 따른 Group Size
    getMushroomSize: (state) => (groupId) => {
      return state.mushroomMap.get(groupId)
    },
    // Mushroom Size에 따른 Mushroom Image 반환
    getMushroomImage: (state) => (size) => {
      if (size < state.midSize) return smallNormal
      if (size < state.finalSize) return midNormal
      return bigNormal
    },

    // Mushroom Size에 따른 Mushroom Lv 반환
    getMushroomLevel: (state) => (size) => {
      if (size < state.midSize) return 1 // Small Level
      if (size < state.finalSize) return 2 // Mid Level
      return 3 // Final Level
    },

    // 현재 그룹이 아닌 다른 그룹의 버섯 정보를 반환
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
    }
  }
})
