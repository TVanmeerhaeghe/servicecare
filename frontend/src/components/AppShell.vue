<template>
  <div class="flex min-h-screen">
    <aside v-if="route.name !== 'login'" :class="['sidebar', collapsed ? 'sidebar-collapsed' : '']">
      <div class="sidebar-header">
        <div class="w-11 h-11 flex items-center justify-center rounded-lg bg-primary text-primary-contrast font-semibold shadow-layer-1">
          SC
        </div>
        <div :class="collapsed ? 'hidden' : ''">
          <div class="font-semibold tracking-wide">ServiceCare</div>
        </div>
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
          <span :class="collapsed ? 'hidden' : ''" class="truncate">{{ item.label }}</span>
        </RouterLink>
      </nav>

      <div class="sidebar-footer">
        <div v-if="auth.isAuthenticated" class="text-xs text-muted truncate">{{ auth.displayName || 'Utilisateur' }}</div>
        <div class="flex gap-2">
          <button v-if="auth.isAuthenticated" @click="onLogout" class="btn btn-ghost flex-1 text-xs">Déconnexion</button>
        </div>
      </div>
    </aside>

    <div class="flex-1 flex flex-col min-w-0">
      <header v-if="route.name !== 'login'" class="app-header bg-bg-soft/24">
        <h1 class="text-lg font-semibold tracking-tight">Dashboard</h1>
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
import { ref } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()
const collapsed = ref(false)

const nav = [
  { to: '/dashboard', label: 'Dashboard', icon: 'DB' },
  { to: '/clients', label: 'Clients', icon: 'CL' },
  { to: '/contracts', label: 'Contrats', icon: 'CT' }
]

function isActive(path: string) { return route.path === path }
function onLogout() { auth.logout(); router.push({ name: 'login' }) }
function toggleCollapse() {
  collapsed.value = !collapsed.value
  const root = document.documentElement
  if (collapsed.value) root.classList.add('sidebar-collapsed')
  else root.classList.remove('sidebar-collapsed')
}
</script>
