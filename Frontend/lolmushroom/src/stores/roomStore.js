import { defineStore } from 'pinia'

export const useRoomStore = defineStore('room', {
  state: () => ({
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
        buttonClicked: false
      }))
    },
    setRooms(groups) {
      groups.forEach((group, index) => {
        if (index < this.rooms.length) {
          this.rooms[index] = {
            // ...this.rooms[index],
            occupants: group.currentUserCount,
            capacity: group.maxUserCount,
            buttonClicked: true,
            groupName: group.groupName,
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
    }
  },
  getters: {
    getRoomCount: (state) => state.roomCount,
    getRooms: (state) => state.rooms
  },
  persist: {
    key: 'room-info-store',
    storage: sessionStorage,
    paths: ['roomCount', 'rooms']
  }
})

/*

import { defineStore } from 'pinia'

export const useRoomStore = defineStore('room', {
  state: () => ({
    roomCount: 10,
    rooms: Array.from({ length: 10 }, () => ({ occupants: 0, capacity: 8, buttonClicked: false })),
    roomName: ''
  }),
  actions: {
    setRoomCount(count) {
      this.roomCount = count
      this.rooms = Array.from({ length: count }, () => ({
        occupants: 0,
        capacity: 8,
        buttonClicked: false
      }))
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
    }
  },
  getters: {
    getRoomCount: (state) => state.roomCount,
    getRooms: (state) => state.rooms
  },
  persist: {
    key: 'room-info-store',
    storage: sessionStorage, // 세션 스토리지에 저장
    paths: ['roomCount', 'rooms', 'roomName'] // 저장할 상태의 경로
  }
})
  */
