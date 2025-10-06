<template>
  <div class="page-stack">
    <header class="page-header">
      <div>
        <p class="text-sm text-muted">Gestion des comptes et contacts</p>
      </div>
      <button class="btn btn-primary" @click="goCreate">
        Ajouter un client
      </button>
    </header>

    <section class="filters-bar">
      <div class="filters-controls">
        <label class="field">
          <span>Recherche</span>
          <input
            v-model.trim="filters.query"
            class="input"
            type="search"
            placeholder="Nom, email, société…"
            @keyup.enter="reload"
          />
        </label>
        <label class="field">
          <span>Statut</span>
          <select v-model="filters.status" class="input" @change="reload">
            <option value="">Tous</option>
            <option value="ACTIVE">Actifs</option>
            <option value="INACTIVE">Inactifs</option>
            <option value="LEAD">Leads</option>
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
            <th>Client</th>
            <th>Email principal</th>
            <th>Téléphone</th>
            <th>Statut</th>
            <th class="text-right">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="4" class="text-center text-muted py-6">
              Chargement…
            </td>
          </tr>

          <tr
            v-for="client in clients"
            v-else
            :key="client.id"
            class="data-table__row"
          >
            <td>
              <div class="data-table__cell">
                <span class="data-table__cell--main">{{ client.name || '—' }}</span>
              </div>
            </td>
            <td>
              <div class="data-table__cell">
                <span class="data-table__cell--main">{{ client.contactEmail || '—' }}</span>
              </div>
            </td>
            <td>
              <div class="data-table__cell">
                <span class="data-table__cell--main">{{ client.contactPhone || '—' }}</span>
              </div>
            </td>
            <td class="data-table__status">
              <span
                class="badge"
                :class="{
                  'badge--status-active': client.status === 'ACTIVE',
                  'badge--status-inactive': client.status === 'INACTIVE',
                  'badge--status-lead': client.status === 'LEAD'
                }"
              >
                {{ client.status || '—' }}
              </span>
            </td>
            <td class="data-table__actions">
              <div class="btn-group">
                <button class="btn btn-ghost text-sm" @click="goToEdit(client.id)">
                  Modifier
                </button>
                <button class="btn btn-ghost text-sm" @click="goToDetails(client.id.toString())">
                  Détails
                </button>
                <button class="btn btn-ghost text-sm text-danger" @click="askDelete(client)">
                  Supprimer
                </button>
              </div>
            </td>
          </tr>

          <tr v-if="!loading && !clients.length">
            <td colspan="4" class="text-center text-muted py-6">
              Aucun client trouvé.
            </td>
          </tr>
        </tbody>
      </table>
    </section>

    <footer class="pagination">
      <div>Page {{ pagination.page }} / {{ totalPages }}</div>
      <div class="pagination-controls">
        <button
          class="btn btn-ghost"
          :disabled="pagination.page === 1"
          @click="changePage(pagination.page - 1)"
        >
          Précédent
        </button>
        <button
          class="btn btn-ghost"
          :disabled="pagination.page === totalPages"
          @click="changePage(pagination.page + 1)"
        >
          Suivant
        </button>
      </div>
    </footer>

    <ConfirmDialog
      v-if="confirmVisible"
      @cancel="confirmVisible = false"
      @confirm="handleDelete"
    >
      <template #title>Supprimer le client</template>
      Confirmer la suppression de <strong>{{ targetName }}</strong> ? Cette action est définitive.
    </ConfirmDialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fetchClients, deleteClient } from '@/api/clients'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import type { Client } from '@/types/clients'

const router = useRouter()

const loading = ref(false)
const clients = ref<Client[]>([])
const pagination = reactive({ page: 1, size: 10, total: 0 })
const filters = reactive({ query: '', status: '' })
const confirmVisible = ref(false)
const targetId = ref<number | null>(null)
const targetName = ref<string>('')

const totalPages = computed(() =>
  Math.max(1, Math.ceil(pagination.total / pagination.size))
)

async function load() {
  loading.value = true
  try {
    const { data } = await fetchClients({
      page: pagination.page - 1,
      size: pagination.size,
      search: filters.query || undefined,
      status: filters.status || undefined,
    })
    clients.value = data.content
    pagination.total = data.totalElements
  } finally {
    loading.value = false
  }
}

function reload() {
  pagination.page = 1
  load()
}

function changePage(next: number) {
  if (next < 1 || next > totalPages.value) return
  pagination.page = next
  load()
}

function goToDetails(id: string) {
  router.push({ name: 'client-details', params: { id } })
}

function goCreate() {
  router.push({ name: 'client-create' })
}

function goToEdit(id: number) {
  router.push({ name: 'client-edit', params: { id } })
}

async function handleDelete() {
  if (targetId.value == null) return
  await deleteClient(targetId.value)
  confirmVisible.value = false
  await load()
}

function askDelete(client: Client) {
  targetId.value = client.id
  targetName.value = client.name || 'Ce client'
  confirmVisible.value = true
}

onMounted(load)
</script>