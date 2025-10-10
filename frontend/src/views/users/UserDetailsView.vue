<template>
  <div class="page-stack" v-if="user">
    <header class="page-header">
      <h1>{{ fullName(user) }}</h1>
      <div class="filters-controls">
        <button class="btn btn-ghost" @click="goBack">Retour</button>
        <button class="btn btn-primary" @click="goEdit">Modifier</button>
      </div>
    </header>

    <article class="data-card">
      <div class="p-5">
        <h2 class="section-kicker">Résumé</h2>
        <dl class="info-grid">
          <div><dt>Email</dt><dd>{{ user.email }}</dd></div>
          <div><dt>Téléphone</dt><dd>{{ user.phone || '—' }}</dd></div>
          <div><dt>Rôle</dt><dd><span class="badge">{{ roleLabel(user.role) }}</span></dd></div>
          <div><dt>Statut</dt><dd><span class="badge">{{ statusLabel(user.status) }}</span></dd></div>
          <div><dt>Client lié</dt><dd>{{ user.clientId ? ('#' + user.clientId) : '—' }}</dd></div>
        </dl>
      </div>
    </article>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchUserDetails } from '@/api/users'
import type { User } from '@/types/users'

const route = useRoute()
const router = useRouter()
const user = ref<User | null>(null)

function roleLabel(v: string) { return ({ ADMIN:'Admin', AGENT:'Agent', TECHNICIAN:'Technicien', CLIENT:'Client' } as any)[v] || v }
function statusLabel(v: string) { return ({ ACTIVE:'Actif', DISABLED:'Désactivé', INVITED:'Invité' } as any)[v] || v }
function fullName(u: User) { return `${u.firstName} ${u.lastName}`.trim() }

async function load() {
  const { data } = await fetchUserDetails(route.params.id as string)
  user.value = data
}
function goBack() { router.push({ name: 'users-list' }) }
function goEdit() { router.push({ name: 'user-edit', params: { id: route.params.id } }) }

onMounted(load)
</script>