import { defineStore } from 'pinia'

/**
 * Progress Socket Subscribe -> Session에 대한 종합적인 정보
 * RES maxUserCount, currentUserCount, URL, GroupsArray
 * IMP : GroupsArray : ( sessionId, groupName, maxUserCount, currentUserCount, UsersArray)
 */

/**
 * TODO : roomStore을 SubScribe된 Data로 받아내야 한다.
 */

export const useRoomStore = defineStore('room', {
  state: () => ({
    activeButtonIndex: 0,
    maxUserCount: 0,
    currentUserCount: 0,
    url: '',
    roomCount: 10,
    rooms: Array.from({ length: 10 }, () => ({
      sessionId: '',
      groupName: '',
      capacity: 8,
      occupants: 0,
      users: [],
      reddy: true,
      buttonClicked: false
    }))
  }),
  actions: {
    setSessionData(sessionData) {
      this.maxUserCount = sessionData.maxUserCount
      this.currentUserCount = sessionData.currentUserCount
      this.url = sessionData.url
      this.setRooms(sessionData.groups)
    },
    setRooms(groups) {
      groups.forEach((group, index) => {
        this.rooms[index] = {
          sessionId: group.sessionId,
          groupName: group.groupName,
          capacity: group.maxUserCount,
          occupants: group.currentUserCount,
          users: group.users,
          buttonClicked: true
        }
        this.activeButtonIndex = index + 1
      })
    }
  },
  getters: {
    getRooms: (state) => state.rooms,
    getActiveRooms: (state) => state.rooms.filter((room) => room.buttonClicked),
    getActiveButtonIndex: (state) => state.activeButtonIndex,
    getTotalUserCount: (state) => state.currentUserCount,
    getTotalMaxUserCount: (state) => state.maxUserCount,
    getGroupInfoBySessionId: (state) => (sessionId) => {
      const groupInfo = state.rooms.find((room) => room.sessionId === sessionId)
      return groupInfo
    }
  },
  persist: {
    key: 'room-info-store',
    storage: sessionStorage,
    paths: ['activeButtonIndex', 'maxUserCount', 'currentUserCount', 'roomCount', 'rooms']
  }
})
