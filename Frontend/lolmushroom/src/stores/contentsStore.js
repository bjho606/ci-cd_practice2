import { defineStore } from 'pinia'

export const useContentsStore = defineStore('contents', {
  state: () => ({
    contents: [],
    routeMapping: {
      1: 'TOF',
      2: null,
      3: 'MushroomGrowContainer',
      4: null,
      5: null,
      6: null,
      7: null,
      8: null,
      null: 'EndingPage'
    },
    currentContentsId: null,
    contentsSequence: null,
    totalContentsCount: null,
    finishGroupCount: null,
    totalGroupCount: null,
    currentGroupState: [] // Array of group states
  }),
  actions: {
    setContents(contents) {
      this.contents = contents
    },
    setCurrentContentsState(contentState) {
      this.currentContentsId = contentState.contentsId
      this.contentsSequence = contentState.contentsSequence
      this.totalContentsCount = contentState.totalContentsCount
      this.finishGroupCount = contentState.finishGroupCount
      this.totalGroupCount = contentState.totalGroupCount
      this.currentGroupState = [...contentState.currentGroupState]
    }
  },
  getters: {
    getContents: (state) => state.contents,
    getCurrentContentsId: (state) => state.currentContentsId,
    getContentsSequence: (state) => state.contentsSequence,
    getRouteMapping: (state) => state.routeMapping
  }
})
