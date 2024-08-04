import { defineStore } from 'pinia'

export const useRoomStore = defineStore('room', {
  state: () => ({
    userCount: 0,
    roomCount: 10,
    rooms: Array.from({ length: 10 }, () => ({
      occupants: 0,
      capacity: 8,
      buttonClicked: false,
      roomName: '',
      sessionId: ''
    }))
  }),
  actions: {
    setRoomCount(count) {
      this.roomCount = count
      this.rooms = Array.from({ length: count }, () => ({
        occupants: 0,
        capacity: 8,
        buttonClicked: false,
        roomName: '',
        sessionId: ''
      }))
    },
    setRooms(groups) {
      groups.forEach((group, index) => {
        if (index < this.rooms.length) {
          this.rooms[index] = {
            occupants: group.currentUserCount,
            capacity: group.maxUserCount,
            buttonClicked: true,
            roomName: group.groupName,
            sessionId: group.sessionId
          }
        }
      })
    },
    updateRoomOccupants(index) {
      if (
        index >= 0 &&
        index < this.roomCount &&
        this.rooms[index].occupants < this.rooms[index].capacity
      ) {
        this.rooms[index].occupants += 1
      }
    },
    setRoomName(name) {
      this.roomName = name
    },
    setButtonState(index, state) {
      if (index >= 0 && index < this.roomCount) {
        this.rooms[index].buttonClicked = state
      }
    },
    setUserCount(count) {
      this.userCount = count
    }
  },
  getters: {
    getRoomCount: (state) => state.roomCount,
    getRooms: (state) => state.rooms,
    getUserCount: (state) => state.userCount,
    getOccupantsBySessionId: (state) => (sessionId) => {
      const room = state.rooms.find(room => room.sessionId === sessionId)
      return room ? room.occupants : null 
    }
  },
  persist: {
    key: 'room-info-store',
    storage: sessionStorage,
    paths: ['roomCount', 'rooms']
  }
})
