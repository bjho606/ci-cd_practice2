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
      this.currentContentsId = event.contentsId // 현재 진행되는 Contents의 아이디
      this.currentContents = event.sequence // 현재 진행될 순서
      this.selectedContents = event.total // 총 컨텐츠 갯수
    }
  }
})
