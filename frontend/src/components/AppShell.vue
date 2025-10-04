<script setup lang="ts">
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
const auth = useAuthStore()
const router = useRouter()

function onLogout() {
  auth.logout()
  router.push({ name: 'login' })
}
</script>

<template>
  <div>
    <header class="container row" style="justify-content: space-between; padding: .75rem 1rem;">
      <RouterLink to="/dashboard" class="row" style="gap:.5rem;">
        <strong>ServiceCare</strong>
      </RouterLink>

      <div class="row" v-if="auth.isAuthenticated" style="gap:1rem;">
        <span style="opacity:.8;">{{ auth.displayName }}</span>
        <button @click="onLogout">Logout</button>
      </div>
    </header>
    <main class="container">
      <RouterView />
    </main>
  </div>
</template>
