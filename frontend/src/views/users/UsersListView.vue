<template>
  <div class="page-stack">
    <header class="page-header">
      <div>
        <p class="text-sm text-muted">Gestion des utilisateurs.</p>
      </div>
      <button class="btn btn-primary" @click="goCreate">Nouvel utilisateur</button>
    </header>

    <section class="filters-bar">
      <div class="filters-controls">
        <label class="field">
          <span>Recherche</span>
          <input v-model.trim="filters.q" class="input" type="search" placeholder="Nom, email, téléphone…" @keyup.enter="reload" />
        </label>
        <label class="field">
          <span>Rôle</span>
          <select v-model="filters.role" class="input" @change="reload">
            <option value="">Tous</option>
            <option value="ADMIN">Admin</option>
            <option value="AGENT">Agent</option>
            <option value="TECHNICIAN">Technicien</option>
            <option value="CLIENT">Client</option>
          </select>
        </label>
        <label class="field">
          <span>Statut</span>
          <select v-model="filters.status" class="input" @change="reload">
            <option value="">Tous</option>
            <option value="ACTIVE">Actif</option>
            <option value="DISABLED">Désactivé</option>
            <option value="INVITED">Invité</option>
          </select>
        </label>
      </div>
      <div class="text-sm text-muted">
        {{ pagination.total }} résultat(s)
      </div>
    </section>

    <section class="data-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>Nom</th>
            <th>Email</th>
            <th>Téléphone</th>
            <th>Rôle</th>
            <th>Statut</th>
            <th class="text-right">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="6" class="text-center text-muted py-6">Chargement…</td>
          </tr>

          <tr v-for="u in users" v-else :key="u.id" class="data-table__row">
            <td>
              <div class="data-table__cell">
                <span class="data-table__cell--main">{{ fullName(u) }}</span>
              </div>
            </td>
            <td><div class="data-table__cell"><span class="data-table__cell--main">{{ u.email }}</span></div></td>
            <td><div class="data-table__cell"><span class="data-table__cell--main">{{ u.phone || '—' }}</span></div></td>
            <td class="data-table__status">
              <span class="badge">{{ roleLabel(u.role) }}</span>
            </td>
            <td class="data-table__status">
              <span class="badge">{{ statusLabel(u.status) }}</span>
            </td>
            <td class="data-table__actions">
              <div class="btn-group">
                <button class="btn btn-ghost text-sm" @click="goToEdit(u.id)">Modifier</button>
                <button class="btn btn-ghost text-sm" @click="goToDetails(u.id)">Détails</button>
                <button class="btn btn-ghost text-sm text-danger" @click="askDelete(u)">Supprimer</button>
              </div>
            </td>
          </tr>

          <tr v-if="!loading && !users.length">
            <td colspan="6" class="text-center text-muted py-6">Aucun utilisateur trouvé.</td>
          </tr>
        </tbody>
      </table>
    </section>

    <footer class="pagination">
      <div>Page {{ pagination.page }} / {{ totalPages }}</div>
      <div class="pagination-controls">
        <button class="btn btn-ghost" :disabled="pagination.page === 1" @click="changePage(pagination.page - 1)">Précédent</button>
        <button class="btn btn-ghost" :disabled="pagination.page === totalPages" @click="changePage(pagination.page + 1)">Suivant</button>
      </div>
    </footer>

    <ConfirmDialog v-if="confirmVisible" @cancel="confirmVisible = false" @confirm="handleDelete">
      <template #title>Supprimer l’utilisateur</template>
      Confirmer la suppression de <strong>{{ targetName }}</strong> ? Cette action est définitive.
    </ConfirmDialog>
  </div>
</template>

<script setup lang="ts">
// filepath: c:\Users\vanme\Desktop\servicecare\frontend\src\views\users\UsersListView.vue
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fetchUsers, searchUsers, deleteUser } from '@/api/users'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import type { User } from '@/types/users'

const router = useRouter()
const loading = ref(false)
const users = ref<User[]>([])
const pagination = reactive({ page: 1, size: 10, total: 0 })
const filters = reactive<{ q: string; role: '' | 'ADMIN' | 'AGENT' | 'TECHNICIAN' | 'CLIENT'; status: '' | 'ACTIVE' | 'DISABLED' | 'INVITED' }>({
  q: '', role: '', status: '',
})
const confirmVisible = ref(false)
const targetId = ref<number | null>(null)
const targetName = ref<string>('')

const totalPages = computed(() => Math.max(1, Math.ceil(pagination.total / pagination.size)))

function roleLabel(v: string) { return ({ ADMIN:'Admin', AGENT:'Agent', TECHNICIAN:'Technicien', CLIENT:'Client' } as any)[v] || v }
function statusLabel(v: string) { return ({ ACTIVE:'Actif', DISABLED:'Désactivé', INVITED:'Invité' } as any)[v] || v }
function fullName(u: User) { return `${u.firstName} ${u.lastName}`.trim() }

async function load() {
  loading.value = true
  try {
    const base = { page: pagination.page - 1, size: pagination.size }
    if (filters.q || filters.role || filters.status) {
      const { data } = await searchUsers({
        ...base,
        q: filters.q || undefined,
        role: (filters.role || undefined) as any,
        status: (filters.status || undefined) as any,
      })
      users.value = data.content
      pagination.total = data.totalElements
    } else {
      const { data } = await fetchUsers(base)
      users.value = data.content
      pagination.total = data.totalElements
    }
  } finally {
    loading.value = false
  }
}

function reload() { pagination.page = 1; load() }
function changePage(next: number) {
  if (next < 1 || next > totalPages.value) return
  pagination.page = next; load()
}

function goToDetails(id: number) { router.push({ name: 'user-details', params: { id } }) }
function goToEdit(id: number) { router.push({ name: 'user-edit', params: { id } }) }
function goCreate() { router.push({ name: 'user-create' }) }

async function handleDelete() {
  if (targetId.value == null) return
  await deleteUser(targetId.value)
  confirmVisible.value = false
  await load()
}
function askDelete(u: User) {
  targetId.value = u.id
  targetName.value = fullName(u) || u.email
  confirmVisible.value = true
}

onMounted(load)
</script>