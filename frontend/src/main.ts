import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import './styles/globals.css'
import AppShell from './components/AppShell.vue'

import { useAuthStore } from '@/stores/auth'
import { setOnUnauthorizedHandler } from '@/api/http'

const app = createApp(AppShell)
const pinia = createPinia()
app.use(pinia)

const auth = useAuthStore()
auth.hydrateFromStorage()

setOnUnauthorizedHandler(() => {
  try {
    auth.logout()
    router.push({ name: 'login' }).catch(() => {})
  } catch (e) {}
})

app.use(router)
app.mount('#app')
