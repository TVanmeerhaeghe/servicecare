<template>
  <div class="page-stack">
    <header class="page-header">
      <div>
        <p class="text-sm text-muted">
          {{ isClientRole ? 'Créer ou mettre à jour votre demande.' : 'Créez ou mettez à jour une demande.' }}
        </p>
      </div>
      <div class="filters-controls">
        <button class="btn btn-ghost text-sm" @click="goBack">Annuler</button>
        <button class="btn btn-primary text-sm" @click="submit" :disabled="submitting">
          {{ submitting ? '...' : (isEdit ? 'Enregistrer' : 'Créer') }}
        </button>
      </div>
    </header>

    <section class="data-card">
      <form class="grid gap-8 p-6" @submit.prevent="submit">
        <div class="form-section">
          <h2 class="form-section-title">Informations</h2>
          <div class="form-grid">
            <label class="field field-col-span-2">
              <span>Titre *</span>
              <input v-model="form.title" class="input" required />
            </label>

            <label class="field" v-if="!isClientRole && !isEdit">
              <span>Client *</span>
              <select v-model="selectedClientId" class="input" required>
                <option value="" disabled>Sélectionner un client</option>
                <option v-for="c in clientOptions" :key="c.id" :value="c.id">
                  {{ c.name || ('Client #' + c.id) }}
                </option>
              </select>
            </label>

            <label class="field" v-if="(isClientRole || form.clientId)">
              <span>Site *</span>
              <select v-model="selectedSiteId" class="input" required :disabled="sitesLoading || !siteOptions.length">
                <option value="" disabled>Sélectionner un site</option>
                <option v-for="s in siteOptions" :key="s.id" :value="s.id">
                  {{ s.name || s.url || ('Site #' + s.id) }}
                </option>
              </select>
              <small v-if="sitesLoading" class="text-xs text-muted">Chargement…</small>
              <small v-else-if="!sitesLoading && form.clientId && !siteOptions.length" class="text-xs text-muted">
                Aucun site trouvé.
              </small>
              <small v-if="sitesError" class="text-xs text-danger">{{ sitesError }}</small>
            </label>

            <input v-if="isClientRole && form.contractId" type="hidden" :value="form.contractId" />

            <label class="field">
              <span>Priorité</span>
              <select v-model="form.priority" class="input">
                <option value="CRITICAL">Critique</option>
                <option value="HIGH">Haute</option>
                <option value="MEDIUM">Moyenne</option>
                <option value="LOW">Basse</option>
              </select>
            </label>

            <label class="field" v-if="!isClientRole">
              <span>Statut</span>
              <select v-model="form.status" class="input">
                <option value="OPEN">Ouvert</option>
                <option value="ASSIGNED">Assigné</option>
                <option value="IN_PROGRESS">En cours</option>
                <option value="WAITING">En attente</option>
                <option value="CLOSED">Fermé</option>
                <option value="CANCELED">Annulé</option>
              </select>
            </label>

            <label class="field" v-if="!isClientRole">
              <span>Raison attente</span>
              <input v-model="form.waitingReason" class="input" />
            </label>
          </div>
        </div>

        <div class="form-section">
          <h2 class="form-section-title">Description</h2>
          <label class="field">
            <textarea
              v-model="form.description"
              class="input"
              rows="6"
              placeholder="Décrivez votre problème ou demande…"
              :readonly="isEdit"
              :disabled="isEdit"
            ></textarea>
            <small v-if="isEdit" class="text-xs text-muted">
              La description initiale n’est pas modifiable. Ajoutez un commentaire pour compléter.
            </small>
          </label>
        </div>

        <div class="form-actions">
          <button type="button" class="btn btn-ghost" @click="goBack">Annuler</button>
            <button type="submit" class="btn btn-primary" :disabled="submitting">
              {{ submitting ? '...' : (isEdit ? 'Enregistrer' : 'Créer') }}
            </button>
        </div>
      </form>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createTicket, updateTicket, fetchTicketDetails } from '@/api/tickets'
import { fetchClients } from '@/api/clients'
import { fetchSitesByClient } from '@/api/sites'
import type { SiteLight } from '@/types/sites'
import type { TicketPayload, Ticket } from '@/types/tickets'
import type { Client } from '@/types/clients'
import { useAuthStore } from '@/stores/auth'
import api from '@/api/http'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const isClientRole = computed(() => auth.isClientRole)
const isEdit = computed(() => Boolean(route.params.id))
const submitting = ref(false)

