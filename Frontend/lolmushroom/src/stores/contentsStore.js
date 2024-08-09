import { defineStore } from 'pinia'

/**
 * IMP : Contents Store
 * * Meshroom의 Contents Progress에 대한 전반적인 진행 상태를 저장한다.
 * * contents : Meshroom의 Contents List를 가져온다.
 * IMP routeMapping : CurrentContentsId에 따라, Mapping해야 하는 Component Name에 대한 Map
 * IMP currentContentsId : currentContents의 ID Value
 * * contentsSequence : 지금 Contents는 선택된 Contents Curation에서 몇번째 인가?
 * * totalContentsCount : Contents Curation에서 선택된 Contents는 몇 개인가?
 * IMP finishGroupCount : 현재 Contents를 마친, Group은 몇 개인가?
 * * totalGroupCount : 현재 Contents를 진행하고 있는 Group은 몇개인가?
 * IMP currentGroupState : 모든 Group들의 상태에 대한 Information Array => ( subSessionId, isFinish )
 */
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
