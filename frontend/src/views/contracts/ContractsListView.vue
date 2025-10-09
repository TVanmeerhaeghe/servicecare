<template>
  <div class="page-stack">
    <header class="page-header">
      <div>
        <p class="text-sm text-muted">Gestion des contrats (SLA, périodes, sites).</p>
      </div>
      <button v-if="!isClientRole" class="btn btn-primary" @click="goCreate">
        Nouveau contrat
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
            placeholder="Nom, description…"
            @keyup.enter="reload"
          />
        </label>
        <label class="field">
          <span>Statut</span>
          <select v-model="filters.status" class="input" @change="reload">
            <option value="">Tous</option>
            <option value="ACTIVE">Actifs</option>
            <option value="INACTIVE">Inactifs</option>
            <option value="EXPIRED">Expirés</option>
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
            <th v-if="!isClientRole">Client</th>
            <th>Période</th>
            <th>Statut</th>
            <th class="text-right">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td :colspan="isClientRole ? 4 : 5" class="text-center text-muted py-6">
              Chargement…
            </td>
          </tr>

          <tr
            v-for="c in items"
            v-else
            :key="c.id"
            class="data-table__row"
          >
            <td>
              <div class="data-table__cell">
                <span class="data-table__cell--main">{{ c.name }}</span>
              </div>
            </td>
            <td v-if="!isClientRole">
              <div class="data-table__cell">
                <span class="data-table__cell--main">{{ c.clientName || ('#' + (c.clientId ?? '—')) }}</span>
              </div>
            </td>
            <td>
              <div class="data-table__cell">
                <span class="data-table__cell--main">{{ fmtDate(c.startDate) }} → {{ fmtDate(c.endDate) }}</span>
              </div>
            </td>
            <td class="data-table__status">
              <span class="badge">{{ c.status }}</span>
            </td>
            <td class="data-table__actions">
              <div class="btn-group">
                <button class="btn btn-ghost text-sm" @click="goToDetails(c.id)">
                  Détails
                </button>
                <button v-if="!isClientRole" class="btn btn-ghost text-sm" @click="goToEdit(c.id)">
                  Modifier
                </button>
                <button v-if="!isClientRole" class="btn btn-ghost text-sm text-danger" @click="askDelete(c)">
                  Supprimer
                </button>
              </div>
            </td>
          </tr>

          <tr v-if="!loading && !items.length">
            <td :colspan="isClientRole ? 4 : 5" class="text-center text-muted py-6">
              Aucun contrat trouvé.
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
      <template #title>Supprimer le contrat</template>
      Confirmer la suppression de <strong>{{ targetName }}</strong> ?
      Cette action est définitive.
    </ConfirmDialog>
  </div>
</template>

<script setup lang="ts">
// filepath: c:\Users\vanme\Desktop\servicecare\frontend\src\views\contracts\ContractsListView.vue
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { fetchContracts, searchContracts, deleteContract } from '@/api/contracts'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import type { Contract } from '@/types/contracts'

const router = useRouter()
const auth = useAuthStore()
const isClientRole = computed(() => auth.isClientRole)

const loading = ref(false)
const items = ref<Contract[]>([])
const pagination = reactive({ page: 1, size: 10, total: 0 })
const filters = reactive<{ query: string; status: '' | 'ACTIVE' | 'INACTIVE' | 'EXPIRED' }>({
  query: '',
  status: '',
})
const confirmVisible = ref(false)
const targetId = ref<number | null>(null)
const targetName = ref<string>('')

const totalPages = computed(() =>
  Math.max(1, Math.ceil(pagination.total / pagination.size))
)

function fmtDate(v?: string | null) {
  if (!v) return '—'
  return new Intl.DateTimeFormat('fr-FR', { dateStyle: 'medium' }).format(new Date(v))
}

async function load() {
  loading.value = true
  try {
    const paramsBase = { page: pagination.page - 1, size: pagination.size }
    const hasFilters = !!(filters.query || filters.status)

    if (isClientRole.value) {
      const { data } = await searchContracts({
        ...paramsBase,
        q: filters.query || undefined,
        status: (filters.status || undefined) as any,
      })
      items.value = data.content
      pagination.total = data.totalElements
    } else {
      if (hasFilters) {
        const { data } = await searchContracts({
          ...paramsBase,
          q: filters.query || undefined,
          status: (filters.status || undefined) as any,
        })
        items.value = data.content
        pagination.total = data.totalElements
      } else {
        const { data } = await fetchContracts(paramsBase)
        items.value = data.content
        pagination.total = data.totalElements
      }
    }
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

function goToDetails(id: number) {
  router.push({ name: 'contract-details', params: { id } })
}
function goToEdit(id: number) {
  router.push({ name: 'contract-edit', params: { id } })
}
function goCreate() {
  router.push({ name: 'contract-create' })
}

async function handleDelete() {
  if (targetId.value == null) return
  await deleteContract(targetId.value)
  confirmVisible.value = false
  await load()
}

function askDelete(c: Contract) {
  targetId.value = c.id
  targetName.value = c.name
  confirmVisible.value = true
}

onMounted(load)
</script>