import App from './App.vue'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import vuetify from './plugins/vuetify'
import VueCountdown from '@chenfengyuan/vue-countdown'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import './assets/main.css'
import router from './router'

const app = createApp(App)

app.component(VueCountdown.name, VueCountdown)
app.use(createPinia().use(piniaPluginPersistedstate))
app.use(router)
app.use(vuetify)
app.mount('#app')

// vuetify
// import 'vuetify/styles'
// import { createVuetify } from 'vuetify'
// import '@mdi/font/css/materialdesignicons.css'
// import * as components from 'vuetify/components'
// import * as directives from 'vuetify/directives'

// const vuetify = createVuetify({
//   components,
//   directives,
//   icons: {
//     iconfont: 'mdi'
//   }
// })
