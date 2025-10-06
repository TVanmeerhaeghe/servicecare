<template>
  <div class="flex min-h-screen">
    <aside v-if="route.name !== 'login'" class="sidebar">
      <div class="sidebar-header">
        <RouterLink to="/dashboard" class="branding">
          <div class="flex items-center gap-2">
            <div class="w-11 h-11 flex items-center justify-center rounded-lg bg-primary text-primary-contrast font-semibold shadow-layer-1">
              SC
            </div>
            <span class="font-semibold tracking-wide"><a href="/dashboard">ServiceCare</a></span>
          </div>

        </RouterLink>
      </div>

      <nav class="nav-section px-2">
        <RouterLink
          v-for="item in nav"
          :key="item.to"
          :to="item.to"
          class="nav-item"
          :class="isActive(item.to) ? 'nav-item-active' : ''"
        >
          <div class="w-9 h-9 flex items-center justify-center rounded-md bg-surface-alt/70 text-text-muted shadow-inset text-[12px] font-semibold">
            {{ item.icon }}
          </div>
          <span class="truncate">{{ item.label }}</span>
        </RouterLink>
      </nav>

      <div class="sidebar-footer">
        <div v-if="auth.isAuthenticated" class="text-xs text-muted truncate">
          {{ auth.displayName || 'Utilisateur' }}
        </div>
        <button v-if="auth.isAuthenticated" @click="onLogout" class="btn btn-ghost flex-1 text-xs">
          Déconnexion
        </button>
      </div>
    </aside>

    <div class="flex-1 flex flex-col min-w-0">
      <header v-if="route.name !== 'login'" class="app-header bg-bg-soft/24">
        <h1 class="text-lg font-semibold tracking-tight">{{ pageTitle }}</h1>
        <div class="ml-auto flex items-center gap-3">
          <button class="btn btn-ghost text-xs">Profil</button>
        </div>
      </header>

      <main class="flex-1 app-main overflow-auto">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const nav = [
  { to: '/tickets', label: 'Tickets', icon: 'TK' },
  { to: '/clients', label: 'Clients', icon: 'CL' },
  { to: '/sites', label: 'Sites', icon: 'ST' },
]

const pageTitle = computed(() => {
  const match = [...route.matched].reverse().find((r) => r.meta?.title)
  return (match?.meta?.title as string) || 'ServiceCare'
})

function isActive(path: string) {
  return route.path === path || route.path.startsWith(`${path}/`)
}

function onLogout() {
  auth.logout()
  router.push({ name: 'login' })
}
</script>
