<template>
  <div class="page-stack">
    <header class="page-header">
      <div>
        <p class="text-sm text-muted">Gestion des environnements clients.</p>
      </div>
      <div class="filters-controls">
        <button class="btn btn-primary text-sm" @click="goToCreate">
          Ajouter un site
        </button>
      </div>
    </header>

    <div class="filters-bar">
        <div class="filters-controls">
          <label class="field">
            <span>Recherche</span>
            <input
              v-model.trim="filters.query"
              class="input"
              type="search"
              placeholder="Nom, URL…"
              @keyup.enter="reload"
            />
          </label>
          <label class="field">
            <span>Statut</span>
            <select v-model="filters.status" class="input" @change="reload">
              <option value="">Tous</option>
              <option value="ACTIVE">Actifs</option>
              <option value="INACTIVE">Inactifs</option>
            </select>
          </label>
          <label class="field">
            <span>Techno</span>
            <select v-model="filters.cms" class="input" @change="reload">
              <option value="">Tous</option>
              <option value="WORDPRESS">WordPress</option>
              <option value="SHOPIFY">Shopify</option>
              <option value="PRESTASHOP">PrestaShop</option>
              <option value="DRUPAL">Drupal</option>
              <option value="JOOMLA">Joomla</option>
              <option value="MAGENTO">Magento</option>
              <option value="WIX">Wix</option>
              <option value="CUSTOM">Custom</option>
              <option value="OTHER">Autre</option>
            </select>
          </label>
        </div>
        <div class="text-sm text-muted">
          {{ pagination.total }} résultat(s)
        </div>
      </div>

    <section class="data-card">

      <table class="data-table">
        <thead>
          <tr>
            <th>Nom</th>
            <th>URL</th>
            <th>Techno</th>
            <th>Type</th>
            <th>Statut</th>
            <th class="text-right">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="6" class="text-center text-muted py-6">Chargement…</td>
          </tr>
          <tr v-else-if="!sites.length">
            <td colspan="6" class="text-center text-muted py-6">Aucun site trouvé.</td>
          </tr>
          <tr v-for="site in sites" :key="site.id">
            <td>
              <div class="data-table__cell">
                <span class="data-table__cell--main">{{ site.name || '—' }}</span>
              </div>
            </td>
            <td>
              <div class="data-table__cell">
                <span class="data-table__cell--main">{{ site.url || '—' }}</span>
              </div>
            </td>
            <td>
              <div class="data-table__cell">
                <span class="data-table__cell--main">{{ site.cms || '—' }}</span>
              </div>
            </td>
            <td>
              <div class="data-table__cell">
                <span class="data-table__cell--main">{{ site.type || '—' }}</span>
              </div>
            </td>
            <td class="data-table__status">
              <span
                class="badge"
                :class="badgeClass(site.status)"
              >
                {{ site.status || '—' }}
              </span>
            </td>
            <td class="data-table__actions">
              <div class="btn-group">
                <button class="btn btn-ghost text-sm" @click="goToEdit(site.id)">Modifier</button>
                <button class="btn btn-ghost text-sm" @click="goToDetails(site.id)">Détails</button>
                <button class="btn btn-ghost text-sm text-danger" @click="askDelete(site)">Supprimer</button>
              </div>
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
  </div>

  <ConfirmDialog
    v-if="confirmVisible"
    @cancel="confirmVisible = false"
    @confirm="handleDelete"
  >
    <template #title>Supprimer le site</template>
    Confirmer la suppression de <strong>{{ targetName }}</strong> ?
  </ConfirmDialog>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fetchSites, deleteSite } from '@/api/sites'
import type { Site } from '@/types/sites'
import ConfirmDialog from '@/components/ConfirmDialog.vue'

const router = useRouter()
const sites = ref<Site[]>([])
const loading = ref(false)
const confirmVisible = ref(false)
const targetId = ref<number | null>(null)
const targetName = ref('')
const filters = reactive({
  query: '',
  status: '',
  cms: '',
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0,
})

const totalPages = computed(() =>
  Math.max(1, Math.ceil(pagination.total / pagination.size))
)

const badgeClass = (status: string | null) => ({
  'badge--status-active': status === 'ACTIVE',
  'badge--status-inactive': status === 'INACTIVE',
})

async function load() {
  loading.value = true
  try {
    const { data } = await fetchSites({
      page: pagination.page - 1,
      size: pagination.size,
      search: filters.query || undefined,
      status: filters.status || undefined,
      cms: filters.cms || undefined,
    })
    sites.value = data.content
    pagination.total = data.totalElements
  } finally {
    loading.value = false
  }
}

function refresh() {
  pagination.page = 1
  load()
}

function resetFilters() {
  filters.query = ''
  filters.status = ''
  filters.cms = ''
  refresh()
}

function goToCreate() {
  router.push({ name: 'site-create' })
}

function goToEdit(id: number) {
  router.push({ name: 'site-edit', params: { id } })
}

function goToDetails(id: number) {
  router.push({ name: 'site-details', params: { id } })
}

function askDelete(site: Site) {
  targetId.value = site.id
  targetName.value = site.name || 'Ce site'
  confirmVisible.value = true
}

async function handleDelete() {
  if (targetId.value == null) return
  await deleteSite(targetId.value)
  confirmVisible.value = false
  await load()
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

onMounted(load)
</script>