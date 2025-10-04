import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import './styles/globals.css'
import AppShell from './components/AppShell.vue'

const app = createApp(AppShell)
app.use(createPinia())
app.use(router)
app.mount('#app')
