import { createRouter, createWebHistory } from 'vue-router'
import MakeSessionView from '@/views/MakeSessionView.vue'
import RoomWatching from '@/components/room/RoomWatching.vue'
import MultiRoom from '@/components/room/MultiRoom.vue'
import ManagerWating from '@/components/room/ManagerWating.vue'
import PlayerChoose from '@/components/room/PlayerChoose.vue'
import RoomWaiting from '@/components/room/RoomWaiting.vue'
import ManagerView from '@/views/ManagerView.vue'
import PlayerView from '@/views/PlayerView.vue'
import GroupSessionView from '@/views/GroupSessionView.vue'
import GroupFightSessionView from '@/views/GroupFightSessionView.vue'
import MushroomContent from '@/components/contents/MushroomContent.vue'
import TOFInputsComponent from '@/components/contents/TOFInputsComponent.vue'
import TOFMainComponent from '@/components/contents/TOFMainComponent.vue'

const routes = [
  // Home Route -> Session을 만드는 사람은 Manager가 된다.
  { path: '/', name: 'home', component: MakeSessionView },

  {
    path: '/admin/:sessionId',
    component: ManagerView,
    children: [
      { path: 'multiroom', name: 'multiroom', component: MultiRoom },
      { path: 'roomwatching', name: 'roomwatching', component: RoomWatching },
      { path: 'managerwating', name: 'managerwating', component: ManagerWating }
    ]
  },

  {
    path: '/:sessionId',
    component: PlayerView,
    children: [
      { path: '', name: 'mainSession', component: PlayerChoose },
      { path: ':subSessionId/roomwaiting', name: 'roomwaiting', component: RoomWaiting },
      {
        path: ':subSessionId/GroupSessionView',
        component: GroupSessionView,
          children: [
            { path: 'TOF', name: 'TOF', component: TOFInputsComponent },
            { path: 'TOFContent', name: 'TOFContent', component: TOFMainComponent }
          ]
      },
      {
        path: ':subSessionId/GroupFightSessionView',
        name: 'GroupFightSessionView',
        component: GroupFightSessionView,
        children: [{ path: 'MushroomContent', name: 'MushroomContent', component: MushroomContent }]
      },
      { path: 'playerchoose', name: 'playerChoose', component: PlayerChoose }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router