import { defineStore } from 'pinia'

export const useContentsStore = defineStore('contents', {
  state: () => ({
    contents: [],
    currentContents: null,
    currentSeq: null,
    total: null
  }),
  actions: {
    setTotalContents(contents) {
      this.contents = contents
    },
    setCurrentContents(event) {
      this.currentContents = event.contentsId // 현재 진행되는 Contents의 아이디
      this.currentSeq = event.sequence // 현재 진행될 순서
      this.selecttotaledContents = event.total // 총 컨텐츠 갯수
    }
  }
})
