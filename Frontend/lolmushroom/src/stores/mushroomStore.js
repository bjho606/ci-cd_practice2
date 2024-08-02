import { defineStore } from 'pinia'
import bigNormal from '@/assets/mushroomGrow/BigNormal.svg'
import midNormal from '@/assets/mushroomGrow/MidNormal.svg'
import smallNormal from '@/assets/mushroomGrow/SmallNormal.svg'

export const useMushroomStore = defineStore('mushroomStore', {
  state: () => ({
    initSize: 20,
    midSize: 30,
    finalSize: 40,
    maxSize: 50,
    previewSize: 10,
    userGroupMushroomSize: 20,
    currentGroup: 'User Group',
    groups: [
      { name: 'Group2', size: 40 },
      { name: 'Group3', size: 40 },
      { name: 'Group4', size: 40 },
      { name: 'Group5', size: 40 },
      { name: 'Group6', size: 40 },
      { name: 'Group7', size: 40 },
      { name: 'Group8', size: 40 },
      { name: 'Group9', size: 50 },
      { name: 'Group10', size: 40 }
    ]
  }),
  getters: {
    userGroupMushroomImage: (state) => {
      return state.getMushroomImage(state.userGroupMushroomSize)
    },
    currentMushroomSize: (state) => {
      return state.currentGroup === 'User Group'
        ? state.userGroupMushroomSize
        : state.groups.find((group) => group.name === state.currentGroup)?.size
    },
    currentMushroomImage: (state) => {
      if (state.currentGroup === 'User Group') {
        return state.getMushroomImage(state.userGroupMushroomSize)
      } else {
        const group = state.groups.find((g) => g.name === state.currentGroup)
        return group
          ? state.getMushroomImage(group.size)
          : state.getMushroomImage(state.initialSize)
      }
    }
  },
  actions: {
    getMushroomImage(size) {
      if (size < this.midSize) return smallNormal
      if (size < this.finalSize) return midNormal
      return bigNormal
    },
    onMushroomClick(event) {
      const rect = event.target.getBoundingClientRect()
      this.clickPosition = {
        x: event.clientX - rect.left,
        y: event.clientY - rect.top
      }
      this.clickEffect = true
      setTimeout(() => (this.clickEffect = false), 100)

      if (this.currentGroup === 'User Group') {
        if (this.userGroupMushroomSize < this.maxSize) {
          this.userGroupMushroomSize += 0.05
          this.sendClickDataToServer('grow')
        }
      } else {
        const group = this.groups.find((g) => g.name === this.currentGroup)
        if (group && group.size > this.initialSize) {
          group.size -= 0.05
          this.currentMushroomSize = group.size
          this.sendClickDataToServer('shrink')
        }
      }
    },
    onGroupMushroomClick(group) {
      this.currentGroup = group.name
    },
    onReturnClick() {
      this.currentGroup = 'User Group'
    },
    sendClickDataToServer(action) {
      console.log(`버섯 클릭 데이터 전송됨: ${action}`)
    }
  }
})
