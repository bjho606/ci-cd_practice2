import { defineStore } from 'pinia'

export const useContentsStore = defineStore('contents', {
  state: () => ({
    totalContents: [],
    currentContentsId: null,
    currentContents: null,
    selectedContents: []
  }),
  actions: {
    setTotalContents(contents) {
      this.totalContents = contents
    },
    setContentsInfo(event) {
      this.currentContentsId = event.contentsId
      this.currentContents = event.sequence
      this.selectedContents = event.total
    }
  }
})