const clientOptions = ref<Client[]>([])
const siteOptions = ref<SiteLight[]>([])
const sitesLoading = ref(false)
const sitesError = ref<string | null>(null)

const form = reactive<TicketPayload>({
  clientId: null,
  title: '',
  description: '',
  siteId: null,
  contractId: null,
  priority: 'MEDIUM',
  assigneeUserId: null,
  status: 'OPEN',
  waitingReason: '',
})

const selectedClientId = computed({
  get: () => form.clientId ?? '',
  set: (v: string | number) => {
    form.clientId = v === '' ? null : Number(v)
    if (!isClientRole.value) {
      form.siteId = null
      form.contractId = null
      loadSitesForClient()
      loadContractForClient()
    }
  },
})

const selectedSiteId = computed({
  get: () => form.siteId ?? '',
  set: (v: string | number) => {
    form.siteId = v === '' ? null : Number(v)
  },
})

async function loadClients() {
  if (isClientRole.value) return
  const { data } = await fetchClients({ page: 0, size: 100 })
  clientOptions.value = data.content
}

async function loadSitesForClient() {
  if (!form.clientId) {
    siteOptions.value = []
    return
  }
  sitesLoading.value = true
  sitesError.value = null
  try {
    const { data } = await fetchSitesByClient(form.clientId)
    siteOptions.value = data
    if (data.length === 1) form.siteId = data[0].id
  } catch {
    sitesError.value = 'Erreur chargement sites'
    siteOptions.value = []
  } finally {
    sitesLoading.value = false
  }
}

async function loadContractForClient() {
  const targetClientId = form.clientId
  if (!targetClientId) return
  const { data } = await api.get('/contracts/search', {
    params: { clientId: targetClientId, page: 0, size: 1 },
  })
  if (data.content?.length) {
    form.contractId = data.content[0].id
  }
}

async function loadClientContextIfClient() {
  if (!isClientRole.value) return
  if (auth.clientId) {
    form.clientId = auth.clientId
    await Promise.all([loadSitesForClient(), loadContractForClient()])
  }
}

async function preload() {
  if (!isEdit.value) return
  const { data } = await fetchTicketDetails(route.params.id as string)
  applyTicketToForm(data)
  if (form.clientId) {
    await Promise.all([loadSitesForClient(), loadContractForClient()])
  }
}

function applyTicketToForm(ticket: Ticket) {
  form.clientId = ticket.clientId ?? null
  form.title = ticket.title
  form.description = ticket.description ?? ''
  form.siteId = ticket.siteId ?? null
  form.contractId = ticket.contractId ?? null
  form.priority = ticket.priority
  form.assigneeUserId = ticket.assigneeUserId ?? null
  form.status = ticket.status
  form.waitingReason = ticket.waitingReason ?? ''
}

function buildUpdatePayload(): Partial<TicketPayload> {
  return {
    title: form.title,
    priority: form.priority,
    assigneeUserId: form.assigneeUserId ?? null,
    status: form.status,
    waitingReason: form.waitingReason || null,
    siteId: form.siteId ?? null,
    contractId: form.contractId ?? null,
  }
}

async function submit() {
  if (!form.title || !form.clientId) return
  if (isClientRole.value && !form.siteId) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateTicket(route.params.id as string, buildUpdatePayload())
    } else {
      const payload: TicketPayload = {
        clientId: form.clientId,
        title: form.title,
        description: form.description || null,
        siteId: form.siteId ?? null,
        contractId: form.contractId ?? null,
        priority: form.priority,
        assigneeUserId: isClientRole.value ? null : form.assigneeUserId,
      }
      await createTicket(payload)
    }
    router.push({ name: 'tickets-list' })
  } finally {
    submitting.value = false
  }
}

function goBack() {
  router.push({ name: 'tickets-list' })
}

async function init() {
  await loadClients()
  await loadClientContextIfClient()
  await preload()
  if (form.clientId && !siteOptions.value.length) {
    await loadSitesForClient()
  }
}

watch(
  () => form.clientId,
  async (n, o) => {
    if (n && n !== o) {
      await loadSitesForClient()
    }
  }
)

onMounted(init)
</script>

<style scoped>
.form-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
}
.field-col-span-2 {
  grid-column: span 2;
  min-width: 320px;
}
@media (max-width: 700px) {
  .field-col-span-2 {
    grid-column: span 1;
    min-width: 0;
  }
}
</style>