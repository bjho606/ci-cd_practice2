import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/User'
import GlobalHeader from '@/components/common/GlobalHeader.vue'
import MakeSessionView from '@/views/MakeSessionView.vue'
import RoomWatching from '@/components/room/RoomWatching.vue'
import MultiRoom from '@/components/room/MultiRoom.vue'
import SessionCuration from '@/components/setting/SessionCuration.vue'
import ManagerWating from '@/components/room/ManagerWating.vue'
import PlayerChoose from '@/components/room/PlayerChoose.vue'
import RoomWaiting from '@/components/room/RoomWaiting.vue'
import CustomRoom from '@/components/room/CustomRoom.vue'
import MushroomMyGroup from '@/components/contents/MushroomMyGroup.vue'
import MushroomGrow from '@/components/contents/Mushroom.vue'

const routes = [
  { path: '/', name: 'home', component: MakeSessionView },
  { path: '/:sessionId', name: 'mainSession', component: PlayerChoose },
  //관리자 라우트

  { path: '/:sessionId/multiroom', name: 'multiroom', component: MultiRoom },
  { path: '/:sessionId/roomwatching', name: 'roomwatching', component: RoomWatching },
  { path: '/:sessionId/managerwating', name: 'managerwating', component: ManagerWating },

  // 사용자 라우트

  // 대기 화면
  { path: '/:sessionId/:num/roomwaiting', name: 'roomwaiting', component: RoomWaiting },
  // 사용자 고르는 화면
  { path: '/:sessionId/playerchoose', name: 'playerChoose', component: PlayerChoose },

  { path: '/MushroomMyGroup', name: 'MushroomMyGroup', component: MushroomMyGroup },
  { path: '/MushroomGrow', name: 'MushroomGrow', component: MushroomGrow }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
